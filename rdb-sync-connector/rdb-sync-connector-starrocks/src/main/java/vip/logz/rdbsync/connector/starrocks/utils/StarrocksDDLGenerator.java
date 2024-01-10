package vip.logz.rdbsync.connector.starrocks.utils;

import vip.logz.rdbsync.connector.starrocks.rule.Starrocks;
import vip.logz.rdbsync.common.rule.table.Mapping;
import vip.logz.rdbsync.common.rule.table.MappingField;
import vip.logz.rdbsync.common.utils.sql.DDLGenerator;
import vip.logz.rdbsync.common.utils.sql.SqlUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Starrocks建表语句生成器
 *
 * @author logz
 * @date 2024-01-10
 */
public class StarrocksDDLGenerator implements DDLGenerator<Starrocks> {

    /** 标志：分桶依据 */
    public static final String TOKEN_DISTRIBUTED_BY = "DISTRIBUTED BY";

    /** 标志：属性 */
    public static final String TOKEN_PROPERTIES = "PROPERTIES";

    /** 函数：散列 */
    public static final String FUNC_HASH = "HASH";

    /** 建表属性 */
    private final Map<String, String> properties;

    /**
     * 构造器
     */
    public StarrocksDDLGenerator() {
        this(new HashMap<>());
    }

    /**
     * 构造器
     * @param properties 建表属性
     */
    public StarrocksDDLGenerator(Map<String, String> properties) {
        this.properties = properties;

        // 启用主键模型
        properties.put("enable_persistent_index", "true");
    }

    /**
     * 生成Starrocks建表语句
     * @param table 表名
     * @param mapping 表映射
     * @return 返回Starrocks建表语句
     */
    @Override
    public String generate(String table, Mapping<Starrocks> mapping) {
        // 1. 表名
        String tableName = SqlUtils.identifierLiteral(table);
        StringBuilder sb = new StringBuilder(TOKEN_CREATE_TABLE)
                .append(WHITESPACE)
                .append(tableName)
                .append(WHITESPACE)
                .append(TOKEN_BRACKET_BEGIN);
        List<String> primaryKeys = new ArrayList<>();

        // 2. 字段信息
        for (MappingField<Starrocks> field : mapping.getFields()) {
            String fieldName = SqlUtils.identifierLiteral(field.getName());
            sb.append(fieldName)
                    .append(WHITESPACE)
                    .append(field.getType());

            if (field.isNonNull()) {
                sb.append(WHITESPACE).append(TOKEN_NOT_NULL);
            }
            if (field.isPrimaryKey()) {
                primaryKeys.add(fieldName);
            }
            if (field.getComment() != null) {
                String comment = SqlUtils.stringLiteral(field.getComment());
                sb.append(WHITESPACE)
                        .append(TOKEN_COMMENT)
                        .append(WHITESPACE)
                        .append(comment);
            }

            sb.append(TOKEN_COMMA);
        }

        // 去除末尾逗号，然后闭合括号
        SqlUtils.deleteTail(sb, TOKEN_COMMA).append(TOKEN_BRACKET_END);

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
            SqlUtils.deleteTail(sb, TOKEN_COMMA).append(TOKEN_BRACKET_END);

            // 3.1. 分桶键
            sb.append(WHITESPACE)
                    .append(TOKEN_DISTRIBUTED_BY)
                    .append(WHITESPACE)
                    .append(FUNC_HASH)
                    .append(TOKEN_BRACKET_BEGIN);
            for (String primaryKey : primaryKeys) {
                sb.append(primaryKey).append(TOKEN_COMMA);
            }

            // 去除末尾逗号，然后闭合括号
            SqlUtils.deleteTail(sb, TOKEN_COMMA).append(TOKEN_BRACKET_END);
        }

        // 4. 属性信息
        if (!properties.isEmpty()) {
            sb.append(WHITESPACE)
                    .append(TOKEN_PROPERTIES)
                    .append(WHITESPACE)
                    .append(TOKEN_BRACKET_BEGIN);
            properties.forEach((key, val) ->
                    sb.append(SqlUtils.stringLiteral(key))
                            .append(TOKEN_EQUAL)
                            .append(SqlUtils.stringLiteral(val))
                            .append(TOKEN_COMMA)
            );

            // 去除末尾逗号，然后闭合括号
            SqlUtils.deleteTail(sb, TOKEN_COMMA).append(TOKEN_BRACKET_END);
        }

        return sb.append(TOKEN_TERMINATOR).toString();
    }

}
