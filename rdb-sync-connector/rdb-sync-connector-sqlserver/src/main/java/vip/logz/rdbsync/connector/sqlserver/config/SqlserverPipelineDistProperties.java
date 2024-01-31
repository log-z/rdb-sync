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

    /** 默认值：JDBC-URL */
    private static final String DEFAULT_JDBC_URL = "jdbc:sqlserver://localhost:1433";

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
        return jdbcUrl != null ? jdbcUrl : DEFAULT_JDBC_URL;
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
     * 获取协议
     */
    @Override
    public String getProtocol() {
        return Sqlserver.class.getSimpleName();
    }

}
