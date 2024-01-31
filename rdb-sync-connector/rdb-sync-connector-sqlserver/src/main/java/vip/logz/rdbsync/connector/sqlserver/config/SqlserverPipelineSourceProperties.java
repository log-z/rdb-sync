package vip.logz.rdbsync.connector.sqlserver.config;

import vip.logz.rdbsync.common.config.PipelineSourceProperties;
import vip.logz.rdbsync.connector.sqlserver.rule.Sqlserver;

/**
 * SQLServer管道来源属性
 *
 * @author logz
 * @date 2024-01-27
 */
public class SqlserverPipelineSourceProperties extends PipelineSourceProperties {

    /** 默认值：主机 */
    private static final String DEFAULT_HOST = "localhost";

    /** 默认值：端口 */
    private static final int DEFAULT_PORT = 1433;

    /** 默认值：模式名 */
    private static final String DEFAULT_SCHEMA = "dbo";

    /** 默认值：用户名 */
    private static final String DEFAULT_USERNAME = "sa";

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

    /** 启动模式 */
    private String startupMode;

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
        return schema != null ? schema : DEFAULT_SCHEMA;
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
     * 获取协议
     */
    @Override
    public String getProtocol() {
        return Sqlserver.class.getSimpleName();
    }

}
