package vip.logz.rdbsync.common.persistence.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import vip.logz.rdbsync.common.annotations.Scannable;
import vip.logz.rdbsync.connector.mysql.config.MysqlConnectDistProperties;

import java.util.List;

/**
 * Mysql连接目标持久化映射
 *
 * @author logz
 * @date 2024-01-09
 */
@Mapper
@Scannable
public interface MysqlConnectDistMapper {

    /**
     * 列出所有
     */
    @Select("select cd.id" +
            ", cd.name" +
            ", cd.protocol" +
            ", cdm.jdbc_url" +
            ", cdm.username" +
            ", cdm.password " +
            "from connect_dist as cd " +
            "inner join connect_dist_mysql as cdm " +
            "on cd.id = cdm.id")
    List<MysqlConnectDistProperties> listAll();

}
