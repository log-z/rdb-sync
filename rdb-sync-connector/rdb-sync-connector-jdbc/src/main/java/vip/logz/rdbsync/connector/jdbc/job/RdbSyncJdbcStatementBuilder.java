package vip.logz.rdbsync.connector.jdbc.job;

import org.apache.flink.connector.jdbc.JdbcStatementBuilder;
import vip.logz.rdbsync.common.exception.UnsupportedRdbSyncEventOpException;
import vip.logz.rdbsync.common.job.RdbSyncEvent;
import vip.logz.rdbsync.common.rule.Rdb;
import vip.logz.rdbsync.common.rule.table.Mapping;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

/**
 * 数据同步JDBC语句构建器
 *
 * @author logz
 * @date 2024-01-18
 * @param <DistDB> 目标数据库实现
 */
public abstract class RdbSyncJdbcStatementBuilder<DistDB extends Rdb> implements JdbcStatementBuilder<RdbSyncEvent> {

    /** 参数索引偏移量 */
    protected static final int PARAMETER_INDEX_OFFSET = 1;

    /** 表映射 */
    protected final Mapping<DistDB> mapping;

    /**
     * 构造器
     * @param mapping 表映射
     */
    public RdbSyncJdbcStatementBuilder(Mapping<DistDB> mapping) {
        this.mapping = mapping;
    }

    /**
     * 为参数设置值
     * @param ps 预编译的语句
     * @param event 数据同步事件
     * @throws SQLException 当设置出错时抛出此异常
     */
    @Override
    public void accept(PreparedStatement ps, RdbSyncEvent event) throws SQLException {
        Map<String, Object> record = event.getRecord();

        switch (event.getOp()) {
            case UPSERT:
                fillingUpsert(ps, record);
                break;
            case DELETE:
                fillingDelete(ps, record);
                break;
            default:
                throw new UnsupportedRdbSyncEventOpException("JDBC", event.getOp());
        }
    }

    /**
     * 填充更新或插入语句的参数
     * @param ps 预编译的语句
     * @param record 数据同步事件
     * @throws SQLException 当设置出错时抛出此异常
     */
    protected abstract void fillingUpsert(PreparedStatement ps, Map<String, Object> record) throws SQLException;

    /**
     * 填充删除语句的参数
     * @param ps 预编译的语句
     * @param record 数据同步事件
     * @throws SQLException 当设置出错时抛出此异常
     */
    protected abstract void fillingDelete(PreparedStatement ps, Map<String, Object> record) throws SQLException;

}
