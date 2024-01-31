package vip.logz.rdbsync.connector.jdbc.utils;

import vip.logz.rdbsync.common.rule.Rdb;
import vip.logz.rdbsync.common.rule.table.Mapping;
import vip.logz.rdbsync.common.rule.table.MappingField;
import vip.logz.rdbsync.common.utils.StringUtils;
import vip.logz.rdbsync.common.utils.sql.SqlDialectService;

/**
 * 通用删除语句生成器
 *
 * @author logz
 * @date 2024-01-19
 * @param <DB> 数据库实现
 */
public class GenericDeleteSqlGenerator<DB extends Rdb> implements DMLGenerator<DB> {

    /** 模式名 */
    private final String schema;

    /** SQL方言服务 */
    private final SqlDialectService sqlDialectService;

    /**
     * 构造器，不启用模式
     * @param sqlDialectService SQL方言服务
     */
    public GenericDeleteSqlGenerator(SqlDialectService sqlDialectService) {
        this(null, sqlDialectService);
    }

    /**
     * 构造器
     * @param schema 模式名，置为 null 则不启用模式
     * @param sqlDialectService SQL方言服务
     */
    public GenericDeleteSqlGenerator(String schema, SqlDialectService sqlDialectService) {
        this.schema = schema;
        this.sqlDialectService = sqlDialectService;
    }

    /**
     * 生成MySQL删除语句
     * @param table 表名
     * @param mapping 表映射
     * @return 返回MySQL删除语句
     */
    @Override
    public String generate(String table, Mapping<DB> mapping) {
        // 1. 表名
        StringBuilder sb = new StringBuilder(TOKEN_DELETE_FROM)
                .append(WHITESPACE);
        // 若已启用模式则将其填入
        if (schema != null) {
            String schemaName = sqlDialectService.identifierLiteral(schema);
            sb.append(schemaName).append(TOKEN_REF_DELIMITER);
        }

        String tableName = sqlDialectService.identifierLiteral(table);
        sb.append(tableName)
                .append(WHITESPACE)
                .append(TOKEN_WHERE)
                .append(WHITESPACE);

        // 2. 条件
        for (MappingField<DB> field : mapping.getFields()) {
            // 忽略非主键字段
            if (!field.isPrimaryKey()) {
                continue;
            }

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
