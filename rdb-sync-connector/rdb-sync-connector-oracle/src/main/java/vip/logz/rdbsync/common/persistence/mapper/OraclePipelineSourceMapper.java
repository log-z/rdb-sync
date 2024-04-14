package vip.logz.rdbsync.common.persistence.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import vip.logz.rdbsync.common.annotations.Scannable;
import vip.logz.rdbsync.common.persistence.entity.OraclePipelineSourcePropertiesEntity;

/**
 * Oracle管道来源持久化映射
 *
 * @author logz
 * @date 2024-03-18
 */
@Mapper
@Scannable
public interface OraclePipelineSourceMapper {

    /**
     * 列出所有
     */
    @Select("select ps.id" +
            ", ps.name" +
            ", pso.host" +
            ", pso.port" +
            ", pso.database" +
            ", pso.schema" +
            ", pso.username" +
            ", pso.password" +
            ", pso.options " +
            "from pipeline_source as ps " +
            "inner join pipeline_source_oracle as pso " +
            "on ps.id = pso.id " +
            "and ps.id = #{id} " +
            "and ps.protocol = 'oracle'")
    OraclePipelineSourcePropertiesEntity get(String id);

}
