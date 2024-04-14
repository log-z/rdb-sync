package vip.logz.rdbsync.connector.oracle.utils;

import vip.logz.rdbsync.common.rule.table.Mapping;
import vip.logz.rdbsync.common.utils.StringUtils;
import vip.logz.rdbsync.connector.jdbc.utils.GenericDeleteSqlGenerator;
import vip.logz.rdbsync.connector.oracle.rule.Oracle;

/**
 * Oracle删除语句生成器
 *
 * @author logz
 * @date 2024-03-29
 */
public class OracleDeleteSqlGenerator extends GenericDeleteSqlGenerator<Oracle> {

    /**
     * 构造器
     * @param schema 模式名
     */
    public OracleDeleteSqlGenerator(String schema) {
        super(schema, new OracleDialectService());
    }

    /**
     * 生成Oracle删除语句
     * @param table 表名
     * @param mapping 表映射
     * @return 返回Oracle删除语句
     */
    @Override
    public String generate(String table, Mapping<Oracle> mapping) {
        String sql = super.generate(table, mapping);
        return StringUtils.removeEnd(sql, TOKEN_TERMINATOR);
    }

}
