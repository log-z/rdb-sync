package vip.logz.rdbsync.common.config.impl;

import com.ververica.cdc.connectors.postgres.source.config.PostgresSourceOptions;
import vip.logz.rdbsync.common.annotations.Scannable;
import vip.logz.rdbsync.common.config.PipelineSourceProperties;
import vip.logz.rdbsync.common.persistence.entity.PostgresPipelineSourcePropertiesEntity;
import vip.logz.rdbsync.common.persistence.mapper.PostgresPipelineSourceMapper;
import vip.logz.rdbsync.common.utils.PropertiesUtils;
import vip.logz.rdbsync.connector.postgres.config.PostgresPipelineSourceProperties;
import vip.logz.rdbsync.connector.postgres.rule.Postgres;

import java.util.Map;

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
        // 1. 获取实体
        PostgresPipelineSourcePropertiesEntity entity = sqlSessionProxy.execute(
                PostgresPipelineSourceMapper.class, mapper -> mapper.get(id)
        );
        if (entity == null) {
            return null;
        }

        // 2. 构建管道来源属性（基于高级选项）
        Map<String, ?> flattedOptions = PropertiesUtils.paresFlatted(entity.getOptions());
        PipelineSourceProperties props = (flattedOptions == null) ?
                new PostgresPipelineSourceProperties():
                new PostgresPipelineSourceProperties(flattedOptions);

        // 3. 覆盖管道来源属性
        props.set(PipelineSourceProperties.ID, entity.getId());
        props.set(PipelineSourceProperties.NAME, entity.getName());
        props.set(PipelineSourceProperties.PROTOCOL, Postgres.class.getSimpleName());
        props.set(PostgresSourceOptions.HOSTNAME, entity.getHost());
        props.set(PostgresSourceOptions.PG_PORT, entity.getPort());
        props.set(PostgresSourceOptions.DATABASE_NAME, entity.getDatabase());
        props.set(PostgresSourceOptions.SCHEMA_NAME, entity.getSchema());
        props.set(PostgresSourceOptions.USERNAME, entity.getUsername());
        props.set(PostgresSourceOptions.PASSWORD, entity.getPassword());
        props.set(PostgresSourceOptions.SLOT_NAME, entity.getSlotName());
        return props;
    }

}
