package vip.logz.rdbsync.common.persistence.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import vip.logz.rdbsync.common.annotations.Scannable;
import vip.logz.rdbsync.connector.postgres.config.PostgresPipelineSourceProperties;

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
            ", ps.protocol" +
            ", ps.parallelism" +
            ", psp.host" +
            ", psp.port" +
            ", psp.database" +
            ", psp.schema" +
            ", psp.username" +
            ", psp.password" +
            ", psp.startup_mode " +
            ", psp.decoding_plugin_name" +
            ", psp.slot_name" +
            ", psp.split_size" +
            ", psp.split_meta_group_size" +
            ", psp.distribution_factor_upper" +
            ", psp.distribution_factor_lower" +
            ", psp.fetch_size" +
            ", psp.connect_timeout_seconds" +
            ", psp.connect_max_retries" +
            ", psp.connection_pool_size" +
            ", psp.heartbeat_interval_seconds " +
            "from pipeline_source as ps " +
            "inner join pipeline_source_postgres as psp " +
            "on ps.id = psp.id " +
            "and ps.id = #{id}")
    PostgresPipelineSourceProperties get(String id);

}
