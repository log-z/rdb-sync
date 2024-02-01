package vip.logz.rdbsync.common.config.impl;

import vip.logz.rdbsync.common.config.PipelineDistProperties;
import vip.logz.rdbsync.common.persistence.SqlSessionProxy;

import java.util.ArrayList;
import java.util.List;
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
     * 加载
     * @param id ID
     * @return 管道目标属性
     */
    @Override
    public PipelineDistProperties load(String id) {
        // 收集所有原生加载器的结果
        List<PipelineDistProperties> list = new ArrayList<>();
        for (PersistPipelineDistPropertiesLoader loader : rawLoaderSet) {
            PipelineDistProperties properties = loader.load(id);
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
