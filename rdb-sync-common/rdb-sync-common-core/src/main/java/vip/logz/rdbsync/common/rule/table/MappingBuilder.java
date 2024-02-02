package vip.logz.rdbsync.common.rule.table;

import vip.logz.rdbsync.common.rule.Builder;
import vip.logz.rdbsync.common.rule.Rdb;

/**
 * 表映射构建器
 *
 * @author logz
 * @date 2024-01-10
 * @param <DistDB> 目标数据库实现
 */
public class MappingBuilder<DistDB extends Rdb> implements Builder<MappingBuilder<DistDB>> {

    /** 表映射 */
    private final Mapping<DistDB> mapping = new Mapping<>();

    /**
     * 设置字段名
     * @param name 字段名
     * @return 返回该字段的构建器
     */
    public MappingFieldBuilder<DistDB> field(String name) {
        return new MappingFieldBuilder<>(this, name);
    }

    /**
     * 提交字段，用于从字段构建器退出时保存字段信息
     * @param field 字段
     */
    void submitField(MappingField<DistDB> field) {
        mapping.getFields().add(field);
    }

    /**
     * 设置注释
     * @param name 设置注释
     * @return 返回当前对象
     */
    public MappingBuilder<DistDB> comment(String name) {
        mapping.setComment(name);
        return this;
    }

    /**
     * 退出当前构建器，回到外围构建器
     * @return 当前构建器已经是最外围，将返回它本身
     */
    @Override
    public MappingBuilder<DistDB> and() {
        return this;
    }

    /**
     * 执行构建
     * @return 返回表映射
     */
    public Mapping<DistDB> build() {
        return mapping;
    }

    /**
     * 工厂方法
     * @return 返回一个新的表映射构建器
     * @param <DistDB> 目标数据库实现
     */
    public static <DistDB extends Rdb> MappingBuilder<DistDB> of() {
        return new MappingBuilder<>();
    }

}
