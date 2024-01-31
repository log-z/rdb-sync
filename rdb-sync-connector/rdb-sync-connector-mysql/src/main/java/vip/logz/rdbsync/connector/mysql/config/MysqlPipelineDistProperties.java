package vip.logz.rdbsync.connector.mysql.config;

import vip.logz.rdbsync.connector.jdbc.config.JdbcPipelineDistProperties;
import vip.logz.rdbsync.connector.mysql.rule.Mysql;

/**
 * Mysql管道目标属性
 *
 * @author logz
 * @date 2024-01-09
 */
public class MysqlPipelineDistProperties extends JdbcPipelineDistProperties {

    /** 默认值：JDBC-URL */
    private static final String DEFAULT_JDBC_URL = "jdbc:mysql://localhost:3306";

    /** 默认值：用户名 */
    private static final String DEFAULT_USERNAME = "root";

    /** 默认值：密码 */
    private static final String DEFAULT_PASSWORD = "root";

    /**
     * 获取JDBC-URL
     */
    public String getJdbcUrl() {
        return jdbcUrl != null ? jdbcUrl : DEFAULT_JDBC_URL;
    }

    /**
     * 获取用户名
     */
    public String getUsername() {
        return username != null ? username : DEFAULT_USERNAME;
    }

    /**
     * 获取密码
     */
    public String getPassword() {
        return password != null ? password : DEFAULT_PASSWORD;
    }

    /**
     * 获取协议
     */
    @Override
    public String getProtocol() {
        return Mysql.class.getSimpleName();
    }

}
