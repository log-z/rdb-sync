package vip.logz.rdbsync.common.rule.data;

import vip.logz.rdbsync.common.rule.Rdb;
import vip.logz.rdbsync.common.rule.convert.Converter;
import vip.logz.rdbsync.common.rule.convert.ConverterRegistrar;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * DOUBLE字段类型
 *
 * @author logz
 * @date 2024-01-09
 */
public class DoubleFieldType<DB extends Rdb> extends AbstractFieldType<DB, Double> {

    /**
     * 构造器
     * @param name 具体名称
     * @param args 参数列表
     */
    public DoubleFieldType(String name, Object... args) {
        super(name, args);
    }

    /**
     * 初始化配置
     * @param converterRegistrar 转换器注册器
     */
    @Override
    public void config(ConverterRegistrar<Double> converterRegistrar) {
        converterRegistrar.withInt(Integer::doubleValue)
                .withLong(Long::doubleValue)
                .withFloat(Float::doubleValue)
                .withDouble(Converter::invariant)
                .withBoolean(val -> val ? 1d : 0d)
                .withBigInteger(BigInteger::doubleValue)
                .withBigDecimal(BigDecimal::doubleValue)
                .withString(Double::valueOf);
    }

}
