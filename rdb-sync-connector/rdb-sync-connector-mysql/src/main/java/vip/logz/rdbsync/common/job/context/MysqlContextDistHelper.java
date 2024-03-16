package vip.logz.rdbsync.common.job.context;

import com.mysql.cj.jdbc.Driver;
import com.mysql.cj.jdbc.MysqlDataSource;
import com.mysql.cj.jdbc.MysqlXADataSource;
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
import vip.logz.rdbsync.connector.mysql.config.MysqlPipelineDistProperties;
import vip.logz.rdbsync.connector.mysql.rule.Mysql;
import vip.logz.rdbsync.connector.mysql.utils.MysqlDialectService;
import vip.logz.rdbsync.connector.mysql.utils.MysqlJdbcStatementBuilder;
import vip.logz.rdbsync.connector.mysql.utils.MysqlUpsertSqlGenerator;

import java.sql.SQLException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Mysql任务上下文目标辅助
 *
 * @author logz
 * @date 2024-01-09
 */
@Scannable
public class MysqlContextDistHelper implements ContextDistHelper<Mysql, RdbSyncEvent> {

    private static final String JDBC_MYSQL_DRIVER = Driver.class.getName();

    /**
     * 获取旁路输出上下文映射
     * @param contextMeta 任务上下文元数据
     */
    @Override
    @SuppressWarnings("unchecked")
    public Map<SideOutputTag, SideOutputContext<RdbSyncEvent>> getSideOutContexts(ContextMeta contextMeta) {
        // 1. 提取元数据
        Pipeline<Mysql> pipeline = (Pipeline<Mysql>) contextMeta.getPipeline();
        MysqlPipelineDistProperties pipelineProperties =
                (MysqlPipelineDistProperties) contextMeta.getPipelineDistProperties();

        // 2. 构建所有旁路输出上下文
        // 旁路输出关联  [旁路输出标签 -> 旁路输出上下文]
        Map<SideOutputTag, SideOutputContext<RdbSyncEvent>> sideOutputContextMap = new HashMap<>();
        for (Binding<Mysql> binding : pipeline.getBindings()) {
            String distTable = binding.getDistTable();
            Mapping<Mysql> mapping = binding.getMapping();

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
            SinkFunction<RdbSyncEvent> sink = buildSink(distTable, mapping, pipelineProperties);
            sideOutputContext.setSink(sink);

            // 旁路输出上下文：配置转换器
            sideOutputContext.setTransformer(new DebeziumEventToJdbcMap<>(mapping));
        }

        return sideOutputContextMap;
    }

    /**
     * 构造出口
     * @param distTable 目标表名
     * @param mapping 表映射
     * @param pipelineProps 管道目标属性
     */
    private SinkFunction<RdbSyncEvent> buildSink(String distTable,
                                                 Mapping<Mysql> mapping,
                                                 MysqlPipelineDistProperties pipelineProps) {
        // MySQL语句模板
        SqlDialectService sqlDialectService = new MysqlDialectService();
        String upsertSql = new MysqlUpsertSqlGenerator().generate(distTable, mapping);
        String deleteSql = new GenericDeleteSqlGenerator<Mysql>(sqlDialectService).generate(distTable, mapping);

        // 获取语义保证
        String semantic = pipelineProps.get(MysqlPipelineDistProperties.SINK_SEMANTIC).toLowerCase();

        // 构造出口，取决于语义保证
        switch (semantic) {
            // 精确同步一次
            case SemanticOptions.EXACTLY_ONCE:
                return buildExactlyOnceSink(upsertSql, deleteSql, mapping, pipelineProps);
            // 至少同步一次
            case SemanticOptions.AT_LEAST_ONCE:
                return buildAtLeastOnceSink(upsertSql, deleteSql, mapping, pipelineProps);
            default:
                throw new UnsupportedDistSemanticException(semantic);
        }
    }

