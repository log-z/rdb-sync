package vip.logz.rdbsync.common.persistence.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import vip.logz.rdbsync.common.annotations.Scannable;
import vip.logz.rdbsync.connector.sqlserver.config.SqlserverPipelineSourceProperties;

/**
 * SQLServer管道来源持久化映射
 *
 * @author logz
 * @date 2024-01-29
 */
@Mapper
@Scannable
public interface SqlserverPipelineSourceMapper {

    /**
     * 列出所有
     */
    @Select("select ps.id" +
            ", ps.name" +
            ", ps.protocol" +
            ", ps.parallelism" +
            ", pss.host" +
            ", pss.port" +
            ", pss.database" +
            ", pss.schema" +
            ", pss.username" +
            ", pss.password" +
            ", pss.startup_mode " +
            "from pipeline_source as ps " +
            "inner join pipeline_source_sqlserver as pss " +
            "on ps.id = pss.id " +
            "and ps.id = #{id}")
    SqlserverPipelineSourceProperties get(String id);

}
