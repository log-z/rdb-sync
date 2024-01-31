package vip.logz.rdbsync.connector.sqlserver.utils;

import vip.logz.rdbsync.common.rule.table.Mapping;
import vip.logz.rdbsync.common.rule.table.MappingField;
import vip.logz.rdbsync.common.utils.StringUtils;
import vip.logz.rdbsync.common.utils.sql.SqlDialectService;
import vip.logz.rdbsync.connector.jdbc.utils.DMLGenerator;
import vip.logz.rdbsync.connector.sqlserver.rule.Sqlserver;

/**
 * SQLServer更新或新增语句生成器
 *
 * @author logz
 * @date 2024-01-29
 */
public class SqlserverUpsertSqlGenerator implements DMLGenerator<Sqlserver> {

    /** 标志：融合 */
    private static final String TOKEN_MERGE = "MERGE";

    /** 标志：使用 */
    private static final String TOKEN_USING = "USING";

    /** 标志：定义 */
    private static final String TOKEN_AS = "AS";

    /** 标志：仅当 */
    private static final String TOKEN_ON = "ON";

    /** 标志：如果 */
    private static final String TOKEN_WHEN = "WHEN";

    /** 标志：匹配 */
    private static final String TOKEN_MATCHED = "MATCHED";

    /** 标志：那么就 */
    private static final String TOKEN_THEN = "THEN";

    /** 标志：非 */
    private static final String TOKEN_NOT = "NOT";

    /** 标志：并且 */
    private static final String TOKEN_AND = "AND";

    /** 标志：引用分隔符（点） */
    private static final String TOKEN_REF_DELIMITER = ".";

    /** 标识符：旧数据集 */
    private static final String IDENTIFIER_OLD_DATASET = "old";

    /** 标识符：新数据集 */
    private static final String IDENTIFIER_NEW_DATASET = "new";

    /** SQLServer方言服务 */
    private final SqlDialectService sqlDialectService = new SqlserverDialectService();

    /** 模式名 */
    private final String schema;

    /**
     * 构造器
     * @param schema 模式名
     */
    public SqlserverUpsertSqlGenerator(String schema) {
        this.schema = schema;
    }

