package vip.logz.rdbsync.common.config.impl;

import vip.logz.rdbsync.common.config.PipelineSourceProperties;
import vip.logz.rdbsync.common.persistence.SqlSessionProxy;

import java.util.ArrayList;
import java.util.List;
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
     * 加载
     * @param id ID
     * @return 管道来源属性
     */
    @Override
    public PipelineSourceProperties load(String id) {
        // 收集所有原生加载器的结果
        List<PipelineSourceProperties> list = new ArrayList<>();
        for (PersistPipelineSourcePropertiesLoader loader : rawLoaderSet) {
            PipelineSourceProperties properties = loader.load(id);
            if (properties != null) {
                list.add(properties);
            }
        }

        // 仅返回一个
        if (list.isEmpty()) {
            return null;
        } else if (list.size() == 1) {
            return list.get(0);
        }

        throw new RuntimeException("pipeline source [" + id +  "] properties found " + list.size() + " duplicated.");
    }

}
