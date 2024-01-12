package vip.logz.rdbsync.common.config.impl;

import vip.logz.rdbsync.common.annotations.Scannable;
import vip.logz.rdbsync.common.config.PipelineDistProperties;
import vip.logz.rdbsync.common.persistence.mapper.MysqlPipelineDistMapper;
import vip.logz.rdbsync.connector.mysql.config.MysqlPipelineDistProperties;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 持久化的MySQL管道目标属性加载器
 *
 * @author logz
 * @date 2024-01-09
 */
@Scannable
public class PersistMysqlPipelineDistPropertiesLoader extends PersistPipelineDistPropertiesLoader {

    /**
     * 加载所有
     * @return [id -> properties]
     */
    @Override
    public Map<String, PipelineDistProperties> loadAll() {
        List<MysqlPipelineDistProperties> list = sqlSessionProxy.execute(
                MysqlPipelineDistMapper.class, MysqlPipelineDistMapper::listAll
        );
        return list.stream().collect(
                Collectors.toMap(MysqlPipelineDistProperties::getId, p -> p)
        );
    }

}
