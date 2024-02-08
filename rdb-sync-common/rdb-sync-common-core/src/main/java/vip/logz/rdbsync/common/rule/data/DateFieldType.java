package vip.logz.rdbsync.common.rule.data;

import vip.logz.rdbsync.common.rule.Rdb;
import vip.logz.rdbsync.common.rule.convert.Converter;
import vip.logz.rdbsync.common.rule.convert.ConverterRegistrar;

/**
 * DATE字段类型
 *
 * @author logz
 * @date 2024-01-09
 */
public class DateFieldType<DB extends Rdb> extends AbstractFieldType<DB, String> {

    /**
     * 构造器
     * @param name 具体名称
     */
    public DateFieldType(String name) {
        super(name);
    }

    /**
     * 初始化配置
     * @param converterRegistrar 转换器注册器
     */
    @Override
    public void config(ConverterRegistrar<String> converterRegistrar) {
        converterRegistrar.withString(Converter::invariant);
    }

}
