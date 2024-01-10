package vip.logz.rdbsync.common.job.context;

import org.apache.flink.connector.jdbc.JdbcConnectionOptions;
import org.apache.flink.connector.jdbc.JdbcSink;
import org.apache.flink.connector.jdbc.JdbcStatementBuilder;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import vip.logz.rdbsync.common.annotations.Scannable;
import vip.logz.rdbsync.common.enums.SideOutputOp;
import vip.logz.rdbsync.common.job.func.process.DispatcherProcess;
import vip.logz.rdbsync.common.rule.Binding;
import vip.logz.rdbsync.common.rule.Channel;
import vip.logz.rdbsync.common.rule.table.Mapping;
import vip.logz.rdbsync.common.rule.table.MappingField;
import vip.logz.rdbsync.connector.mysql.config.MysqlConnectDistProperties;
import vip.logz.rdbsync.connector.mysql.job.func.DebeziumEventToMysqlMap;
import vip.logz.rdbsync.connector.mysql.rule.Mysql;
import vip.logz.rdbsync.connector.mysql.utils.MysqlDeleteSqlGenerator;
import vip.logz.rdbsync.connector.mysql.utils.MysqlUpsertSqlGenerator;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Mysql任务上下文目标辅助
 *
 * @author logz
 * @date 2024-01-09
 */
@Scannable
public class MysqlContextDistHelper implements ContextDistHelper<Mysql, Map<String, Object>> {

    /**
     * 获取旁路输出上下文映射
     * @param contextMeta 任务上下文元数据
     */
    @Override
    @SuppressWarnings("unchecked")
    public Map<SideOutputTag, SideOutputContext<Map<String, Object>>> getSideOutContexts(ContextMeta contextMeta) {
        // 1. 提取元数据
        Channel<Mysql> channel = (Channel<Mysql>) contextMeta.getChannel();
        MysqlConnectDistProperties connectDistProperties =
                (MysqlConnectDistProperties) contextMeta.getConnectDistProperties();

        // 2. 构建所有旁路输出上下文
        Map<SideOutputTag, SideOutputContext<Map<String, Object>>> sideOutputContextMap = new HashMap<>();
        for (Binding<Mysql> binding : channel.getBindings()) {
            String distTable = binding.getDistTable();
            // 旁路输出标签
            SideOutputTag upsertOutputTag = new SideOutputTag(distTable, SideOutputOp.UPSERT);
            SideOutputTag deleteOutputTag = new SideOutputTag(distTable, SideOutputOp.DELETE);
            // 旁路输出上下文
            SideOutputContext<Map<String, Object>> upsertSideOutputContext = new SideOutputContext<>();
            SideOutputContext<Map<String, Object>> deleteSideOutputContext = new SideOutputContext<>();
            sideOutputContextMap.put(upsertOutputTag, upsertSideOutputContext);
            sideOutputContextMap.put(deleteOutputTag, deleteSideOutputContext);

            // JDBC连接选项
            JdbcConnectionOptions options = new JdbcConnectionOptions.JdbcConnectionOptionsBuilder()
                    .withUrl(connectDistProperties.getUrl())
                    // .withDriverName()
                    .withUsername(connectDistProperties.getUsername())
                    .withPassword(connectDistProperties.getPassword())
                    .build();

            // MySQL模板生成器
            MysqlUpsertSqlGenerator upsertSqlGenerator = new MysqlUpsertSqlGenerator();
            MysqlDeleteSqlGenerator deleteSqlGenerator = new MysqlDeleteSqlGenerator();

            // 旁路输出上下文：初始化Sink
            Mapping<Mysql> mapping = binding.getMapping();
            SinkFunction<Map<String, Object>> upsertSink = JdbcSink.sink(
                    upsertSqlGenerator.generate(distTable, mapping),
                    new MysqlJdbcStatementBuilder(mapping, SideOutputOp.UPSERT),
                    options
            );
            SinkFunction<Map<String, Object>> deleteSink = JdbcSink.sink(
                    deleteSqlGenerator.generate(distTable, mapping),
                    new MysqlJdbcStatementBuilder(mapping, SideOutputOp.DELETE),
                    options
            );
            upsertSideOutputContext.setSink(upsertSink);
            deleteSideOutputContext.setSink(deleteSink);

            // 旁路输出上下文：初始化转换器
            DebeziumEventToMysqlMap transformer = new DebeziumEventToMysqlMap(mapping);
            upsertSideOutputContext.setTransformer(transformer);
            deleteSideOutputContext.setTransformer(transformer);
        }

        return sideOutputContextMap;
    }

    /**
     * MySQL语句构造器
     */
    private static class MysqlJdbcStatementBuilder implements JdbcStatementBuilder<Map<String, Object>> {
        private final Mapping<Mysql> mapping;
        private final SideOutputOp op;

        public MysqlJdbcStatementBuilder(Mapping<Mysql> mapping, SideOutputOp op) {
            this.mapping = mapping;
            this.op = op;
        }

        @Override
        public void accept(PreparedStatement ps, Map<String, Object> record) throws SQLException {
            boolean isDelete = op == SideOutputOp.DELETE;
            List<MappingField<Mysql>> fields = mapping.getFields();

            // 1. 删除或新增操作
            int index;
            int offset = 1;
            for (index = 0; index < fields.size(); index++) {
                MappingField<Mysql> field = fields.get(index);
                // 删除操作：仅填充主键值作为条件
                if (isDelete && !field.isPrimaryKey()) {
                    continue;
                }

                Object val = record.get(field.getName());
                ps.setObject(index + offset, val);
            }

            if (isDelete) {
                return;
            }

            // 2. 更新操作：键冲突处理（更新字段值）
            offset += index;
            for (index = 0; index < fields.size(); index++) {
                MappingField<Mysql> field = fields.get(index);
                Object val = record.get(field.getName());
                ps.setObject(index + offset, val);
            }
        }

    }

    /**
     * 获取分发器
     * @param contextMeta 任务上下文元数据
     */
    @Override
    public DispatcherProcess getDispatcher(ContextMeta contextMeta) {
        return new DispatcherProcess(contextMeta.getChannel(), true);
    }

}
