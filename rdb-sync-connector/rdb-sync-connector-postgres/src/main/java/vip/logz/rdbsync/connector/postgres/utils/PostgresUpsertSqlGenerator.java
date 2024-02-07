package vip.logz.rdbsync.connector.postgres.utils;

import vip.logz.rdbsync.common.rule.table.Mapping;
import vip.logz.rdbsync.common.rule.table.MappingField;
import vip.logz.rdbsync.common.utils.StringUtils;
import vip.logz.rdbsync.common.utils.sql.SqlDialectService;
import vip.logz.rdbsync.connector.jdbc.utils.DMLGenerator;
import vip.logz.rdbsync.connector.postgres.rule.Postgres;

/**
 * Postgres更新或新增语句生成器
 *
 * @author logz
 * @date 2024-02-06
 */
public class PostgresUpsertSqlGenerator implements DMLGenerator<Postgres> {

    /** 标志：当发生冲突时 */
    private static final String TOKEN_ON_CONFLICT = "ON CONFLICT";

    /** 标志：执行更新值 */
    private static final String TOKEN_DO_UPDATE_SET = "DO UPDATE SET";

    /** Postgres方言服务 */
    private final SqlDialectService sqlDialectService = new PostgresDialectService();

    /** 模式名 */
    private final String schema;

    /**
     * 构造器
     * @param schema 模式名
     */
    public PostgresUpsertSqlGenerator(String schema) {
        this.schema = schema;
    }

    /**
     * 生成Postgres更新或新增语句
     * @param table 表名
     * @param mapping 表映射
     * @return 返回Postgres更新或新增语句
     */
    @Override
    public String generate(String table, Mapping<Postgres> mapping) {
        // 1. 表名
        String schemaName = sqlDialectService.identifierLiteral(schema);
        String tableName = sqlDialectService.identifierLiteral(table);
        StringBuilder sb = new StringBuilder(TOKEN_INSERT_INTO)
                .append(WHITESPACE)
                .append(schemaName)
                .append(TOKEN_REF_DELIMITER)
                .append(tableName)
                .append(WHITESPACE)
                .append(TOKEN_BRACKET_BEGIN);

        // 2. 字段信息
        for (MappingField<Postgres> field : mapping.getFields()) {
            String fieldName = sqlDialectService.identifierLiteral(field.getName());
            sb.append(fieldName).append(TOKEN_COMMA);
        }

        // 去除末尾逗号，然后闭合括号
        StringUtils.removeEnd(sb, TOKEN_COMMA).append(TOKEN_BRACKET_END);

        // 3. 值信息
        sb.append(WHITESPACE)
                .append(TOKEN_VALUES)
                .append(WHITESPACE)
                .append(TOKEN_BRACKET_BEGIN);
        for (MappingField<Postgres> ignored : mapping.getFields()) {
            sb.append(JDBC_PARAMETER).append(TOKEN_COMMA);
        }

        // 去除末尾逗号，然后闭合括号
        StringUtils.removeEnd(sb, TOKEN_COMMA).append(TOKEN_BRACKET_END);

        // 4. 冲突判定条件
        sb.append(WHITESPACE)
                .append(TOKEN_ON_CONFLICT)
                .append(WHITESPACE)
                .append(TOKEN_BRACKET_BEGIN);
        for (MappingField<Postgres> field : mapping.getFields()) {
            // 只检测主键冲突
            if (!field.isPrimaryKey()) {
                continue;
            }

            String fieldName = sqlDialectService.identifierLiteral(field.getName());
            sb.append(fieldName)
                    .append(TOKEN_COMMA);
        }

        // 去除末尾逗号，然后闭合括号
        StringUtils.removeEnd(sb, TOKEN_COMMA).append(TOKEN_BRACKET_END);

        // 5. 冲突处理（更新字段值）
        sb.append(WHITESPACE)
                .append(TOKEN_DO_UPDATE_SET)
                .append(WHITESPACE);
        for (MappingField<Postgres> field : mapping.getFields()) {
            String fieldName = sqlDialectService.identifierLiteral(field.getName());
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
