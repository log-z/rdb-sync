package vip.logz.rdbsync.connector.sqlserver.job.debezium;

import io.debezium.spi.converter.CustomConverter;
import io.debezium.spi.converter.RelationalColumn;
import org.apache.kafka.connect.data.SchemaBuilder;
import vip.logz.rdbsync.common.job.debezium.DebeziumConverterFallback;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
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

    /** 时间格式器 */
    private static final DateTimeFormatter FORMATTER_TIME = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .append(DateTimeFormatter.ISO_LOCAL_TIME)
            .toFormatter();

    /** 时区ID */
    private final ZoneId zoneId = ZoneId.systemDefault();

    @Override
    public void configure(Properties properties) {
    }

    @Override
    public void converterFor(RelationalColumn field, ConverterRegistration<SchemaBuilder> registration) {
        // 检查：只处理TIME类型
        if (!field.typeName().equalsIgnoreCase(TYPE_TIME)) {
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
            if (x instanceof Timestamp) {
                Timestamp timestamp = (Timestamp) x;
                ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(timestamp.toInstant(), zoneId);
                return FORMATTER_TIME.format(zonedDateTime);
            }

            // 情形2：读取日志
            if (x instanceof Time) {
                Time time = (Time) x;
                Instant instant = Instant.ofEpochMilli(time.getTime());
                LocalTime localTime = LocalTime.ofInstant(instant, zoneId);
                return localTime.format(DateTimeFormatter.ISO_LOCAL_TIME);
            }

            return field.isOptional() ? null : DebeziumConverterFallback.TIME;
        });
    }

}
