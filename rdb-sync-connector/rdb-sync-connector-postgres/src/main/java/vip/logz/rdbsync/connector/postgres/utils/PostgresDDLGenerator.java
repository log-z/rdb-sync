package vip.logz.rdbsync.connector.postgres.utils;

import vip.logz.rdbsync.common.rule.table.Mapping;
import vip.logz.rdbsync.common.rule.table.MappingField;
import vip.logz.rdbsync.common.utils.StringUtils;
import vip.logz.rdbsync.common.utils.sql.DDLGenerator;
import vip.logz.rdbsync.common.utils.sql.SqlDialectService;
import vip.logz.rdbsync.connector.postgres.config.PostgresOptions;
import vip.logz.rdbsync.connector.postgres.rule.Postgres;

import java.util.ArrayList;
import java.util.List;

/**
 * Postgres建表语句生成器
 *
 * @author logz
 * @date 2024-02-06
 */
public class PostgresDDLGenerator implements DDLGenerator<Postgres> {

    /** 标志：注释于 */
    public static final String TOKEN_COMMENT_ON = "COMMENT ON";

    /** 标志：表 */
    public static final String TOKEN_TABLE = "TABLE";

    /** 标志：列 */
    public static final String TOKEN_COLUMN = "COLUMN";

    /** 标志：是 */
    public static final String TOKEN_IS = "IS";

    /** Postgres方言服务 */
    private final SqlDialectService sqlDialectService = new PostgresDialectService();

    /** 模式名 */
    private final String schema;

    /**
     * 构造器
     */
    public PostgresDDLGenerator() {
        this(PostgresOptions.DEFAULT_SCHEMA);
    }

    /**
     * 构造器
     * @param schema 模式名
     */
    public PostgresDDLGenerator(String schema) {
        this.schema = schema;
    }

    /**
     * 生成Postgres建表语句
     * @param table 表名
     * @param mapping 表映射
     * @return 返回Postgres建表语句
     */
    @Override
    public String generate(String table, Mapping<Postgres> mapping) {
        // 基本建表语句
        StringBuilder basicSql = generateBasic(table, mapping);
        // 附加注释语句
        StringBuilder descriptionSql = generateComment(table, mapping);

        return basicSql.append(WHITESPACE)
                .append(descriptionSql)
                .toString();
    }

    /**
     * 生成Postgres基本建表语句
     * @param table 表名
     * @param mapping 表映射
     */
    private StringBuilder generateBasic(String table, Mapping<Postgres> mapping) {
        // 1. 表名
        String schemaName = sqlDialectService.identifierLiteral(schema);
        String tableName = sqlDialectService.identifierLiteral(table);
        StringBuilder sb = new StringBuilder(TOKEN_CREATE_TABLE)
                .append(WHITESPACE)
                .append(schemaName)
                .append(TOKEN_REF_DELIMITER)
                .append(tableName)
                .append(WHITESPACE)
                .append(TOKEN_BRACKET_BEGIN);
        List<String> primaryKeys = new ArrayList<>();

        // 2. 字段信息
        for (MappingField<Postgres> field : mapping.getFields()) {
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
            sb.append(TOKEN_PRIMARY_KEY)
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

        // 去除末尾逗号，然后闭合括号，结束DDL语句
        return StringUtils.removeEnd(sb, TOKEN_COMMA)
                .append(TOKEN_BRACKET_END)
                .append(TOKEN_TERMINATOR);
    }

    /**
     * 生成Postgres附加描述语句
     * @param table 表名
     * @param mapping 表映射
     */
    private StringBuilder generateComment(String table, Mapping<Postgres> mapping) {
        StringBuilder sb = new StringBuilder();
        String schemaName = sqlDialectService.identifierLiteral(schema);
        String tableName = sqlDialectService.identifierLiteral(table);

        // 1. 表注释信息
        String tableComment = mapping.getComment();
        if (tableComment != null) {
            sb.append(WHITESPACE)
                    .append(TOKEN_COMMENT_ON)
                    .append(WHITESPACE)
                    .append(TOKEN_TABLE)
                    .append(WHITESPACE)
                    .append(schemaName)
                    .append(TOKEN_REF_DELIMITER)
                    .append(tableName)
                    .append(WHITESPACE)
                    .append(TOKEN_IS)
                    .append(WHITESPACE)
                    .append(sqlDialectService.stringLiteral(tableComment))
                    .append(TOKEN_TERMINATOR);
        }

        // 2. 字段注释信息
        for (MappingField<Postgres> field : mapping.getFields()) {
            String fieldComment = field.getComment();
            if (fieldComment == null) {
                continue;
            }

            String columnName = sqlDialectService.identifierLiteral(field.getName());
            sb.append(WHITESPACE)
                    .append(TOKEN_COMMENT_ON)
                    .append(WHITESPACE)
                    .append(TOKEN_COLUMN)
                    .append(WHITESPACE)
                    .append(schemaName)
                    .append(TOKEN_REF_DELIMITER)
                    .append(tableName)
                    .append(TOKEN_REF_DELIMITER)
                    .append(columnName)
                    .append(WHITESPACE)
                    .append(TOKEN_IS)
                    .append(WHITESPACE)
                    .append(sqlDialectService.stringLiteral(fieldComment))
                    .append(TOKEN_TERMINATOR);
        }

        return sb;
    }

}
