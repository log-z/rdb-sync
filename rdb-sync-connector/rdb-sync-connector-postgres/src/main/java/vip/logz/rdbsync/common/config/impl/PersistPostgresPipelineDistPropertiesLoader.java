package vip.logz.rdbsync.common.config.impl;

import vip.logz.rdbsync.common.annotations.Scannable;
import vip.logz.rdbsync.common.config.PipelineDistProperties;
import vip.logz.rdbsync.common.config.PipelineProperties;
import vip.logz.rdbsync.common.persistence.entity.PostgresPipelineDistPropertiesEntity;
import vip.logz.rdbsync.common.persistence.mapper.PostgresPipelineDistMapper;
import vip.logz.rdbsync.common.utils.PropertiesUtils;
import vip.logz.rdbsync.connector.postgres.config.PostgresPipelineDistProperties;
import vip.logz.rdbsync.connector.postgres.rule.Postgres;

import java.util.Map;

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
        // 1. 获取实体
        PostgresPipelineDistPropertiesEntity entity = sqlSessionProxy.execute(
                PostgresPipelineDistMapper.class, mapper -> mapper.get(id)
        );
        if (entity == null) {
            return null;
        }

        // 2. 构建管道目标属性（基于高级选项）
        Map<String, ?> flattedOptions = PropertiesUtils.paresFlatted(entity.getOptions());
        PostgresPipelineDistProperties props = (flattedOptions == null) ?
                new PostgresPipelineDistProperties():
                new PostgresPipelineDistProperties(flattedOptions);

        // 3. 覆盖管道目标属性
        props.set(PipelineProperties.ID, entity.getId());
        props.set(PipelineProperties.NAME, entity.getName());
        props.set(PipelineProperties.PROTOCOL, Postgres.class.getSimpleName());
        props.setString(PostgresPipelineDistProperties.HOSTNAMES, entity.getHosts());
        props.setString(PostgresPipelineDistProperties.PORTS, entity.getPorts());
        props.set(PostgresPipelineDistProperties.DATABASE_NAME, entity.getDatabase());
        props.set(PostgresPipelineDistProperties.SCHEMA_NAME, entity.getSchema());
        props.set(PostgresPipelineDistProperties.USERNAME, entity.getUsername());
        props.set(PostgresPipelineDistProperties.PASSWORD, entity.getPassword());
        return props;
    }

}
