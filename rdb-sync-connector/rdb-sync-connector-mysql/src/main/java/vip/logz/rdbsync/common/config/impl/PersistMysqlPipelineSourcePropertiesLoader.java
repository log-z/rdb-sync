package vip.logz.rdbsync.common.config.impl;

import vip.logz.rdbsync.common.annotations.Scannable;
import vip.logz.rdbsync.common.config.PipelineSourceProperties;
import vip.logz.rdbsync.connector.mysql.config.MysqlPipelineSourceProperties;
import vip.logz.rdbsync.common.persistence.mapper.MysqlPipelineSourceMapper;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 持久化的MySQL管道来源属性加载器
 *
 * @author logz
 * @date 2024-01-09
 */
@Scannable
public class PersistMysqlPipelineSourcePropertiesLoader extends PersistPipelineSourcePropertiesLoader {

    /**
     * 加载所有
     * @return [id -> properties]
     */
    @Override
    public Map<String, PipelineSourceProperties> loadAll() {
        List<MysqlPipelineSourceProperties> list = sqlSessionProxy.execute(
                MysqlPipelineSourceMapper.class, MysqlPipelineSourceMapper::listAll
        );
        return list.stream().collect(
                Collectors.toMap(MysqlPipelineSourceProperties::getId, p -> p)
        );
    }

}
