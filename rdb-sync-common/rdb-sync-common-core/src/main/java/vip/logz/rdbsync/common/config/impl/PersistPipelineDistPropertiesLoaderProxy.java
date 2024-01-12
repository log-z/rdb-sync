package vip.logz.rdbsync.common.config.impl;

import vip.logz.rdbsync.common.config.PipelineDistProperties;
import vip.logz.rdbsync.common.persistence.SqlSessionProxy;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 持久化的管道目标属性加载器的代理
 *
 * @author logz
 * @date 2024-01-09
 */
public class PersistPipelineDistPropertiesLoaderProxy extends PersistPipelineDistPropertiesLoader {

    /**
     * 原生加载器集合
     */
    private final Set<PersistPipelineDistPropertiesLoader> rawLoaderSet;

    /**
     * 构造器
     * @param sqlSessionProxy SQL会话代理
     */
    public PersistPipelineDistPropertiesLoaderProxy(SqlSessionProxy sqlSessionProxy) {
        // 扫描并创建原生加载器
        rawLoaderSet = PersistPropertiesLoaders.createLoaders(PersistPipelineDistPropertiesLoader.class);
        for (PersistPipelineDistPropertiesLoader loader : rawLoaderSet) {
            loader.sqlSessionProxy = sqlSessionProxy;
        }
    }

    /**
     * 加载所有
     * @return [id -> properties]
     */
    @Override
    public Map<String, PipelineDistProperties> loadAll() {
        // 将所有原生加载器的结果合并
        Map<String, PipelineDistProperties> map = new HashMap<>();
        for (PersistPipelineDistPropertiesLoader loader : rawLoaderSet) {
            map.putAll(loader.loadAll());
        }

        return map;
    }

}
