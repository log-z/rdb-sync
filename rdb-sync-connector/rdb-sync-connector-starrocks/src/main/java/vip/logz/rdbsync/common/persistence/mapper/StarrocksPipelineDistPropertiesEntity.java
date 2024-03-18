package vip.logz.rdbsync.common.persistence.mapper;

import vip.logz.rdbsync.connector.starrocks.config.StarrocksOptions;

/**
 * StarRocks管道目标属性实体
 *
 * @author logz
 * @date 2024-01-09
 */
public class StarrocksPipelineDistPropertiesEntity {

    /** ID */
    private String id;

    /** 名称 */
    private String name;

    /** FE-MySQL服务主机列表 */
    private String hosts;

    /** FE-MySQL服务端口列表 */
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

    /** 高级选项 */
    private String options;

    /**
     * 获取ID
     */
    public String getId() {
        return id;
    }

    /**
     * 设置ID
     * @param id ID
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 获取名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置名称
     * @param name 名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取FE-MySQL服务主机列表
     */
    public String getHosts() {
        return hosts;
    }

    /**
     * 设置FE-MySQL服务主机列表
     * @param hosts FE-MySQL服务主机列表
     */
    public void setHosts(String hosts) {
        this.hosts = hosts;
    }

    /**
     * 获取FE-MySQL服务端口列表
     */
    public String getPorts() {
        return ports;
    }

    /**
     * 设置FE-MySQL服务端口列表
     * @param ports FE-MySQL服务端口列表
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
     * 获取高级选项
     */
    public String getOptions() {
        return options;
    }

    /**
     * 设置高级选项
     * @param options 高级选项
     */
    public void setOptions(String options) {
        this.options = options;
    }

}
