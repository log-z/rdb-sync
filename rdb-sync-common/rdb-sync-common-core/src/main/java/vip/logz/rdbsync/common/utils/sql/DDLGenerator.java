package vip.logz.rdbsync.common.utils.sql;

import vip.logz.rdbsync.common.rule.Pipeline;
import vip.logz.rdbsync.common.rule.Rdb;
import vip.logz.rdbsync.common.rule.table.Mapping;

import java.util.List;
import java.util.stream.Collectors;

/**
 * SQL建表语句生成器
 *
 * @author logz
 * @date 2024-01-10
 * @param <DB> 数据库实现
 */
public interface DDLGenerator<DB extends Rdb> extends SqlGenerator<DB> {

    /** 标志：创建表 */
    String TOKEN_CREATE_TABLE = "CREATE TABLE";

    /** 标志：不为NULL */
    String TOKEN_NOT_NULL = "NOT NULL";

    /** 标志：主键 */
    String TOKEN_PRIMARY_KEY = "PRIMARY KEY";

    /** 标志：注释 */
    String TOKEN_COMMENT = "COMMENT";

    /**
     * 生成SQL建表语句
     * @param table 表名
     * @param mapping 表映射
     * @return 返回SQL建表语句
     */
    String generate(String table, Mapping<DB> mapping);

    /**
     * 批量生成SQL建表语句
     * @param pipeline 管道
     * @return 返回SQL建表语句列表
     */
    default List<String> generate(Pipeline<DB> pipeline) {
        return pipeline.getBindings().stream()
                .map(binding -> generate(binding.getDistTable(), binding.getMapping()))
                .collect(Collectors.toList());
    }

}
