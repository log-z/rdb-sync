package vip.logz.rdbsync.connector.oracle.config;

import com.ververica.cdc.connectors.base.options.JdbcSourceOptions;
import org.apache.flink.configuration.ConfigOption;
import org.apache.flink.configuration.ConfigOptions;
import vip.logz.rdbsync.common.config.PipelineProperties;
import vip.logz.rdbsync.common.config.PipelineSourceProperties;

import java.util.List;
import java.util.Map;

/**
 * Oracle管道来源属性
 *
 * @author logz
 * @date 2024-03-18
 */
public class OraclePipelineSourceProperties extends PipelineSourceProperties {

    /** 属性定义：主机名 */
    public static final ConfigOption<String> HOSTNAME = ConfigOptions.key("hostname")
            .stringType()
            .defaultValue("localhost");

    /** 属性定义：数据库名 */
    public static final ConfigOption<String> DATABASE_NAME = ConfigOptions.key("database-name")
            .stringType()
            .defaultValue("ORCLCDB");

    /** 属性定义：Debezium - 指定一个 PDB 名称 */
    public static final ConfigOption<String> DEBEZIUM_DATABASE_PDB_NAME = ConfigOptions.key("debezium.database.pdb.name")
            .stringType()
            .noDefaultValue();

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

        /** 跳过快照，仅读取最新日志 */
        public static final String LATEST = "latest-offset";

    }

    /**
     * 构造器
     */
    public OraclePipelineSourceProperties() {
    }

    /**
     * 构造器
     * @param props 初始属性
     */
    public OraclePipelineSourceProperties(Map<String, ?> props) {
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
