package vip.logz.rdbsync.connector.mysql.utils;

import vip.logz.rdbsync.common.rule.table.Mapping;
import vip.logz.rdbsync.common.rule.table.MappingField;
import vip.logz.rdbsync.common.utils.StringUtils;
import vip.logz.rdbsync.common.utils.sql.DDLGenerator;
import vip.logz.rdbsync.common.utils.sql.SqlUtils;
import vip.logz.rdbsync.connector.mysql.enums.MysqlEngine;
import vip.logz.rdbsync.connector.mysql.rule.Mysql;

import java.util.ArrayList;
import java.util.List;

/**
 * MySQL建表语句生成器
 *
 * @author logz
 * @date 2024-01-10
 */
public class MysqlDDLGenerator implements DDLGenerator<Mysql> {

    /** 标志：引擎 */
    public static final String TOKEN_ENGINE = "ENGINE";

    /** 引擎 */
    private final MysqlEngine engine;

    /**
     * 构造器，默认使用InnoDB引擎
     */
    public MysqlDDLGenerator() {
        this(MysqlEngine.INNO_DB);
    }

    /**
     * 构造器
     * @param engine 引擎
     */
    public MysqlDDLGenerator(MysqlEngine engine) {
        this.engine = engine;
    }

    /**
     * 生成MySQL建表语句
     * @param table 表名
     * @param mapping 表映射
     * @return 返回MySQL建表语句
     */
    @Override
    public String generate(String table, Mapping<Mysql> mapping) {
        // 1. 表名
        String tableName = SqlUtils.identifierLiteral(table);
        StringBuilder sb = new StringBuilder(TOKEN_CREATE_TABLE)
                .append(WHITESPACE)
                .append(tableName)
                .append(WHITESPACE)
                .append(TOKEN_BRACKET_BEGIN);
        List<String> primaryKeys = new ArrayList<>();

        // 2. 字段信息
        for (MappingField<Mysql> field : mapping.getFields()) {
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

        // 4. 指定引擎
        sb.append(WHITESPACE)
                .append(TOKEN_ENGINE)
                .append(TOKEN_EQUAL)
                .append(engine);

        return sb.append(TOKEN_TERMINATOR).toString();
    }

}
