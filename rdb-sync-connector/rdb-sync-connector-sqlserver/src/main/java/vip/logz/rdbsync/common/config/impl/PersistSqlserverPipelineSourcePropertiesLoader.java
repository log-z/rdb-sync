package vip.logz.rdbsync.common.config.impl;

import vip.logz.rdbsync.common.annotations.Scannable;
import vip.logz.rdbsync.common.config.PipelineSourceProperties;
import vip.logz.rdbsync.common.persistence.mapper.SqlserverPipelineSourceMapper;
import vip.logz.rdbsync.connector.sqlserver.config.SqlserverPipelineSourceProperties;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 持久化的SQLServer管道来源属性加载器
 *
 * @author logz
 * @date 2024-01-29
 */
@Scannable
public class PersistSqlserverPipelineSourcePropertiesLoader extends PersistPipelineSourcePropertiesLoader {

    /**
     * 加载所有
     * @return [id -> properties]
     */
    @Override
    public Map<String, PipelineSourceProperties> loadAll() {
        List<SqlserverPipelineSourceProperties> list = sqlSessionProxy.execute(
                SqlserverPipelineSourceMapper.class, SqlserverPipelineSourceMapper::listAll
        );
        return list.stream().collect(
                Collectors.toMap(SqlserverPipelineSourceProperties::getId, p -> p)
        );
    }

}
