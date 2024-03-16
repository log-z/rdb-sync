package vip.logz.rdbsync.common.job.context;

import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.connector.jdbc.JdbcConnectionOptions;
import org.apache.flink.connector.jdbc.JdbcExactlyOnceOptions;
import org.apache.flink.connector.jdbc.JdbcExecutionOptions;
import org.apache.flink.connector.jdbc.table.JdbcConnectorOptions;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.postgresql.Driver;
import org.postgresql.ds.PGSimpleDataSource;
import org.postgresql.xa.PGXADataSource;
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
import vip.logz.rdbsync.connector.postgres.config.PostgresOptions;
import vip.logz.rdbsync.connector.postgres.config.PostgresPipelineDistProperties;
import vip.logz.rdbsync.connector.postgres.rule.Postgres;
import vip.logz.rdbsync.connector.postgres.utils.PostgresDialectService;
import vip.logz.rdbsync.connector.postgres.utils.PostgresJdbcStatementBuilder;
import vip.logz.rdbsync.connector.postgres.utils.PostgresUpsertSqlGenerator;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Postgres任务上下文目标辅助
 *
 * @author logz
 * @date 2024-02-05
 */
@Scannable
public class PostgresContextDistHelper implements ContextDistHelper<Postgres, RdbSyncEvent> {

    private static final String JDBC_POSTGRES_DRIVER = Driver.class.getName();

    /** 连接属性：字符串类型的值自动推断其具体类型 */
    private static final String CONN_PROPERTY_STRING_TYPE = "unspecified";

