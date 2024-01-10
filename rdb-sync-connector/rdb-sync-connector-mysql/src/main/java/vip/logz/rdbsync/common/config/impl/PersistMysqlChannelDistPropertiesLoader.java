package vip.logz.rdbsync.common.config.impl;

import vip.logz.rdbsync.common.annotations.Scannable;
import vip.logz.rdbsync.common.config.ChannelDistProperties;
import vip.logz.rdbsync.common.persistence.mapper.MysqlChannelDistMapper;
import vip.logz.rdbsync.connector.mysql.config.MysqlChannelDistProperties;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 持久化的MySQL频道目标属性加载器
 *
 * @author logz
 * @date 2024-01-09
 */
@Scannable
public class PersistMysqlChannelDistPropertiesLoader extends PersistChannelDistPropertiesLoader {

    /**
     * 加载所有
     * @return [id -> properties]
     */
    @Override
    public Map<String, ChannelDistProperties> loadAll() {
        List<MysqlChannelDistProperties> list = sqlSessionProxy.execute(
                MysqlChannelDistMapper.class, MysqlChannelDistMapper::listAll
        );
        return list.stream().collect(
                Collectors.toMap(MysqlChannelDistProperties::getId, p -> p)
        );
    }

}
