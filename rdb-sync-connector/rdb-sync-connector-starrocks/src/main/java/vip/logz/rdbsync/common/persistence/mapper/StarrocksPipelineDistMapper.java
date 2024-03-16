package vip.logz.rdbsync.common.persistence.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import vip.logz.rdbsync.common.annotations.Scannable;

/**
 * StarRocks管道目标持久化映射
 *
 * @author logz
 * @date 2024-01-09
 */
@Mapper
@Scannable
public interface StarrocksPipelineDistMapper {

    /**
     * 列出所有
     */
    @Select("select pd.id" +
            ", pd.name" +
            ", pds.hosts" +
            ", pds.ports" +
            ", pds.load_hosts" +
            ", pds.load_ports" +
            ", pds.`database`" +
            ", pds.username" +
            ", pds.password" +
            ", pds.options " +
            "from pipeline_dist as pd " +
            "inner join pipeline_dist_starrocks as pds " +
            "on pd.id = pds.id " +
            "and pd.id = #{id} " +
            "and pd.protocol = 'starrocks'")
    StarrocksPipelineDistPropertiesEntity get(String id);

}
