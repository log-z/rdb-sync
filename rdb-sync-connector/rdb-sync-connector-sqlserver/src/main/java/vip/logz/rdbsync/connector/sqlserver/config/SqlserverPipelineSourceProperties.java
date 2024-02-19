package vip.logz.rdbsync.connector.sqlserver.config;

import com.ververica.cdc.connectors.sqlserver.source.SqlServerSourceBuilder;
import vip.logz.rdbsync.common.config.PipelineSourceProperties;
import vip.logz.rdbsync.connector.sqlserver.rule.Sqlserver;

import java.time.Duration;

/**
 * SQLServer管道来源属性
 *
 * @author logz
 * @date 2024-01-27
 */
public class SqlserverPipelineSourceProperties extends PipelineSourceProperties {

    /** 启动模式：先做快照，再读取最新日志 */
    public static final String STARTUP_MODE_INITIAL = "initial";

    /** 启动模式：只做快照，不读取日志（暂不可用） */
    public static final String STARTUP_MODE_INITIAL_ONLY = "initial-only";

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
     * 数据库的会话时区
     * @see SqlServerSourceBuilder#serverTimeZone(String)
     */
    private String serverTimeZone;

    /**
     * 启动模式
     * @see #STARTUP_MODE_INITIAL
     * @see #STARTUP_MODE_INITIAL_ONLY
     * @see #STARTUP_MODE_LATEST
     */
    private String startupMode;

    /**
     * 快照属性：表快照的分块大小（行数）
     * @see SqlServerSourceBuilder#splitSize(int)
     */
    private Integer splitSize;

    /**
     * 快照属性：拆分元数据的分组大小
     * @see SqlServerSourceBuilder#splitMetaGroupSize(int)
     */
    private Integer splitMetaGroupSize;

    /**
     * 快照属性：均匀分布因子的上限
     * @see SqlServerSourceBuilder#distributionFactorUpper(double)
     */
    private Double distributionFactorUpper;

    /**
     * 快照属性：均匀分布因子的下限
     * @see SqlServerSourceBuilder#distributionFactorLower(double)
     */
    private Double distributionFactorLower;

    /**
     * 快照属性：每次轮询所能获取的最大行数
     * @see SqlServerSourceBuilder#fetchSize(int)
     */
    private Integer fetchSize;

    /**
     * 连接超时秒数
     * @see SqlServerSourceBuilder#connectTimeout(Duration)
     */
    private Long connectTimeoutSeconds;

    /**
     * 连接最大重试次数
     * @see SqlServerSourceBuilder#connectMaxRetries(int)
     */
    private Integer connectMaxRetries;

    /**
     * 连接池大小
     * @see SqlServerSourceBuilder#connectionPoolSize(int)
     */
    private Integer connectionPoolSize;

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
     * 获取数据库的会话时区
     */
    public String getServerTimeZone() {
        return serverTimeZone;
    }

    /**
     * 设置数据库的会话时区
     * @param serverTimeZone 数据库的会话时区
     */
    public void setServerTimeZone(String serverTimeZone) {
        this.serverTimeZone = serverTimeZone;
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
     * 获取协议
     */
    @Override
    public String getProtocol() {
        return Sqlserver.class.getSimpleName();
    }

}
