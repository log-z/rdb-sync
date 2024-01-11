package vip.logz.rdbsync.common.persistence.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import vip.logz.rdbsync.common.annotations.Scannable;
import vip.logz.rdbsync.connector.mysql.config.MysqlChannelSourceProperties;

import java.util.List;

/**
 * Mysql频道来源持久化映射
 *
 * @author logz
 * @date 2024-01-09
 */
@Mapper
@Scannable
public interface MysqlChannelSourceMapper {

    /**
     * 列出所有
     */
    @Select("select cs.id" +
            ", cs.name" +
            ", cs.protocol" +
            ", csm.host" +
            ", csm.port" +
            ", csm.database" +
            ", csm.username" +
            ", csm.password" +
            ", csm.connect_timeout_seconds" +
            ", csm.jdbc_properties" +
            ", csm.server_id" +
            ", csm.startup_mode" +
            ", csm.startup_specific_offset_file" +
            ", csm.startup_specific_offset_pos" +
            ", csm.startup_specific_offset_gtid_set" +
            ", csm.startup_timestamp_millis " +
            "from channel_source as cs " +
            "inner join channel_source_mysql as csm " +
            "on cs.id = csm.id")
    List<MysqlChannelSourceProperties> listAll();

}
