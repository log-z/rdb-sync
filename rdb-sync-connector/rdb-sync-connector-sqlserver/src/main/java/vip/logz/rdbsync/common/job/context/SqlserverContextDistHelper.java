package vip.logz.rdbsync.common.job.context;

import com.microsoft.sqlserver.jdbc.SQLServerDriver;
import com.microsoft.sqlserver.jdbc.SQLServerXADataSource;
import org.apache.flink.connector.jdbc.JdbcConnectionOptions;
import org.apache.flink.connector.jdbc.JdbcExactlyOnceOptions;
import org.apache.flink.connector.jdbc.JdbcExecutionOptions;
import org.apache.flink.connector.jdbc.table.JdbcConnectorOptions;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import vip.logz.rdbsync.common.annotations.Scannable;
import vip.logz.rdbsync.common.config.SemanticOptions;
import vip.logz.rdbsync.common.exception.UnsupportedDistSemanticException;
import vip.logz.rdbsync.common.job.RdbSyncEvent;
import vip.logz.rdbsync.common.rule.Binding;
import vip.logz.rdbsync.common.rule.Pipeline;
import vip.logz.rdbsync.common.rule.table.Mapping;
import vip.logz.rdbsync.common.utils.sql.SqlDialectService;
import vip.logz.rdbsync.connector.jdbc.job.func.DebeziumEventToJdbcMap;
import vip.logz.rdbsync.connector.jdbc.job.func.RdbSyncJdbcSink;
import vip.logz.rdbsync.connector.jdbc.utils.GenericDeleteSqlGenerator;
import vip.logz.rdbsync.connector.sqlserver.config.SqlserverPipelineDistProperties;
import vip.logz.rdbsync.connector.sqlserver.rule.Sqlserver;
import vip.logz.rdbsync.connector.sqlserver.utils.SqlserverDialectService;
import vip.logz.rdbsync.connector.sqlserver.utils.SqlserverJdbcStatementBuilder;
import vip.logz.rdbsync.connector.sqlserver.utils.SqlserverUpsertSqlGenerator;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * SQLServer任务上下文目标辅助
 *
 * @author logz
 * @date 2024-01-27
 */
@Scannable
public class SqlserverContextDistHelper implements ContextDistHelper<Sqlserver, RdbSyncEvent> {

    private static final String JDBC_SQLSERVER_DRIVER = SQLServerDriver.class.getName();

    /**
     * 获取旁路输出上下文映射
     * @param contextMeta 任务上下文元数据
     */
    @Override
    @SuppressWarnings("unchecked")
    public Map<SideOutputTag, SideOutputContext<RdbSyncEvent>> getSideOutContexts(ContextMeta contextMeta) {
        // 1. 提取元数据
        Pipeline<Sqlserver> pipeline = (Pipeline<Sqlserver>) contextMeta.getPipeline();
        SqlserverPipelineDistProperties pipelineProperties =
                (SqlserverPipelineDistProperties) contextMeta.getPipelineDistProperties();
        String schema = pipelineProperties.get(SqlserverPipelineDistProperties.SCHEMA_NAME);

        // 2. 构建所有旁路输出上下文
        // 旁路输出关联  [旁路输出标签 -> 旁路输出上下文]
        Map<SideOutputTag, SideOutputContext<RdbSyncEvent>> sideOutputContextMap = new HashMap<>();
        for (Binding<Sqlserver> binding : pipeline.getBindings()) {
            String distTable = binding.getDistTable();
            Mapping<Sqlserver> mapping = binding.getMapping();

            // 2.1. 旁路输出标签
            SideOutputTag sideOutputTag = new SideOutputTag(distTable);

            // 2.2. 旁路输出上下文
            if (sideOutputContextMap.containsKey(sideOutputTag)) {
                continue;
            }

            // 旁路输出上下文：与标签建立关联
            SideOutputContext<RdbSyncEvent> sideOutputContext = new SideOutputContext<>();
            sideOutputContextMap.put(sideOutputTag, sideOutputContext);

            // 旁路输出上下文：配置出口
            SinkFunction<RdbSyncEvent> sink = buildSink(schema, distTable, mapping, pipelineProperties);
            sideOutputContext.setSink(sink);

            // 旁路输出上下文：初始化转换器
            sideOutputContext.setTransformer(new DebeziumEventToJdbcMap<>(mapping));
        }

        return sideOutputContextMap;
    }

    /**
     * 构造出口
     * @param schema 模式名
     * @param distTable 目标表名
     * @param mapping 表映射
     * @param pipelineProps 管道目标属性
     */
    private SinkFunction<RdbSyncEvent> buildSink(String schema,
                                                 String distTable,
                                                 Mapping<Sqlserver> mapping,
                                                 SqlserverPipelineDistProperties pipelineProps) {
        // Postgres语句模板
        SqlDialectService sqlDialectService = new SqlserverDialectService();
        String upsertSql = new SqlserverUpsertSqlGenerator(schema).generate(distTable, mapping);
        String deleteSql = new GenericDeleteSqlGenerator<Sqlserver>(sqlDialectService).generate(distTable, mapping);

        // 获取语义保证
        String semantic = pipelineProps.get(SqlserverPipelineDistProperties.SINK_SEMANTIC).toLowerCase();

        // 构造出口，取决于语义保证
        switch (semantic) {
            // 至少同步一次
            case SemanticOptions.AT_LEAST_ONCE:
                return buildAtLeastOnceSink(upsertSql, deleteSql, mapping, pipelineProps);
            // 精确同步一次
            case SemanticOptions.EXACTLY_ONCE:
                return buildExactlyOnceSink(upsertSql, deleteSql, mapping, pipelineProps);
            default:
                throw new UnsupportedDistSemanticException(semantic);
        }
    }

