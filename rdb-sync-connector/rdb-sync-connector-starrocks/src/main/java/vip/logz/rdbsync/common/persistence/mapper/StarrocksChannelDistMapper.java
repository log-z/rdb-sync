package vip.logz.rdbsync.common.persistence.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import vip.logz.rdbsync.common.annotations.Scannable;
import vip.logz.rdbsync.connector.starrocks.config.StarrocksChannelDistProperties;

import java.util.List;

/**
 * Starrocks频道目标持久化映射
 *
 * @author logz
 * @date 2024-01-09
 */
@Mapper
@Scannable
public interface StarrocksChannelDistMapper {

    /**
     * 列出所有
     */
    @Select("select cs.id" +
            ", cs.name" +
            ", cs.connect_id" +
            ", cs.protocol " +
            "from channel_dist as cs " +
            "where cs.protocol = 'Starrocks'")
    List<StarrocksChannelDistProperties> listAll();

}
