package vip.logz.rdbsync.common.persistence;

/**
 * 持久化数据源属性
 *
 * @author logz
 * @date 2024-01-09
 */
public class DataSourceProperties {

    /** 属性在配置文件中的路径 */
    public static final String PATH = "rdb-sync.datasource";

    /** JDBC驱动 */
    private String driver;

    /** JDBC-URL */
    private String url;

    /** 用户名 */
    private String username;

    /** 密码 */
    private String password;

    /**
     * 获取JDBC驱动
     */
    public String getDriver() {
        return driver;
    }

    /**
     * 设置JDBC驱动
     * @param driver JDBC驱动
     */
    public void setDriver(String driver) {
        this.driver = driver;
    }

    /**
     * 获取JDBC-URL
     */
    public String getUrl() {
        return url;
    }

    /**
     * 设置JDBC-URL
     * @param url JDBC-URL
     */
    public void setUrl(String url) {
        this.url = url;
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

}
