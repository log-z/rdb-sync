package vip.logz.rdbsync.common.rule.data;

import vip.logz.rdbsync.common.rule.Rdb;
import vip.logz.rdbsync.common.rule.convert.ConverterRegistrar;

/**
 * 平凡的字段类型
 *
 * <p>其值不限定与某一类型，也不需要进行任何转换
 *
 * @author logz
 * @date 2024-01-09
 */
public class PlainFieldType<DB extends Rdb> extends AbstractFieldType<DB, Object> {

    /**
     * 构造器
     * @param name 具体名称
     * @param args 参数列表
     */
    public PlainFieldType(String name, Object... args) {
        super(name, args);
    }

    /**
     * 保持值不变的转换
     */
    @Override
    public Object convart(Object val) {
        return val;
    }

    /**
     * 初始化配置
     * @param converterRegistrar 转换器注册器
     */
    @Override
    protected void config(ConverterRegistrar<Object> converterRegistrar) {
        // 无需注册任何转换器
    }

}