    /**
     * 生成SQLServer更新或新增语句
     * @param table 表名
     * @param mapping 表映射
     * @return 返回SQLServer更新或新增语句
     */
    @Override
    public String generate(String table, Mapping<Sqlserver> mapping) {
        String oldDataset = sqlDialectService.identifierLiteral(IDENTIFIER_OLD_DATASET);
        String newDataset = sqlDialectService.identifierLiteral(IDENTIFIER_NEW_DATASET);

        // 1. 表名
        String schemaName = sqlDialectService.identifierLiteral(schema);
        String tableName = sqlDialectService.identifierLiteral(table);
        StringBuilder sb = new StringBuilder(TOKEN_MERGE)
                .append(WHITESPACE)
                .append(schemaName)
                .append(TOKEN_REF_DELIMITER)
                .append(tableName)
                .append(WHITESPACE)
                .append(TOKEN_AS)
                .append(WHITESPACE)
                .append(oldDataset);

        // 2. 值信息
        sb.append(WHITESPACE)
                .append(TOKEN_USING)
                .append(WHITESPACE)
                .append(TOKEN_BRACKET_BEGIN)
                .append(TOKEN_VALUES)
                .append(WHITESPACE)
                .append(TOKEN_BRACKET_BEGIN);
        for (MappingField<Sqlserver> ignored : mapping.getFields()) {
            sb.append(JDBC_PARAMETER).append(TOKEN_COMMA);
        }

        // 去除末尾逗号，然后闭合括号
        StringUtils.removeEnd(sb, TOKEN_COMMA)
                .append(TOKEN_BRACKET_END)
                .append(TOKEN_BRACKET_END);

        // 2. 字段信息
        sb.append(WHITESPACE)
                .append(TOKEN_AS)
                .append(WHITESPACE)
                .append(newDataset)
                .append(WHITESPACE)
                .append(TOKEN_BRACKET_BEGIN);
        for (MappingField<Sqlserver> field : mapping.getFields()) {
            String fieldName = sqlDialectService.identifierLiteral(field.getName());
            sb.append(fieldName).append(TOKEN_COMMA);
        }

        // 去除末尾逗号，然后闭合括号
        StringUtils.removeEnd(sb, TOKEN_COMMA).append(TOKEN_BRACKET_END);

        // 4. 融合条件：仅比较主键
        sb.append(WHITESPACE)
                .append(TOKEN_ON);
        for (MappingField<Sqlserver> field : mapping.getFields()) {
            if (!field.isPrimaryKey()) {
                continue;
            }

            String fieldName = sqlDialectService.identifierLiteral(field.getName());
            sb.append(WHITESPACE)
                    .append(oldDataset)
                    .append(TOKEN_REF_DELIMITER)
                    .append(fieldName)
                    .append(TOKEN_EQUAL)
                    .append(newDataset)
                    .append(TOKEN_REF_DELIMITER)
                    .append(fieldName)
                    .append(WHITESPACE)
                    .append(TOKEN_AND);
        }

        // 去除末尾AND
        StringUtils.removeEnd(sb, WHITESPACE + TOKEN_AND);

        // 5. 若匹配，则更新
        sb.append(WHITESPACE)
                .append(TOKEN_WHEN)
                .append(WHITESPACE)
                .append(TOKEN_MATCHED)
                .append(WHITESPACE)
                .append(TOKEN_THEN)
                .append(WHITESPACE)
                .append(TOKEN_UPDATE)
                .append(WHITESPACE)
                .append(TOKEN_SET)
                .append(WHITESPACE);
        for (MappingField<Sqlserver> field : mapping.getFields()) {
            String fieldName = sqlDialectService.identifierLiteral(field.getName());
            sb.append(oldDataset)
                    .append(TOKEN_REF_DELIMITER)
                    .append(fieldName)
                    .append(TOKEN_EQUAL)
                    .append(newDataset)
                    .append(TOKEN_REF_DELIMITER)
                    .append(fieldName)
                    .append(TOKEN_COMMA);
        }

        // 去除末尾逗号
        StringUtils.removeEnd(sb, TOKEN_COMMA);

        // 6. 若不匹配，则插入
        // 6.1. 旧数据集字段信息
        sb.append(WHITESPACE)
                .append(TOKEN_WHEN)
                .append(WHITESPACE)
                .append(TOKEN_NOT)
                .append(WHITESPACE)
                .append(TOKEN_MATCHED)
                .append(WHITESPACE)
                .append(TOKEN_THEN)
                .append(WHITESPACE)
                .append(TOKEN_INSERT)
                .append(WHITESPACE)
                .append(TOKEN_BRACKET_BEGIN);
        for (MappingField<Sqlserver> field : mapping.getFields()) {
            String fieldName = sqlDialectService.identifierLiteral(field.getName());
            sb.append(fieldName)
                    .append(TOKEN_COMMA);
        }

        // 去除末尾逗号，然后闭合括号
        StringUtils.removeEnd(sb, TOKEN_COMMA).append(TOKEN_BRACKET_END);

        // 6.2. 新数据集字段信息
        sb.append(WHITESPACE)
                .append(TOKEN_VALUES)
                .append(WHITESPACE)
                .append(TOKEN_BRACKET_BEGIN);
        for (MappingField<Sqlserver> field : mapping.getFields()) {
            String fieldName = sqlDialectService.identifierLiteral(field.getName());
            sb.append(newDataset)
                    .append(TOKEN_REF_DELIMITER)
                    .append(fieldName)
                    .append(TOKEN_COMMA);
        }

        // 去除末尾逗号，然后闭合括号
        StringUtils.removeEnd(sb, TOKEN_COMMA).append(TOKEN_BRACKET_END);

        // 结束
        return sb.append(TOKEN_TERMINATOR)
                .toString();
    }

}
