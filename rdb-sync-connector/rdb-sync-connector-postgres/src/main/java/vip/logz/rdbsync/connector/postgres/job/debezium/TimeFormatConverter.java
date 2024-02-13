package vip.logz.rdbsync.connector.postgres.job.debezium;

import io.debezium.spi.converter.CustomConverter;
import io.debezium.spi.converter.RelationalColumn;
import org.apache.kafka.connect.data.SchemaBuilder;
import vip.logz.rdbsync.common.job.debezium.DebeziumConverterFallback;

import java.sql.Time;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.List;
import java.util.Properties;

/**
 * TIME格式化转换器
 *
 * @author logz
 * @date 2024-02-08
 */
public class TimeFormatConverter implements CustomConverter<SchemaBuilder, RelationalColumn> {

    /** 类型：TIME */
    private static final String TYPE_TIME = "TIME";

    /** 类型：TIMETZ */
    private static final String TYPE_TIMETZ = "TIMETZ";

    /** 支持的类型 */
    private static final List<String> SUPPORTED_TYPES = List.of(TYPE_TIME, TYPE_TIMETZ);

    /** 时间格式器：有时区 */
    private static final DateTimeFormatter FORMATTER_ZONED_TIME = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .append(DateTimeFormatter.ISO_TIME)
            .toFormatter();

    /** 时区ID */
    private final ZoneId zoneId = ZoneId.systemDefault();

    @Override
    public void configure(Properties properties) {
    }

    @Override
    public void converterFor(RelationalColumn field, ConverterRegistration<SchemaBuilder> registration) {
        // 检查：只处理支持的类型
        if (!SUPPORTED_TYPES.contains(field.typeName().toUpperCase())) {
            return;
        }

        // 定义转换后的类型
        SchemaBuilder schemaBuilder = SchemaBuilder.string();
        if (field.isOptional()) {
            schemaBuilder.optional();
        }

        // 转换逻辑
        registration.register(schemaBuilder, x -> {
            // 情形1：读取快照
            if (x instanceof Time) {
                Time time = (Time) x;
                Instant instant = Instant.ofEpochMilli(time.getTime());

                if (field.typeName().equalsIgnoreCase(TYPE_TIMETZ)) {
                    // 情形1.1：TIMETZ
                    ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(instant, zoneId);
                    return zonedDateTime.format(DateTimeFormatter.ISO_TIME);
                } else {
                    // 情形1.2：TIME
                    LocalTime localTime = LocalTime.ofInstant(instant, zoneId);
                    return localTime.format(DateTimeFormatter.ISO_LOCAL_TIME);
                }
            }

            // 情形2：读取复制流
            if (x instanceof OffsetTime) {
                // 情形2.1：TIMETZ
                return FORMATTER_ZONED_TIME.format((OffsetTime) x);
            }
            if (x instanceof String) {
                // 情形2.2：TIME
                return x.toString();
            }

            return field.isOptional() ? null : DebeziumConverterFallback.TIME;
        });
    }

}
