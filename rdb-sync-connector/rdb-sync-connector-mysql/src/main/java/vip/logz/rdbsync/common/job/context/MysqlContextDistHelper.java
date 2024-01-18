package vip.logz.rdbsync.common.job.context;

import org.apache.flink.connector.jdbc.JdbcConnectionOptions;
import org.apache.flink.connector.jdbc.JdbcExecutionOptions;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import vip.logz.rdbsync.connector.jdbc.job.func.RdbSyncJdbcSink;
import vip.logz.rdbsync.common.annotations.Scannable;
import vip.logz.rdbsync.common.job.RdbSyncEvent;
import vip.logz.rdbsync.common.job.func.process.DispatcherProcess;
import vip.logz.rdbsync.common.rule.Binding;
import vip.logz.rdbsync.common.rule.Pipeline;
import vip.logz.rdbsync.common.rule.table.Mapping;
import vip.logz.rdbsync.connector.mysql.config.MysqlPipelineDistProperties;
import vip.logz.rdbsync.connector.mysql.job.func.DebeziumEventToMysqlMap;
import vip.logz.rdbsync.connector.mysql.rule.Mysql;
import vip.logz.rdbsync.connector.mysql.utils.MysqlDeleteSqlGenerator;
import vip.logz.rdbsync.connector.mysql.utils.MysqlJdbcStatementBuilder;
import vip.logz.rdbsync.connector.mysql.utils.MysqlUpsertSqlGenerator;

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

    private static final String JDBC_MYSQL_DRIVER = "com.mysql.cj.jdbc.Driver";

    /**
     * 获取旁路输出上下文映射
     * @param contextMeta 任务上下文元数据
     */
    @Override
    @SuppressWarnings("unchecked")
    public Map<SideOutputTag, SideOutputContext<RdbSyncEvent>> getSideOutContexts(ContextMeta contextMeta) {
        // 1. 提取元数据
        Pipeline<Mysql> pipeline = (Pipeline<Mysql>) contextMeta.getPipeline();
        MysqlPipelineDistProperties pipelineDistProperties =
                (MysqlPipelineDistProperties) contextMeta.getPipelineDistProperties();

        // 2. 构建所有旁路输出上下文
        Map<SideOutputTag, SideOutputContext<RdbSyncEvent>> sideOutputContextMap = new HashMap<>();
        for (Binding<Mysql> binding : pipeline.getBindings()) {
            String distTable = binding.getDistTable();
            // 旁路输出标签
            SideOutputTag sideOutputTag = new SideOutputTag(distTable);
            // 旁路输出上下文
            SideOutputContext<RdbSyncEvent> sideOutputContext = new SideOutputContext<>();
            sideOutputContextMap.put(sideOutputTag, sideOutputContext);

            // JDBC执行选项
            JdbcExecutionOptions executionOptions = JdbcExecutionOptions.builder()
                    .withBatchSize(1)
                    .withBatchIntervalMs(0L)
                    .withMaxRetries(1)
                    .build();
            // JDBC连接选项
            JdbcConnectionOptions options = new JdbcConnectionOptions.JdbcConnectionOptionsBuilder()
                    .withUrl(pipelineDistProperties.getUrl())
                    .withDriverName(JDBC_MYSQL_DRIVER)
                    .withUsername(pipelineDistProperties.getUsername())
                    .withPassword(pipelineDistProperties.getPassword())
                    .build();

            // MySQL模板生成器
            MysqlUpsertSqlGenerator upsertSqlGenerator = new MysqlUpsertSqlGenerator();
            MysqlDeleteSqlGenerator deleteSqlGenerator = new MysqlDeleteSqlGenerator();

            // 旁路输出上下文：初始化Sink
            Mapping<Mysql> mapping = binding.getMapping();
            SinkFunction<RdbSyncEvent> sink = RdbSyncJdbcSink.sink(
                    upsertSqlGenerator.generate(distTable, mapping),
                    deleteSqlGenerator.generate(distTable, mapping),
                    new MysqlJdbcStatementBuilder(mapping),
                    executionOptions,
                    options
            );
            sideOutputContext.setSink(sink);

            // 旁路输出上下文：初始化转换器
            DebeziumEventToMysqlMap transformer = new DebeziumEventToMysqlMap(mapping);
            sideOutputContext.setTransformer(transformer);
        }

        return sideOutputContextMap;
    }

    /**
     * 获取分发器
     * @param contextMeta 任务上下文元数据
     */
    @Override
    public DispatcherProcess getDispatcher(ContextMeta contextMeta) {
        return new DispatcherProcess(contextMeta.getPipeline());
    }

}
