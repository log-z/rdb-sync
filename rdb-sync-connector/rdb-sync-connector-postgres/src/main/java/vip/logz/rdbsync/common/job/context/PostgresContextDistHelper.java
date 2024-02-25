package vip.logz.rdbsync.common.job.context;

import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.connector.jdbc.JdbcConnectionOptions;
import org.apache.flink.connector.jdbc.JdbcExactlyOnceOptions;
import org.apache.flink.connector.jdbc.JdbcExecutionOptions;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.postgresql.Driver;
import org.postgresql.ds.PGSimpleDataSource;
import org.postgresql.xa.PGXADataSource;
import vip.logz.rdbsync.common.annotations.Scannable;
import vip.logz.rdbsync.common.config.GuaranteeOptions;
import vip.logz.rdbsync.common.exception.UnsupportedDistGuaranteeException;
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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
        String schema = pipelineProperties.getSchema();

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
     * @param pipelineProperties 管道目标属性
     */
    private SinkFunction<RdbSyncEvent> buildSink(String schema,
                                                 String distTable,
                                                 Mapping<Postgres> mapping,
                                                 PostgresPipelineDistProperties pipelineProperties) {
        // Postgres语句模板
        SqlDialectService sqlDialectService = new PostgresDialectService();
        String upsertSql = new PostgresUpsertSqlGenerator(schema).generate(distTable, mapping);
        String deleteSql = new GenericDeleteSqlGenerator<Postgres>(sqlDialectService).generate(distTable, mapping);

        // 获取容错保证
        String guarantee = Optional.ofNullable(pipelineProperties.getGuarantee())
                .orElse(GuaranteeOptions.AT_LEAST_ONCE)
                .toLowerCase();

        // 构造出口，取决于容错保证
        switch (guarantee) {
            // 至少同步一次
            case GuaranteeOptions.AT_LEAST_ONCE:
                return buildAtLeastOnceSink(upsertSql, deleteSql, mapping, pipelineProperties);
            // 精确同步一次
            case GuaranteeOptions.EXACTLY_ONCE:
                return buildExactlyOnceSink(upsertSql, deleteSql, mapping, pipelineProperties);
            default:
                throw new UnsupportedDistGuaranteeException(guarantee);
        }
    }

    /**
     * 构造至少同步一次出口
     * @param upsertSql 更新或插入语句
     * @param deleteSql 删除语句
     * @param mapping 表映射
     * @param pipelineProperties 管道目标属性
     */
    private SinkFunction<RdbSyncEvent> buildAtLeastOnceSink(String upsertSql,
                                                            String deleteSql,
                                                            Mapping<Postgres> mapping,
                                                            PostgresPipelineDistProperties pipelineProperties) {
        // JDBC执行选项
        JdbcExecutionOptions executionOptions = JdbcExecutionOptions.builder()
                .withBatchSize(pipelineProperties.getExecBatchSize())
                .withBatchIntervalMs(pipelineProperties.getExecBatchIntervalMs())
                .withMaxRetries(pipelineProperties.getExecMaxRetries())
                .build();

        // 解析多主机与端口
        Tuple2<String[], int[]> hostsAndPorts = parseHostsAndPorts(pipelineProperties);

        // MySQL数据源，用于生成JDBC-URL
        PGSimpleDataSource ds = new PGSimpleDataSource();
        ds.setServerNames(hostsAndPorts.getField(0));
        ds.setPortNumbers(hostsAndPorts.getField(1));
        ds.setDatabaseName(pipelineProperties.getDatabase());
        ds.setStringType(CONN_PROPERTY_STRING_TYPE);

        // JDBC连接选项
        JdbcConnectionOptions connectionOptions = new JdbcConnectionOptions.JdbcConnectionOptionsBuilder()
                .withUrl(ds.getUrl())
                .withDriverName(JDBC_POSTGRES_DRIVER)
                .withUsername(pipelineProperties.getUsername())
                .withPassword(pipelineProperties.getPassword())
                .withConnectionCheckTimeoutSeconds(pipelineProperties.getConnTimeoutSeconds())
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
     * @param pipelineProperties 管道目标属性
     */
    private SinkFunction<RdbSyncEvent> buildExactlyOnceSink(String upsertSql,
                                                            String deleteSql,
                                                            Mapping<Postgres> mapping,
                                                            PostgresPipelineDistProperties pipelineProperties) {
        // JDBC执行选项
        JdbcExecutionOptions executionOptions = JdbcExecutionOptions.builder()
                .withBatchSize(pipelineProperties.getExecBatchSize())
                .withBatchIntervalMs(pipelineProperties.getExecBatchIntervalMs())
                .withMaxRetries(0)  // 参照 FLINK-22311
                .build();

        // JDBC精确同步一次选项
        JdbcExactlyOnceOptions exactlyOnceOptions = JdbcExactlyOnceOptions.builder()
                .withMaxCommitAttempts(pipelineProperties.getTxMaxCommitAttempts())
                .withTimeoutSec(Optional.ofNullable(pipelineProperties.getTxTimeoutSeconds()))
                .withTransactionPerConnection(true)
                .build();

        // 解析多主机与端口
        Tuple2<String[], int[]> hostsAndPorts = parseHostsAndPorts(pipelineProperties);

        // MySQL数据源
        PGXADataSource ds = new SerializablePGXADataSource();
        ds.setServerNames(hostsAndPorts.getField(0));
        ds.setPortNumbers(hostsAndPorts.getField(1));
        ds.setDatabaseName(pipelineProperties.getDatabase());
        ds.setUser(pipelineProperties.getUsername());
        ds.setPassword(pipelineProperties.getPassword());
        ds.setConnectTimeout(pipelineProperties.getConnTimeoutSeconds());
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

    /** 多个值的分隔符 */
    private static final String MULTI_VALUES_DELIMITER = ",";

    /**
     * 解析多主机与端口
     * @param pipelineProperties Postgres管道目标属性
     */
    private static Tuple2<String[], int[]> parseHostsAndPorts(PostgresPipelineDistProperties pipelineProperties) {
        String hosts = pipelineProperties.getHosts();
        String ports = pipelineProperties.getPorts();

        // 解析主机列表
        String[] hostList = (hosts == null || hosts.isEmpty()) ?
                new String[] {PostgresOptions.DEFAULT_HOST} :
                hosts.split(MULTI_VALUES_DELIMITER);

        // 解析端口列表
        String[] portList = (ports == null || ports.isEmpty()) ?
                new String[0] :
                ports.split(MULTI_VALUES_DELIMITER);

        // 对齐端口列表
        int[] portNumbers = new int[hostList.length];
        for (int i = 0; i < hostList.length; i++) {
            hostList[i] = hostList[i].strip();

            if (i >= portList.length) {
                portNumbers[i] = PostgresOptions.DEFAULT_PORT;
            } else {
                try {
                    portNumbers[i] = Integer.parseInt(portList[i].strip());
                } catch (NumberFormatException e) {
                    throw new RuntimeException("解析 Postgres 管道目标端口列表失败，格式不正确", e);
                }
            }
        }

        return Tuple2.of(hostList, portNumbers);
    }

}
