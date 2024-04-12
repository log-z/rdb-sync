package vip.logz.rdbsync.connector.oracle.job.debezium;

import io.debezium.spi.converter.CustomConverter;
import io.debezium.spi.converter.RelationalColumn;
import oracle.sql.TIMESTAMP;
import org.apache.kafka.connect.data.SchemaBuilder;
import vip.logz.rdbsync.common.job.debezium.DebeziumConverterFallback;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * TIMESTAMP格式化转换器
 *
 * @author logz
 * @date 2024-03-21
 */
public class TimestampFormatConverter implements CustomConverter<SchemaBuilder, RelationalColumn> {

    /** 匹配字段类型：时间戳 */
    private static final Pattern PATTERN_TYPE_TIMESTAMP = Pattern.compile("TIMESTAMP\\(\\d+\\)");

    /** 匹配时间戳字面量，在读取日志阶段使用 */
    private static final Pattern PATTERN_TIMESTAMP_IN_LOG =
            Pattern.compile("TO_TIMESTAMP\\('(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d+)'\\)");

    @Override
    public void configure(Properties properties) {
    }

    @Override
    public void converterFor(RelationalColumn field, ConverterRegistration<SchemaBuilder> registration) {
        // 检查：只处理支持的类型
        if (!PATTERN_TYPE_TIMESTAMP.matcher(field.typeName()).matches()) {
            return;
        }

        // 定义转换后的类型
        SchemaBuilder schemaBuilder = SchemaBuilder.string();
        if (field.isOptional()) {
            schemaBuilder.optional();
        }

        // 转换逻辑
        registration.register(schemaBuilder, x -> {
            // 情形1：TIMESTAMP【快照】
            if (x instanceof TIMESTAMP) {
                return x.toString();
            }

            // 情形2：TIMESTAMP【日志】
            if (x instanceof String) {
                Matcher matcher = PATTERN_TIMESTAMP_IN_LOG.matcher((String) x);
                if (matcher.find()) {
                    return matcher.group(1);
                }
            }

            return field.isOptional() ? null : DebeziumConverterFallback.DATETIME;
        });
    }

}
