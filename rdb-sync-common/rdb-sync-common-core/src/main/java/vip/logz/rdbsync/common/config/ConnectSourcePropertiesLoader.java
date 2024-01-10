package vip.logz.rdbsync.common.config;

import java.util.Map;

/**
 * 连接来源属性加载器
 *
 * @author logz
 * @date 2024-01-09
 */
public interface ConnectSourcePropertiesLoader {

    /**
     * 加载所有
     * @return [id -> properties]
     */
    Map<String, ConnectSourceProperties> loadAll();

}
