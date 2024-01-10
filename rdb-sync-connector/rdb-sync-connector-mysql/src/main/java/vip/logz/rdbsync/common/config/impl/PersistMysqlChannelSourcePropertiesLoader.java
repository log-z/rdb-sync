package vip.logz.rdbsync.common.config.impl;

import vip.logz.rdbsync.common.annotations.Scannable;
import vip.logz.rdbsync.common.config.ChannelSourceProperties;
import vip.logz.rdbsync.connector.mysql.config.MysqlChannelSourceProperties;
import vip.logz.rdbsync.common.persistence.mapper.MysqlChannelSourceMapper;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 持久化的MySQL频道来源属性加载器
 *
 * @author logz
 * @date 2024-01-09
 */
@Scannable
public class PersistMysqlChannelSourcePropertiesLoader extends PersistChannelSourcePropertiesLoader {

    /**
     * 加载所有
     * @return [id -> properties]
     */
    @Override
    public Map<String, ChannelSourceProperties> loadAll() {
        List<MysqlChannelSourceProperties> list = sqlSessionProxy.execute(
                MysqlChannelSourceMapper.class, MysqlChannelSourceMapper::listAll
        );
        return list.stream().collect(
                Collectors.toMap(MysqlChannelSourceProperties::getId, p -> p)
        );
    }

}
