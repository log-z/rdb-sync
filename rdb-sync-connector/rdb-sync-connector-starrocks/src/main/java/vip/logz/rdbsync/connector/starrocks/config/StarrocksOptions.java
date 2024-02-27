package vip.logz.rdbsync.connector.starrocks.config;

/**
 * StarRocks选项
 *
 * @author logz
 * @date 2024-02-27
 */
public interface StarrocksOptions {


    /** 默认值：主机 */
    String DEFAULT_HOST = "localhost";

    /** 默认值：BE-MySQL服务端口 */
    int DEFAULT_PORT = 9030;

    /** 默认值：FE-HTTP服务端口 */
    int DEFAULT_LOAD_PORT = 8030;

    /** 默认值：用户名 */
    String DEFAULT_USERNAME = "root";

    /** 默认值：密码 */
    String DEFAULT_PASSWORD = "";

}
