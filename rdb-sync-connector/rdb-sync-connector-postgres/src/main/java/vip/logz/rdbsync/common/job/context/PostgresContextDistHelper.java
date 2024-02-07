package vip.logz.rdbsync.common.job.context;

import org.apache.flink.connector.jdbc.JdbcConnectionOptions;
import org.apache.flink.connector.jdbc.JdbcExecutionOptions;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.postgresql.Driver;
import vip.logz.rdbsync.common.annotations.Scannable;
import vip.logz.rdbsync.common.job.RdbSyncEvent;
import vip.logz.rdbsync.common.rule.Binding;
import vip.logz.rdbsync.common.rule.Pipeline;
import vip.logz.rdbsync.common.rule.table.Mapping;
import vip.logz.rdbsync.common.utils.sql.SqlDialectService;
import vip.logz.rdbsync.connector.jdbc.job.func.DebeziumEventToJdbcMap;
import vip.logz.rdbsync.connector.jdbc.job.func.RdbSyncJdbcSink;
import vip.logz.rdbsync.connector.jdbc.utils.GenericDeleteSqlGenerator;
import vip.logz.rdbsync.connector.postgres.config.PostgresPipelineDistProperties;
import vip.logz.rdbsync.connector.postgres.rule.Postgres;
import vip.logz.rdbsync.connector.postgres.utils.PostgresDialectService;
import vip.logz.rdbsync.connector.postgres.utils.PostgresJdbcStatementBuilder;
import vip.logz.rdbsync.connector.postgres.utils.PostgresUpsertSqlGenerator;

import java.util.HashMap;
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

    /**
     * 获取旁路输出上下文映射
     * @param contextMeta 任务上下文元数据
     */
    @Override
    @SuppressWarnings("unchecked")
    public Map<SideOutputTag, SideOutputContext<RdbSyncEvent>> getSideOutContexts(ContextMeta contextMeta) {
        // 1. 提取元数据
        Pipeline<Postgres> pipeline = (Pipeline<Postgres>) contextMeta.getPipeline();
        PostgresPipelineDistProperties pipelineDistProperties =
                (PostgresPipelineDistProperties) contextMeta.getPipelineDistProperties();

        // 2. 构建所有旁路输出上下文
        Map<SideOutputTag, SideOutputContext<RdbSyncEvent>> sideOutputContextMap = new HashMap<>();
        for (Binding<Postgres> binding : pipeline.getBindings()) {
            String distTable = binding.getDistTable();
            // 旁路输出标签
            SideOutputTag sideOutputTag = new SideOutputTag(distTable);
            // 旁路输出上下文
            SideOutputContext<RdbSyncEvent> sideOutputContext = new SideOutputContext<>();
            sideOutputContextMap.put(sideOutputTag, sideOutputContext);

            // JDBC执行选项
            JdbcExecutionOptions executionOptions = JdbcExecutionOptions.builder()
                    .withBatchSize(pipelineDistProperties.getExecBatchSize())
                    .withBatchIntervalMs(pipelineDistProperties.getExecBatchIntervalMs())
                    .withMaxRetries(pipelineDistProperties.getExecMaxRetries())
                    .build();
            // JDBC连接选项
            JdbcConnectionOptions options = new JdbcConnectionOptions.JdbcConnectionOptionsBuilder()
                    .withUrl(pipelineDistProperties.getJdbcUrl())
                    .withDriverName(JDBC_POSTGRES_DRIVER)
                    .withUsername(pipelineDistProperties.getUsername())
                    .withPassword(pipelineDistProperties.getPassword())
                    .withConnectionCheckTimeoutSeconds(pipelineDistProperties.getConnTimeoutSeconds())
                    .build();

            // Postgres模板生成器
            String schema = pipelineDistProperties.getSchema();
            SqlDialectService sqlDialectService = new PostgresDialectService();
            PostgresUpsertSqlGenerator upsertSqlGenerator = new PostgresUpsertSqlGenerator(schema);
            GenericDeleteSqlGenerator<Postgres> deleteSqlGenerator = new GenericDeleteSqlGenerator<>(schema, sqlDialectService);

            // 旁路输出上下文：初始化Sink
            Mapping<Postgres> mapping = binding.getMapping();
            SinkFunction<RdbSyncEvent> sink = RdbSyncJdbcSink.sink(
                    upsertSqlGenerator.generate(distTable, mapping),
                    deleteSqlGenerator.generate(distTable, mapping),
                    new PostgresJdbcStatementBuilder(mapping),
                    executionOptions,
                    options
            );
            sideOutputContext.setSink(sink);

            // 旁路输出上下文：初始化转换器
            DebeziumEventToJdbcMap<Postgres> transformer = new DebeziumEventToJdbcMap<>(mapping);
            sideOutputContext.setTransformer(transformer);
        }

        return sideOutputContextMap;
    }

}
