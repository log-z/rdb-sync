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

    /** 默认值：模式名 */
    private static final String DEFAULT_SCHEMA = "dbo";

    /** 标志：执行 */
    String TOKEN_EXEC = "EXEC";

    /** 存储过程：添加扩展属性 */
    String SP_ADD_EXTENDED_PROPERTY = "sp_addextendedproperty";

    /** 值：扩展属性名-描述 */
    String VAL_EXTENDED_PROPERTY_NAME_DESCRIPTION = "MS_Description";

    /** 值：扩展属性类型-模式 */
    String VAL_EXTENDED_PROPERTY_TYPE_SCHEMA = "SCHEMA";

    /** 值：扩展属性类型-表 */
    String VAL_EXTENDED_PROPERTY_TYPE_TABLE = "TABLE";

    /** 值：扩展属性类型-列 */
    String VAL_EXTENDED_PROPERTY_TYPE_COLUMN = "COLUMN";

    /** SQLServer方言服务 */
    private final SqlDialectService sqlDialectService = new SqlserverDialectService();

    /** 模式名 */
    private final String schema;

    /**
     * 构造器
     */
    public SqlserverDDLGenerator() {
        this(DEFAULT_SCHEMA);
    }

    /**
     * 构造器
     * @param schema 模式名
     */
    public SqlserverDDLGenerator(String schema) {
        this.schema = schema;
    }

    /**
     * 生成SQLServer建表语句
     * @param table 表名
     * @param mapping 表映射
     * @return 返回SQLServer建表语句
     */
    @Override
    public String generate(String table, Mapping<Sqlserver> mapping) {
        // 基本建表语句
        StringBuilder basicSql = generateBasic(table, mapping);
        // 附加描述语句
        StringBuilder descriptionSql = generateDescription(table, mapping);

        return basicSql.append(WHITESPACE)
                .append(descriptionSql)
                .toString();
    }

    /**
     * 生成SQLServer基本建表语句
     * @param table 表名
     * @param mapping 表映射
     */
    private StringBuilder generateBasic(String table, Mapping<Sqlserver> mapping) {
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
     * 生成SQLServer附加描述语句
     * @param table 表名
     * @param mapping 表映射
     */
    private StringBuilder generateDescription(String table, Mapping<Sqlserver> mapping) {
        StringBuilder sb = new StringBuilder();

        // 构建字符串字面量，作为存储过程的参数值
        String valEPNameDescription = sqlDialectService.stringLiteral(VAL_EXTENDED_PROPERTY_NAME_DESCRIPTION);
        String valEPTypeSchema = sqlDialectService.stringLiteral(VAL_EXTENDED_PROPERTY_TYPE_SCHEMA);
        String valEPTypeTable = sqlDialectService.stringLiteral(VAL_EXTENDED_PROPERTY_TYPE_TABLE);
        String valEPTypColumn = sqlDialectService.stringLiteral(VAL_EXTENDED_PROPERTY_TYPE_COLUMN);
        String valSchema = sqlDialectService.stringLiteral(schema);
        String valTable = sqlDialectService.stringLiteral(table);

        // 1. 表注释信息
        String tableComment = mapping.getComment();
        if (tableComment != null) {
            String valTableComment = sqlDialectService.stringLiteral(tableComment);
            sb.append(WHITESPACE)
                    .append(TOKEN_EXEC)
                    .append(WHITESPACE)
                    .append(SP_ADD_EXTENDED_PROPERTY)
                    .append(WHITESPACE)
                    .append(valEPNameDescription)
                    .append(TOKEN_COMMA)
                    .append(valTableComment)
                    .append(TOKEN_COMMA)
                    .append(valEPTypeSchema)
                    .append(TOKEN_COMMA)
                    .append(valSchema)
                    .append(TOKEN_COMMA)
                    .append(valEPTypeTable)
                    .append(TOKEN_COMMA)
                    .append(valTable)
                    .append(TOKEN_TERMINATOR);
        }

        // 2. 字段注释信息
        for (MappingField<Sqlserver> field : mapping.getFields()) {
            String fieldComment = field.getComment();
            if (fieldComment == null) {
                continue;
            }

            String valColumn = sqlDialectService.stringLiteral(field.getName());
            String valColumnComment = sqlDialectService.stringLiteral(fieldComment);
            sb.append(WHITESPACE)
                    .append(TOKEN_EXEC)
                    .append(WHITESPACE)
                    .append(SP_ADD_EXTENDED_PROPERTY)
                    .append(WHITESPACE)
                    .append(valEPNameDescription)
                    .append(TOKEN_COMMA)
                    .append(valColumnComment)
                    .append(TOKEN_COMMA)
                    .append(valEPTypeSchema)
                    .append(TOKEN_COMMA)
                    .append(valSchema)
                    .append(TOKEN_COMMA)
                    .append(valEPTypeTable)
                    .append(TOKEN_COMMA)
                    .append(valTable)
                    .append(TOKEN_COMMA)
                    .append(valEPTypColumn)
                    .append(TOKEN_COMMA)
                    .append(valColumn)
                    .append(TOKEN_TERMINATOR);
        }

        return sb;
    }

}
