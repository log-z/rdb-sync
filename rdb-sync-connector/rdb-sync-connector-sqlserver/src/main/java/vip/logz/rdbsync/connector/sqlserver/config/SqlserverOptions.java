package vip.logz.rdbsync.connector.sqlserver.config;

/**
 * SQLServer选项
 *
 * @author logz
 * @date 2024-02-19
 */
public interface SqlserverOptions {

    /** 默认值：主机 */
    String DEFAULT_HOST = "localhost";

    /** 默认值：端口 */
    int DEFAULT_PORT = 1433;

    /** 默认值：模式名 */
    String DEFAULT_SCHEMA = "dbo";

    /** 默认值：用户名 */
    String DEFAULT_USERNAME = "sa";

}
