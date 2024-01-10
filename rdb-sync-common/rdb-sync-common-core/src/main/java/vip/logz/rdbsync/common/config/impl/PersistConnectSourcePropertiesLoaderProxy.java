package vip.logz.rdbsync.common.config.impl;

import vip.logz.rdbsync.common.config.ConnectSourceProperties;
import vip.logz.rdbsync.common.persistence.SqlSessionProxy;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 持久化的连接来源属性加载器的代理
 *
 * @author logz
 * @date 2024-01-09
 */
public class PersistConnectSourcePropertiesLoaderProxy extends PersistConnectSourcePropertiesLoader {

    /**
     * 原生加载器集合
     */
    private final Set<PersistConnectSourcePropertiesLoader> rawLoaderSet;

    /**
     * 构造器
     * @param sqlSessionProxy SQL会话代理
     */
    public PersistConnectSourcePropertiesLoaderProxy(SqlSessionProxy sqlSessionProxy) {
        // 扫描并创建原生加载器
        rawLoaderSet = PersistPropertiesLoaders.createLoaders(PersistConnectSourcePropertiesLoader.class);
        for (PersistConnectSourcePropertiesLoader loader : rawLoaderSet) {
            loader.sqlSessionProxy = sqlSessionProxy;
        }
    }

    /**
     * 加载所有
     * @return [id -> properties]
     */
    @Override
    public Map<String, ConnectSourceProperties> loadAll() {
        // 将所有原生加载器的结果合并
        Map<String, ConnectSourceProperties> map = new HashMap<>();
        for (PersistConnectSourcePropertiesLoader loader : rawLoaderSet) {
            map.putAll(loader.loadAll());
        }

        return map;
    }

}
