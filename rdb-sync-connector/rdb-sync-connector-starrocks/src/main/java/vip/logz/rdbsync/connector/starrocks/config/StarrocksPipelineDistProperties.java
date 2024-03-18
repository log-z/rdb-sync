package vip.logz.rdbsync.connector.starrocks.config;

import com.starrocks.connector.flink.table.sink.StarRocksSinkOptions;
import org.apache.flink.configuration.ConfigOption;
import org.apache.flink.configuration.ConfigOptions;
import vip.logz.rdbsync.common.config.PipelineDistProperties;
import vip.logz.rdbsync.common.config.PipelineProperties;

import java.util.List;
import java.util.Map;

/**
 * StarRocks管道目标属性
 *
 * @author logz
 * @date 2024-01-09
 */
public class StarrocksPipelineDistProperties extends PipelineDistProperties {

    /** 属性定义：FE-MySQL服务主机名列表 */
    public static final ConfigOption<List<String>> HOSTNAMES = ConfigOptions.key("hostnames")
            .stringType()
            .asList()
            .defaultValues(StarrocksOptions.DEFAULT_HOST);

    /** 属性定义：FE-MySQL服务端口列表 */
    public static final ConfigOption<List<Integer>> PORTS = ConfigOptions.key("ports")
            .intType()
            .asList()
            .defaultValues(StarrocksOptions.DEFAULT_PORT);

    /** 属性定义：FE-HTTP服务主机名列表 */
    public static final ConfigOption<List<String>> LOAD_HOSTNAMES = ConfigOptions.key("load-hostnames")
            .stringType()
            .asList()
            .defaultValues(StarrocksOptions.DEFAULT_HOST);

    /** 属性定义：FE-HTTP服务端口列表 */
    public static final ConfigOption<List<Integer>> LOAD_PORTS = ConfigOptions.key("load-ports")
            .intType()
            .asList()
            .defaultValues(StarrocksOptions.DEFAULT_LOAD_PORT);

    /** 属性定义：用户名 */
    public static final ConfigOption<String> USERNAME = ConfigOptions.key(StarRocksSinkOptions.USERNAME.key())
            .stringType()
            .defaultValue(StarrocksOptions.DEFAULT_USERNAME);

    /** 属性定义：密码 */
    public static final ConfigOption<String> PASSWORD = ConfigOptions.key(StarRocksSinkOptions.PASSWORD.key())
            .stringType()
            .defaultValue(StarrocksOptions.DEFAULT_PASSWORD);

    /** 属性定义：安全性 - 敏感属性的键名列表 */
    public static final ConfigOption<List<String>> SECURITY_SENSITIVE_KEYS = ConfigOptions
            .key(PipelineProperties.SECURITY_SENSITIVE_KEYS.key())
            .stringType()
            .asList()
            .defaultValues(USERNAME.key(), PASSWORD.key());

    /**
     * 构造器
     */
    public StarrocksPipelineDistProperties() {
    }

    /**
     * 构造器
     * @param props 初始属性
     */
    public StarrocksPipelineDistProperties(Map<String, ?> props) {
        super(props);
    }

    /**
     * 获取属性定义：安全性 - 敏感属性的键名列表
     */
    @Override
    protected ConfigOption<List<String>> configOptionWithSensitiveKeys() {
        return SECURITY_SENSITIVE_KEYS;
    }

}
