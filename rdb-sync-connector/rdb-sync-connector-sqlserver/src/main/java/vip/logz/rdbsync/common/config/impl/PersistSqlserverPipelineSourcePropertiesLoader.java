package vip.logz.rdbsync.common.config.impl;

import vip.logz.rdbsync.common.annotations.Scannable;
import vip.logz.rdbsync.common.config.PipelineSourceProperties;
import vip.logz.rdbsync.common.persistence.mapper.SqlserverPipelineSourceMapper;

/**
 * 持久化的SQLServer管道来源属性加载器
 *
 * @author logz
 * @date 2024-01-29
 */
@Scannable
public class PersistSqlserverPipelineSourcePropertiesLoader extends PersistPipelineSourcePropertiesLoader {

    /**
     * 加载
     * @param id ID
     * @return 管道来源属性
     */
    @Override
    public PipelineSourceProperties load(String id) {
        return sqlSessionProxy.execute(
                SqlserverPipelineSourceMapper.class, mapper -> mapper.get(id)
        );
    }

}
