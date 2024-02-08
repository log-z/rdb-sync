package vip.logz.rdbsync.connector.mysql.job.debezium;

import io.debezium.spi.converter.CustomConverter;
import io.debezium.spi.converter.RelationalColumn;
import org.apache.kafka.connect.data.SchemaBuilder;
import vip.logz.rdbsync.common.job.debezium.DebeziumConverterFallback;

import java.time.Duration;
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

    /** 时间格式化 */
    private static final String FORMATTER_TIME = "%02d:%02d:%02d.%09d";

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
            if (x instanceof Duration) {
                Duration duration = (Duration) x;
                return String.format(
                        FORMATTER_TIME,
                        duration.toHours(),
                        duration.toMinutesPart(),
                        duration.toSecondsPart(),
                        duration.toNanosPart()
                );
            }

            return field.isOptional() ? null : DebeziumConverterFallback.TIME;
        });
    }

}
