package vip.logz.rdbsync.common.config.impl;

import vip.logz.rdbsync.common.config.ConnectDistProperties;
import vip.logz.rdbsync.common.persistence.SqlSessionProxy;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 持久化的连接目标属性加载器的代理
 *
 * @author logz
 * @date 2024-01-09
 */
public class PersistConnectDistPropertiesLoaderProxy extends PersistConnectDistPropertiesLoader {

    /**
     * 原生加载器集合
     */
    private final Set<PersistConnectDistPropertiesLoader> rawLoaderSet;

    /**
     * 构造器
     * @param sqlSessionProxy SQL会话代理
     */
    public PersistConnectDistPropertiesLoaderProxy(SqlSessionProxy sqlSessionProxy) {
        // 扫描并创建原生加载器
        rawLoaderSet = PersistPropertiesLoaders.createLoaders(PersistConnectDistPropertiesLoader.class);
        for (PersistConnectDistPropertiesLoader loader : rawLoaderSet) {
            loader.sqlSessionProxy = sqlSessionProxy;
        }
    }

    /**
     * 加载所有
     * @return [id -> properties]
     */
    @Override
    public Map<String, ConnectDistProperties> loadAll() {
        // 将所有原生加载器的结果合并
        Map<String, ConnectDistProperties> map = new HashMap<>();
        for (PersistConnectDistPropertiesLoader loader : rawLoaderSet) {
            map.putAll(loader.loadAll());
        }

        return map;
    }

}
