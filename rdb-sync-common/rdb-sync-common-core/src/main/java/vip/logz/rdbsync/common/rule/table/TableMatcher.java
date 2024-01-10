package vip.logz.rdbsync.common.rule.table;

import java.io.Serializable;

/**
 * 表匹配器
 *
 * @author logz
 * @date 2024-01-09
 */
public interface TableMatcher extends Serializable {

    /** 最高优先级 */
    int HIGHEST_PRECEDENCE = Integer.MIN_VALUE;

    /** 最低优先级 */
    int LOWEST_PRECEDENCE = Integer.MAX_VALUE;

    /**
     * 匹配
     * @param table 待匹配表名
     * @return 返回是否匹配
     */
    boolean match(String table);

    /**
     * 获取优先级
     * @see #HIGHEST_PRECEDENCE
     * @see #LOWEST_PRECEDENCE
     */
    default int order() {
        return LOWEST_PRECEDENCE;
    }

}
