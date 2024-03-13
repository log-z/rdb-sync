package vip.logz.rdbsync.common.config;

import org.apache.flink.configuration.ConfigOption;
import org.apache.flink.configuration.ConfigOptions;
import org.apache.flink.configuration.Configuration;
import vip.logz.rdbsync.common.utils.Configurations;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 管道属性
 *
 * @author logz
 * @date 2024-03-10
 */
public abstract class PipelineProperties {

    /** 属性定义：ID */
    public static final ConfigOption<String> ID = ConfigOptions.key("id")
            .stringType()
            .noDefaultValue();

    /** 属性定义：名称 */
    public static final ConfigOption<String> NAME = ConfigOptions.key("name")
            .stringType()
            .noDefaultValue();

    /** 属性定义：协议 */
    public static final ConfigOption<String> PROTOCOL = ConfigOptions.key("protocol")
            .stringType()
            .noDefaultValue();

    /** 属性定义：安全性 - 需要脱敏的属性名 */
    public static final ConfigOption<List<String>> SECURITY_DESENSITIZE_KEYS = ConfigOptions.key("security.desensitize-keys")
            .stringType()
            .asList()
            .defaultValues("username", "password");

    /** 属性的存储介质 */
    private final Configuration conf;

    /**
     * 构造器
     */
    public PipelineProperties() {
        this(Collections.emptyMap());
    }

    /**
     * 构造器
     * @param props 初始属性
     */
    public PipelineProperties(Map<String, ?> props) {
        conf = Configurations.parse(props);
    }

    /**
     * 获取属性值
     * @param option 属性定义
     */
    public <T> T get(ConfigOption<T> option) {
        return conf.get(option);
    }

    /**
     * 获取属性值的Optional
     * @param option 属性定义
     */
    public <T> Optional<T> getOptional(ConfigOption<T> option) {
        Optional<T> optional = conf.getOptional(option);
        return optional.isPresent() ?
                optional :
                Optional.ofNullable(option.defaultValue());
    }

    /**
     * 设置属性值
     * @param option 属性定义
     * @param value 属性值
     */
    public <T> void set(ConfigOption<T> option, T value) {
        if (value != null) {
            conf.set(option, value);
        }
    }

    /**
     * 设置已格式化的属性值，例如列表等
     * @param option 属性定义
     * @param formattedValue 已格式化的属性值
     */
    public <T> void setString(ConfigOption<T> option, String formattedValue) {
        if (formattedValue != null) {
            conf.setString(option.key(), formattedValue);
        }
    }

    /**
     * 获取属性定义：安全性 - 需要脱敏的属性名
     */
    protected ConfigOption<List<String>> configOptionWithDesensitizeKeys() {
        return SECURITY_DESENSITIZE_KEYS;
    }

    /**
     * 输出描述信息，同时进行脱敏
     */
    @Override
    public String toString() {
        Map<String, String> map = conf.toMap();
        map.remove(SECURITY_DESENSITIZE_KEYS.key());
        for (String desensitizeKey : get(configOptionWithDesensitizeKeys())) {
            if (map.containsKey(desensitizeKey)) {
                map.put(desensitizeKey, "****");
            }
        }

        return map.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> entry.getKey() + " = " + entry.getValue())
                .collect(Collectors.joining("\n"));
    }

    /**
     * 转换为配置
     */
    public Configuration toConfiguration() {
        return new Configuration(conf);
    }

}
