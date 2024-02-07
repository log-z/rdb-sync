package vip.logz.rdbsync.connector.postgres.config;

import com.ververica.cdc.connectors.postgres.source.PostgresSourceBuilder;
import vip.logz.rdbsync.common.config.PipelineSourceProperties;
import vip.logz.rdbsync.connector.postgres.rule.Postgres;

import java.time.Duration;

/**
 * Postgres管道来源属性
 *
 * @author logz
 * @date 2024-02-05
 */
public class PostgresPipelineSourceProperties extends PipelineSourceProperties {

    /** 启动模式：先做快照，再读取最新日志 */
    public static final String STARTUP_MODE_INITIAL = "initial";

    /** 启动模式：跳过快照，仅读取最新日志 */
    public static final String STARTUP_MODE_LATEST = "latest-offset";

    /** 主机 */
    private String host;

    /** 端口 */
    private Integer port;

    /** 数据库名 */
    private String database;

    /** 模式名 */
    private String schema;

    /** 用户名 */
    private String username;

    /** 密码 */
    private String password;

    /**
     * 槽名称
     * @see PostgresSourceBuilder#slotName(String)
     */
    private String slotName;

    /**
     * 启动模式
     * @see #STARTUP_MODE_INITIAL
     * @see #STARTUP_MODE_LATEST
     */
    private String startupMode;

    /**
     * 逻辑解码插件名称
     * @see PostgresSourceBuilder#decodingPluginName(String)
     */
    private String decodingPluginName;

    /**
     * 快照属性：表快照的分块大小（行数）
     * @see PostgresSourceBuilder#splitSize(int)
     */
    private Integer splitSize;

    /**
     * 快照属性：拆分元数据的分组大小
     * @see PostgresSourceBuilder#splitMetaGroupSize(int)
     */
    private Integer splitMetaGroupSize;

    /**
     * 快照属性：均匀分布因子的上限
     * @see PostgresSourceBuilder#distributionFactorUpper(double)
     */
    private Double distributionFactorUpper;

    /**
     * 快照属性：均匀分布因子的下限
     * @see PostgresSourceBuilder#distributionFactorLower(double)
     */
    private Double distributionFactorLower;

    /**
     * 快照属性：每次轮询所能获取的最大行数
     * @see PostgresSourceBuilder#fetchSize(int)
     */
    private Integer fetchSize;

    /**
     * 连接超时秒数
     * @see PostgresSourceBuilder#connectTimeout(Duration)
     */
    private Long connectTimeoutSeconds;

    /**
     * 连接最大重试次数
     * @see PostgresSourceBuilder#connectMaxRetries(int)
     */
    private Integer connectMaxRetries;

    /**
     * 连接池大小
     * @see PostgresSourceBuilder#connectionPoolSize(int)
     */
    private Integer connectionPoolSize;

    /**
     * 心跳检测间隔秒数
     * @see PostgresSourceBuilder#heartbeatInterval(Duration)
     */
    private Long heartbeatIntervalSeconds;

    /**
     * 获取主机
     */
    public String getHost() {
        return host;
    }

    /**
     * 设置主机
     * @param host 主机
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * 获取端口
     */
    public Integer getPort() {
        return port;
    }

    /**
     * 设置端口
     * @param port 端口
     */
    public void setPort(Integer port) {
        this.port = port;
    }

    /**
     * 获取数据库名
     */
    public String getDatabase() {
        return database;
    }

    /**
     * 设置数据库名
     * @param database 数据库名
     */
    public void setDatabase(String database) {
        this.database = database;
    }

    /**
     * 获取模式名
     */
    public String getSchema() {
        return schema;
    }

