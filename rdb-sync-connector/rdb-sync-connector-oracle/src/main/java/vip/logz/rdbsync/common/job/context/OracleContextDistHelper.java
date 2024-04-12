package vip.logz.rdbsync.common.job.context;

import oracle.jdbc.xa.client.OracleXADataSource;
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
import vip.logz.rdbsync.connector.jdbc.job.func.DebeziumEventToJdbcMap;
import vip.logz.rdbsync.connector.jdbc.job.func.RdbSyncJdbcSink;
import vip.logz.rdbsync.connector.oracle.config.OraclePipelineDistProperties;
import vip.logz.rdbsync.connector.oracle.rule.Oracle;
import vip.logz.rdbsync.connector.oracle.utils.OracleDeleteSqlGenerator;
import vip.logz.rdbsync.connector.oracle.utils.OracleJdbcStatementBuilder;
import vip.logz.rdbsync.connector.oracle.utils.OracleUpsertSqlGenerator;

import java.sql.SQLException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Oracle任务上下文目标辅助
 *
 * @author logz
 * @date 2024-03-18
 */
@Scannable
public class OracleContextDistHelper implements ContextDistHelper<Oracle, RdbSyncEvent> {

    private static final String JDBC_ORACLE_DRIVER = "oracle.jdbc.driver.OracleDriver";

    /** 前缀：JDBC-URL */
    private static final String PREFIX_JDBC_URL = "jdbc:oracle:thin:@";

    /** 默认驱动类型 */
    private static final String DEFAULT_DRIVER_TYPE = "thin";

