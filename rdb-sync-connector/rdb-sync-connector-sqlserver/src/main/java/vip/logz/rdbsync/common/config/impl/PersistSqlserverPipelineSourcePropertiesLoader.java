package vip.logz.rdbsync.common.config.impl;

import com.ververica.cdc.connectors.base.options.JdbcSourceOptions;
import vip.logz.rdbsync.common.annotations.Scannable;
import vip.logz.rdbsync.common.config.PipelineSourceProperties;
import vip.logz.rdbsync.common.persistence.entity.SqlserverPipelineSourcePropertiesEntity;
import vip.logz.rdbsync.common.persistence.mapper.SqlserverPipelineSourceMapper;
import vip.logz.rdbsync.common.utils.PropertiesUtils;
import vip.logz.rdbsync.connector.sqlserver.config.SqlserverPipelineSourceProperties;
import vip.logz.rdbsync.connector.sqlserver.rule.Sqlserver;

import java.util.Map;

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
        // 1. 获取实体
        SqlserverPipelineSourcePropertiesEntity entity = sqlSessionProxy.execute(
                SqlserverPipelineSourceMapper.class, mapper -> mapper.get(id)
        );
        if (entity == null) {
            return null;
        }

        // 2. 构建管道来源属性（基于高级选项）
        Map<String, ?> flattedOptions = PropertiesUtils.paresFlatted(entity.getOptions());
        PipelineSourceProperties props = (flattedOptions == null) ?
                new SqlserverPipelineSourceProperties():
                new SqlserverPipelineSourceProperties(flattedOptions);

        // 3. 覆盖管道来源属性
        props.set(PipelineSourceProperties.ID, entity.getId());
        props.set(PipelineSourceProperties.NAME, entity.getName());
        props.set(PipelineSourceProperties.PROTOCOL, Sqlserver.class.getSimpleName());
        props.set(JdbcSourceOptions.HOSTNAME, entity.getHost());
        props.set(SqlserverPipelineSourceProperties.PORT, entity.getPort());
        props.set(JdbcSourceOptions.DATABASE_NAME, entity.getDatabase());
        props.set(JdbcSourceOptions.SCHEMA_NAME, entity.getSchema());
        props.set(JdbcSourceOptions.USERNAME, entity.getUsername());
        props.set(JdbcSourceOptions.PASSWORD, entity.getPassword());
        return props;
    }

}
