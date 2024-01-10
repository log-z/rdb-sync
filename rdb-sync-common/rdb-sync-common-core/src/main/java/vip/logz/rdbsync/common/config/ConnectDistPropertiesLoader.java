package vip.logz.rdbsync.common.config;

import java.util.Map;

/**
 * 连接目标属性加载器
 *
 * @author logz
 * @date 2024-01-09
 */
public interface ConnectDistPropertiesLoader {

    /**
     * 加载所有
     * @return [id -> properties]
     */
    Map<String, ConnectDistProperties> loadAll();

}
