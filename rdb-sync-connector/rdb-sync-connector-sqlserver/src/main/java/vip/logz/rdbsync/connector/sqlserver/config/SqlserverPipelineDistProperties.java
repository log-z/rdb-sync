package vip.logz.rdbsync.connector.sqlserver.config;

import vip.logz.rdbsync.connector.jdbc.config.JdbcPipelineDistProperties;
import vip.logz.rdbsync.connector.sqlserver.rule.Sqlserver;

/**
 * SQLServer管道目标属性
 *
 * @author logz
 * @date 2024-01-27
 */
public class SqlserverPipelineDistProperties extends JdbcPipelineDistProperties {

    /** 前缀：JDBC-URL */
    private static final String PREFIX_JDBC_URL = "jdbc:sqlserver://";

    /** 默认值：端口 */
    private static final int DEFAULT_PORT = 1433;

    /** 默认值：模式名 */
    private static final String DEFAULT_SCHEMA = "dbo";

    /** 默认值：用户名 */
    private static final String DEFAULT_USERNAME = "sa";

    /** 模式名 */
    private String schema;

    /**
     * 获取JDBC-URL
     */
    public String getJdbcUrl() {
        return PREFIX_JDBC_URL + getHost() + ":" + getPort() +
                ";databaseName=" + getDatabase();
    }

    /**
     * 获取端口
     */
    @Override
    public Integer getPort() {
        Integer port = super.getPort();
        return port != null ? port : DEFAULT_PORT;
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
    @Override
    public String getUsername() {
        return username != null ? username : DEFAULT_USERNAME;
    }

    /**
     * 获取协议
     */
    @Override
    public String getProtocol() {
        return Sqlserver.class.getSimpleName();
    }

}