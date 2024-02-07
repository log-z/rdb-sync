package vip.logz.rdbsync.connector.postgres.config;

/**
 * Postgres选项
 *
 * @author logz
 * @date 2024-02-06
 */
public interface PostgresOptions {

    /** 默认值：主机 */
    String DEFAULT_HOST = "localhost";

    /** 默认值：端口 */
    int DEFAULT_PORT = 5432;

    /** 默认值：模式名 */
    String DEFAULT_SCHEMA = "public";

    /** 默认值：用户名 */
    String DEFAULT_USERNAME = "postgres";

    /** 默认值：密码 */
    String DEFAULT_PASSWORD = "postgres";

}
