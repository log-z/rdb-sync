package vip.logz.rdbsync.connector.jdbc.persistence.entity;

/**
 * JDBC管道目标属性实体
 *
 * @author logz
 * @date 2024-01-09
 */
public class JdbcPipelineDistPropertiesEntity {

    /** ID */
    private String id;

    /** 名称 */
    private String name;

    /** 主机 */
    private String host;

    /** 端口 */
    private Integer port;

    /** 数据库名 */
    private String database;

    /** 模式名 */
    private String schema;

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
     * 获取主机
     */
    public String getHost() {
        return host;
    }

    /**
     * 设置主机
     * @param host 主机
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * 获取端口
     */
    public Integer getPort() {
        return port;
    }

    /**
     * 设置端口
     * @param port 端口
     */
    public void setPort(Integer port) {
        this.port = port;
    }

    /**
     * 获取数据库名
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
     * 获取模式名
     */
    public String getSchema() {
        return schema;
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
        return username;
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
        return password;
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
