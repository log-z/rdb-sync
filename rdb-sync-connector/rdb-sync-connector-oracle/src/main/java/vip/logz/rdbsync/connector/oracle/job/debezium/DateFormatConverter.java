package vip.logz.rdbsync.connector.oracle.job.debezium;

import io.debezium.spi.converter.CustomConverter;
import io.debezium.spi.converter.RelationalColumn;
import org.apache.kafka.connect.data.SchemaBuilder;
import vip.logz.rdbsync.common.job.debezium.DebeziumConverterFallback;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * DATE格式化转换器
 *
 * @author logz
 * @date 2024-03-20
 */
public class DateFormatConverter implements CustomConverter<SchemaBuilder, RelationalColumn> {

    /** 类型：DATE */
    private static final String TYPE_DATE = "DATE";

    /** 匹配日期字面量，在读取日志阶段使用 */
    private static final Pattern PATTERN_DATE_IN_LOG =
            Pattern.compile("TO_DATE\\('(\\d{4}-\\d{2}-\\d{2}) \\d{2}:\\d{2}:\\d{2}', 'YYYY-MM-DD HH24:MI:SS'\\)");

    @Override
    public void configure(Properties properties) {
    }

    @Override
    public void converterFor(RelationalColumn field, ConverterRegistration<SchemaBuilder> registration) {
        // 检查：只处理DATE类型
        if (!field.typeName().equalsIgnoreCase(TYPE_DATE)) {
            return;
        }

        // 定义转换后的类型
        SchemaBuilder schemaBuilder = SchemaBuilder.string();
        if (field.isOptional()) {
            schemaBuilder.optional();
        }

        // 转换逻辑
        registration.register(schemaBuilder, x -> {
            // 情形1：DATE【快照】
            if (x instanceof Timestamp) {
                LocalDateTime localDateTime = ((Timestamp) x).toLocalDateTime();
                return localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE);
            }

            // 情形2：DATE【日志】
            if (x instanceof String) {
                Matcher matcher = PATTERN_DATE_IN_LOG.matcher((String) x);
                if (matcher.find()) {
                    return matcher.group(1);
                }
            }

            return field.isOptional() ? null : DebeziumConverterFallback.DATE;
        });
    }

}
