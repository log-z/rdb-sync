package vip.logz.rdbsync.common.rule.data;

import vip.logz.rdbsync.common.rule.Rdb;
import vip.logz.rdbsync.common.rule.convert.Converter;
import vip.logz.rdbsync.common.rule.convert.ConverterRegistrar;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * FLOAT字段类型
 *
 * @author logz
 * @date 2024-01-09
 */
public class FloatFieldType<DB extends Rdb> extends AbstractFieldType<DB, Float> {

    /**
     * 构造器
     * @param name 具体名称
     * @param args 参数列表
     */
    public FloatFieldType(String name, Object... args) {
        super(name, args);
    }

    /**
     * 初始化配置
     * @param converterRegistrar 转换器注册器
     */
    @Override
    public void config(ConverterRegistrar<Float> converterRegistrar) {
        converterRegistrar.withInt(Integer::floatValue)
                .withLong(Long::floatValue)
                .withFloat(Converter::invariant)
                .withDouble(Double::floatValue)
                .withBoolean(val -> val ? 1f : 0f)
                .withBigInteger(BigInteger::floatValue)
                .withBigDecimal(BigDecimal::floatValue)
                .withString(Float::valueOf);
    }

}
