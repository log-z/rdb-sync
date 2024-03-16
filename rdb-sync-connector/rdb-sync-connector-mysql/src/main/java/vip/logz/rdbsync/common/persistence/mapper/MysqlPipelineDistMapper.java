package vip.logz.rdbsync.common.persistence.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import vip.logz.rdbsync.common.annotations.Scannable;
import vip.logz.rdbsync.connector.jdbc.persistence.entity.JdbcPipelineDistPropertiesEntity;

/**
 * MySQL管道目标持久化映射
 *
 * @author logz
 * @date 2024-01-09
 */
@Mapper
@Scannable
public interface MysqlPipelineDistMapper {

    /**
     * 列出所有
     */
    @Select("select pd.id" +
            ", pd.name" +
            ", pdm.host" +
            ", pdm.port" +
            ", pdm.database" +
            ", pdm.username" +
            ", pdm.password" +
            ", pdm.options " +
            "from pipeline_dist as pd " +
            "inner join pipeline_dist_mysql as pdm " +
            "on pd.id = pdm.id " +
            "and pd.id = #{id} " +
            "and pd.protocol = 'mysql'")
    JdbcPipelineDistPropertiesEntity get(String id);

}
