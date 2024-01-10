package vip.logz.rdbsync.common.utils.sql;

import vip.logz.rdbsync.common.rule.Rdb;

/**
 * SQL语句生成器
 *
 * @author logz
 * @date 2024-01-10
 * @param <DB> 数据库实现
 */
public interface SqlGenerator<DB extends Rdb> {

    /** 空格符 */
    String WHITESPACE = " ";

    /** 标志：括号头 */
    String TOKEN_BRACKET_BEGIN = "(";

    /** 标志：括号尾 */
    String TOKEN_BRACKET_END = ")";

    /** 标志：逗号 */
    String TOKEN_COMMA = ", ";

    /** 标志：等于 */
    String TOKEN_EQUAL = " = ";

    /** 标志：终止符（分号） */
    String TOKEN_TERMINATOR = ";";

}
