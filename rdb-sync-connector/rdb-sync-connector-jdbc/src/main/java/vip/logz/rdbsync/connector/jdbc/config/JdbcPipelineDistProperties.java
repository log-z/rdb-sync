package vip.logz.rdbsync.connector.jdbc.config;

import vip.logz.rdbsync.common.config.PipelineDistProperties;

/**
 * JDBC管道目标属性
 *
 * @author logz
 * @date 2024-01-09
 */
public abstract class JdbcPipelineDistProperties extends PipelineDistProperties {

    /** 默认值：执行批次间隔毫秒数 */
    private static final long DEFAULT_EXEC_BATCH_INTERVAL_MS = 0L;

    /** 默认值：执行批次最大容量 */
    private static final int DEFAULT_EXEC_BATCH_SIZE = 5000;

    /** 默认值：执行最大重试次数 */
    private static final int DEFAULT_EXEC_MAX_RETRIES = 3;

    /** 默认值：连接超时秒数 */
    private static final int DEFAULT_CONN_TIMEOUT_SECONDS = 30;

    /** JDBC-URL */
    protected String jdbcUrl;

    /** 用户名 */
    protected String username;

    /** 密码 */
    protected String password;

    /** 执行批次间隔毫秒数 */
    protected Long execBatchIntervalMs;

    /** 执行批次最大容量 */
    protected Integer execBatchSize;

    /** 执行最大重试次数 */
    protected Integer execMaxRetries;

    /** 连接超时秒数 */
    protected Integer connTimeoutSeconds;

    /**
     * 获取JDBC-URL
     */
    public String getJdbcUrl() {
        return jdbcUrl;
    }

    /**
     * 设置JDBC-URL
     * @param jdbcUrl JDBC-URL
     */
    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    /**
     * 获取用户名
     */
    public String getUsername() {
        return username;
    }

    /**
     * 设置用户名
     * @param username 用户名
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * 获取密码
     */
    public String getPassword() {
        return password;
    }

    /**
     * 设置密码
     * @param password 密码
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 获取执行批次间隔毫秒数
     */
    public Long getExecBatchIntervalMs() {
        return execBatchIntervalMs != null ? execBatchIntervalMs : DEFAULT_EXEC_BATCH_INTERVAL_MS;
    }

    /**
     * 设置执行批次间隔毫秒数
     */
    public void setExecBatchIntervalMs(Long execBatchIntervalMs) {
        this.execBatchIntervalMs = execBatchIntervalMs;
    }

    /**
     * 获取批次最大容量
     */
    public Integer getExecBatchSize() {
        return execBatchSize != null ? execBatchSize : DEFAULT_EXEC_BATCH_SIZE;
    }

    /**
     * 设置批次最大容量
     */
    public void setExecBatchSize(Integer execBatchSize) {
        this.execBatchSize = execBatchSize;
    }

    /**
     * 获取执行最大重试次数
     */
    public Integer getExecMaxRetries() {
        return execMaxRetries != null ? execMaxRetries : DEFAULT_EXEC_MAX_RETRIES;
    }

    /**
     * 设置执行最大重试次数
     */
    public void setExecMaxRetries(Integer execMaxRetries) {
        this.execMaxRetries = execMaxRetries;
    }

    /**
     * 获取连接超时秒数
     */
    public Integer getConnTimeoutSeconds() {
        return connTimeoutSeconds != null ? connTimeoutSeconds : DEFAULT_CONN_TIMEOUT_SECONDS;
    }

    /**
     * 设置连接超时秒数
     */
    public void setConnTimeoutSeconds(Integer connTimeoutSeconds) {
        this.connTimeoutSeconds = connTimeoutSeconds;
    }

}
