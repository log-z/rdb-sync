package vip.logz.rdbsync.common.rule.data;

import vip.logz.rdbsync.common.rule.Rdb;
import vip.logz.rdbsync.common.rule.convert.Converter;
import vip.logz.rdbsync.common.rule.convert.ConverterRegistrar;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 抽象的字段类型
 *
 * @author logz
 * @date 2024-01-09
 * @param <DB> 数据库实现
 * @param <T> 有效值类型
 */
public abstract class AbstractFieldType<DB extends Rdb, T> implements FieldType<DB, T> {

    /** 参数列表 */
    private final Object[] args;

    /** 转换器映射 [srcType -> Converter] */
    private final Map<Class<?>, Converter<?, T>> converterMap;

    /**
     * 构造器
     * @param args 参数列表
     */
    public AbstractFieldType(Object... args) {
        this.args = args;

        // 获取字段的所有转换器，并记录
        ConverterRegistrar<T> converterRegistrar = new ConverterRegistrar<>();
        config(converterRegistrar);
        converterMap = converterRegistrar.getConverterMap();
    }

    /**
     * 为不同的字段进行具体配置，比如注册转换器
     * @param converterRegistrar 转换器注册器
     */
    protected abstract void config(ConverterRegistrar<T> converterRegistrar);

    /**
     * 转换
     * @param val 原始值
     * @return 返回转换后的有效值
     * @param <S> 原始值类型
     */
    @Override
    @SuppressWarnings("unchecked")
    public <S> T convart(S val) {
        if (val == null) {
            return null;
        }

        Converter<S, T> converter = (Converter<S, T>) converterMap.get(val.getClass());
        return converter.convert(val);
    }

    /**
     * 获取名称
     */
    public String getName() {
        return getClass().getSimpleName();
    }

    /**
     * 转换为字符串
     */
    @Override
    public String toString() {
        if (args.length == 0) {
            return getName();
        }

        String argsText = Arrays.stream(this.args)
                .map(Object::toString)
                .collect(Collectors.joining(", "));
        return getName() + "(" + argsText + ")";
    }

}
