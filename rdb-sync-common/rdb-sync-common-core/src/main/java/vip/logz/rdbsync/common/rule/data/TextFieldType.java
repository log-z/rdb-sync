package vip.logz.rdbsync.common.rule.data;

import vip.logz.rdbsync.common.rule.Rdb;
import vip.logz.rdbsync.common.rule.convert.Converter;
import vip.logz.rdbsync.common.rule.convert.ConverterRegistrar;

import java.math.BigDecimal;

/**
 * TEXT字段类型
 *
 * @author logz
 * @date 2024-01-09
 */
public class TextFieldType<DB extends Rdb> extends AbstractFieldType<DB, String> {

    /**
     * 构造器
     * @param name 具体名称
     * @param args 参数列表
     */
    public TextFieldType(String name, Object... args) {
        super(name, args);
    }

    /**
     * 初始化配置
     * @param converterRegistrar 转换器注册器
     */
    @Override
    public void config(ConverterRegistrar<String> converterRegistrar) {
        converterRegistrar.withInt(Object::toString)
                .withLong(Object::toString)
                .withFloat(Object::toString)
                .withDouble(Object::toString)
                .withBoolean(Object::toString)
                .withBigInteger(Object::toString)
                .withBigDecimal(BigDecimal::toPlainString)
                .withString(Converter::invariant);
    }

}
