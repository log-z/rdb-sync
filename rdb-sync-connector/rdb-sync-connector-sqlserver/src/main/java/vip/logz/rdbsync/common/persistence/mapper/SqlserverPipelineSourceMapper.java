package vip.logz.rdbsync.common.persistence.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import vip.logz.rdbsync.common.annotations.Scannable;
import vip.logz.rdbsync.connector.sqlserver.config.SqlserverPipelineSourceProperties;

import java.util.List;

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
            ", psm.host" +
            ", psm.port" +
            ", psm.database" +
            ", psm.schema" +
            ", psm.username" +
            ", psm.password" +
            ", psm.startup_mode " +
            "from pipeline_source as ps " +
            "inner join pipeline_source_sqlserver as psm " +
            "on ps.id = psm.id")
    List<SqlserverPipelineSourceProperties> listAll();

}
