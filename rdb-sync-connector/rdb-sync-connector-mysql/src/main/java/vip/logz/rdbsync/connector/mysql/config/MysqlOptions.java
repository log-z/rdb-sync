package vip.logz.rdbsync.connector.mysql.config;

/**
 * MySQL选项
 *
 * @author logz
 * @date 2024-02-18
 */
public interface MysqlOptions {

    /** 默认值：主机 */
    String DEFAULT_HOST = "localhost";

    /** 默认值：端口 */
    int DEFAULT_PORT = 3306;

    /** 默认值：用户名 */
    String DEFAULT_USERNAME = "root";

    /** 默认值：密码 */
    String DEFAULT_PASSWORD = "root";

}
