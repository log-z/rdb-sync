package vip.logz.rdbsync.connector.sqlserver.config;

import com.ververica.cdc.connectors.base.options.JdbcSourceOptions;
import org.apache.flink.configuration.ConfigOption;
import org.apache.flink.configuration.ConfigOptions;
import vip.logz.rdbsync.common.config.PipelineProperties;
import vip.logz.rdbsync.common.config.PipelineSourceProperties;

import java.util.List;
import java.util.Map;

/**
 * SQLServer管道来源属性
 *
 * @author logz
 * @date 2024-01-27
 */
public class SqlserverPipelineSourceProperties extends PipelineSourceProperties {

    /** 属性定义：主机名 */
    public static final ConfigOption<String> HOSTNAME = ConfigOptions.key("hostname")
            .stringType()
            .defaultValue(SqlserverOptions.DEFAULT_HOST);

    /** 属性定义：端口 */
    public static final ConfigOption<Integer> PORT = ConfigOptions.key("port")
            .intType()
            .defaultValue(SqlserverOptions.DEFAULT_PORT)
            .withDescription("Integer port number of the SQLServer database server.");

    /** 属性定义：模式名 */
    public static final ConfigOption<String> SCHEMA_NAME = ConfigOptions.key("schema-name")
            .stringType()
            .defaultValue(SqlserverOptions.DEFAULT_SCHEMA);

    /** 属性定义：用户名 */
    public static final ConfigOption<String> USERNAME = ConfigOptions.key("username")
            .stringType()
            .defaultValue(SqlserverOptions.DEFAULT_USERNAME);

    /** 属性定义：安全性 - 敏感属性的键名列表 */
    public static final ConfigOption<List<String>> SECURITY_SENSITIVE_KEYS = ConfigOptions
            .key(PipelineProperties.SECURITY_SENSITIVE_KEYS.key())
            .stringType()
            .asList()
            .defaultValues(JdbcSourceOptions.USERNAME.key(), JdbcSourceOptions.PASSWORD.key());

    /**
     * 启动模式
     */
    public static class StartupMode {

        /** 先做快照，再读取最新日志 */
        public static final String INITIAL = "initial";

        /** 只做快照，不读取日志（暂不可用） */
        public static final String INITIAL_ONLY = "initial-only";

        /** 跳过快照，仅读取最新日志 */
        public static final String LATEST = "latest-offset";

    }

    /**
     * 构造器
     */
    public SqlserverPipelineSourceProperties() {
    }

    /**
     * 构造器
     * @param props 初始属性
     */
    public SqlserverPipelineSourceProperties(Map<String, ?> props) {
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
