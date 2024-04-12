package vip.logz.rdbsync.connector.oracle.config;

import org.apache.flink.configuration.ConfigOption;
import org.apache.flink.configuration.ConfigOptions;
import org.apache.flink.connector.jdbc.table.JdbcConnectorOptions;
import vip.logz.rdbsync.common.config.PipelineProperties;
import vip.logz.rdbsync.connector.jdbc.config.JdbcPipelineDistProperties;

import java.util.List;
import java.util.Map;

/**
 * Oracle管道目标属性
 *
 * @author logz
 * @date 2024-03-18
 */
public class OraclePipelineDistProperties extends JdbcPipelineDistProperties {

    /** 属性定义：端口 */
    public static final ConfigOption<Integer> PORT = ConfigOptions.key("port")
            .intType()
            .defaultValue(1521);

    /** 属性定义：数据库名 */
    public static final ConfigOption<String> DATABASE_NAME = ConfigOptions.key("database-name")
            .stringType()
            .defaultValue("ORCLCDB");

    /** 属性定义：安全性 - 敏感属性的键名列表 */
    public static final ConfigOption<List<String>> SECURITY_SENSITIVE_KEYS = ConfigOptions
            .key(PipelineProperties.SECURITY_SENSITIVE_KEYS.key())
            .stringType()
            .asList()
            .defaultValues(JdbcConnectorOptions.USERNAME.key(), JdbcConnectorOptions.PASSWORD.key());

    /**
     * 构造器
     */
    public OraclePipelineDistProperties() {
    }

    /**
     * 构造器
     * @param props 初始属性
     */
    public OraclePipelineDistProperties(Map<String, ?> props) {
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
