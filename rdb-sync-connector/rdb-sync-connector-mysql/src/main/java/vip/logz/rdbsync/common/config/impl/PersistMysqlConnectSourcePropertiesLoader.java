package vip.logz.rdbsync.common.config.impl;

import vip.logz.rdbsync.common.annotations.Scannable;
import vip.logz.rdbsync.common.config.ConnectSourceProperties;
import vip.logz.rdbsync.connector.mysql.config.MysqlConnectSourceProperties;
import vip.logz.rdbsync.common.persistence.mapper.MysqlConnectSourceMapper;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 持久化的MySQL连接来源属性加载器
 *
 * @author logz
 * @date 2024-01-09
 */
@Scannable
public class PersistMysqlConnectSourcePropertiesLoader extends PersistConnectSourcePropertiesLoader {

    /**
     * 加载所有
     * @return [id -> properties]
     */
    @Override
    public Map<String, ConnectSourceProperties> loadAll() {
        List<MysqlConnectSourceProperties> list = sqlSessionProxy.execute(
                MysqlConnectSourceMapper.class, MysqlConnectSourceMapper::listAll
        );
        return list.stream().collect(
                Collectors.toMap(MysqlConnectSourceProperties::getId, p -> p)
        );
    }

}
