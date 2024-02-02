package vip.logz.rdbsync.common.rule.table;

import vip.logz.rdbsync.common.rule.Rdb;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 表映射
 *
 * @author logz
 * @date 2024-01-10
 * @param <DistDB> 目标数据库实现
 */
public class Mapping<DistDB extends Rdb> implements Serializable {

    private static final long serialVersionUID = 2L;

    /** 字段列表 */
    private final List<MappingField<DistDB>> fields = new ArrayList<>();

    /** 注释 */
    private String comment;

    /**
     * 获取字段列表
     */
    public List<MappingField<DistDB>> getFields() {
        return fields;
    }

    /**
     * 获取注释
     */
    public String getComment() {
        return comment;
    }

    /**
     * 设置注释
     * @param comment 注释
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

}
