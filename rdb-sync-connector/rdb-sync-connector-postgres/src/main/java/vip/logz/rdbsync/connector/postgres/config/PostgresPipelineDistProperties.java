package vip.logz.rdbsync.connector.postgres.config;

import vip.logz.rdbsync.connector.jdbc.config.JdbcPipelineDistProperties;
import vip.logz.rdbsync.connector.postgres.rule.Postgres;

/**
 * Postgres管道目标属性
 *
 * @author logz
 * @date 2024-02-05
 */
public class PostgresPipelineDistProperties extends JdbcPipelineDistProperties {

    /** 前缀：JDBC-URL */
    private static final String PREFIX_JDBC_URL = "jdbc:postgresql://";

    /** 模式名 */
    private String schema;

    /**
     * 获取JDBC-URL
     */
    public String getJdbcUrl() {
        return PREFIX_JDBC_URL + getHost() + ":" + getPort() +
                "/" + getDatabase() +
                "?stringtype=unspecified";
    }

    /**
     * 获取端口
     */
    @Override
    public Integer getPort() {
        Integer port = super.getPort();
        return port != null ? port : PostgresOptions.DEFAULT_PORT;
    }

    /**
     * 获取模式名
     */
    public String getSchema() {
        return schema != null ? schema : PostgresOptions.DEFAULT_SCHEMA;
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
        return username != null ? username : PostgresOptions.DEFAULT_USERNAME;
    }

    @Override
    public String getPassword() {
        return password != null ? password : PostgresOptions.DEFAULT_PASSWORD;
    }

    /**
     * 获取协议
     */
    @Override
    public String getProtocol() {
        return Postgres.class.getSimpleName();
    }

}
