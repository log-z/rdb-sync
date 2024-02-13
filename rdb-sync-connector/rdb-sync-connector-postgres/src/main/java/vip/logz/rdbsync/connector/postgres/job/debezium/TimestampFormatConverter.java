package vip.logz.rdbsync.connector.postgres.job.debezium;

import io.debezium.spi.converter.CustomConverter;
import io.debezium.spi.converter.RelationalColumn;
import org.apache.kafka.connect.data.SchemaBuilder;
import vip.logz.rdbsync.common.job.debezium.DebeziumConverterFallback;

import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.List;
import java.util.Properties;

/**
 * TIMESTAMP格式化转换器
 *
 * @author logz
 * @date 2024-02-08
 */
public class TimestampFormatConverter implements CustomConverter<SchemaBuilder, RelationalColumn> {

    /** 类型：TIMESTAMP */
    private static final String TYPE_TIMESTAMP = "TIMESTAMP";

    /** 类型：TIMESTAMPTZ */
    private static final String TYPE_TIMESTAMPTZ = "TIMESTAMPTZ";

    /** 支持的类型 */
    private static final List<String> SUPPORTED_TYPES = List.of(TYPE_TIMESTAMP, TYPE_TIMESTAMPTZ);

    /** 日期时间格式器：有时区 */
    private static final DateTimeFormatter FORMATTER_ZONED_DATETIME = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .append(DateTimeFormatter.ISO_LOCAL_DATE)
            .appendLiteral(' ')
            .append(DateTimeFormatter.ISO_TIME)
            .toFormatter();

    /** 日期时间格式器：无时区 */
    private static final DateTimeFormatter FORMATTER_LOCAL_DATETIME = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .append(DateTimeFormatter.ISO_LOCAL_DATE)
            .appendLiteral(' ')
            .append(DateTimeFormatter.ISO_LOCAL_TIME)
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
            if (x instanceof Timestamp) {
                Timestamp timestamp = (Timestamp) x;

                if (field.typeName().equalsIgnoreCase(TYPE_TIMESTAMPTZ)) {
                    // 情形1.1：TIMESTAMPTZ
                    ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(timestamp.toInstant(), zoneId);
                    return FORMATTER_ZONED_DATETIME.format(zonedDateTime);
                } else {
                    // 情形1.2：TIMESTAMP
                    return timestamp.toString();
                }
            }

            // 情形2：读取复制流
            if (x instanceof OffsetDateTime) {
                // 情形2.1：TIMESTAMPTZ
                return FORMATTER_ZONED_DATETIME.format((OffsetDateTime) x);
            }
            if (x instanceof Instant) {
                // 情形2.2：TIMESTAMP
                LocalDateTime localDateTime = LocalDateTime.ofInstant((Instant) x, ZoneOffset.UTC);
                return FORMATTER_LOCAL_DATETIME.format(localDateTime);
            }

            return field.isOptional() ? null : DebeziumConverterFallback.DATETIME;
        });
    }

}
