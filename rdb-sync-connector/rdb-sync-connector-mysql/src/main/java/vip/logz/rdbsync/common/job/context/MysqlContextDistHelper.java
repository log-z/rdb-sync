package vip.logz.rdbsync.common.job.context;

import com.mysql.cj.jdbc.Driver;
import com.mysql.cj.jdbc.MysqlDataSource;
import com.mysql.cj.jdbc.MysqlXADataSource;
import org.apache.flink.connector.jdbc.JdbcConnectionOptions;
import org.apache.flink.connector.jdbc.JdbcExactlyOnceOptions;
import org.apache.flink.connector.jdbc.JdbcExecutionOptions;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
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
import vip.logz.rdbsync.connector.mysql.config.MysqlPipelineDistProperties;
import vip.logz.rdbsync.connector.mysql.rule.Mysql;
import vip.logz.rdbsync.connector.mysql.utils.MysqlDialectService;
import vip.logz.rdbsync.connector.mysql.utils.MysqlJdbcStatementBuilder;
import vip.logz.rdbsync.connector.mysql.utils.MysqlUpsertSqlGenerator;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
     * @param pipelineProperties 管道目标属性
     */
    private SinkFunction<RdbSyncEvent> buildSink(String distTable,
                                                 Mapping<Mysql> mapping,
                                                 MysqlPipelineDistProperties pipelineProperties) {
        // MySQL语句模板
        SqlDialectService sqlDialectService = new MysqlDialectService();
        String upsertSql = new MysqlUpsertSqlGenerator().generate(distTable, mapping);
        String deleteSql = new GenericDeleteSqlGenerator<Mysql>(sqlDialectService).generate(distTable, mapping);

        // 获取容错保证
        String guarantee = Optional.ofNullable(pipelineProperties.getGuarantee())
                .orElse(GuaranteeOptions.AT_LEAST_ONCE)
                .toLowerCase();

        // 构造出口，取决于容错保证
        switch (guarantee) {
            // 精确同步一次
            case GuaranteeOptions.EXACTLY_ONCE:
                return buildExactlyOnceSink(upsertSql, deleteSql, mapping, pipelineProperties);
            // 至少同步一次
            case GuaranteeOptions.AT_LEAST_ONCE:
                return buildSink(upsertSql, deleteSql, mapping, pipelineProperties);
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
    private SinkFunction<RdbSyncEvent> buildSink(String upsertSql,
                                                 String deleteSql,
                                                 Mapping<Mysql> mapping,
                                                 MysqlPipelineDistProperties pipelineProperties) {
        // JDBC执行选项
        JdbcExecutionOptions executionOptions = JdbcExecutionOptions.builder()
                .withBatchSize(pipelineProperties.getExecBatchSize())
                .withBatchIntervalMs(pipelineProperties.getExecBatchIntervalMs())
                .withMaxRetries(pipelineProperties.getExecMaxRetries())
                .build();

        // MySQL数据源，用于生成JDBC-URL
        MysqlDataSource ds = new MysqlDataSource();
        ds.setServerName(pipelineProperties.getHost());
        ds.setPort(pipelineProperties.getPort());
        ds.setDatabaseName(pipelineProperties.getDatabase());

        // JDBC连接选项
        JdbcConnectionOptions connectionOptions = new JdbcConnectionOptions.JdbcConnectionOptionsBuilder()
                .withUrl(ds.getUrl())
                .withDriverName(JDBC_MYSQL_DRIVER)
                .withUsername(pipelineProperties.getUsername())
                .withPassword(pipelineProperties.getPassword())
                .withConnectionCheckTimeoutSeconds(pipelineProperties.getConnTimeoutSeconds())
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
     * @param pipelineProperties 管道目标属性
     */
    private SinkFunction<RdbSyncEvent> buildExactlyOnceSink(String upsertSql,
                                                            String deleteSql,
                                                            Mapping<Mysql> mapping,
                                                            MysqlPipelineDistProperties pipelineProperties) {
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

        // MySQL数据源
        MysqlXADataSource ds = new MysqlXADataSource();
        ds.setServerName(pipelineProperties.getHost());
        ds.setPort(pipelineProperties.getPort());
        ds.setDatabaseName(pipelineProperties.getDatabase());
        ds.setUser(pipelineProperties.getUsername());
        ds.setPassword(pipelineProperties.getPassword());
        try {
            ds.setConnectTimeout(pipelineProperties.getConnTimeoutSeconds() * 1000);
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
