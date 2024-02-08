package vip.logz.rdbsync.common.rule.data;

import vip.logz.rdbsync.common.rule.Rdb;
import vip.logz.rdbsync.common.rule.convert.Converter;
import vip.logz.rdbsync.common.rule.convert.ConverterRegistrar;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * BIGINT字段类型
 *
 * @author logz
 * @date 2024-01-09
 */
public class BigintFieldType<DB extends Rdb> extends AbstractFieldType<DB, Long> {

    /**
     * 构造器
     * @param name 具体名称
     * @param args 参数列表
     */
    public BigintFieldType(String name, Object... args) {
        super(name, args);
    }

    /**
     * 初始化配置
     * @param converterRegistrar 转换器注册器
     */
    @Override
    public void config(ConverterRegistrar<Long> converterRegistrar) {
        converterRegistrar.withInt(Integer::longValue)
                .withLong(Converter::invariant)
                .withFloat(Float::longValue)
                .withDouble(Double::longValue)
                .withBoolean(val -> val ? 1L : 0L)
                .withBigInteger(BigInteger::longValue)
                .withBigDecimal(BigDecimal::longValue)
                .withString(Long::valueOf);
    }

}
