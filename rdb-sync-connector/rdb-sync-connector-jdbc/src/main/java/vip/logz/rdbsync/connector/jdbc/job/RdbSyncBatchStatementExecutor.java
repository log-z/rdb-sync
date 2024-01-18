package vip.logz.rdbsync.connector.jdbc.job;

import org.apache.flink.connector.jdbc.internal.executor.JdbcBatchStatementExecutor;
import vip.logz.rdbsync.common.exception.UnsupportedRdbSyncEventOpException;
import vip.logz.rdbsync.common.job.RdbSyncEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据同步JDBC批量执行器
 *
 * @author logz
 * @date 2024-01-18
 */
public class RdbSyncBatchStatementExecutor implements JdbcBatchStatementExecutor<RdbSyncEvent> {

    /** 更新或插入语句 */
    private final String upsertSql;

    /** 删除语句 */
    private final String deleteSql;

    /** JDBC语句构建器 */
    private final RdbSyncJdbcStatementBuilder<?> parameterSetter;

    /** 批次缓存 */
    private final List<RdbSyncEvent> batch = new ArrayList<>();

    /** 更新或插入的预编译语句 */
    private transient PreparedStatement upsertPst;

    /** 删除的预编译语句 */
    private transient PreparedStatement deletePst;

    /**
     * 构造器
     * @param upsertSql 更新或插入语句
     * @param deleteSql 删除语句
     * @param parameterSetter JDBC语句构建器，用于设置参数
     */
    public RdbSyncBatchStatementExecutor(String upsertSql,
                                         String deleteSql,
                                         RdbSyncJdbcStatementBuilder<?> parameterSetter) {
        this.upsertSql = upsertSql;
        this.deleteSql = deleteSql;
        this.parameterSetter = parameterSetter;
    }

    /**
     * 为语句执行预编译
     * @param connection 数据库连接
     */
    @Override
    public void prepareStatements(Connection connection) throws SQLException {
        upsertPst = connection.prepareStatement(upsertSql);
        deletePst = connection.prepareStatement(deleteSql);
    }

    /**
     * 将数据同步事件添加到批次缓存
     * @param event 数据同步事件
     */
    @Override
    public void addToBatch(RdbSyncEvent event) {
        switch (event.getOp()) {
            case UPSERT:
            case DELETE:
                break;
            default:
                throw new UnsupportedRdbSyncEventOpException("JDBC", event.getOp());
        }

        batch.add(event);
    }

    /**
     * 提交缓存中的批次，将数据同步事件转换为SQL并批量执行
     */
    @Override
    public void executeBatch() throws SQLException {
        if (batch.isEmpty()) {
            return;
        }

        // 最近一个操作的预编译语句
        PreparedStatement lastPst = null;

        // 懒策略
        // 1. 当事件的操作与此前一致（或是第一个）时，则加入当前操作的（预编译语句）待执行队列。
        // 2. 当事件的操作与此前不同时，先批量执行此前操作的队列，然后再加入到当前操作的待执行队列。
        for (RdbSyncEvent event : batch) {
            switch (event.getOp()) {
                case UPSERT:
                    if (lastPst != null && lastPst != upsertPst) {
                        lastPst.executeBatch();
                    }
                    parameterSetter.accept(upsertPst, event);
                    upsertPst.addBatch();
                    lastPst = upsertPst;
                    break;
                case DELETE:
                    if (lastPst != null && lastPst != deletePst) {
                        lastPst.executeBatch();
                    }
                    parameterSetter.accept(deletePst, event);
                    deletePst.addBatch();
                    lastPst = deletePst;
                    break;
            }
        }

        // 3. 收尾工作，批量执行最后一个操作的待执行队列。
        if (lastPst != null) {
            lastPst.executeBatch();
        }

        // 清空批次缓存
        batch.clear();
    }

    /**
     * 关闭JDBC语句
     */
    @Override
    public void closeStatements() throws SQLException {
        if (upsertPst != null) {
            upsertPst.close();
            upsertPst = null;
        }
        if (deletePst != null) {
            deletePst.close();
            deletePst = null;
        }
    }

}