    /**
     * 获取旁路输出上下文映射
     * @param contextMeta 任务上下文元数据
     */
    @Override
    @SuppressWarnings("unchecked")
    public Map<SideOutputTag, SideOutputContext<RdbSyncEvent>> getSideOutContexts(ContextMeta contextMeta) {
        // 1. 提取元数据
        Pipeline<Oracle> pipeline = (Pipeline<Oracle>) contextMeta.getPipeline();
        OraclePipelineDistProperties pipelineProperties =
                (OraclePipelineDistProperties) contextMeta.getPipelineDistProperties();
        String schema = pipelineProperties.get(OraclePipelineDistProperties.SCHEMA_NAME);

        // 2. 构建所有旁路输出上下文
        // 旁路输出关联  [旁路输出标签 -> 旁路输出上下文]
        Map<SideOutputTag, SideOutputContext<RdbSyncEvent>> sideOutputContextMap = new HashMap<>();
        for (Binding<Oracle> binding : pipeline.getBindings()) {
            String distTable = binding.getDistTable();
            Mapping<Oracle> mapping = binding.getMapping();

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
                                                 Mapping<Oracle> mapping,
                                                 OraclePipelineDistProperties pipelineProps) {
        // Oracle语句模板
        String upsertSql = new OracleUpsertSqlGenerator(schema).generate(distTable, mapping);
        String deleteSql = new OracleDeleteSqlGenerator(schema).generate(distTable, mapping);

        // 获取语义保证
        String semantic = pipelineProps.get(OraclePipelineDistProperties.SINK_SEMANTIC).toLowerCase();

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

    /**
     * 构造至少同步一次出口
     * @param upsertSql 更新或插入语句
     * @param deleteSql 删除语句
     * @param mapping 表映射
     * @param pipelineProps 管道目标属性
     */
    private SinkFunction<RdbSyncEvent> buildAtLeastOnceSink(String upsertSql,
                                                            String deleteSql,
                                                            Mapping<Oracle> mapping,
                                                            OraclePipelineDistProperties pipelineProps) {
        // JDBC执行选项
        JdbcExecutionOptions executionOptions = JdbcExecutionOptions.builder()
                .withBatchSize(pipelineProps.get(JdbcConnectorOptions.SINK_BUFFER_FLUSH_MAX_ROWS))
                .withBatchIntervalMs(pipelineProps.get(JdbcConnectorOptions.SINK_BUFFER_FLUSH_INTERVAL).toMillis())
                .withMaxRetries(pipelineProps.get(JdbcConnectorOptions.SINK_MAX_RETRIES))
                .build();

        // Oracle JDBC-URL
        String url = pipelineProps.get(JdbcConnectorOptions.URL);
        if (url == null || url.isEmpty()) {
            String host = pipelineProps.get(OraclePipelineDistProperties.HOSTNAME);
            int port = pipelineProps.get(OraclePipelineDistProperties.PORT);
            String database = pipelineProps.get(OraclePipelineDistProperties.DATABASE_NAME);
            url = PREFIX_JDBC_URL + host + ":" + port + "/" + database;
        }

        // JDBC连接选项
        JdbcConnectionOptions connectionOptions = new JdbcConnectionOptions.JdbcConnectionOptionsBuilder()
                .withUrl(url)
                .withDriverName(JDBC_ORACLE_DRIVER)
                .withUsername(pipelineProps.getOptional(JdbcConnectorOptions.USERNAME)
                        .orElseThrow(() -> new IllegalArgumentException("Pipeline Dist [username] not specified.")))
                .withPassword(pipelineProps.getOptional(JdbcConnectorOptions.PASSWORD)
                        .orElseThrow(() -> new IllegalArgumentException("Pipeline Dist [password] not specified.")))
                .withConnectionCheckTimeoutSeconds(pipelineProps.getOptional(JdbcConnectorOptions.MAX_RETRY_TIMEOUT)
                        .map(Duration::toSeconds)
                        .map(Long::intValue)
                        .orElse(-1))
                .build();

        // 构造至少同步一次出口，并返回
        return RdbSyncJdbcSink.sink(
                upsertSql,
                deleteSql,
                new OracleJdbcStatementBuilder(mapping),
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
                                                            Mapping<Oracle> mapping,
                                                            OraclePipelineDistProperties pipelineProps) {
        // JDBC执行选项
        JdbcExecutionOptions executionOptions = JdbcExecutionOptions.builder()
                .withBatchSize(pipelineProps.get(JdbcConnectorOptions.SINK_BUFFER_FLUSH_MAX_ROWS))
                .withBatchIntervalMs(pipelineProps.get(JdbcConnectorOptions.SINK_BUFFER_FLUSH_INTERVAL).toMillis())
                .withMaxRetries(0)  // 参照 FLINK-22311
                .build();

        // JDBC精确同步一次选项
        JdbcExactlyOnceOptions exactlyOnceOptions = JdbcExactlyOnceOptions.builder()
                .withMaxCommitAttempts(pipelineProps.get(OraclePipelineDistProperties.SINK_XA_MAX_COMMIT_ATTEMPTS))
                .withTimeoutSec(pipelineProps.getOptional(OraclePipelineDistProperties.SINK_XA_TIMEOUT)
                        .map(Duration::toSeconds)
                        .map(Long::intValue))
                .withTransactionPerConnection(pipelineProps.get(OraclePipelineDistProperties.SINK_XA_TRANSACTION_PER_CONNECTION))
                .build();

        // 构造OracleXA数据源
        OracleXADataSource ds;
        try {
            ds = new OracleXADataSource();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        String url = pipelineProps.get(JdbcConnectorOptions.URL);
        if (url != null && !url.isEmpty()) {
            ds.setURL(url);
        } else {
            ds.setDriverType(DEFAULT_DRIVER_TYPE);
            ds.setServerName(pipelineProps.get(OraclePipelineDistProperties.HOSTNAME));
            ds.setPortNumber(pipelineProps.get(OraclePipelineDistProperties.PORT));
            ds.setDatabaseName(pipelineProps.get(OraclePipelineDistProperties.DATABASE_NAME));
        }
        ds.setUser(pipelineProps.getOptional(JdbcConnectorOptions.USERNAME)
                .orElseThrow(() -> new IllegalArgumentException("Pipeline Dist [username] not specified.")));
        ds.setPassword(pipelineProps.getOptional(JdbcConnectorOptions.PASSWORD)
                .orElseThrow(() -> new IllegalArgumentException("Pipeline Dist [password] not specified.")));

        // 构造精确同步一次出口，并返回
        return RdbSyncJdbcSink.exactlyOnceSink(
                upsertSql,
                deleteSql,
                new OracleJdbcStatementBuilder(mapping),
                executionOptions,
                exactlyOnceOptions,
                () -> ds
        );
    }

}
