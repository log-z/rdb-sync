package vip.logz.rdbsync.common.persistence.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import vip.logz.rdbsync.common.annotations.Scannable;
import vip.logz.rdbsync.common.persistence.entity.PostgresPipelineDistPropertiesEntity;

/**
 * Postgres管道目标持久化映射
 *
 * @author logz
 * @date 2024-02-05
 */
@Mapper
@Scannable
public interface PostgresPipelineDistMapper {

    /**
     * 列出所有
     */
    @Select("select pd.id" +
            ", pd.name" +
            ", pdp.hosts" +
            ", pdp.ports" +
            ", pdp.database" +
            ", pdp.schema" +
            ", pdp.username" +
            ", pdp.password" +
            ", pdp.options " +
            "from pipeline_dist as pd " +
            "inner join pipeline_dist_postgres as pdp " +
            "on pd.id = pdp.id " +
            "and pd.id = #{id} " +
            "and pd.protocol = 'postgres'")
    PostgresPipelineDistPropertiesEntity get(String id);

}
