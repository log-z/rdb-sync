package vip.logz.rdbsync.common.config.impl;

import vip.logz.rdbsync.common.annotations.Scannable;
import vip.logz.rdbsync.common.config.PipelineDistProperties;
import vip.logz.rdbsync.common.config.PipelineProperties;
import vip.logz.rdbsync.common.persistence.mapper.MysqlPipelineDistMapper;
import vip.logz.rdbsync.common.utils.PropertiesUtils;
import vip.logz.rdbsync.connector.jdbc.persistence.entity.JdbcPipelineDistPropertiesEntity;
import vip.logz.rdbsync.connector.mysql.config.MysqlPipelineDistProperties;
import vip.logz.rdbsync.connector.mysql.rule.Mysql;

import java.util.Map;

/**
 * 持久化的MySQL管道目标属性加载器
 *
 * @author logz
 * @date 2024-01-09
 */
@Scannable
public class PersistMysqlPipelineDistPropertiesLoader extends PersistPipelineDistPropertiesLoader {

    /**
     * 加载
     * @param id ID
     * @return 管道目标属性
     */
    @Override
    public PipelineDistProperties load(String id) {
        // 1. 获取实体
        JdbcPipelineDistPropertiesEntity entity = sqlSessionProxy.execute(
                MysqlPipelineDistMapper.class, mapper -> mapper.get(id)
        );
        if (entity == null) {
            return null;
        }

        // 2. 构建管道目标属性（基于高级选项）
        Map<String, ?> flattedOptions = PropertiesUtils.paresFlatted(entity.getOptions());
        MysqlPipelineDistProperties props = (flattedOptions == null) ?
                new MysqlPipelineDistProperties():
                new MysqlPipelineDistProperties(flattedOptions);

        // 3. 覆盖管道目标属性
        props.set(PipelineProperties.ID, entity.getId());
        props.set(PipelineProperties.NAME, entity.getName());
        props.set(PipelineProperties.PROTOCOL, Mysql.class.getSimpleName());
        props.set(MysqlPipelineDistProperties.HOSTNAME, entity.getHost());
        props.set(MysqlPipelineDistProperties.PORT, entity.getPort());
        props.set(MysqlPipelineDistProperties.DATABASE_NAME, entity.getDatabase());
        props.set(MysqlPipelineDistProperties.USERNAME, entity.getUsername());
        props.set(MysqlPipelineDistProperties.PASSWORD, entity.getPassword());
        return props;
    }

}
