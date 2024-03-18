package vip.logz.rdbsync.common.config.impl;

import com.starrocks.connector.flink.table.sink.StarRocksSinkOptions;
import vip.logz.rdbsync.common.annotations.Scannable;
import vip.logz.rdbsync.common.config.PipelineDistProperties;
import vip.logz.rdbsync.common.config.PipelineProperties;
import vip.logz.rdbsync.common.persistence.mapper.StarrocksPipelineDistMapper;
import vip.logz.rdbsync.common.persistence.mapper.StarrocksPipelineDistPropertiesEntity;
import vip.logz.rdbsync.common.utils.PropertiesUtils;
import vip.logz.rdbsync.connector.starrocks.config.StarrocksPipelineDistProperties;
import vip.logz.rdbsync.connector.starrocks.rule.Starrocks;

import java.util.Map;

/**
 * 持久化的StarRocks管道目标属性加载器
 *
 * @author logz
 * @date 2024-01-09
 */
@Scannable
public class PersistStarrocksPipelineDistPropertiesLoader extends PersistPipelineDistPropertiesLoader {

    /**
     * 加载
     * @param id ID
     * @return 管道目标属性
     */
    @Override
    public PipelineDistProperties load(String id) {
        // 1. 获取实体
        StarrocksPipelineDistPropertiesEntity entity = sqlSessionProxy.execute(
                StarrocksPipelineDistMapper.class, mapper -> mapper.get(id)
        );
        if (entity == null) {
            return null;
        }

        // 2. 构建管道目标属性（基于高级选项）
        Map<String, ?> flattedOptions = PropertiesUtils.paresFlatted(entity.getOptions());
        StarrocksPipelineDistProperties props = (flattedOptions == null) ?
                new StarrocksPipelineDistProperties():
                new StarrocksPipelineDistProperties(flattedOptions);

        // 3. 覆盖管道目标属性
        props.set(PipelineProperties.ID, entity.getId());
        props.set(PipelineProperties.NAME, entity.getName());
        props.set(PipelineProperties.PROTOCOL, Starrocks.class.getSimpleName());
        props.setString(StarrocksPipelineDistProperties.HOSTNAMES, entity.getHosts());
        props.setString(StarrocksPipelineDistProperties.PORTS, entity.getPorts());
        props.setString(StarrocksPipelineDistProperties.LOAD_HOSTNAMES, entity.getLoadHosts());
        props.setString(StarrocksPipelineDistProperties.LOAD_PORTS, entity.getLoadPorts());
        props.set(StarRocksSinkOptions.DATABASE_NAME, entity.getDatabase());
        props.set(StarrocksPipelineDistProperties.USERNAME, entity.getUsername());
        props.set(StarrocksPipelineDistProperties.PASSWORD, entity.getPassword());
        return props;
    }

}
