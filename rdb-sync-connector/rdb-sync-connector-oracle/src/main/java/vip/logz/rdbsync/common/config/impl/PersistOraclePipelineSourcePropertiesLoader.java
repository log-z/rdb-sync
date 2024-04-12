package vip.logz.rdbsync.common.config.impl;

import com.ververica.cdc.connectors.oracle.source.config.OracleSourceOptions;
import vip.logz.rdbsync.common.annotations.Scannable;
import vip.logz.rdbsync.common.config.PipelineSourceProperties;
import vip.logz.rdbsync.common.persistence.entity.OraclePipelineSourcePropertiesEntity;
import vip.logz.rdbsync.common.persistence.mapper.OraclePipelineSourceMapper;
import vip.logz.rdbsync.common.utils.PropertiesUtils;
import vip.logz.rdbsync.connector.oracle.config.OraclePipelineSourceProperties;
import vip.logz.rdbsync.connector.oracle.rule.Oracle;

import java.util.Map;

/**
 * 持久化的Oracle管道来源属性加载器
 *
 * @author logz
 * @date 2024-03-18
 */
@Scannable
public class PersistOraclePipelineSourcePropertiesLoader extends PersistPipelineSourcePropertiesLoader {

    /**
     * 加载
     * @param id ID
     * @return 管道来源属性
     */
    @Override
    public PipelineSourceProperties load(String id) {
        // 1. 获取实体
        OraclePipelineSourcePropertiesEntity entity = sqlSessionProxy.execute(
                OraclePipelineSourceMapper.class, mapper -> mapper.get(id)
        );
        if (entity == null) {
            return null;
        }

        // 2. 构建管道来源属性（基于高级选项）
        Map<String, ?> flattedOptions = PropertiesUtils.paresFlatted(entity.getOptions());
        PipelineSourceProperties props = (flattedOptions == null) ?
                new OraclePipelineSourceProperties():
                new OraclePipelineSourceProperties(flattedOptions);

        // 3. 覆盖管道来源属性
        props.set(PipelineSourceProperties.ID, entity.getId());
        props.set(PipelineSourceProperties.NAME, entity.getName());
        props.set(PipelineSourceProperties.PROTOCOL, Oracle.class.getSimpleName());
        props.set(OraclePipelineSourceProperties.HOSTNAME, entity.getHost());
        props.set(OracleSourceOptions.PORT, entity.getPort());
        props.set(OraclePipelineSourceProperties.DATABASE_NAME, entity.getDatabase());
        props.set(OracleSourceOptions.SCHEMA_NAME, entity.getSchema());
        props.set(OracleSourceOptions.USERNAME, entity.getUsername());
        props.set(OracleSourceOptions.PASSWORD, entity.getPassword());
        return props;
    }

}
