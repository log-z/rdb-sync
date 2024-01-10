package vip.logz.rdbsync.common.job.debezium;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ververica.cdc.debezium.DebeziumDeserializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.typeutils.GenericTypeInfo;
import org.apache.flink.util.Collector;
import org.apache.kafka.connect.json.DecimalFormat;
import org.apache.kafka.connect.json.JsonConverter;
import org.apache.kafka.connect.json.JsonConverterConfig;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.storage.ConverterConfig;
import org.apache.kafka.connect.storage.ConverterType;
import vip.logz.rdbsync.common.utils.JacksonUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 简单Debezium事件反序列化模式
 *
 * @author logz
 * @date 2024-01-09
 * @see com.ververica.cdc.debezium.JsonDebeziumDeserializationSchema
 */
public class SimpleDebeziumDeserializationSchema implements DebeziumDeserializationSchema<DebeziumEvent> {

    private static final long serialVersionUID = 1L;

    /** JSON转换器 */
    private transient JsonConverter jsonConverter;

    /**
     * Configuration whether to enable {@link JsonConverterConfig#SCHEMAS_ENABLE_CONFIG} to include
     * schema in messages.
     */
    private final Boolean includeSchema;

    /** The custom configurations for {@link JsonConverter}. */
    private Map<String, Object> customConverterConfigs;

    /** 对象转换器 */
    private final ObjectMapper objectMapper = JacksonUtils.createInstance();

    /**
     * 构造器
     */
    public SimpleDebeziumDeserializationSchema() {
        this(false);
    }

    /**
     * 构造器
     * @param includeSchema 详见 {@link #includeSchema}
     */
    public SimpleDebeziumDeserializationSchema(Boolean includeSchema) {
        this.includeSchema = includeSchema;
    }

    /**
     * 构造器
     * @param includeSchema 详见 {@link #includeSchema}
     * @param customConverterConfigs 详见 {@link #customConverterConfigs}
     */
    public SimpleDebeziumDeserializationSchema(
            Boolean includeSchema, Map<String, Object> customConverterConfigs) {
        this.includeSchema = includeSchema;
        this.customConverterConfigs = customConverterConfigs;
    }

    /**
     * 反序列化
     * @param record 记录
     * @param out 输出
     * @throws Exception 详见 {@link DebeziumDeserializationSchema#deserialize(SourceRecord, Collector)}
     */
    @Override
    public void deserialize(SourceRecord record, Collector<DebeziumEvent> out) throws Exception {
        // 初始化JSON转换器
        if (jsonConverter == null) {
            initializeJsonConverter();
        }

        // 序列化事件
        String json = new String(
                jsonConverter.fromConnectData(record.topic(), record.valueSchema(), record.value())
        );

        // 反序列化事件
        DebeziumEvent event = objectMapper.readValue(json, DebeziumEvent.class);
        out.collect(event);
    }

    /**
     * @see com.ververica.cdc.debezium.JsonDebeziumDeserializationSchema
     */
    private void initializeJsonConverter() {
        jsonConverter = new JsonConverter();
        final HashMap<String, Object> configs = new HashMap<>(2);
        configs.put(ConverterConfig.TYPE_CONFIG, ConverterType.VALUE.getName());
        configs.put(JsonConverterConfig.SCHEMAS_ENABLE_CONFIG, includeSchema);
        configs.put(JsonConverterConfig.DECIMAL_FORMAT_CONFIG, DecimalFormat.NUMERIC.name());
        if (customConverterConfigs != null) {
            configs.putAll(customConverterConfigs);
        }
        jsonConverter.configure(configs);
    }

    @Override
    public TypeInformation<DebeziumEvent> getProducedType() {
        return new GenericTypeInfo<>(DebeziumEvent.class);
    }

}
