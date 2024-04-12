package vip.logz.rdbsync.connector.oracle.utils;

import vip.logz.rdbsync.common.rule.table.Mapping;
import vip.logz.rdbsync.common.rule.table.MappingField;
import vip.logz.rdbsync.common.utils.StringUtils;
import vip.logz.rdbsync.common.utils.sql.DDLGenerator;
import vip.logz.rdbsync.common.utils.sql.SqlDialectService;
import vip.logz.rdbsync.connector.oracle.rule.Oracle;

import java.util.ArrayList;
import java.util.List;

/**
 * Oracle建表语句生成器
 *
 * @author logz
 * @date 2024-03-19
 */
public class OracleDDLGenerator implements DDLGenerator<Oracle> {

    /** 标志：注释于 */
    String TOKEN_COMMENT_ON = "COMMENT ON";

    /** 标志：表 */
    String TOKEN_TABLE = "TABLE";

    /** 标志：列 */
    String TOKEN_COLUMN = "COLUMN";

    /** 标志：是 */
    String TOKEN_IS = "IS";

    /** Oracle方言服务 */
    private final SqlDialectService sqlDialectService = new OracleDialectService();

    /** 模式名 */
    private final String schema;

    /**
     * 构造器
     * @param schema 模式名
     */
    public OracleDDLGenerator(String schema) {
        this.schema = schema;
    }

    /**
     * 生成Oracle建表语句
     * @param table 表名
     * @param mapping 表映射
     * @return 返回Oracle建表语句
     */
    @Override
    public String generate(String table, Mapping<Oracle> mapping) {
        // 基本建表语句
        StringBuilder basicSql = generateBasic(table, mapping);
        // 附加注释语句
        StringBuilder descriptionSql = generateComments(table, mapping);

        return basicSql.append(WHITESPACE)
                .append(descriptionSql)
                .toString();
    }

    /**
     * 生成Oracle基本建表语句
     * @param table 表名
     * @param mapping 表映射
     */
    private StringBuilder generateBasic(String table, Mapping<Oracle> mapping) {
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
        for (MappingField<Oracle> field : mapping.getFields()) {
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
     * 生成Oracle附加注释语句
     * @param table 表名
     * @param mapping 表映射
     */
    private StringBuilder generateComments(String table, Mapping<Oracle> mapping) {
        StringBuilder sb = new StringBuilder();

        // 构建字符串字面量，作为存储过程的参数值
        String schemaName = sqlDialectService.identifierLiteral(schema);
        String tableName = sqlDialectService.identifierLiteral(table);

        // 1. 表注释信息
        String tableComment = mapping.getComment();
        if (tableComment != null) {
            String tableCommentStr = sqlDialectService.stringLiteral(tableComment);
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
                    .append(tableCommentStr)
                    .append(TOKEN_TERMINATOR);
        }

        // 2. 字段注释信息
        for (MappingField<Oracle> field : mapping.getFields()) {
            String fieldComment = field.getComment();
            if (fieldComment == null) {
                continue;
            }

            String columnName = sqlDialectService.identifierLiteral(field.getName());
            String columnCommentStr = sqlDialectService.stringLiteral(fieldComment);
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
                    .append(columnCommentStr)
                    .append(TOKEN_TERMINATOR);
        }

        return sb;
    }

}
