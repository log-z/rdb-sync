package vip.logz.rdbsync.connector.sqlserver.config;

import org.apache.flink.configuration.ConfigOption;
import org.apache.flink.configuration.ConfigOptions;
import vip.logz.rdbsync.common.config.PipelineProperties;
import vip.logz.rdbsync.connector.jdbc.config.JdbcPipelineDistProperties;

import java.util.List;
import java.util.Map;

/**
 * SQLServer管道目标属性
 *
 * @author logz
 * @date 2024-01-27
 */
public class SqlserverPipelineDistProperties extends JdbcPipelineDistProperties {

    /** 属性定义：端口 */
    public static final ConfigOption<Integer> PORT = ConfigOptions.key("port")
            .intType()
            .defaultValue(SqlserverOptions.DEFAULT_PORT);

    /** 属性定义：模式名 */
    public static final ConfigOption<String> SCHEMA_NAME = ConfigOptions.key("schema-name")
            .stringType()
            .defaultValue(SqlserverOptions.DEFAULT_SCHEMA);

    /** 属性定义：用户名 */
    public static final ConfigOption<String> USERNAME = ConfigOptions.key("username")
            .stringType()
            .defaultValue(SqlserverOptions.DEFAULT_USERNAME);

    /** 属性定义：密码 */
    public static final ConfigOption<String> PASSWORD = ConfigOptions.key("password")
            .stringType()
            .noDefaultValue();

    /** 属性定义：安全性 - 敏感属性的键名列表 */
    public static final ConfigOption<List<String>> SECURITY_SENSITIVE_KEYS = ConfigOptions
            .key(PipelineProperties.SECURITY_SENSITIVE_KEYS.key())
            .stringType()
            .asList()
            .defaultValues(USERNAME.key(), PASSWORD.key());

    /**
     * 构造器
     */
    public SqlserverPipelineDistProperties() {
    }

    /**
     * 构造器
     * @param props 初始属性
     */
    public SqlserverPipelineDistProperties(Map<String, ?> props) {
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