    /**
     * 构造至少同步一次出口
     * @param upsertSql 更新或插入语句
     * @param deleteSql 删除语句
     * @param mapping 表映射
     * @param pipelineProps 管道目标属性
     */
    private SinkFunction<RdbSyncEvent> buildAtLeastOnceSink(String upsertSql,
                                                            String deleteSql,
                                                            Mapping<Mysql> mapping,
                                                            MysqlPipelineDistProperties pipelineProps) {
        // JDBC执行选项
        JdbcExecutionOptions executionOptions = JdbcExecutionOptions.builder()
                .withBatchSize(pipelineProps.get(JdbcConnectorOptions.SINK_BUFFER_FLUSH_MAX_ROWS))
                .withBatchIntervalMs(pipelineProps.get(JdbcConnectorOptions.SINK_BUFFER_FLUSH_INTERVAL).toMillis())
                .withMaxRetries(pipelineProps.get(JdbcConnectorOptions.SINK_MAX_RETRIES))
                .build();

        // MySQL数据源，用于生成JDBC-URL
        MysqlDataSource ds = new MysqlDataSource();
        ds.setServerName(pipelineProps.get(MysqlPipelineDistProperties.HOSTNAME));
        ds.setPort(pipelineProps.get(MysqlPipelineDistProperties.PORT));
        ds.setDatabaseName(pipelineProps.getOptional(MysqlPipelineDistProperties.DATABASE_NAME)
                .orElseThrow(() -> new IllegalArgumentException("Pipeline Dist [database-name] not specified.")));

        // JDBC连接选项
        JdbcConnectionOptions connectionOptions = new JdbcConnectionOptions.JdbcConnectionOptionsBuilder()
                .withUrl(ds.getUrl())
                .withDriverName(JDBC_MYSQL_DRIVER)
                .withUsername(pipelineProps.get(MysqlPipelineDistProperties.USERNAME))
                .withPassword(pipelineProps.get(MysqlPipelineDistProperties.PASSWORD))
                .withConnectionCheckTimeoutSeconds(pipelineProps.getOptional(JdbcConnectorOptions.MAX_RETRY_TIMEOUT)
                        .map(Duration::toSeconds)
                        .map(Long::intValue)
                        .orElse(-1))
                .build();

        // 构造至少同步一次出口，并返回
        return RdbSyncJdbcSink.sink(
                upsertSql,
                deleteSql,
                new MysqlJdbcStatementBuilder(mapping),
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
                                                            Mapping<Mysql> mapping,
                                                            MysqlPipelineDistProperties pipelineProps) {
        // JDBC执行选项
        JdbcExecutionOptions executionOptions = JdbcExecutionOptions.builder()
                .withBatchSize(pipelineProps.get(JdbcConnectorOptions.SINK_BUFFER_FLUSH_MAX_ROWS))
                .withBatchIntervalMs(pipelineProps.get(JdbcConnectorOptions.SINK_BUFFER_FLUSH_INTERVAL).toMillis())
                .withMaxRetries(0)  // 参照 FLINK-22311
                .build();

        // JDBC精确同步一次选项
        JdbcExactlyOnceOptions exactlyOnceOptions = JdbcExactlyOnceOptions.builder()
                .withMaxCommitAttempts(pipelineProps.get(MysqlPipelineDistProperties.SINK_XA_MAX_COMMIT_ATTEMPTS))
                .withTimeoutSec(pipelineProps.getOptional(MysqlPipelineDistProperties.SINK_XA_TIMEOUT)
                        .map(Duration::toSeconds)
                        .map(Long::intValue))
                .withTransactionPerConnection(true)
                .build();

        // MySQL数据源
        MysqlXADataSource ds = new MysqlXADataSource();
        ds.setServerName(pipelineProps.get(MysqlPipelineDistProperties.HOSTNAME));
        ds.setPort(pipelineProps.get(MysqlPipelineDistProperties.PORT));
        ds.setDatabaseName(pipelineProps.getOptional(MysqlPipelineDistProperties.DATABASE_NAME)
                .orElseThrow(() -> new IllegalArgumentException("Pipeline Dist [database-name] not specified.")));
        ds.setUser(pipelineProps.get(MysqlPipelineDistProperties.USERNAME));
        ds.setPassword(pipelineProps.get(MysqlPipelineDistProperties.PASSWORD));
        try {
            ds.setConnectTimeout(pipelineProps.getOptional(JdbcConnectorOptions.MAX_RETRY_TIMEOUT)
                    .map(Duration::toMillis)
                    .map(Long::intValue)
                    .orElse(-1));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // 构造精确同步一次出口，并返回
        return RdbSyncJdbcSink.exactlyOnceSink(
                upsertSql,
                deleteSql,
                new MysqlJdbcStatementBuilder(mapping),
                executionOptions,
                exactlyOnceOptions,
                () -> ds
        );
    }

}
