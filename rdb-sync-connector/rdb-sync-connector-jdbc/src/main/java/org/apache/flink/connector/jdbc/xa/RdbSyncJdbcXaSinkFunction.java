package org.apache.flink.connector.jdbc.xa;

import org.apache.flink.connector.jdbc.JdbcExactlyOnceOptions;
import org.apache.flink.connector.jdbc.JdbcExecutionOptions;
import org.apache.flink.connector.jdbc.internal.JdbcOutputFormat;
import vip.logz.rdbsync.common.job.RdbSyncEvent;
import vip.logz.rdbsync.connector.jdbc.job.RdbSyncBatchStatementExecutor;
import vip.logz.rdbsync.connector.jdbc.job.RdbSyncJdbcStatementBuilder;

/**
 * 数据同步JDBC出口函数，一个事件精确同步一次
 *
 * @author logz
 * @date 2024-02-22
 */
public class RdbSyncJdbcXaSinkFunction extends JdbcXaSinkFunction<RdbSyncEvent> {

    /**
     * 构造器
     * @param upsertSql 更新或插入语句
     * @param deleteSql 删除语句
     * @param statementBuilder JDBC语句构建器，用于设置参数
     * @param xaFacade XA数据源与连接配置相关的封装
     * @param executionOptions JDBC执行选项
     * @param options 精确一次选项
     */
    public RdbSyncJdbcXaSinkFunction(
            String upsertSql,
            String deleteSql,
            RdbSyncJdbcStatementBuilder<?> statementBuilder,
            XaFacade xaFacade,
            JdbcExecutionOptions executionOptions,
            JdbcExactlyOnceOptions options
    ) {
        super(
                new JdbcOutputFormat<>(
                        xaFacade,
                        executionOptions,
                        context -> new RdbSyncBatchStatementExecutor(
                                upsertSql, deleteSql, statementBuilder
                        ),
                        JdbcOutputFormat.RecordExtractor.identity()
                ),
                xaFacade,
                XidGenerator.semanticXidGenerator(),
                new XaSinkStateHandlerImpl(),
                options,
                new XaGroupOpsImpl(xaFacade)
        );
    }

}
