package vip.logz.rdbsync.common.rule.data;

import vip.logz.rdbsync.common.rule.Rdb;
import vip.logz.rdbsync.common.rule.convert.Converter;
import vip.logz.rdbsync.common.rule.convert.ConverterRegistrar;
import vip.logz.rdbsync.common.utils.DatetimeUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * DATE字段类型
 *
 * @author logz
 * @date 2024-01-09
 */
public class DateFieldType<DB extends Rdb> extends AbstractFieldType<DB, String> {

    private static final long serialVersionUID = 1L;

    /** 具体名称 */
    private final String name;

    /**
     * 构造器
     * @param name 具体名称
     */
    public DateFieldType(String name) {
        super();
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
        converterRegistrar.withInt(DateFieldType::convert)
                .withLong(DateFieldType::convert)
                .withBigInteger(val -> convert(val.longValue()))
                .withBigDecimal(val -> convert(val.longValue()))
                .withString(Converter::invariant);
    }

    /**
     * 从 {@link Integer} 类型转换
     * @param timestamp 时间戳
     * @return 返回格式化的日期文本
     */
    private static String convert(Integer timestamp) {
        return convert(timestamp.longValue());
    }

    /**
     * 从 {@link Long} 类型转换
     * @param timestamp 时间戳
     * @return 返回格式化的日期文本
     */
    private static String convert(Long timestamp) {
        LocalDateTime dateTime = DatetimeUtils.ofUtc(timestamp);
        return convert(dateTime);
    }

    /**
     * 从 {@link LocalDateTime} 类型转换
     * @param localDateTime 日期时间
     * @return 返回格式化的日期文本
     */
    private static String convert(LocalDateTime localDateTime) {
        return localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

}
