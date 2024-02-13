package vip.logz.rdbsync.connector.mysql.job.debezium;

import io.debezium.spi.converter.CustomConverter;
import io.debezium.spi.converter.RelationalColumn;
import org.apache.kafka.connect.data.SchemaBuilder;
import vip.logz.rdbsync.common.job.debezium.DebeziumConverterFallback;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.List;
import java.util.Properties;

/**
 * DATETIME格式化转换器
 *
 * @author logz
 * @date 2024-02-08
 */
public class DatetimeFormatConverter implements CustomConverter<SchemaBuilder, RelationalColumn> {

    /** 支持的类型 */
    private static final List<String> SUPPORTED_TYPES = List.of("DATETIME", "TIMESTAMP");

    /** 日期时间格式器 */
    private static final DateTimeFormatter FORMATTER_DATETIME = new DateTimeFormatterBuilder()
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
            // 情形1：DATETIME【快照】，TIMESTAMP【快照】，TIMESTAMP(fsp)【快照】
            if (x instanceof Timestamp) {
                Timestamp timestamp = (Timestamp) x;
                return timestamp.toString();
            }

            // 情形2：DATETIME【日志】，DATETIME(fsp)【快照/日志】
            if (x instanceof LocalDateTime) {
                LocalDateTime localDateTime = (LocalDateTime) x;
                return FORMATTER_DATETIME.format(localDateTime);
            }

            // 情形3：TIMESTAMP【日志】，TIMESTAMP(fsp)【日志】
            if (x instanceof ZonedDateTime) {
                ZonedDateTime zonedDateTime = ((ZonedDateTime) x).withZoneSameInstant(zoneId);
                return FORMATTER_DATETIME.format(zonedDateTime);
            }

            return field.isOptional() ? null : DebeziumConverterFallback.DATETIME;
        });
    }

}
