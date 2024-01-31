package vip.logz.rdbsync.connector.mysql.utils;

import vip.logz.rdbsync.common.rule.table.Mapping;
import vip.logz.rdbsync.common.rule.table.MappingField;
import vip.logz.rdbsync.connector.jdbc.job.RdbSyncJdbcStatementBuilder;
import vip.logz.rdbsync.connector.mysql.rule.Mysql;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * MySQL语句构造器
 *
 * @author logz
 * @date 2024-01-18
 */
public class MysqlJdbcStatementBuilder extends RdbSyncJdbcStatementBuilder<Mysql> {

    /**
     * 构造器
     * @param mapping 表映射
     */
    public MysqlJdbcStatementBuilder(Mapping<Mysql> mapping) {
        super(mapping);
    }

    /**
     * 填充更新或插入语句的参数
     * @param ps 预编译的语句
     * @param record 数据同步事件
     * @throws SQLException 当设置出错时抛出此异常
     */
    @Override
    protected void fillingUpsert(PreparedStatement ps, Map<String, Object> record) throws SQLException {
        List<MappingField<Mysql>> fields = mapping.getFields();

        // 1. 新增操作
        int index = PARAMETER_INDEX_OFFSET;
        for (MappingField<Mysql> field : fields) {
            Object val = record.get(field.getName());
            ps.setObject(index++, val);
        }

        // 2. 更新操作：键冲突处理（更新字段值）
        for (MappingField<Mysql> field : fields) {
            Object val = record.get(field.getName());
            ps.setObject(index++, val);
        }
    }

}