    /**
     * 获取旁路输出上下文映射
     * @param contextMeta 任务上下文元数据
     */
    @Override
    @SuppressWarnings("unchecked")
    public Map<SideOutputTag, SideOutputContext<RdbSyncEvent>> getSideOutContexts(ContextMeta contextMeta) {
        // 1. 提取元数据
        Pipeline<Postgres> pipeline = (Pipeline<Postgres>) contextMeta.getPipeline();
        PostgresPipelineDistProperties pipelineProperties =
                (PostgresPipelineDistProperties) contextMeta.getPipelineDistProperties();
        String schema = pipelineProperties.get(PostgresPipelineDistProperties.SCHEMA_NAME);

        // 2. 构建所有旁路输出上下文
        // 旁路输出关联  [旁路输出标签 -> 旁路输出上下文]
        Map<SideOutputTag, SideOutputContext<RdbSyncEvent>> sideOutputContextMap = new HashMap<>();
        for (Binding<Postgres> binding : pipeline.getBindings()) {
            String distTable = binding.getDistTable();
            Mapping<Postgres> mapping = binding.getMapping();

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
                                                 Mapping<Postgres> mapping,
                                                 PostgresPipelineDistProperties pipelineProps) {
        // Postgres语句模板
        SqlDialectService sqlDialectService = new PostgresDialectService();
        String upsertSql = new PostgresUpsertSqlGenerator(schema).generate(distTable, mapping);
        String deleteSql = new GenericDeleteSqlGenerator<Postgres>(sqlDialectService).generate(distTable, mapping);

        // 获取语义保证
        String semantic = pipelineProps.get(PostgresPipelineDistProperties.SINK_SEMANTIC).toLowerCase();

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
                                                            Mapping<Postgres> mapping,
                                                            PostgresPipelineDistProperties pipelineProps) {
        // JDBC执行选项
        JdbcExecutionOptions executionOptions = JdbcExecutionOptions.builder()
                .withBatchSize(pipelineProps.get(JdbcConnectorOptions.SINK_BUFFER_FLUSH_MAX_ROWS))
                .withBatchIntervalMs(pipelineProps.get(JdbcConnectorOptions.SINK_BUFFER_FLUSH_INTERVAL).toMillis())
                .withMaxRetries(pipelineProps.get(JdbcConnectorOptions.SINK_MAX_RETRIES))
                .build();

        // 解析多主机与端口
        Tuple2<String[], int[]> hostsAndPorts = parseHostsAndPorts(pipelineProps);

        // Postgres数据源，用于生成JDBC-URL
        PGSimpleDataSource ds = new PGSimpleDataSource();
        ds.setServerNames(hostsAndPorts.getField(0));
        ds.setPortNumbers(hostsAndPorts.getField(1));
        ds.setDatabaseName(pipelineProps.getOptional(PostgresPipelineDistProperties.DATABASE_NAME)
                .orElseThrow(() -> new IllegalArgumentException("Pipeline Dist [database-name] not specified.")));
        ds.setStringType(CONN_PROPERTY_STRING_TYPE);

        // JDBC连接选项
        JdbcConnectionOptions connectionOptions = new JdbcConnectionOptions.JdbcConnectionOptionsBuilder()
                .withUrl(ds.getUrl())
                .withDriverName(JDBC_POSTGRES_DRIVER)
                .withUsername(pipelineProps.get(PostgresPipelineDistProperties.USERNAME))
                .withPassword(pipelineProps.get(PostgresPipelineDistProperties.PASSWORD))
                .withConnectionCheckTimeoutSeconds(pipelineProps.getOptional(JdbcConnectorOptions.MAX_RETRY_TIMEOUT)
                        .map(Duration::toSeconds)
                        .map(Long::intValue)
                        .orElse(-1))
                .build();

        // 构造至少同步一次出口，并返回
        return RdbSyncJdbcSink.sink(
                upsertSql,
                deleteSql,
                new PostgresJdbcStatementBuilder(mapping),
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
                                                            Mapping<Postgres> mapping,
                                                            PostgresPipelineDistProperties pipelineProps) {
        // JDBC执行选项
        JdbcExecutionOptions executionOptions = JdbcExecutionOptions.builder()
                .withBatchSize(pipelineProps.get(JdbcConnectorOptions.SINK_BUFFER_FLUSH_MAX_ROWS))
                .withBatchIntervalMs(pipelineProps.get(JdbcConnectorOptions.SINK_BUFFER_FLUSH_INTERVAL).toMillis())
                .withMaxRetries(0)  // 参照 FLINK-22311
                .build();

        // JDBC精确同步一次选项
        JdbcExactlyOnceOptions exactlyOnceOptions = JdbcExactlyOnceOptions.builder()
                .withMaxCommitAttempts(pipelineProps.get(PostgresPipelineDistProperties.SINK_XA_MAX_COMMIT_ATTEMPTS))
                .withTimeoutSec(pipelineProps.getOptional(PostgresPipelineDistProperties.SINK_XA_TIMEOUT)
                        .map(Duration::toSeconds)
                        .map(Long::intValue))
                .withTransactionPerConnection(true)
                .build();

        // 解析多主机与端口
        Tuple2<String[], int[]> hostsAndPorts = parseHostsAndPorts(pipelineProps);

        // Postgres数据源
        PGXADataSource ds = new SerializablePGXADataSource();
        ds.setServerNames(hostsAndPorts.getField(0));
        ds.setPortNumbers(hostsAndPorts.getField(1));
        ds.setDatabaseName(pipelineProps.getOptional(PostgresPipelineDistProperties.DATABASE_NAME)
                .orElseThrow(() -> new IllegalArgumentException("Pipeline Dist [database-name] not specified.")));
        ds.setUser(pipelineProps.get(PostgresPipelineDistProperties.USERNAME));
        ds.setPassword(pipelineProps.get(PostgresPipelineDistProperties.PASSWORD));
        ds.setConnectTimeout(pipelineProps.getOptional(JdbcConnectorOptions.MAX_RETRY_TIMEOUT)
                .map(Duration::toMillis)
                .map(Long::intValue)
                .orElse(-1));
        ds.setStringType(CONN_PROPERTY_STRING_TYPE);

        // 构造精确同步一次出口，并返回
        return RdbSyncJdbcSink.exactlyOnceSink(
                upsertSql,
                deleteSql,
                new PostgresJdbcStatementBuilder(mapping),
                executionOptions,
                exactlyOnceOptions,
                () -> ds
        );
    }

    /**
     * 解析多主机与端口
     * @param pipelineProps Postgres管道目标属性
     */
    private static Tuple2<String[], int[]> parseHostsAndPorts(PostgresPipelineDistProperties pipelineProps) {
        // 解析主机和列表
        List<String> hostList = pipelineProps.get(PostgresPipelineDistProperties.HOSTNAMES);
        List<Integer> portList = pipelineProps.get(PostgresPipelineDistProperties.PORTS);
        String[] hosts = (hostList.isEmpty() ?
                PostgresPipelineDistProperties.HOSTNAMES.defaultValue() :
                hostList
        ).toArray(String[]::new);

        // 对齐端口列表
        int[] ports = new int[hosts.length];
        for (int i = 0; i < hosts.length; i++) {
            hosts[i] = hosts[i].strip();
            ports[i] = (i >= portList.size()) ?
                    PostgresOptions.DEFAULT_PORT :
                    portList.get(i);
        }

        return Tuple2.of(hosts, ports);
    }

}
