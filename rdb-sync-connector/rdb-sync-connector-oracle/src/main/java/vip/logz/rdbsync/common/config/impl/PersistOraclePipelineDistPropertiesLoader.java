package vip.logz.rdbsync.common.config.impl;

import org.apache.flink.connector.jdbc.table.JdbcConnectorOptions;
import vip.logz.rdbsync.common.annotations.Scannable;
import vip.logz.rdbsync.common.config.PipelineDistProperties;
import vip.logz.rdbsync.common.config.PipelineProperties;
import vip.logz.rdbsync.common.persistence.mapper.OraclePipelineDistMapper;
import vip.logz.rdbsync.common.utils.PropertiesUtils;
import vip.logz.rdbsync.connector.jdbc.persistence.entity.JdbcPipelineDistPropertiesEntity;
import vip.logz.rdbsync.connector.oracle.config.OraclePipelineDistProperties;
import vip.logz.rdbsync.connector.oracle.rule.Oracle;

import java.util.Map;

/**
 * 持久化的Oracle管道目标属性加载器
 *
 * @author logz
 * @date 2024-03-18
 */
@Scannable
public class PersistOraclePipelineDistPropertiesLoader extends PersistPipelineDistPropertiesLoader {

    /**
     * 加载
     * @param id ID
     * @return 管道目标属性
     */
    @Override
    public PipelineDistProperties load(String id) {
        // 1. 获取实体
        JdbcPipelineDistPropertiesEntity entity = sqlSessionProxy.execute(
                OraclePipelineDistMapper.class, mapper -> mapper.get(id)
        );
        if (entity == null) {
            return null;
        }

        // 2. 构建管道目标属性（基于高级选项）
        Map<String, ?> flattedOptions = PropertiesUtils.paresFlatted(entity.getOptions());
        OraclePipelineDistProperties props = (flattedOptions == null) ?
                new OraclePipelineDistProperties():
                new OraclePipelineDistProperties(flattedOptions);

        // 3. 覆盖管道目标属性
        props.set(PipelineProperties.ID, entity.getId());
        props.set(PipelineProperties.NAME, entity.getName());
        props.set(PipelineProperties.PROTOCOL, Oracle.class.getSimpleName());
        props.set(OraclePipelineDistProperties.HOSTNAME, entity.getHost());
        props.set(OraclePipelineDistProperties.PORT, entity.getPort());
        props.set(OraclePipelineDistProperties.DATABASE_NAME, entity.getDatabase());
        props.set(OraclePipelineDistProperties.SCHEMA_NAME, entity.getSchema());
        props.set(JdbcConnectorOptions.USERNAME, entity.getUsername());
        props.set(JdbcConnectorOptions.PASSWORD, entity.getPassword());
        return props;
    }

}
