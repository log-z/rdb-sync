package vip.logz.rdbsync.connector.mysql.config;

import vip.logz.rdbsync.connector.jdbc.config.JdbcPipelineDistProperties;
import vip.logz.rdbsync.connector.mysql.rule.Mysql;

/**
 * MySQL管道目标属性
 *
 * @author logz
 * @date 2024-01-09
 */
public class MysqlPipelineDistProperties extends JdbcPipelineDistProperties {

    /** 前缀：JDBC-URL */
    private static final String PREFIX_JDBC_URL = "jdbc:mysql://";

    /**
     * 获取JDBC-URL
     */
    public String getJdbcUrl() {
        return PREFIX_JDBC_URL + getHost() + ":" + getPort() + "/" + getDatabase();
    }

    /**
     * 获取端口
     */
    @Override
    public Integer getPort() {
        Integer port = super.getPort();
        return port != null ? port : MysqlOptions.DEFAULT_PORT;
    }

    /**
     * 获取用户名
     */
    @Override
    public String getUsername() {
        return username != null ? username : MysqlOptions.DEFAULT_USERNAME;
    }

    /**
     * 获取密码
     */
    @Override
    public String getPassword() {
        return password != null ? password : MysqlOptions.DEFAULT_PASSWORD;
    }

    /**
     * 获取协议
     */
    @Override
    public String getProtocol() {
        return Mysql.class.getSimpleName();
    }

}
