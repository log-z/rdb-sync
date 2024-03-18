package vip.logz.rdbsync.connector.postgres.config;

import org.apache.flink.configuration.ConfigOption;
import org.apache.flink.configuration.ConfigOptions;
import vip.logz.rdbsync.common.config.PipelineProperties;
import vip.logz.rdbsync.connector.jdbc.config.JdbcPipelineDistProperties;

import java.util.List;
import java.util.Map;

/**
 * Postgres管道目标属性
 *
 * @author logz
 * @date 2024-02-05
 */
public class PostgresPipelineDistProperties extends JdbcPipelineDistProperties {

    /** 属性定义：主机名列表 */
    public static final ConfigOption<List<String>> HOSTNAMES = ConfigOptions.key("hostnames")
            .stringType()
            .asList()
            .defaultValues(PostgresOptions.DEFAULT_HOST);

    /** 属性定义：端口列表 */
    public static final ConfigOption<List<Integer>> PORTS = ConfigOptions.key("ports")
            .intType()
            .asList()
            .defaultValues(PostgresOptions.DEFAULT_PORT);

    /** 属性定义：模式名 */
    public static final ConfigOption<String> SCHEMA_NAME = ConfigOptions.key("schema-name")
            .stringType()
            .defaultValue(PostgresOptions.DEFAULT_SCHEMA);

    /** 属性定义：用户名 */
    public static final ConfigOption<String> USERNAME = ConfigOptions.key("username")
            .stringType()
            .defaultValue(PostgresOptions.DEFAULT_USERNAME);

    /** 属性定义：密码 */
    public static final ConfigOption<String> PASSWORD = ConfigOptions.key("password")
            .stringType()
            .defaultValue(PostgresOptions.DEFAULT_PASSWORD);

    /** 属性定义：安全性 - 敏感属性的键名列表 */
    public static final ConfigOption<List<String>> SECURITY_SENSITIVE_KEYS = ConfigOptions
            .key(PipelineProperties.SECURITY_SENSITIVE_KEYS.key())
            .stringType()
            .asList()
            .defaultValues(USERNAME.key(), PASSWORD.key());

    /**
     * 构造器
     */
    public PostgresPipelineDistProperties() {
    }

    /**
     * 构造器
     * @param props 初始属性
     */
    public PostgresPipelineDistProperties(Map<String, ?> props) {
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
