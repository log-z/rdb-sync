package vip.logz.rdbsync.connector.starrocks.config;

import vip.logz.rdbsync.common.config.PipelineDistProperties;
import vip.logz.rdbsync.connector.starrocks.rule.Starrocks;

/**
 * StarRocks管道目标属性
 *
 * @author logz
 * @date 2024-01-09
 */
public class StarrocksPipelineDistProperties extends PipelineDistProperties {

    /** 前缀：JDBC-URL */
    private static final String PREFIX_JDBC_URL = "jdbc:mysql://";

    /** 默认值：主机 */
    private static final String DEFAULT_HOST = "localhost";

    /** 默认值：BE-MySQL服务端口 */
    private static final int DEFAULT_PORT = 9030;

    /** 默认值：FE-HTTP服务端口 */
    private static final int DEFAULT_LOAD_PORT = 8030;

    /** 默认值：用户名 */
    private static final String DEFAULT_USERNAME = "root";

    /** 默认值：密码 */
    private static final String DEFAULT_PASSWORD = "";

    /** BE-MySQL服务主机 */
    private String host;

    /** BE-MySQL服务端口 */
    private Integer port;

    /** FE-HTTP服务主机 */
    private String loadHost;

    /** FE-HTTP服务端口 */
    private Integer loadPort;

    /** 数据库名 */
    private String database;

    /** 用户名 */
    private String username;

    /** 密码 */
    private String password;

    /**
     * 读取JDBC-URL
     */
    public String getJdbcUrl() {
        return PREFIX_JDBC_URL + getHost() + ":" + getPort();
    }

    /**
     * 获取BE-MySQL服务主机
     */
    public String getHost() {
        return host != null ? host : DEFAULT_HOST;
    }

    /**
     * 设置BE-MySQL服务主机
     * @param host BE-MySQL服务主机
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * 获取BE-MySQL服务端口
     */
    public Integer getPort() {
        return port != null ? port : DEFAULT_PORT;
    }

    /**
     * 设置BE-MySQL服务端口
     * @param port BE-MySQL服务端口
     */
    public void setPort(Integer port) {
        this.port = port;
    }

    /**
     * 读取FE-HTTP服务器
     */
    public String getLoadUrl() {
        return getLoadHost() + ":" + getLoadPort();
    }

    /**
     * 获取FE-HTTP服务主机
     */
    public String getLoadHost() {
        return loadHost != null ? loadHost : DEFAULT_HOST;
    }

    /**
     * 设置FE-HTTP服务主机
     * @param loadHost FE-HTTP服务主机
     */
    public void setLoadHost(String loadHost) {
        this.loadHost = loadHost;
    }

    /**
     * 获取FE-HTTP服务端口
     */
    public Integer getLoadPort() {
        return loadPort != null ? loadPort : DEFAULT_LOAD_PORT;
    }

    /**
     * 设置FE-HTTP服务端口
     * @param loadPort FE-HTTP服务端口
     */
    public void setLoadPort(Integer loadPort) {
        this.loadPort = loadPort;
    }

    /**
     * 读取数据库名
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
     * 读取用户名
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
     * 读取密码
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
     * 获取协议
     */
    @Override
    public String getProtocol() {
        return Starrocks.class.getSimpleName();
    }

}
