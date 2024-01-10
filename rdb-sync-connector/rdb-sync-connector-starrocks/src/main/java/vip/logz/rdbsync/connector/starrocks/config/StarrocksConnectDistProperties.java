package vip.logz.rdbsync.connector.starrocks.config;

import vip.logz.rdbsync.common.config.ConnectDistProperties;
import vip.logz.rdbsync.connector.starrocks.rule.Starrocks;

/**
 * Starrocks连接目标属性
 *
 * @author logz
 * @date 2024-01-09
 */
public class StarrocksConnectDistProperties extends ConnectDistProperties {

    /** 默认值：JDBC-URL */
    private static final String DEFAULT_JDBC_URL = "jdbc:mysql://localhost:9030";

    /** 默认值：FE-HTTP服务器 */
    private static final String DEFAULT_LOAD_URL = "localhost:8030";

    /** 默认值：用户名 */
    private static final String DEFAULT_USERNAME = "root";

    /** 默认值：密码 */
    private static final String DEFAULT_PASSWORD = "";

    /** JDBC-URL */
    private String jdbcUrl;

    /** FE-HTTP服务器 */
    private String loadUrl;

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
        return jdbcUrl != null ? jdbcUrl : DEFAULT_JDBC_URL;
    }

    /**
     * 设置JDBC-URL
     * @param jdbcUrl JDBC-URL
     */
    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    /**
     * 读取FE-HTTP服务器
     */
    public String getLoadUrl() {
        return loadUrl != null ? loadUrl : DEFAULT_LOAD_URL;
    }

    /**
     * 设置FE-HTTP服务器
     * @param loadUrl FE-HTTP服务器
     */
    public void setLoadUrl(String loadUrl) {
        this.loadUrl = loadUrl;
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
