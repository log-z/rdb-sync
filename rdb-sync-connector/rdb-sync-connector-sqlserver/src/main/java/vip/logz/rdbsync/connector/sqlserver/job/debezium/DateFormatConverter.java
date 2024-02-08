package vip.logz.rdbsync.connector.sqlserver.job.debezium;

import io.debezium.spi.converter.CustomConverter;
import io.debezium.spi.converter.RelationalColumn;
import org.apache.kafka.connect.data.SchemaBuilder;
import vip.logz.rdbsync.common.job.debezium.DebeziumConverterFallback;

import java.sql.Date;
import java.util.Properties;

/**
 * DATE格式化转换器
 *
 * @author logz
 * @date 2024-02-08
 */
public class DateFormatConverter implements CustomConverter<SchemaBuilder, RelationalColumn> {

    /** 类型：DATE */
    private static final String TYPE_DATE = "DATE";

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
            if (x instanceof Date) {
                return x.toString();
            }

            return field.isOptional() ? null : DebeziumConverterFallback.DATE;
        });
    }

}
