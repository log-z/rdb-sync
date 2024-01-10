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

    private static final long serialVersionUID = 1L;

    /** 具体名称 */
    private final String name;

    /**
     * 构造器
     * @param name 具体名称
     * @param args 参数列表
     */
    public TextFieldType(String name, Object... args) {
        super(args);
        this.name = name;
    }

    /**
     * 返回具体名称
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * 初始化配置
     * @param converterRegistrar 转换器注册器
     */
    @Override
    public void config(ConverterRegistrar<String> converterRegistrar) {
        converterRegistrar.withInt(Converter::toString)
                .withLong(Converter::toString)
                .withFloat(Converter::toString)
                .withDouble(Converter::toString)
                .withBoolean(Converter::toString)
                .withBigInteger(Converter::toString)
                .withBigDecimal(BigDecimal::toPlainString)
                .withString(Converter::invariant);
    }

}
