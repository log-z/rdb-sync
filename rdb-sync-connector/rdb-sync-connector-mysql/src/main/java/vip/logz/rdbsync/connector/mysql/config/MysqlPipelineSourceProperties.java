package vip.logz.rdbsync.connector.mysql.config;

import com.ververica.cdc.connectors.mysql.source.config.MySqlSourceOptions;
import org.apache.flink.configuration.ConfigOption;
import org.apache.flink.configuration.ConfigOptions;
import vip.logz.rdbsync.common.config.PipelineProperties;
import vip.logz.rdbsync.common.config.PipelineSourceProperties;

import java.util.List;
import java.util.Map;

/**
 * MySQL管道来源属性
 *
 * @author logz
 * @date 2024-01-09
 */
public class MysqlPipelineSourceProperties extends PipelineSourceProperties {

    /** 属性定义：主机名 */
    public static final ConfigOption<String> HOSTNAME = ConfigOptions.key("hostname")
            .stringType()
            .defaultValue(MysqlOptions.DEFAULT_HOST);

    /** 属性定义：用户名 */
    public static final ConfigOption<String> USERNAME = ConfigOptions.key("username")
            .stringType()
            .defaultValue(MysqlOptions.DEFAULT_USERNAME);

    /** 属性定义：密码 */
    public static final ConfigOption<String> PASSWORD = ConfigOptions.key("password")
            .stringType()
            .defaultValue(MysqlOptions.DEFAULT_PASSWORD);

    /** 属性定义：JDBC属性 */
    public static final ConfigOption<String> JDBC_PROPS = ConfigOptions.key("jdbc-props")
            .stringType()
            .noDefaultValue();

    /** 属性定义：安全性 - 需要脱敏的属性名 */
    public static final ConfigOption<List<String>> SECURITY_DESENSITIZE_KEYS = ConfigOptions
            .key(PipelineProperties.SECURITY_DESENSITIZE_KEYS.key())
            .stringType()
            .asList()
            .defaultValues(MySqlSourceOptions.USERNAME.key(), MySqlSourceOptions.PASSWORD.key());

    /**
     * 启动模式
     */
    public static class StartupMode {

        /** 先做快照，再读取最新日志 */
        public static final String INITIAL = "initial";

        /** 跳过快照，从最早可用位置读取日志 */
        public static final String EARLIEST = "earliest-offset";

        /** 跳过快照，仅读取最新日志 */
        public static final String LATEST = "latest-offset";

        /** 跳过快照，从指定位置开始读取日志 */
        public static final String SPECIFIC_OFFSET = "specific-offset";

        /** 跳过快照，从指定时间戳开始读取日志 */
        public static final String TIMESTAMP = "timestamp-offset";

    }

    /**
     * 构造器
     */
    public MysqlPipelineSourceProperties() {
    }

    /**
     * 构造器
     * @param props 初始属性
     */
    public MysqlPipelineSourceProperties(Map<String, ?> props) {
        super(props);
    }

    /**
     * 获取属性定义：安全性 - 需要脱敏的属性名
     */
    @Override
    protected ConfigOption<List<String>> configOptionWithDesensitizeKeys() {
        return SECURITY_DESENSITIZE_KEYS;
    }

}
