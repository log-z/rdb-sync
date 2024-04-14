package vip.logz.rdbsync.connector.oracle.utils;

import vip.logz.rdbsync.common.rule.table.Mapping;
import vip.logz.rdbsync.common.rule.table.MappingField;
import vip.logz.rdbsync.connector.jdbc.job.RdbSyncJdbcStatementBuilder;
import vip.logz.rdbsync.connector.oracle.rule.Oracle;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

/**
 * Oracle语句构造器
 *
 * @author logz
 * @date 2024-03-19
 */
public class OracleJdbcStatementBuilder extends RdbSyncJdbcStatementBuilder<Oracle> {

    /**
     * 构造器
     * @param mapping 表映射
     */
    public OracleJdbcStatementBuilder(Mapping<Oracle> mapping) {
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
        for (MappingField<Oracle> field : mapping.getFields()) {
            Object val = record.get(field.getName());
            ps.setObject(index++, val);
        }
    }

}
