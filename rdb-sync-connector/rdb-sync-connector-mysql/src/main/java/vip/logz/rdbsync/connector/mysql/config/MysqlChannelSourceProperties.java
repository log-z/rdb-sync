package vip.logz.rdbsync.connector.mysql.config;

import vip.logz.rdbsync.common.config.ChannelSourceProperties;
import vip.logz.rdbsync.connector.mysql.rule.Mysql;

/**
 * Mysql频道来源属性
 *
 * @author logz
 * @date 2024-01-09
 */
public class MysqlChannelSourceProperties extends ChannelSourceProperties {

    /** 默认值：主机 */
    private static final String DEFAULT_HOST = "localhost";

    /** 默认值：端口 */
    private static final int DEFAULT_PORT = 3306;

    /** 默认值：数据库名 */
    private static final String DEFAULT_DATABASE = "mysql";

    /** 默认值：用户名 */
    private static final String DEFAULT_USERNAME = "root";

    /** 默认值：密码 */
    private static final String DEFAULT_PASSWORD = "root";

    /** 默认值：连接超时秒数 */
    private static final int DEFAULT_CONNECT_TIMEOUT_SECONDS = 10;

    /** 启动模式：先做快照，再读取最新日志 */
    public static final String STARTUP_MODE_INITIAL = "initial";

    /** 启动模式：跳过快照，从最早可用位置读取日志 */
    public static final String STARTUP_MODE_EARLIEST = "earliest-offset";

    /** 启动模式：跳过快照，仅读取最新日志 */
    public static final String STARTUP_MODE_LATEST = "latest-offset";

    /** 启动模式：跳过快照，从指定位置开始读取日志 */
    public static final String STARTUP_MODE_SPECIFIC_OFFSET = "specific-offset";

    /** 启动模式：跳过快照，从指定时间戳开始读取日志 */
    public static final String STARTUP_MODE_TIMESTAMP = "timestamp-offset";

    /** 主机 */
    private String host;

    /** 端口 */
    private Integer port;

    /** 数据库名 */
    private String database;

    /** 用户名 */
    private String username;

    /** 密码 */
    private String password;

    /** 连接超时秒数 */
    private Long connectTimeoutSeconds;

    /** JDBC属性 */
    private String jdbcProperties;

    /** 模拟服务端ID */
    private String serverId;

    /** 启动模式 */
    private String startupMode;

    /** 启动参数：起始日志文件 */
    private String startupSpecificOffsetFile;

    /** 启动参数：起始日志文件内位置 */
    private Long startupSpecificOffsetPos;

    /** 启动参数：起始事务编码 */
    private String startupSpecificOffsetGtidSet;

    /** 启动参数：起始时间戳 */
    private Long startupTimestampMillis;

    /**
     * 获取主机
     */
    public String getHost() {
        return host != null ? host : DEFAULT_HOST;
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
        return port != null ? port : DEFAULT_PORT;
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
        return database != null ? database : DEFAULT_DATABASE;
    }

    /**
     * 设置数据库名
     * @param database 数据库名
     */
    public void setDatabase(String database) {
        this.database = database;
    }

    /**
     * 获取用户名
     */
    public String getUsername() {
        return username != null ? username : DEFAULT_USERNAME;
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
        return password != null ? password : DEFAULT_PASSWORD;
    }

    /**
     * 设置密码
     * @param password 密码
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 获取连接超时秒数
     */
    public Long getConnectTimeoutSeconds() {
        return connectTimeoutSeconds != null ? connectTimeoutSeconds : DEFAULT_CONNECT_TIMEOUT_SECONDS;
    }

    /**
     * 设置连接超时秒数
     * @param connectTimeoutSeconds 连接超时秒数
     */
    public void setConnectTimeoutSeconds(Long connectTimeoutSeconds) {
        this.connectTimeoutSeconds = connectTimeoutSeconds;
    }

    /**
     * 获取JDBC属性
     */
    public String getJdbcProperties() {
        return jdbcProperties;
    }

    /**
     * 设置JDBC属性
     * @param jdbcProperties JDBC属性
     */
    public void setJdbcProperties(String jdbcProperties) {
        this.jdbcProperties = jdbcProperties;
    }

    /**
     * 获取模拟服务端ID
     */
    public String getServerId() {
        return serverId;
    }

    /**
     * 设置模拟服务端ID
     * @param serverId 模拟服务端ID
     */
    public void setServerId(String serverId) {
        this.serverId = serverId;
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
     * 获取启动参数：起始日志文件
     */
    public String getStartupSpecificOffsetFile() {
        return startupSpecificOffsetFile;
    }

    /**
     * 设置启动参数：起始日志文件
     * @param startupSpecificOffsetFile 启动参数：起始日志文件
     */
    public void setStartupSpecificOffsetFile(String startupSpecificOffsetFile) {
        this.startupSpecificOffsetFile = startupSpecificOffsetFile;
    }

    /**
     * 获取启动参数：起始日志文件内位置
     */
    public Long getStartupSpecificOffsetPos() {
        return startupSpecificOffsetPos != null ? startupSpecificOffsetPos : 0L;
    }

    /**
     * 设置启动参数：起始日志文件内位置
     * @param startupSpecificOffsetPos 启动参数：起始日志文件内位置
     */
    public void setStartupSpecificOffsetPos(Long startupSpecificOffsetPos) {
        this.startupSpecificOffsetPos = startupSpecificOffsetPos;
    }

    /**
     * 获取启动参数：起始GTID
     */
    public String getStartupSpecificOffsetGtidSet() {
        return startupSpecificOffsetGtidSet;
    }

    /**
     * 设置启动参数：起始GTID
     * @param startupSpecificOffsetGtidSet 启动参数：起始GTID
     */
    public void setStartupSpecificOffsetGtidSet(String startupSpecificOffsetGtidSet) {
        this.startupSpecificOffsetGtidSet = startupSpecificOffsetGtidSet;
    }

    /**
     * 获取启动参数：起始时间戳
     */
    public Long getStartupTimestampMillis() {
        return startupTimestampMillis;
    }

    /**
     * 设置启动参数：起始时间戳
     * @param startupTimestampMillis 启动参数：起始时间戳
     */
    public void setStartupTimestampMillis(Long startupTimestampMillis) {
        this.startupTimestampMillis = startupTimestampMillis;
    }

    /**
     * 获取协议
     */
    @Override
    public String getProtocol() {
        return Mysql.class.getSimpleName();
    }

}
