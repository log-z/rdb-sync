package vip.logz.rdbsync.connector.sqlserver.job.debezium;

import io.debezium.spi.converter.CustomConverter;
import io.debezium.spi.converter.RelationalColumn;
import microsoft.sql.DateTimeOffset;
import org.apache.kafka.connect.data.SchemaBuilder;
import vip.logz.rdbsync.common.job.debezium.DebeziumConverterFallback;

import java.sql.Timestamp;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

/**
 * DATETIME格式化转换器
 *
 * @author logz
 * @date 2024-02-08
 */
public class DatetimeFormatConverter implements CustomConverter<SchemaBuilder, RelationalColumn> {

    /** 支持的类型 */
    private static final List<String> SUPPORTED_TYPES = List.of(
            "DATETIME", "DATETIME2", "SMALLDATETIME", "DATETIMEOFFSET"
    );

    /** 用于匹配 {@link DateTimeOffset#toString()} 输出的时间与时区偏移之间多余的空格 */
    private static final Pattern PATTERN_DATETIMEOFFSET_TAINT = Pattern.compile(" (?=[+-])");

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
            // 情形1：TYPE_DATETIME, TYPE_DATETIME2, TYPE_SMALLDATETIME
            if (x instanceof Timestamp) {
                Timestamp timestamp = (Timestamp) x;
                return timestamp.toString();
            }

            // 情形2：DATETIMEOFFSET
            if (x instanceof DateTimeOffset) {
                String dateTimeOffset = x.toString();
                return PATTERN_DATETIMEOFFSET_TAINT.matcher(dateTimeOffset).replaceFirst("");
            }

            return field.isOptional() ? null : DebeziumConverterFallback.DATETIME;
        });
    }

}
