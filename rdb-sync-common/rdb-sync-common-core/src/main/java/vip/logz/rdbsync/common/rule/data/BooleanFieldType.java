package vip.logz.rdbsync.common.rule.data;

import vip.logz.rdbsync.common.rule.Rdb;
import vip.logz.rdbsync.common.rule.convert.Converter;
import vip.logz.rdbsync.common.rule.convert.ConverterRegistrar;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * BOOLEAN字段类型
 *
 * @author logz
 * @date 2024-02-06
 */
public class BooleanFieldType<DB extends Rdb> extends AbstractFieldType<DB, Boolean> {

    /**
     * 构造器
     * @param name 具体名称
     * @param args 参数列表
     */
    public BooleanFieldType(String name, Object... args) {
        super(name, args);
    }

    /**
     * 初始化配置
     * @param converterRegistrar 转换器注册器
     */
    @Override
    public void config(ConverterRegistrar<Boolean> converterRegistrar) {
        converterRegistrar.withInt(x -> x != 0)
                .withLong(val -> val != 0L)
                .withFloat(val -> val != 0.0f)
                .withDouble(val -> val != 0.0d)
                .withBoolean(Converter::invariant)
                .withBigInteger(val -> !val.equals(BigInteger.ZERO))
                .withBigDecimal(val -> val.compareTo(BigDecimal.ZERO) != 0)
                .withString(val -> val.equals(Boolean.TRUE.toString()));
    }

}
