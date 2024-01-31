package vip.logz.rdbsync.common.job.context;

import com.microsoft.sqlserver.jdbc.SQLServerDriver;
import org.apache.flink.connector.jdbc.JdbcConnectionOptions;
import org.apache.flink.connector.jdbc.JdbcExecutionOptions;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import vip.logz.rdbsync.common.annotations.Scannable;
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
        SqlserverPipelineDistProperties pipelineDistProperties =
                (SqlserverPipelineDistProperties) contextMeta.getPipelineDistProperties();

        // 2. 构建所有旁路输出上下文
        Map<SideOutputTag, SideOutputContext<RdbSyncEvent>> sideOutputContextMap = new HashMap<>();
        for (Binding<Sqlserver> binding : pipeline.getBindings()) {
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
                    .withDriverName(JDBC_SQLSERVER_DRIVER)
                    .withUsername(pipelineDistProperties.getUsername())
                    .withPassword(pipelineDistProperties.getPassword())
                    .withConnectionCheckTimeoutSeconds(pipelineDistProperties.getConnTimeoutSeconds())
                    .build();

            // SQLServer模板生成器
            String schema = pipelineDistProperties.getSchema();
            SqlDialectService sqlDialectService = new SqlserverDialectService();
            SqlserverUpsertSqlGenerator upsertSqlGenerator = new SqlserverUpsertSqlGenerator(schema);
            GenericDeleteSqlGenerator<Sqlserver> deleteSqlGenerator = new GenericDeleteSqlGenerator<>(schema, sqlDialectService);

            // 旁路输出上下文：初始化Sink
            Mapping<Sqlserver> mapping = binding.getMapping();
            SinkFunction<RdbSyncEvent> sink = RdbSyncJdbcSink.sink(
                    upsertSqlGenerator.generate(distTable, mapping),
                    deleteSqlGenerator.generate(distTable, mapping),
                    new SqlserverJdbcStatementBuilder(mapping),
                    executionOptions,
                    options
            );
            sideOutputContext.setSink(sink);

            // 旁路输出上下文：初始化转换器
            DebeziumEventToJdbcMap<Sqlserver> transformer = new DebeziumEventToJdbcMap<>(mapping);
            sideOutputContext.setTransformer(transformer);
        }

        return sideOutputContextMap;
    }

}
