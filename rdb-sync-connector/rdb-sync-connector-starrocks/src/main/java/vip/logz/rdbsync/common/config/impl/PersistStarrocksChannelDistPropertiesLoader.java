package vip.logz.rdbsync.common.config.impl;

import vip.logz.rdbsync.common.annotations.Scannable;
import vip.logz.rdbsync.common.config.ChannelDistProperties;
import vip.logz.rdbsync.common.persistence.mapper.StarrocksChannelDistMapper;
import vip.logz.rdbsync.connector.starrocks.config.StarrocksChannelDistProperties;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 持久化的Starrocks频道目标属性加载器
 *
 * @author logz
 * @date 2024-01-09
 */
@Scannable
public class PersistStarrocksChannelDistPropertiesLoader extends PersistChannelDistPropertiesLoader {

    /**
     * 加载所有
     * @return [id -> properties]
     */
    @Override
    public Map<String, ChannelDistProperties> loadAll() {
        List<StarrocksChannelDistProperties> list = sqlSessionProxy.execute(
                StarrocksChannelDistMapper.class, StarrocksChannelDistMapper::listAll
        );
        return list.stream().collect(
                Collectors.toMap(StarrocksChannelDistProperties::getId, p -> p)
        );
    }

}
