package vip.logz.rdbsync.common.persistence.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import vip.logz.rdbsync.common.annotations.Scannable;
import vip.logz.rdbsync.common.persistence.entity.PostgresPipelineSourcePropertiesEntity;

/**
 * Postgres管道来源持久化映射
 *
 * @author logz
 * @date 2024-02-05
 */
@Mapper
@Scannable
public interface PostgresPipelineSourceMapper {

    /**
     * 列出所有
     */
    @Select("select ps.id" +
            ", ps.name" +
            ", psp.host" +
            ", psp.port" +
            ", psp.database" +
            ", psp.schema" +
            ", psp.username" +
            ", psp.password" +
            ", psp.slot_name" +
            ", psp.options " +
            "from pipeline_source as ps " +
            "inner join pipeline_source_postgres as psp " +
            "on ps.id = psp.id " +
            "and ps.id = #{id} " +
            "and ps.protocol = 'postgres'")
    PostgresPipelineSourcePropertiesEntity get(String id);

}
