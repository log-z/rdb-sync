package vip.logz.rdbsync.connector.sqlserver.utils;

import vip.logz.rdbsync.common.rule.table.Mapping;
import vip.logz.rdbsync.common.rule.table.MappingField;
import vip.logz.rdbsync.common.utils.StringUtils;
import vip.logz.rdbsync.common.utils.sql.DDLGenerator;
import vip.logz.rdbsync.common.utils.sql.SqlDialectService;
import vip.logz.rdbsync.connector.sqlserver.rule.Sqlserver;

import java.util.ArrayList;
import java.util.List;

/**
 * SQLServer建表语句生成器
 *
 * @author logz
 * @date 2024-01-29
 */
public class SqlserverDDLGenerator implements DDLGenerator<Sqlserver> {

    /** SQLServer方言服务 */
    private final SqlDialectService sqlDialectService = new SqlserverDialectService();

    /**
     * 生成SQLServer建表语句
     * @param table 表名
     * @param mapping 表映射
     * @return 返回SQLServer建表语句
     */
    @Override
    public String generate(String table, Mapping<Sqlserver> mapping) {
        // 1. 表名
        String tableName = sqlDialectService.identifierLiteral(table);
        StringBuilder sb = new StringBuilder(TOKEN_CREATE_TABLE)
                .append(WHITESPACE)
                .append(tableName)
                .append(WHITESPACE)
                .append(TOKEN_BRACKET_BEGIN);
        List<String> primaryKeys = new ArrayList<>();

        // 2. 字段信息
        for (MappingField<Sqlserver> field : mapping.getFields()) {
            String fieldName = sqlDialectService.identifierLiteral(field.getName());
            sb.append(fieldName)
                    .append(WHITESPACE)
                    .append(field.getType());

            if (field.isNonNull()) {
                sb.append(WHITESPACE).append(TOKEN_NOT_NULL);
            }
            if (field.isPrimaryKey()) {
                primaryKeys.add(fieldName);
            }

            sb.append(TOKEN_COMMA);
        }

        // 3. 主键信息
        if (!primaryKeys.isEmpty()) {
            // 3.1. 主键
            sb.append(WHITESPACE)
                    .append(TOKEN_PRIMARY_KEY)
                    .append(WHITESPACE)
                    .append(TOKEN_BRACKET_BEGIN);
            for (String primaryKey : primaryKeys) {
                sb.append(primaryKey).append(TOKEN_COMMA);
            }

            // 去除末尾逗号，然后闭合括号
            StringUtils.removeEnd(sb, TOKEN_COMMA)
                    .append(TOKEN_BRACKET_END)
                    .append(TOKEN_COMMA);
        }

        // 去除末尾逗号，然后闭合括号
        StringUtils.removeEnd(sb, TOKEN_COMMA).append(TOKEN_BRACKET_END);

        return sb.append(TOKEN_TERMINATOR).toString();

        // TODO：注释信息
    }

}
