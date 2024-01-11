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
    @Select("select cd.id" +
            ", cd.name" +
            ", cd.protocol" +
            ", cds.jdbc_url" +
            ", cds.load_url" +
            ", cds.`database`" +
            ", cds.username" +
            ", cds.password " +
            "from channel_dist as cd " +
            "inner join channel_dist_starrocks as cds " +
            "on cd.id = cds.id")
    List<StarrocksChannelDistProperties> listAll();

}
