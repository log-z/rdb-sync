package vip.logz.rdbsync.common.config.impl;

import vip.logz.rdbsync.common.config.PipelineSourceProperties;
import vip.logz.rdbsync.common.persistence.SqlSessionProxy;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 持久化的管道来源属性加载器的代理
 *
 * @author logz
 * @date 2024-01-09
 */
public class PersistPipelineSourcePropertiesLoaderProxy extends PersistPipelineSourcePropertiesLoader {

    /**
     * 原生加载器集合
     */
    private final Set<PersistPipelineSourcePropertiesLoader> rawLoaderSet;

    /**
     * 构造器
     * @param sqlSessionProxy SQL会话代理
     */
    public PersistPipelineSourcePropertiesLoaderProxy(SqlSessionProxy sqlSessionProxy) {
        // 扫描并创建原生加载器
        rawLoaderSet = PersistPropertiesLoaders.createLoaders(PersistPipelineSourcePropertiesLoader.class);
        for (PersistPipelineSourcePropertiesLoader loader : rawLoaderSet) {
            loader.sqlSessionProxy = sqlSessionProxy;
        }
    }

    /**
     * 加载所有
     * @return [id -> properties]
     */
    @Override
    public Map<String, PipelineSourceProperties> loadAll() {
        // 将所有原生加载器的结果合并
        Map<String, PipelineSourceProperties> map = new HashMap<>();
        for (PersistPipelineSourcePropertiesLoader loader : rawLoaderSet) {
            map.putAll(loader.loadAll());
        }

        return map;
    }

}
