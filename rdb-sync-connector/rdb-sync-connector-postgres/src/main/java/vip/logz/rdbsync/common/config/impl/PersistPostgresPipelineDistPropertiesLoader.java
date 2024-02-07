package vip.logz.rdbsync.common.config.impl;

import vip.logz.rdbsync.common.annotations.Scannable;
import vip.logz.rdbsync.common.config.PipelineDistProperties;
import vip.logz.rdbsync.common.persistence.mapper.PostgresPipelineDistMapper;

/**
 * 持久化的Postgres管道目标属性加载器
 *
 * @author logz
 * @date 2024-02-05
 */
@Scannable
public class PersistPostgresPipelineDistPropertiesLoader extends PersistPipelineDistPropertiesLoader {

    /**
     * 加载
     * @param id ID
     * @return 管道目标属性
     */
    @Override
    public PipelineDistProperties load(String id) {
        return sqlSessionProxy.execute(
                PostgresPipelineDistMapper.class, mapper -> mapper.get(id)
        );
    }

}
