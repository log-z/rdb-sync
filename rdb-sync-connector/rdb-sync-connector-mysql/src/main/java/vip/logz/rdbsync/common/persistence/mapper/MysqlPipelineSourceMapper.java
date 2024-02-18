package vip.logz.rdbsync.common.persistence.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import vip.logz.rdbsync.common.annotations.Scannable;
import vip.logz.rdbsync.connector.mysql.config.MysqlPipelineSourceProperties;

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
            ", ps.protocol" +
            ", ps.parallelism" +
            ", psm.host" +
            ", psm.port" +
            ", psm.database" +
            ", psm.username" +
            ", psm.password" +
            ", psm.server_id" +
            ", psm.server_time_zone" +
            ", psm.startup_mode" +
            ", psm.startup_specific_offset_file" +
            ", psm.startup_specific_offset_pos" +
            ", psm.startup_specific_offset_gtid_set" +
            ", psm.startup_timestamp_millis" +
            ", psm.split_size" +
            ", psm.split_meta_group_size" +
            ", psm.distribution_factor_upper" +
            ", psm.distribution_factor_lower" +
            ", psm.fetch_size" +
            ", psm.connect_timeout_seconds" +
            ", psm.connect_max_retries" +
            ", psm.connection_pool_size" +
            ", psm.heartbeat_interval_seconds" +
            ", psm.jdbc_properties " +
            "from pipeline_source as ps " +
            "inner join pipeline_source_mysql as psm " +
            "on ps.id = psm.id " +
            "and ps.id = #{id}")
    MysqlPipelineSourceProperties get(String id);

}
