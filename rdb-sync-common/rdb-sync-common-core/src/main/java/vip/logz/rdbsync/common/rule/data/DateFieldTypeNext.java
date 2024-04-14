package vip.logz.rdbsync.common.rule.data;

import vip.logz.rdbsync.common.rule.Rdb;
import vip.logz.rdbsync.common.rule.convert.ConverterRegistrar;

import java.time.LocalDate;

/**
 * DATE字段类型
 *
 * @author logz
 * @date 2024-01-09
 */
public class DateFieldTypeNext<DB extends Rdb> extends AbstractFieldType<DB, LocalDate> {

    /**
     * 构造器
     * @param name 具体名称
     */
    public DateFieldTypeNext(String name) {
        super(name);
    }

    /**
     * 初始化配置
     * @param converterRegistrar 转换器注册器
     */
    @Override
    public void config(ConverterRegistrar<LocalDate> converterRegistrar) {
        converterRegistrar.withString(LocalDate::parse);
    }

}
