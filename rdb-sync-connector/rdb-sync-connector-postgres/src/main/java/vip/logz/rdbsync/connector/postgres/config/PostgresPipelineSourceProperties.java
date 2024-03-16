package vip.logz.rdbsync.connector.postgres.config;

import com.ververica.cdc.connectors.postgres.source.config.PostgresSourceOptions;
import org.apache.flink.configuration.ConfigOption;
import org.apache.flink.configuration.ConfigOptions;
import vip.logz.rdbsync.common.config.PipelineProperties;
import vip.logz.rdbsync.common.config.PipelineSourceProperties;

import java.util.List;
import java.util.Map;

/**
 * Postgres管道来源属性
 *
 * @author logz
 * @date 2024-02-05
 */
public class PostgresPipelineSourceProperties extends PipelineSourceProperties {

    /** 属性定义：主机名 */
    public static final ConfigOption<String> HOSTNAME = ConfigOptions.key("hostname")
            .stringType()
            .defaultValue(PostgresOptions.DEFAULT_HOST);

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

    /** 属性定义：逻辑解码插件名称 */
    public static final ConfigOption<String> DECODING_PLUGIN_NAME = ConfigOptions.key("decoding.plugin.name")
            .stringType()
            .defaultValue("pgoutput");

    /** 属性定义：安全性 - 敏感属性的键名列表 */
    public static final ConfigOption<List<String>> SECURITY_SENSITIVE_KEYS = ConfigOptions
            .key(PipelineProperties.SECURITY_SENSITIVE_KEYS.key())
            .stringType()
            .asList()
            .defaultValues(PostgresSourceOptions.USERNAME.key(), PostgresSourceOptions.PASSWORD.key());

    /**
     * 启动模式
     */
    public static class StartupMode {

        /** 先做快照，再读取最新日志 */
        public static final String INITIAL = "initial";

        /** 跳过快照，仅读取最新日志 */
        public static final String LATEST = "latest-offset";

    }

    /**
     * 构造器
     */
    public PostgresPipelineSourceProperties() {
    }

    /**
     * 构造器
     * @param props 初始属性
     */
    public PostgresPipelineSourceProperties(Map<String, ?> props) {
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
