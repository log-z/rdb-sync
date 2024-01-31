package vip.logz.rdbsync.common.persistence.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import vip.logz.rdbsync.common.annotations.Scannable;
import vip.logz.rdbsync.connector.sqlserver.config.SqlserverPipelineDistProperties;

import java.util.List;

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
            ", pdm.jdbc_url" +
            ", pdm.schema" +
            ", pdm.username" +
            ", pdm.password" +
            ", pdm.exec_batch_interval_ms" +
            ", pdm.exec_batch_size" +
            ", pdm.exec_max_retries" +
            ", pdm.conn_timeout_seconds " +
            "from pipeline_dist as pd " +
            "inner join pipeline_dist_sqlserver as pdm " +
            "on pd.id = pdm.id")
    List<SqlserverPipelineDistProperties> listAll();

}
