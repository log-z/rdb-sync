package vip.logz.rdbsync.common.config.impl;

import vip.logz.rdbsync.common.annotations.Scannable;
import vip.logz.rdbsync.common.config.ConnectDistProperties;
import vip.logz.rdbsync.connector.starrocks.config.StarrocksConnectDistProperties;
import vip.logz.rdbsync.common.persistence.mapper.StarrocksConnectDistMapper;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 持久化的Starrocks连接来源属性加载器
 *
 * @author logz
 * @date 2024-01-09
 */
@Scannable
public class PersistStarrocksConnectDistPropertiesLoader extends PersistConnectDistPropertiesLoader {

    /**
     * 加载所有
     * @return [id -> properties]
     */
    @Override
    public Map<String, ConnectDistProperties> loadAll() {
        List<StarrocksConnectDistProperties> list = sqlSessionProxy.execute(
                StarrocksConnectDistMapper.class, StarrocksConnectDistMapper::listAll
        );
        return list.stream().collect(
                Collectors.toMap(StarrocksConnectDistProperties::getId, p -> p)
        );
    }

}
