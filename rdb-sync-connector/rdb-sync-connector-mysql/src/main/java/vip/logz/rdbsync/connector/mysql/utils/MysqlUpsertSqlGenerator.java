package vip.logz.rdbsync.connector.mysql.utils;

import vip.logz.rdbsync.common.rule.table.Mapping;
import vip.logz.rdbsync.common.rule.table.MappingField;
import vip.logz.rdbsync.common.utils.StringUtils;
import vip.logz.rdbsync.common.utils.sql.DMLGenerator;
import vip.logz.rdbsync.common.utils.sql.SqlUtils;
import vip.logz.rdbsync.connector.mysql.rule.Mysql;

/**
 * MySQL更新或新增语句生成器
 *
 * @author logz
 * @date 2024-01-10
 */
public class MysqlUpsertSqlGenerator implements DMLGenerator<Mysql> {

    /** 标志：当键重复时 */
    private static final String TOKEN_ON_DUPLICATE_KEY = "ON DUPLICATE KEY";

    /**
     * 生成MySQL更新或新增语句
     * @param table 表名
     * @param mapping 表映射
     * @return 返回MySQL更新或新增语句
     */
    @Override
    public String generate(String table, Mapping<Mysql> mapping) {
        // 1. 表名
        String tableName = SqlUtils.identifierLiteral(table);
        StringBuilder sb = new StringBuilder(TOKEN_INSERT_INTO)
                .append(WHITESPACE)
                .append(tableName)
                .append(WHITESPACE)
                .append(TOKEN_BRACKET_BEGIN);

        // 2. 字段信息
        for (MappingField<Mysql> field : mapping.getFields()) {
            String fieldName = SqlUtils.identifierLiteral(field.getName());
            sb.append(fieldName).append(TOKEN_COMMA);
        }

        // 去除末尾逗号，然后闭合括号
        StringUtils.removeEnd(sb, TOKEN_COMMA).append(TOKEN_BRACKET_END);

        // 3. 值信息
        sb.append(WHITESPACE)
                .append(TOKEN_VALUES)
                .append(WHITESPACE)
                .append(TOKEN_BRACKET_BEGIN);
        for (MappingField<Mysql> ignored : mapping.getFields()) {
            sb.append(JDBC_PARAMETER).append(TOKEN_COMMA);
        }

        // 去除末尾逗号，然后闭合括号
        StringUtils.removeEnd(sb, TOKEN_COMMA).append(TOKEN_BRACKET_END);

        // 4. 键冲突处理（更新字段值）
        sb.append(WHITESPACE)
                .append(TOKEN_ON_DUPLICATE_KEY)
                .append(WHITESPACE)
                .append(TOKEN_UPDATE)
                .append(WHITESPACE);
        for (MappingField<Mysql> field : mapping.getFields()) {
            String fieldName = SqlUtils.identifierLiteral(field.getName());
            sb.append(fieldName)
                    .append(TOKEN_EQUAL)
                    .append(JDBC_PARAMETER)
                    .append(TOKEN_COMMA);
        }

        // 去除末尾逗号，然后结束
        return StringUtils.removeEnd(sb, TOKEN_COMMA)
                .append(TOKEN_TERMINATOR)
                .toString();
    }

}
