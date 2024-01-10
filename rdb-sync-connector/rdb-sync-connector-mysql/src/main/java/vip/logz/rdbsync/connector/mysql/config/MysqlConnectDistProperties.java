package vip.logz.rdbsync.connector.mysql.config;

import vip.logz.rdbsync.common.config.ConnectDistProperties;
import vip.logz.rdbsync.connector.mysql.rule.Mysql;

/**
 * Mysql连接目标属性
 *
 * @author logz
 * @date 2024-01-09
 */
public class MysqlConnectDistProperties extends ConnectDistProperties {

    /** 默认值：JDBC-URL */
    private static final String DEFAULT_JDBC_URL = "jdbc:mysql://localhost:3306";

    /** 默认值：用户名 */
    private static final String DEFAULT_USERNAME = "root";

    /** 默认值：密码 */
    private static final String DEFAULT_PASSWORD = "root";

    /** JDBC-URL */
    private String jdbcUrl;

    /** 用户名 */
    private String username;

    /** 密码 */
    private String password;

    /**
     * 获取JDBC-URL
     */
    public String getUrl() {
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
     * 获取协议
     */
    @Override
    public String getProtocol() {
        return Mysql.class.getSimpleName();
    }

}
