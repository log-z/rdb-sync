package vip.logz.rdbsync.common.persistence.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import vip.logz.rdbsync.common.annotations.Scannable;
import vip.logz.rdbsync.common.persistence.entity.MysqlPipelineSourcePropertiesEntity;

/**
 * MySQL管道来源持久化映射
 *
 * @author logz
 * @date 2024-01-09
 */
@Mapper
@Scannable
public interface MysqlPipelineSourceMapper {

    /**
     * 列出所有
     */
    @Select("select ps.id" +
            ", ps.name" +
            ", psm.host" +
            ", psm.port" +
            ", psm.database" +
            ", psm.username" +
            ", psm.password" +
            ", psm.options " +
            "from pipeline_source as ps " +
            "inner join pipeline_source_mysql as psm " +
            "on ps.id = psm.id " +
            "and ps.id = #{id} " +
            "and ps.protocol = 'mysql'")
    MysqlPipelineSourcePropertiesEntity get(String id);

}
