package vip.logz.rdbsync.common.config.impl;

import vip.logz.rdbsync.common.annotations.Scannable;
import vip.logz.rdbsync.common.config.PipelineDistProperties;
import vip.logz.rdbsync.common.config.PipelineProperties;
import vip.logz.rdbsync.common.persistence.mapper.SqlserverPipelineDistMapper;
import vip.logz.rdbsync.common.utils.PropertiesUtils;
import vip.logz.rdbsync.connector.jdbc.persistence.entity.JdbcPipelineDistPropertiesEntity;
import vip.logz.rdbsync.connector.sqlserver.config.SqlserverPipelineDistProperties;
import vip.logz.rdbsync.connector.sqlserver.rule.Sqlserver;

import java.util.Map;

/**
 * 持久化的SQLServer管道目标属性加载器
 *
 * @author logz
 * @date 2024-01-27
 */
@Scannable
public class PersistSqlserverPipelineDistPropertiesLoader extends PersistPipelineDistPropertiesLoader {

    /**
     * 加载
     * @param id ID
     * @return 管道目标属性
     */
    @Override
    public PipelineDistProperties load(String id) {
        // 1. 获取实体
        JdbcPipelineDistPropertiesEntity entity = sqlSessionProxy.execute(
                SqlserverPipelineDistMapper.class, mapper -> mapper.get(id)
        );
        if (entity == null) {
            return null;
        }

        // 2. 构建管道目标属性（基于高级选项）
        Map<String, ?> flattedOptions = PropertiesUtils.paresFlatted(entity.getOptions());
        SqlserverPipelineDistProperties props = (flattedOptions == null) ?
                new SqlserverPipelineDistProperties():
                new SqlserverPipelineDistProperties(flattedOptions);

        // 3. 覆盖管道目标属性
        props.set(PipelineProperties.ID, entity.getId());
        props.set(PipelineProperties.NAME, entity.getName());
        props.set(PipelineProperties.PROTOCOL, Sqlserver.class.getSimpleName());
        props.set(SqlserverPipelineDistProperties.HOSTNAME, entity.getHost());
        props.set(SqlserverPipelineDistProperties.PORT, entity.getPort());
        props.set(SqlserverPipelineDistProperties.DATABASE_NAME, entity.getDatabase());
        props.set(SqlserverPipelineDistProperties.SCHEMA_NAME, entity.getSchema());
        props.set(SqlserverPipelineDistProperties.USERNAME, entity.getUsername());
        props.set(SqlserverPipelineDistProperties.PASSWORD, entity.getPassword());
        return props;
    }

}
