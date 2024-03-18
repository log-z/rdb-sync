package vip.logz.rdbsync.common.persistence.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import vip.logz.rdbsync.common.annotations.Scannable;
import vip.logz.rdbsync.connector.jdbc.persistence.entity.JdbcPipelineDistPropertiesEntity;

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
            ", pds.host" +
            ", pds.port" +
            ", pds.database" +
            ", pds.schema" +
            ", pds.username" +
            ", pds.password" +
            ", pds.options " +
            "from pipeline_dist as pd " +
            "inner join pipeline_dist_sqlserver as pds " +
            "on pd.id = pds.id " +
            "and pd.id = #{id} " +
            "and pd.protocol = 'sqlserver'")
    JdbcPipelineDistPropertiesEntity get(String id);

}
