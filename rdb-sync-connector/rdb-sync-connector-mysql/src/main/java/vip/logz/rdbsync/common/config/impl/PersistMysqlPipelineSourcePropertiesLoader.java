package vip.logz.rdbsync.common.config.impl;

import com.ververica.cdc.connectors.mysql.source.config.MySqlSourceOptions;
import vip.logz.rdbsync.common.annotations.Scannable;
import vip.logz.rdbsync.common.config.PipelineSourceProperties;
import vip.logz.rdbsync.common.persistence.entity.MysqlPipelineSourcePropertiesEntity;
import vip.logz.rdbsync.common.persistence.mapper.MysqlPipelineSourceMapper;
import vip.logz.rdbsync.common.utils.PropertiesUtils;
import vip.logz.rdbsync.connector.mysql.config.MysqlPipelineSourceProperties;
import vip.logz.rdbsync.connector.mysql.rule.Mysql;

import java.util.Map;

/**
 * 持久化的MySQL管道来源属性加载器
 *
 * @author logz
 * @date 2024-01-09
 */
@Scannable
public class PersistMysqlPipelineSourcePropertiesLoader extends PersistPipelineSourcePropertiesLoader {

    /**
     * 加载
     * @param id ID
     * @return 管道来源属性
     */
    @Override
    public PipelineSourceProperties load(String id) {
        // 1. 获取实体
        MysqlPipelineSourcePropertiesEntity entity = sqlSessionProxy.execute(
                MysqlPipelineSourceMapper.class, mapper -> mapper.get(id)
        );
        if (entity == null) {
            return null;
        }

        // 2. 构建管道来源属性（基于高级选项）
        Map<String, ?> flattedOptions = PropertiesUtils.paresFlatted(entity.getOptions());
        MysqlPipelineSourceProperties props = (flattedOptions == null) ?
                new MysqlPipelineSourceProperties():
                new MysqlPipelineSourceProperties(flattedOptions);

        // 3. 覆盖管道来源属性
        props.set(PipelineSourceProperties.ID, entity.getId());
        props.set(PipelineSourceProperties.NAME, entity.getName());
        props.set(PipelineSourceProperties.PROTOCOL, Mysql.class.getSimpleName());
        props.set(MySqlSourceOptions.HOSTNAME, entity.getHost());
        props.set(MySqlSourceOptions.PORT, entity.getPort());
        props.set(MySqlSourceOptions.DATABASE_NAME, entity.getDatabase());
        props.set(MySqlSourceOptions.USERNAME, entity.getUsername());
        props.set(MySqlSourceOptions.PASSWORD, entity.getPassword());
        return props;
    }

}
