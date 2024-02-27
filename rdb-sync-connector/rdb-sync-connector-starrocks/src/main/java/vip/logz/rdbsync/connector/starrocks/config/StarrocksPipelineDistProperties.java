package vip.logz.rdbsync.connector.starrocks.config;

import vip.logz.rdbsync.common.config.PipelineDistProperties;
import vip.logz.rdbsync.common.config.SemanticOptions;
import vip.logz.rdbsync.connector.starrocks.rule.Starrocks;

/**
 * StarRocks管道目标属性
 *
 * @author logz
 * @date 2024-01-09
 */
public class StarrocksPipelineDistProperties extends PipelineDistProperties {

    /** BE-MySQL服务主机列表 */
    private String hosts;

    /** BE-MySQL服务端口列表 */
    private String ports;

    /** FE-HTTP服务主机列表 */
    private String loadHosts;

    /** FE-HTTP服务端口列表 */
    private String loadPorts;

    /** 数据库名 */
    private String database;

    /** 用户名 */
    private String username;

    /** 密码 */
    private String password;

    /**
     * 语义保证
     * @see SemanticOptions#EXACTLY_ONCE
     * @see SemanticOptions#AT_LEAST_ONCE
     */
    private String semantic;

    /** StreamLoad的标签前缀  */
    private String labelPrefix;

    /**
     * 获取BE-MySQL服务主机列表
     */
    public String getHosts() {
        return hosts;
    }

    /**
     * 设置BE-MySQL服务主机列表
     * @param hosts BE-MySQL服务主机列表
     */
    public void setHosts(String hosts) {
        this.hosts = hosts;
    }

    /**
     * 获取BE-MySQL服务端口列表
     */
    public String getPorts() {
        return ports;
    }

    /**
     * 设置BE-MySQL服务端口列表
     * @param ports BE-MySQL服务端口列表
     */
    public void setPorts(String ports) {
        this.ports = ports;
    }

    /**
     * 获取FE-HTTP服务主机列表
     */
    public String getLoadHosts() {
        return loadHosts;
    }

    /**
     * 设置FE-HTTP服务主机列表
     * @param loadHosts FE-HTTP服务主机列表
     */
    public void setLoadHosts(String loadHosts) {
        this.loadHosts = loadHosts;
    }

    /**
     * 获取FE-HTTP服务端口列表
     */
    public String getLoadPorts() {
        return loadPorts;
    }

    /**
     * 设置FE-HTTP服务端口列表
     * @param loadPorts FE-HTTP服务端口列表
     */
    public void setLoadPorts(String loadPorts) {
        this.loadPorts = loadPorts;
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
        return username != null ? username : StarrocksOptions.DEFAULT_USERNAME;
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
        return password != null ? password : StarrocksOptions.DEFAULT_PASSWORD;
    }

    /**
     * 设置密码
     * @param password 密码
     */
    public void setPassword(String password) {
        this.password = password;
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
     * 获取StreamLoad的标签前缀
     */
    public String getLabelPrefix() {
        return labelPrefix;
    }

    /**
     * 设置StreamLoad的标签前缀
     * @param labelPrefix StreamLoad的标签前缀
     */
    public void setLabelPrefix(String labelPrefix) {
        this.labelPrefix = labelPrefix;
    }

    /**
     * 获取协议
     */
    @Override
    public String getProtocol() {
        return Starrocks.class.getSimpleName();
    }

}
