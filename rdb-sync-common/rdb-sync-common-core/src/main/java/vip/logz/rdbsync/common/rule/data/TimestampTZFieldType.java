package vip.logz.rdbsync.common.rule.data;

import vip.logz.rdbsync.common.rule.Rdb;
import vip.logz.rdbsync.common.rule.convert.ConverterRegistrar;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;

/**
 * TIMESTAMP（有时区）字段类型
 *
 * @author logz
 * @date 2024-03-21
 */
public class TimestampTZFieldType<DB extends Rdb> extends AbstractFieldType<DB, OffsetDateTime> {

    /** SQL有时区日期时间格式器 */
    private static final DateTimeFormatter SQL_OFFSET_DATE_TIME_FORMATTER = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .append(DateTimeFormatter.ISO_LOCAL_DATE)
            .appendLiteral(' ')
            .append(DateTimeFormatter.ISO_OFFSET_TIME)
            .toFormatter();

    /**
     * 构造器
     * @param name 具体名称
     * @param args 参数列表
     */
    public TimestampTZFieldType(String name, Object... args) {
        super(name, args);
    }

    /**
     * 初始化配置
     * @param converterRegistrar 转换器注册器
     */
    @Override
    public void config(ConverterRegistrar<OffsetDateTime> converterRegistrar) {
        converterRegistrar.withString(x -> {
            try {
                return OffsetDateTime.parse(x, SQL_OFFSET_DATE_TIME_FORMATTER);
            } catch (DateTimeParseException ignored) {
            }

            return OffsetDateTime.parse(x, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        });
    }

}
