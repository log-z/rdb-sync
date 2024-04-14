package vip.logz.rdbsync.common.persistence.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import vip.logz.rdbsync.common.annotations.Scannable;
import vip.logz.rdbsync.connector.jdbc.persistence.entity.JdbcPipelineDistPropertiesEntity;

/**
 * Oracle管道目标持久化映射
 *
 * @author logz
 * @date 2024-03-18
 */
@Mapper
@Scannable
public interface OraclePipelineDistMapper {

    /**
     * 列出所有
     */
    @Select("select pd.id" +
            ", pd.name" +
            ", pdo.host" +
            ", pdo.port" +
            ", pdo.database" +
            ", pdo.schema" +
            ", pdo.username" +
            ", pdo.password" +
            ", pdo.options " +
            "from pipeline_dist as pd " +
            "inner join pipeline_dist_oracle as pdo " +
            "on pd.id = pdo.id " +
            "and pd.id = #{id} " +
            "and pd.protocol = 'oracle'")
    JdbcPipelineDistPropertiesEntity get(String id);

}