    /** 前缀：JDBC-URL */
    private static final String PREFIX_JDBC_URL = "jdbc:sqlserver://";

    /**
     * 构造至少同步一次出口
     * @param upsertSql 更新或插入语句
     * @param deleteSql 删除语句
     * @param mapping 表映射
     * @param pipelineProps 管道目标属性
     */
    private SinkFunction<RdbSyncEvent> buildAtLeastOnceSink(String upsertSql,
                                                            String deleteSql,
                                                            Mapping<Sqlserver> mapping,
                                                            SqlserverPipelineDistProperties pipelineProps) {
        // JDBC执行选项
        JdbcExecutionOptions executionOptions = JdbcExecutionOptions.builder()
                .withBatchSize(pipelineProps.get(JdbcConnectorOptions.SINK_BUFFER_FLUSH_MAX_ROWS))
                .withBatchIntervalMs(pipelineProps.get(JdbcConnectorOptions.SINK_BUFFER_FLUSH_INTERVAL).toMillis())
                .withMaxRetries(pipelineProps.get(JdbcConnectorOptions.SINK_MAX_RETRIES))
                .build();

        // SQLServer数据源，用于生成JDBC-URL
        String host = pipelineProps.get(SqlserverPipelineDistProperties.HOSTNAME);
        int port = pipelineProps.get(SqlserverPipelineDistProperties.PORT);
        String database = pipelineProps.getOptional(SqlserverPipelineDistProperties.DATABASE_NAME)
                .orElseThrow(() -> new IllegalArgumentException("Pipeline Dist [database-name] not specified."));
        String url = PREFIX_JDBC_URL + host + ":" + port +
                ";databaseName=" + database;

        // JDBC连接选项
        JdbcConnectionOptions connectionOptions = new JdbcConnectionOptions.JdbcConnectionOptionsBuilder()
                .withUrl(url)
                .withDriverName(JDBC_SQLSERVER_DRIVER)
                .withUsername(pipelineProps.get(SqlserverPipelineDistProperties.USERNAME))
                .withPassword(pipelineProps.get(SqlserverPipelineDistProperties.PASSWORD))
                .withConnectionCheckTimeoutSeconds(pipelineProps.getOptional(JdbcConnectorOptions.MAX_RETRY_TIMEOUT)
                        .map(Duration::toSeconds)
                        .map(Long::intValue)
                        .orElse(-1))
                .build();

        // 构造至少同步一次出口，并返回
        return RdbSyncJdbcSink.sink(
                upsertSql,
                deleteSql,
                new SqlserverJdbcStatementBuilder(mapping),
                executionOptions,
                connectionOptions
        );
    }

    /**
     * 构造精确同步一次出口
     * @param upsertSql 更新或插入语句
     * @param deleteSql 删除语句
     * @param mapping 表映射
     * @param pipelineProps 管道目标属性
     */
    private SinkFunction<RdbSyncEvent> buildExactlyOnceSink(String upsertSql,
                                                            String deleteSql,
                                                            Mapping<Sqlserver> mapping,
                                                            SqlserverPipelineDistProperties pipelineProps) {
        // JDBC执行选项
        JdbcExecutionOptions executionOptions = JdbcExecutionOptions.builder()
                .withBatchSize(pipelineProps.get(JdbcConnectorOptions.SINK_BUFFER_FLUSH_MAX_ROWS))
                .withBatchIntervalMs(pipelineProps.get(JdbcConnectorOptions.SINK_BUFFER_FLUSH_INTERVAL).toMillis())
                .withMaxRetries(0)  // 参照 FLINK-22311
                .build();

        // JDBC精确同步一次选项
        JdbcExactlyOnceOptions exactlyOnceOptions = JdbcExactlyOnceOptions.builder()
                .withMaxCommitAttempts(pipelineProps.get(SqlserverPipelineDistProperties.SINK_XA_MAX_COMMIT_ATTEMPTS))
                .withTimeoutSec(pipelineProps.getOptional(SqlserverPipelineDistProperties.SINK_XA_TIMEOUT)
                        .map(Duration::toSeconds)
                        .map(Long::intValue))
                .withTransactionPerConnection(pipelineProps.get(SqlserverPipelineDistProperties.SINK_XA_TRANSACTION_PER_CONNECTION))
                .build();

        // SQLServer数据源信息
        final String host = pipelineProps.get(SqlserverPipelineDistProperties.HOSTNAME);
        final int port = pipelineProps.get(SqlserverPipelineDistProperties.PORT);
        final String database = pipelineProps.getOptional(SqlserverPipelineDistProperties.DATABASE_NAME)
                .orElseThrow(() -> new IllegalArgumentException("Pipeline Dist [database-name] not specified."));
        final String username = pipelineProps.get(SqlserverPipelineDistProperties.USERNAME);
        final String password = pipelineProps.get(SqlserverPipelineDistProperties.PASSWORD);

        // 构造精确同步一次出口，并返回
        return RdbSyncJdbcSink.exactlyOnceSink(
                upsertSql,
                deleteSql,
                new SqlserverJdbcStatementBuilder(mapping),
                executionOptions,
                exactlyOnceOptions,
                () -> {
                    SQLServerXADataSource ds = new SQLServerXADataSource();
                    ds.setServerName(host);
                    ds.setPortNumber(port);
                    ds.setDatabaseName(database);
                    ds.setUser(username);
                    ds.setPassword(password);
                    return ds;
                }
        );
    }

}
