package vip.logz.rdbsync.common.rule.table;

import vip.logz.rdbsync.common.rule.Builder;
import vip.logz.rdbsync.common.rule.Rdb;
import vip.logz.rdbsync.common.rule.data.FieldType;

/**
 * 表映射的字段信息的构建器
 *
 * @author logz
 * @date 2024-01-10
 * @param <DistDB> 目标数据库实现
 */
public class MappingFieldBuilder<DistDB extends Rdb> implements Builder<MappingBuilder<DistDB>> {

    /** 外围构建器 */
    private final MappingBuilder<DistDB> outsideBuilder;

    /** 字段信息 */
    private final MappingField<DistDB> field = new MappingField<>();

    /**
     * 构造器
     * @param outsideBuilder 外围构建器
     * @param fieldName 字段名称
     */
    public MappingFieldBuilder(MappingBuilder<DistDB> outsideBuilder, String fieldName) {
        this.outsideBuilder = outsideBuilder;
        field.setName(fieldName);
    }

    /**
     * 设置类型
     * @param type 类型
     * @return 返回当前对象
     */
    public MappingFieldBuilder<DistDB> type(FieldType<DistDB, ?> type) {
        field.setType(type);
        return this;
    }

    /**
     * 设置为不允许为NULL
     * @return 返回当前对象
     */
    public MappingFieldBuilder<DistDB> nonNull() {
        field.setNonNull(true);
        return this;
    }

    /**
     * 设置为主键
     * @return 返回当前对象
     */
    public MappingFieldBuilder<DistDB> primaryKey() {
        field.setPrimaryKey(true);
        return this;
    }

    /**
     * 设置注释
     * @param comment 注释
     * @return 返回当前对象
     */
    public MappingFieldBuilder<DistDB> comment(String comment) {
        field.setComment(comment);
        return this;
    }

    /**
     * 退出当前构建器，回到外围构建器
     * @return 返回外围构建器
     */
    @Override
    public MappingBuilder<DistDB> and() {
        outsideBuilder.submitField(field);
        return outsideBuilder;
    }

}
