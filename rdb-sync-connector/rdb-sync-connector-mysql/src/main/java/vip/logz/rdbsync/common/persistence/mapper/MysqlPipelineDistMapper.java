package vip.logz.rdbsync.common.persistence.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import vip.logz.rdbsync.common.annotations.Scannable;
import vip.logz.rdbsync.connector.mysql.config.MysqlPipelineDistProperties;

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
            ", pd.protocol" +
            ", pdm.host" +
            ", pdm.port" +
            ", pdm.database" +
            ", pdm.username" +
            ", pdm.password" +
            ", pdm.guarantee" +
            ", pdm.exec_batch_interval_ms" +
            ", pdm.exec_batch_size" +
            ", pdm.exec_max_retries" +
            ", pdm.conn_timeout_seconds" +
            ", pdm.tx_max_commit_attempts" +
            ", pdm.tx_timeout_seconds " +
            "from pipeline_dist as pd " +
            "inner join pipeline_dist_mysql as pdm " +
            "on pd.id = pdm.id " +
            "and pd.id = #{id}")
    MysqlPipelineDistProperties get(String id);

}
