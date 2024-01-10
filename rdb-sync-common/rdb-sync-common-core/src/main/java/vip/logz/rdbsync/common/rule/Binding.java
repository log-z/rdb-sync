package vip.logz.rdbsync.common.rule;

import vip.logz.rdbsync.common.rule.table.Mapping;
import vip.logz.rdbsync.common.rule.table.TableMatcher;

import java.io.Serializable;

/**
 * 来源与目标表的绑定
 *
 * @author logz
 * @date 2024-01-10
 * @param <DistDB> 目标数据库实现
 */
public class Binding<DistDB extends Rdb> implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 来源表匹配器 */
    private final TableMatcher sourceTableMatcher;

    /** 目标表名 */
    private final String distTable;

    /** 表映射 */
    private final Mapping<DistDB> mapping;

    /**
     * 构造器
     * @param sourceTableMatcher 来源表匹配器
     * @param distTable 目标表名
     * @param mapping 表映射
     */
    public Binding(TableMatcher sourceTableMatcher, String distTable, Mapping<DistDB> mapping) {
        this.sourceTableMatcher = sourceTableMatcher;
        this.distTable = distTable;
        this.mapping = mapping;
    }

    /**
     * 获取来源表匹配器
     */
    public TableMatcher getSourceTableMatcher() {
        return sourceTableMatcher;
    }

    /**
     * 获取目标表名
     */
    public String getDistTable() {
        return distTable;
    }

    /**
     * 获取表映射
     */
    public Mapping<DistDB> getMapping() {
        return mapping;
    }

}
