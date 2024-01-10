package vip.logz.rdbsync.common.config.impl;

import vip.logz.rdbsync.common.config.ChannelDistProperties;
import vip.logz.rdbsync.common.persistence.SqlSessionProxy;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 持久化的频道目标属性加载器的代理
 *
 * @author logz
 * @date 2024-01-09
 */
public class PersistChannelDistPropertiesLoaderProxy extends PersistChannelDistPropertiesLoader {

    /**
     * 原生加载器集合
     */
    private final Set<PersistChannelDistPropertiesLoader> rawLoaderSet;

    /**
     * 构造器
     * @param sqlSessionProxy SQL会话代理
     */
    public PersistChannelDistPropertiesLoaderProxy(SqlSessionProxy sqlSessionProxy) {
        // 扫描并创建原生加载器
        rawLoaderSet = PersistPropertiesLoaders.createLoaders(PersistChannelDistPropertiesLoader.class);
        for (PersistChannelDistPropertiesLoader loader : rawLoaderSet) {
            loader.sqlSessionProxy = sqlSessionProxy;
        }
    }

    /**
     * 加载所有
     * @return [id -> properties]
     */
    @Override
    public Map<String, ChannelDistProperties> loadAll() {
        // 将所有原生加载器的结果合并
        Map<String, ChannelDistProperties> map = new HashMap<>();
        for (PersistChannelDistPropertiesLoader loader : rawLoaderSet) {
            map.putAll(loader.loadAll());
        }

        return map;
    }

}
