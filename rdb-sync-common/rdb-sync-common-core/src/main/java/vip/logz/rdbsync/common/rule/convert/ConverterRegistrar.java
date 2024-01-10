package vip.logz.rdbsync.common.rule.convert;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 字段值转换器注册器
 * @param <T> 转换后的值类型
 */
public class ConverterRegistrar<T> {

    /**
     * 记录已注册的转换器 [srcType -> Converter]
     */
    private final Map<Class<?>, Converter<?, T>> converterMap = new HashMap<>();

    /**
     * 获取已注册的转换器
     */
    public Map<Class<?>, Converter<?, T>> getConverterMap() {
        return Collections.unmodifiableMap(converterMap);
    }

    /**
     * 从 {@link Integer} 类型转换
     * @param converter 转换器
     * @return 返回当前对象
     */
    public ConverterRegistrar<T> withInt(Converter<Integer, T> converter) {
        converterMap.put(Integer.class, converter);
        return this;
    }

    /**
     * 从 {@link Long} 类型转换
     * @param converter 转换器
     * @return 返回当前对象
     */
    public ConverterRegistrar<T> withLong(Converter<Long, T> converter) {
        converterMap.put(Long.class, converter);
        return this;
    }

    /**
     * 从 {@link Float} 类型转换
     * @param converter 转换器
     * @return 返回当前对象
     */
    public ConverterRegistrar<T> withFloat(Converter<Float, T> converter) {
        converterMap.put(Float.class, converter);
        return this;
    }

    /**
     * 从 {@link Double} 类型转换
     * @param converter 转换器
     * @return 返回当前对象
     */
    public ConverterRegistrar<T> withDouble(Converter<Double, T> converter) {
        converterMap.put(Double.class, converter);
        return this;
    }

    /**
     * 从 {@link Boolean} 类型转换
     * @param converter 转换器
     * @return 返回当前对象
     */
    public ConverterRegistrar<T> withBoolean(Converter<Boolean, T> converter) {
        converterMap.put(Boolean.class, converter);
        return this;
    }

    /**
     * 从 {@link BigInteger} 类型转换
     * @param converter 转换器
     * @return 返回当前对象
     */
    public ConverterRegistrar<T> withBigInteger(Converter<BigInteger, T> converter) {
        converterMap.put(BigInteger.class, converter);
        return this;
    }

    /**
     * 从 {@link BigDecimal} 类型转换
     * @param converter 转换器
     * @return 返回当前对象
     */
    public ConverterRegistrar<T> withBigDecimal(Converter<BigDecimal, T> converter) {
        converterMap.put(BigDecimal.class, converter);
        return this;
    }

    /**
     * 从 {@link String} 类型转换
     * @param converter 转换器
     * @return 返回当前对象
     */
    public ConverterRegistrar<T> withString(Converter<String, T> converter) {
        converterMap.put(String.class, converter);
        return this;
    }

}
