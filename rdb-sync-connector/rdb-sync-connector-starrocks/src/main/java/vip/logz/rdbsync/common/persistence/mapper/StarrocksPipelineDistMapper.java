package vip.logz.rdbsync.common.persistence.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import vip.logz.rdbsync.common.annotations.Scannable;
import vip.logz.rdbsync.connector.starrocks.config.StarrocksPipelineDistProperties;

import java.util.List;

/**
 * Starrocks管道目标持久化映射
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
            ", pd.protocol" +
            ", pds.jdbc_url" +
            ", pds.load_url" +
            ", pds.`database`" +
            ", pds.username" +
            ", pds.password " +
            "from pipeline_dist as pd " +
            "inner join pipeline_dist_starrocks as pds " +
            "on pd.id = pds.id")
    List<StarrocksPipelineDistProperties> listAll();

}
