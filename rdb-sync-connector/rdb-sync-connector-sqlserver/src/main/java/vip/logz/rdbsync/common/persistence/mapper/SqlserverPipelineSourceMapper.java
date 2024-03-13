package vip.logz.rdbsync.common.persistence.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import vip.logz.rdbsync.common.annotations.Scannable;
import vip.logz.rdbsync.common.persistence.entity.SqlserverPipelineSourcePropertiesEntity;

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
            ", pss.host" +
            ", pss.port" +
            ", pss.database" +
            ", pss.schema" +
            ", pss.username" +
            ", pss.password" +
            ", pss.options " +
            "from pipeline_source as ps " +
            "inner join pipeline_source_sqlserver as pss " +
            "on ps.id = pss.id " +
            "and ps.id = #{id} " +
            "and ps.protocol = 'sqlserver'")
    SqlserverPipelineSourcePropertiesEntity get(String id);

}
