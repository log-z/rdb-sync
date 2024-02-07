package vip.logz.rdbsync.common.config.impl;

import vip.logz.rdbsync.common.annotations.Scannable;
import vip.logz.rdbsync.common.config.PipelineSourceProperties;
import vip.logz.rdbsync.common.persistence.mapper.PostgresPipelineSourceMapper;

/**
 * 持久化的Postgres管道来源属性加载器
 *
 * @author logz
 * @date 2024-02-05
 */
@Scannable
public class PersistPostgresPipelineSourcePropertiesLoader extends PersistPipelineSourcePropertiesLoader {

    /**
     * 加载
     * @param id ID
     * @return 管道来源属性
     */
    @Override
    public PipelineSourceProperties load(String id) {
        return sqlSessionProxy.execute(
                PostgresPipelineSourceMapper.class, mapper -> mapper.get(id)
        );
    }

}
