package vip.logz.rdbsync.connector.jdbc.utils;

import vip.logz.rdbsync.common.rule.Rdb;
import vip.logz.rdbsync.common.rule.table.Mapping;
import vip.logz.rdbsync.common.utils.sql.SqlGenerator;

/**
 * SQL操作语句生成器
 *
 * @author logz
 * @date 2024-01-10
 * @param <DB> 数据库实现
 */
public interface DMLGenerator<DB extends Rdb> extends SqlGenerator<DB> {

    /** 标志：插入 */
    String TOKEN_INSERT = "INSERT";

    /** 标志：插入到 */
    String TOKEN_INSERT_INTO = "INSERT INTO";

    /** 标志：值 */
    String TOKEN_VALUES = "VALUES";

    /** 标志：更新 */
    String TOKEN_UPDATE = "UPDATE";

    /** 标志：设置 */
    String TOKEN_SET = "SET";

    /** 标志：删除 */
    String TOKEN_DELETE_FROM = "DELETE FROM";

    /** 标志：条件 */
    String TOKEN_WHERE = "WHERE";

    /** 标志：JDBC参数占位符 */
    String JDBC_PARAMETER = "?";

    /**
     * 生成SQL操作语句
     * @param table 表名
     * @param mapping 表映射
     * @return 返回SQL操作语句
     */
    String generate(String table, Mapping<DB> mapping);

}
