package vip.logz.rdbsync.common.persistence.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import vip.logz.rdbsync.common.annotations.Scannable;
import vip.logz.rdbsync.connector.sqlserver.config.SqlserverPipelineDistProperties;

/**
 * SQLServer管道目标持久化映射
 *
 * @author logz
 * @date 2024-01-27
 */
@Mapper
@Scannable
public interface SqlserverPipelineDistMapper {

    /**
     * 列出所有
     */
    @Select("select pd.id" +
            ", pd.name" +
            ", pd.protocol" +
            ", pds.host" +
            ", pds.port" +
            ", pds.database" +
            ", pds.schema" +
            ", pds.username" +
            ", pds.password" +
            ", pds.exec_batch_interval_ms" +
            ", pds.exec_batch_size" +
            ", pds.exec_max_retries" +
            ", pds.conn_timeout_seconds " +
            "from pipeline_dist as pd " +
            "inner join pipeline_dist_sqlserver as pds " +
            "on pd.id = pds.id " +
            "and pd.id = #{id}")
    SqlserverPipelineDistProperties get(String id);

}
