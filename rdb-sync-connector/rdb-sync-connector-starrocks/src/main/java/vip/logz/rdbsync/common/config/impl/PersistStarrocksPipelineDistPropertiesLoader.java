package vip.logz.rdbsync.common.config.impl;

import vip.logz.rdbsync.common.annotations.Scannable;
import vip.logz.rdbsync.common.config.PipelineDistProperties;
import vip.logz.rdbsync.common.persistence.mapper.StarrocksPipelineDistMapper;
import vip.logz.rdbsync.connector.starrocks.config.StarrocksPipelineDistProperties;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 持久化的Starrocks管道目标属性加载器
 *
 * @author logz
 * @date 2024-01-09
 */
@Scannable
public class PersistStarrocksPipelineDistPropertiesLoader extends PersistPipelineDistPropertiesLoader {

    /**
     * 加载所有
     * @return [id -> properties]
     */
    @Override
    public Map<String, PipelineDistProperties> loadAll() {
        List<StarrocksPipelineDistProperties> list = sqlSessionProxy.execute(
                StarrocksPipelineDistMapper.class, StarrocksPipelineDistMapper::listAll
        );
        return list.stream().collect(
                Collectors.toMap(StarrocksPipelineDistProperties::getId, p -> p)
        );
    }

}
