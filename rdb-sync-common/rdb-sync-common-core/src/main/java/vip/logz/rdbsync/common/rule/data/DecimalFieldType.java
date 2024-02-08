package vip.logz.rdbsync.common.rule.data;

import vip.logz.rdbsync.common.rule.Rdb;
import vip.logz.rdbsync.common.rule.convert.Converter;
import vip.logz.rdbsync.common.rule.convert.ConverterRegistrar;

import java.math.BigDecimal;

/**
 * DECIMAL字段类型
 *
 * @author logz
 * @date 2024-01-09
 */
public class DecimalFieldType<DB extends Rdb> extends AbstractFieldType<DB, BigDecimal> {

    /**
     * 构造器
     * @param name 具体名称
     * @param args 参数列表
     */
    public DecimalFieldType(String name, Object... args) {
        super(name, args);
    }

    /**
     * 初始化配置
     * @param converterRegistrar 转换器注册器
     */
    @Override
    public void config(ConverterRegistrar<BigDecimal> converterRegistrar) {
        converterRegistrar.withInt(val -> new BigDecimal(Integer.toString(val)))
                .withLong(BigDecimal::valueOf)
                .withFloat(val -> new BigDecimal(Float.toString(val)))
                .withDouble(BigDecimal::valueOf)
                .withBoolean(val -> val ? BigDecimal.ONE : BigDecimal.ZERO)
                .withBigInteger(BigDecimal::new)
                .withBigDecimal(Converter::invariant)
                .withString(BigDecimal::new);
    }

}
