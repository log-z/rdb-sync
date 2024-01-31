package vip.logz.rdbsync.common.config.impl;

import vip.logz.rdbsync.common.annotations.Scannable;
import vip.logz.rdbsync.common.config.PipelineDistProperties;
import vip.logz.rdbsync.common.persistence.mapper.SqlserverPipelineDistMapper;
import vip.logz.rdbsync.connector.sqlserver.config.SqlserverPipelineDistProperties;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 持久化的SQLServer管道目标属性加载器
 *
 * @author logz
 * @date 2024-01-27
 */
@Scannable
public class PersistSqlserverPipelineDistPropertiesLoader extends PersistPipelineDistPropertiesLoader {

    /**
     * 加载所有
     * @return [id -> properties]
     */
    @Override
    public Map<String, PipelineDistProperties> loadAll() {
        List<SqlserverPipelineDistProperties> list = sqlSessionProxy.execute(
                SqlserverPipelineDistMapper.class, SqlserverPipelineDistMapper::listAll
        );
        return list.stream().collect(
                Collectors.toMap(SqlserverPipelineDistProperties::getId, p -> p)
        );
    }

}
