package vip.logz.rdbsync.connector.mysql.utils;

import vip.logz.rdbsync.common.rule.table.Mapping;
import vip.logz.rdbsync.common.rule.table.MappingField;
import vip.logz.rdbsync.common.utils.sql.DMLGenerator;
import vip.logz.rdbsync.common.utils.sql.SqlUtils;
import vip.logz.rdbsync.connector.mysql.rule.Mysql;

/**
 * MySQL删除语句生成器
 *
 * @author logz
 * @date 2024-01-10
 */
public class MysqlDeleteSqlGenerator implements DMLGenerator<Mysql> {

    /**
     * 生成MySQL删除语句
     * @param table 表名
     * @param mapping 表映射
     * @return 返回MySQL删除语句
     */
    @Override
    public String generate(String table, Mapping<Mysql> mapping) {
        // 1. 表名
        String tableName = SqlUtils.identifierLiteral(table);
        StringBuilder sb = new StringBuilder(TOKEN_DELETE_FROM)
                .append(WHITESPACE)
                .append(tableName)
                .append(WHITESPACE)
                .append(TOKEN_WHERE)
                .append(WHITESPACE);

        // 2. 条件
        for (MappingField<Mysql> field : mapping.getFields()) {
            // 忽略非主键字段
            if (!field.isPrimaryKey()) {
                continue;
            }

            String fieldName = SqlUtils.identifierLiteral(field.getName());
            sb.append(fieldName)
                    .append(TOKEN_EQUAL)
                    .append(JDBC_PARAMETER)
                    .append(TOKEN_COMMA);
        }

        // 去除末尾逗号，然后结束
        return SqlUtils.deleteTail(sb, TOKEN_COMMA)
                .append(TOKEN_TERMINATOR)
                .toString();
    }

}
