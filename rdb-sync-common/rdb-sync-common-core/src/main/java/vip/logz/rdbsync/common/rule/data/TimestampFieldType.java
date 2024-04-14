package vip.logz.rdbsync.common.rule.data;

import vip.logz.rdbsync.common.rule.Rdb;
import vip.logz.rdbsync.common.rule.convert.ConverterRegistrar;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;

/**
 * TIMESTAMP（无时区）字段类型
 *
 * @author logz
 * @date 2024-03-21
 */
public class TimestampFieldType<DB extends Rdb> extends AbstractFieldType<DB, LocalDateTime> {

    /** SQL本地日期时间格式器 */
    private static final DateTimeFormatter SQL_LOCAL_DATE_TIME_FORMATTER = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .append(DateTimeFormatter.ISO_LOCAL_DATE)
            .appendLiteral(' ')
            .append(DateTimeFormatter.ISO_LOCAL_TIME)
            .toFormatter();

    /**
     * 构造器
     * @param name 具体名称
     * @param args 参数列表
     */
    public TimestampFieldType(String name, Object... args) {
        super(name, args);
    }

    /**
     * 初始化配置
     * @param converterRegistrar 转换器注册器
     */
    @Override
    public void config(ConverterRegistrar<LocalDateTime> converterRegistrar) {
        converterRegistrar.withString(x -> {
            try {
                return LocalDateTime.parse(x, SQL_LOCAL_DATE_TIME_FORMATTER);
            } catch (DateTimeParseException ignored) {
            }

            return LocalDateTime.parse(x, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        });
    }

}
