package vip.logz.rdbsync.connector.jdbc.config;

import org.apache.flink.configuration.ConfigOption;
import org.apache.flink.configuration.ConfigOptions;
import vip.logz.rdbsync.common.config.PipelineDistProperties;
import vip.logz.rdbsync.common.config.SemanticOptions;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;

/**
 * JDBC管道目标属性实体
 *
 * @author logz
 * @date 2024-01-09
 */
public abstract class JdbcPipelineDistProperties extends PipelineDistProperties {

    /** 属性定义：主机名 */
    public static final ConfigOption<String> HOSTNAME = ConfigOptions.key("hostname")
            .stringType()
            .defaultValue("localhost");

    /** 属性定义：数据库名 */
    public static final ConfigOption<String> DATABASE_NAME = ConfigOptions.key("database-name")
            .stringType()
            .noDefaultValue();

    /** 属性定义：模式名 */
    public static final ConfigOption<String> SCHEMA_NAME = ConfigOptions.key("schema-name")
            .stringType()
            .noDefaultValue();

    /** 属性定义：语义保证 */
    public static final ConfigOption<String> SINK_SEMANTIC = ConfigOptions.key("sink.semantic")
            .stringType()
            .defaultValue(SemanticOptions.AT_LEAST_ONCE);

    /**
     * 属性定义：XA事务提交的尝试次数
     * @see org.apache.flink.connector.jdbc.JdbcExactlyOnceOptions.JDBCExactlyOnceOptionsBuilder#withMaxCommitAttempts(int)
     */
    public static final ConfigOption<Integer> SINK_XA_MAX_COMMIT_ATTEMPTS = ConfigOptions.key("sink.xa.max-commit-attempts")
            .intType()
            .defaultValue(3);

    /**
     * 属性定义：XA事务超时秒数
     * @see org.apache.flink.connector.jdbc.JdbcExactlyOnceOptions.JDBCExactlyOnceOptionsBuilder#withTimeoutSec(Optional)
     */
    public static final ConfigOption<Duration> SINK_XA_TIMEOUT = ConfigOptions.key("sink.xa.timeout")
            .durationType()
            .defaultValue(Duration.ofSeconds(30));

    /**
     * 属性定义：同一连接是否可以用于多个XA事务
     * @see org.apache.flink.connector.jdbc.JdbcExactlyOnceOptions.JDBCExactlyOnceOptionsBuilder#withTransactionPerConnection(boolean)
     */
    public static final ConfigOption<Boolean> SINK_XA_TRANSACTION_PER_CONNECTION = ConfigOptions
            .key("sink.xa.transaction-per-connection")
            .booleanType()
            .defaultValue(false);

    /**
     * 构造器
     */
    public JdbcPipelineDistProperties() {
    }

    /**
     * 构造器
     * @param props 初始属性
     */
    public JdbcPipelineDistProperties(Map<String, ?> props) {
        super(props);
    }

}
