package vip.logz.rdbsync.common.rule.table;

import java.util.regex.Pattern;

/**
 * 正则表匹配器
 *
 * @author logz
 * @date 2024-01-09
 */
public class RegexTableMatcher implements TableMatcher {

    private static final long serialVersionUID = 1L;

    /** 正则模式 */
    private final Pattern pattern;

    /**
     * 构造器
     * @param regex 正则表达式
     */
    private RegexTableMatcher(String regex) {
        this.pattern = Pattern.compile(regex);
    }

    /**
     * 工厂方法
     * @param regex 正则表达式
     * @return 返回一个新的正则表匹配器
     */
    public static RegexTableMatcher of(String regex) {
        return new RegexTableMatcher(regex);
    }

    /**
     * 匹配
     * @param table 待匹配表名
     * @return 返回是否匹配
     */
    @Override
    public boolean match(String table) {
        return pattern.matcher(table).matches();
    }

    /**
     * 获取优先级
     * @see #HIGHEST_PRECEDENCE
     * @see #LOWEST_PRECEDENCE
     */
    @Override
    public int order() {
        return 10;
    }


}
