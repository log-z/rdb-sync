package vip.logz.rdbsync.common.rule.data;

import vip.logz.rdbsync.common.rule.Rdb;
import vip.logz.rdbsync.common.rule.convert.Converter;
import vip.logz.rdbsync.common.rule.convert.ConverterRegistrar;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * INT字段类型
 *
 * @author logz
 * @date 2024-01-09
 */
public class IntFieldType<DB extends Rdb> extends AbstractFieldType<DB, Integer> {

    /**
     * 构造器
     * @param name 具体名称
     * @param args 参数列表
     */
    public IntFieldType(String name, Object... args) {
        super(name, args);
    }

    /**
     * 初始化配置
     * @param converterRegistrar 转换器注册器
     */
    @Override
    public void config(ConverterRegistrar<Integer> converterRegistrar) {
        converterRegistrar.withInt(Converter::invariant)
                .withLong(Long::intValue)
                .withFloat(Float::intValue)
                .withDouble(Double::intValue)
                .withBoolean(val -> val ? 1 : 0)
                .withBigInteger(BigInteger::intValue)
                .withBigDecimal(BigDecimal::intValue)
                .withString(Integer::valueOf);
    }

}
