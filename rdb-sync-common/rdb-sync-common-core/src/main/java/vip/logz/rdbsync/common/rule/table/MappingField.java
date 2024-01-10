package vip.logz.rdbsync.common.rule.table;

import vip.logz.rdbsync.common.rule.Rdb;
import vip.logz.rdbsync.common.rule.data.FieldType;

import java.io.Serializable;

/**
 * 表映射的字段信息
 *
 * @author logz
 * @date 2024-01-10
 * @param <DistDB> 目标数据库实现
 */
public class MappingField<DistDB extends Rdb> implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 名称 */
    private String name;

    /** 类型 */
    private FieldType<DistDB, ?> type;

    /** 不允许为NULL */
    private boolean nonNull;

    /** 是否为主键 */
    private boolean primaryKey;

    /** 注释 */
    private String comment;

    /**
     * 获取名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置名称
     * @param name 名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取类型
     */
    public FieldType<DistDB, ?> getType() {
        return type;
    }

    /**
     * 设置类型
     * @param type 类型
     */
    public void setType(FieldType<DistDB, ?> type) {
        this.type = type;
    }

    /**
     * 检查不允许为NULL
     */
    public boolean isNonNull() {
        return nonNull;
    }

    /**
     * 设置不允许为NULL
     * @param nonNull {@code true} 表示不允许为NULL
     */
    public void setNonNull(boolean nonNull) {
        this.nonNull = nonNull;
    }

    /**
     * 检查是否为主键
     */
    public boolean isPrimaryKey() {
        return primaryKey;
    }

    /**
     * 设置是否为主键
     * @param primaryKey 是否为主键
     */
    public void setPrimaryKey(boolean primaryKey) {
        this.primaryKey = primaryKey;
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
