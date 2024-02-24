package vip.logz.rdbsync.connector.jdbc.config;

/**
 * JDBC精确同步一次的管道目标属性
 *
 * @author logz
 * @date 2024-02-24
 */
public class JdbcExactlyOncePipelineDistProperties {

    /** 默认值：事务提交尝试次数 */
    private static final int DEFAULT_TX_MAX_COMMIT_ATTEMPTS = 3;

    /** 事务提交尝试次数 */
    private Integer txMaxCommitAttempts;

    /** 事务超时秒数 */
    private Integer txTimeoutSeconds;

    /**
     * 获取事务提交尝试次数
     */
    public Integer getTxMaxCommitAttempts() {
        return txMaxCommitAttempts != null ? txMaxCommitAttempts : DEFAULT_TX_MAX_COMMIT_ATTEMPTS;
    }

    /**
     * 设置事务提交尝试次数
     * @param txMaxCommitAttempts 事务提交尝试次数
     */
    public void setTxMaxCommitAttempts(Integer txMaxCommitAttempts) {
        this.txMaxCommitAttempts = txMaxCommitAttempts;
    }

    /**
     * 获取事务超时秒数
     */
    public Integer getTxTimeoutSeconds() {
        return txTimeoutSeconds;
    }

    /**
     * 设置事务超时秒数
     * @param txTimeoutSeconds 事务超时秒数
     */
    public void setTxTimeoutSeconds(Integer txTimeoutSeconds) {
        this.txTimeoutSeconds = txTimeoutSeconds;
    }

}
