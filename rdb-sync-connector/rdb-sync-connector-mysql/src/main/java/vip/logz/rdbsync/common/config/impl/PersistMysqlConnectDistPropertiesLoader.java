package vip.logz.rdbsync.common.config.impl;

import vip.logz.rdbsync.common.annotations.Scannable;
import vip.logz.rdbsync.common.config.ConnectDistProperties;
import vip.logz.rdbsync.connector.mysql.config.MysqlConnectDistProperties;
import vip.logz.rdbsync.common.persistence.mapper.MysqlConnectDistMapper;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 持久化的MySQL连接目标属性加载器
 *
 * @author logz
 * @date 2024-01-09
 */
@Scannable
public class PersistMysqlConnectDistPropertiesLoader extends PersistConnectDistPropertiesLoader {

    /**
     * 加载所有
     * @return [id -> properties]
     */
    @Override
    public Map<String, ConnectDistProperties> loadAll() {
        List<MysqlConnectDistProperties> list = sqlSessionProxy.execute(
                MysqlConnectDistMapper.class, MysqlConnectDistMapper::listAll
        );
        return list.stream().collect(
                Collectors.toMap(MysqlConnectDistProperties::getId, p -> p)
        );
    }

}