    /**
     * 设置模式名
     * @param schema 模式名
     */
    public void setSchema(String schema) {
        this.schema = schema;
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
     * 获取槽名称
     */
    public String getSlotName() {
        return slotName;
    }

    /**
     * 设置槽名称
     * @param slotName 槽名称
     */
    public void setSlotName(String slotName) {
        this.slotName = slotName;
    }

    /**
     * 获取启动模式
     */
    public String getStartupMode() {
        return startupMode != null ? startupMode : STARTUP_MODE_INITIAL;
    }

    /**
     * 设置启动模式
     * @param startupMode 启动模式
     */
    public void setStartupMode(String startupMode) {
        this.startupMode = startupMode;
    }

    /**
     * 获取逻辑解码插件名称
     */
    public String getDecodingPluginName() {
        return decodingPluginName;
    }

    /**
     * 设置逻辑解码插件名称
     * @param decodingPluginName 逻辑解码插件名称
     */
    public void setDecodingPluginName(String decodingPluginName) {
        this.decodingPluginName = decodingPluginName;
    }

    /**
     * 获取快照属性：表快照的分块大小（行数）
     */
    public Integer getSplitSize() {
        return splitSize;
    }

    /**
     * 设置快照属性：表快照的分块大小（行数）
     * @param splitSize 快照属性：表快照的分块大小（行数）
     */
    public void setSplitSize(Integer splitSize) {
        this.splitSize = splitSize;
    }

    /**
     * 获取快照属性：拆分元数据的分组大小
     */
    public Integer getSplitMetaGroupSize() {
        return splitMetaGroupSize;
    }

    /**
     * 设置快照属性：拆分元数据的分组大小
     * @param splitMetaGroupSize 快照属性：拆分元数据的分组大小
     */
    public void setSplitMetaGroupSize(Integer splitMetaGroupSize) {
        this.splitMetaGroupSize = splitMetaGroupSize;
    }

    /**
     * 获取快照属性：均匀分布因子的上限
     */
    public Double getDistributionFactorUpper() {
        return distributionFactorUpper;
    }

    /**
     * 设置快照属性：均匀分布因子的上限
     * @param distributionFactorUpper 快照属性：均匀分布因子的上限
     */
    public void setDistributionFactorUpper(Double distributionFactorUpper) {
        this.distributionFactorUpper = distributionFactorUpper;
    }

    /**
     * 获取快照属性：均匀分布因子的下限
     */
    public Double getDistributionFactorLower() {
        return distributionFactorLower;
    }

    /**
     * 设置快照属性：均匀分布因子的下限
     * @param distributionFactorLower 快照属性：均匀分布因子的下限
     */
    public void setDistributionFactorLower(Double distributionFactorLower) {
        this.distributionFactorLower = distributionFactorLower;
    }

    /**
     * 获取快照属性：每次轮询所能获取的最大行数
     */
    public Integer getFetchSize() {
        return fetchSize;
    }

    /**
     * 设置快照属性：每次轮询所能获取的最大行数
     * @param fetchSize 快照属性：每次轮询所能获取的最大行数
     */
    public void setFetchSize(Integer fetchSize) {
        this.fetchSize = fetchSize;
    }

    /**
     * 获取连接超时秒数
     */
    public Long getConnectTimeoutSeconds() {
        return connectTimeoutSeconds;
    }

    /**
     * 设置连接超时秒数
     * @param connectTimeoutSeconds 连接超时秒数
     */
    public void setConnectTimeoutSeconds(Long connectTimeoutSeconds) {
        this.connectTimeoutSeconds = connectTimeoutSeconds;
    }

    /**
     * 获取连接最大重试次数
     */
    public Integer getConnectMaxRetries() {
        return connectMaxRetries;
    }

    /**
     * 设置连接最大重试次数
     * @param connectMaxRetries 连接最大重试次数
     */
    public void setConnectMaxRetries(Integer connectMaxRetries) {
        this.connectMaxRetries = connectMaxRetries;
    }

    /**
     * 获取连接池大小
     */
    public Integer getConnectionPoolSize() {
        return connectionPoolSize;
    }

    /**
     * 设置连接池大小
     * @param connectionPoolSize 连接池大小
     */
    public void setConnectionPoolSize(Integer connectionPoolSize) {
        this.connectionPoolSize = connectionPoolSize;
    }

    /**
     * 获取心跳检测间隔秒数
     */
    public Long getHeartbeatIntervalSeconds() {
        return heartbeatIntervalSeconds;
    }

    /**
     * 设置心跳检测间隔秒数
     * @param heartbeatIntervalSeconds 心跳检测间隔秒数
     */
    public void setHeartbeatIntervalSeconds(Long heartbeatIntervalSeconds) {
        this.heartbeatIntervalSeconds = heartbeatIntervalSeconds;
    }

    /**
     * 获取协议
     */
    @Override
    public String getProtocol() {
        return Postgres.class.getSimpleName();
    }

}
