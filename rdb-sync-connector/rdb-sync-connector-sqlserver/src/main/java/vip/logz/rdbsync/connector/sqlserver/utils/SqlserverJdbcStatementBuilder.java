package vip.logz.rdbsync.connector.sqlserver.utils;

import vip.logz.rdbsync.common.rule.table.Mapping;
import vip.logz.rdbsync.common.rule.table.MappingField;
import vip.logz.rdbsync.connector.jdbc.job.RdbSyncJdbcStatementBuilder;
import vip.logz.rdbsync.connector.sqlserver.rule.Sqlserver;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

/**
 * SQLServer语句构造器
 *
 * @author logz
 * @date 2024-01-29
 */
public class SqlserverJdbcStatementBuilder extends RdbSyncJdbcStatementBuilder<Sqlserver> {

    /**
     * 构造器
     * @param mapping 表映射
     */
    public SqlserverJdbcStatementBuilder(Mapping<Sqlserver> mapping) {
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
        int index = PARAMETER_INDEX_OFFSET;
        for (MappingField<Sqlserver> field : mapping.getFields()) {
            Object val = record.get(field.getName());
            ps.setObject(index++, val);
        }
    }

}
