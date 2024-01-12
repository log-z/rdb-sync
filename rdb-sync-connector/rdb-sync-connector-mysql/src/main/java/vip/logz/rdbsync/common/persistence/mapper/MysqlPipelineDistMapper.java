package vip.logz.rdbsync.common.persistence.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import vip.logz.rdbsync.common.annotations.Scannable;
import vip.logz.rdbsync.connector.mysql.config.MysqlPipelineDistProperties;

import java.util.List;

/**
 * Mysql管道目标持久化映射
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
            ", pd.protocol" +
            ", pdm.jdbc_url" +
            ", pdm.username" +
            ", pdm.password " +
            "from pipeline_dist as pd " +
            "inner join pipeline_dist_mysql as pdm " +
            "on pd.id = pdm.id")
    List<MysqlPipelineDistProperties> listAll();

}
