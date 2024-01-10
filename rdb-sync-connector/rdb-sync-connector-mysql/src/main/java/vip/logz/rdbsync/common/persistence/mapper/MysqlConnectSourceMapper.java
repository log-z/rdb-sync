package vip.logz.rdbsync.common.persistence.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import vip.logz.rdbsync.common.annotations.Scannable;
import vip.logz.rdbsync.connector.mysql.config.MysqlConnectSourceProperties;

import java.util.List;

/**
 * Mysql连接来源持久化映射
 *
 * @author logz
 * @date 2024-01-09
 */
@Mapper
@Scannable
public interface MysqlConnectSourceMapper {

    /**
     * 列出所有
     */
    @Select("select cs.id" +
            ", cs.name" +
            ", cs.protocol" +
            ", csm.host" +
            ", csm.port" +
            ", csm.`database`" +
            ", csm.username" +
            ", csm.password" +
            ", csm.connect_timeout_seconds" +
            ", csm.jdbc_properties " +
            "from connect_source as cs " +
            "inner join connect_source_mysql as csm " +
            "on cs.id = csm.id")
    List<MysqlConnectSourceProperties> listAll();

}
