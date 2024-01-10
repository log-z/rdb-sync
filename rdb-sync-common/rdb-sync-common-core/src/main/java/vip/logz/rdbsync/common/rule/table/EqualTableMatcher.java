package vip.logz.rdbsync.common.rule.table;

import java.util.Objects;

/**
 * 等值表匹配器
 *
 * @author logz
 * @date 2024-01-09
 */
public class EqualTableMatcher implements TableMatcher {

    private static final long serialVersionUID = 1L;

    /** 有效表名 */
    private final String table;

    /**
     * 构造器
     * @param table 有效表名
     */
    private EqualTableMatcher(String table) {
        this.table = table;
    }

    /**
     * 工厂方法
     * @param table 有效表名
     * @return 返回一个新的等值表匹配器
     */
    public static EqualTableMatcher of(String table) {
        return new EqualTableMatcher(table);
    }

    /**
     * 匹配
     * @param table 待匹配表名
     * @return 返回是否匹配
     */
    @Override
    public boolean match(String table) {
        return Objects.equals(table, this.table);
    }

    /**
     * 获取优先级
     * @see #HIGHEST_PRECEDENCE
     * @see #LOWEST_PRECEDENCE
     */
    @Override
    public int order() {
        return 0;
    }

}
