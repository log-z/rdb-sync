package vip.logz.rdbsync.connector.postgres.config;

import vip.logz.rdbsync.common.config.SemanticOptions;
import vip.logz.rdbsync.connector.jdbc.config.JdbcExactlyOncePipelineDistProperties;
import vip.logz.rdbsync.connector.jdbc.config.JdbcPipelineDistProperties;
import vip.logz.rdbsync.connector.postgres.rule.Postgres;

/**
 * Postgres管道目标属性
 *
 * @author logz
 * @date 2024-02-05
 */
public class PostgresPipelineDistProperties extends JdbcPipelineDistProperties {

    /** 精确同步一次的管道目标属性 */
    private final JdbcExactlyOncePipelineDistProperties exactlyOnceProps = new JdbcExactlyOncePipelineDistProperties();

    /** 主机列表 */
    private String hosts;

    /** 端口列表 */
    private String ports;

    /** 模式名 */
    private String schema;

    /**
     * 语义保证
     * @see SemanticOptions#EXACTLY_ONCE
     * @see SemanticOptions#AT_LEAST_ONCE
     */
    private String semantic;

    /**
     * 获取主机
     * @see #getPorts()
     */
    @Override
    @Deprecated
    public String getHost() {
        return null;
    }

    /**
     * 设置主机
     * @param host 主机
     * @see #setHosts(String)
     */
    @Override
    @Deprecated
    public void setHost(String host) {
    }

    /**
     * 获取主机列表
     */
    public String getHosts() {
        return hosts;
    }

    /**
     * 设置主机列表
     * @param hosts 主机列表
     */
    public void setHosts(String hosts) {
        this.hosts = hosts;
    }

    /**
     * 获取端口
     * @see #getPorts()
     */
    @Override
    @Deprecated
    public Integer getPort() {
        return null;
    }

    /**
     * 设置端口
     * @param port 端口
     * @see #setPorts(String)
     */
    @Override
    @Deprecated
    public void setPort(Integer port) {
    }

    /**
     * 获取端口列表
     */
    public String getPorts() {
        return ports;
    }

    /**
     * 设置端口列表
     * @param ports 端口列表
     */
    public void setPorts(String ports) {
        this.ports = ports;
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
     * 获取语义保证
     */
    public String getSemantic() {
        return semantic;
    }

    /**
     * 设置语义保证
     * @param semantic 语义保证
     */
    public void setSemantic(String semantic) {
        this.semantic = semantic;
    }

    /**
     * 获取精确一次属性：事务提交尝试次数
     */
    public Integer getTxMaxCommitAttempts() {
        return exactlyOnceProps.getTxMaxCommitAttempts();
    }

    /**
     * 设置精确一次属性：事务提交尝试次数
     * @param txMaxCommitAttempts 精确一次属性：事务提交尝试次数
     */
    public void setTxMaxCommitAttempts(Integer txMaxCommitAttempts) {
        exactlyOnceProps.setTxMaxCommitAttempts(txMaxCommitAttempts);
    }

    /**
     * 获取精确一次属性：事务超时秒数
     */
    public Integer getTxTimeoutSeconds() {
        return exactlyOnceProps.getTxTimeoutSeconds();
    }

    /**
     * 设置精确一次属性：事务超时秒数
     * @param txTimeoutSeconds 精确一次属性：事务超时秒数
     */
    public void setTxTimeoutSeconds(Integer txTimeoutSeconds) {
        exactlyOnceProps.setTxTimeoutSeconds(txTimeoutSeconds);
    }

    /**
     * 获取协议
     */
    @Override
    public String getProtocol() {
        return Postgres.class.getSimpleName();
    }

}
