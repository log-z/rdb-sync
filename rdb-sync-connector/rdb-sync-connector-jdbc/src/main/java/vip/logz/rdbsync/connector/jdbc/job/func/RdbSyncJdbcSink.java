package vip.logz.rdbsync.connector.jdbc.job.func;

import org.apache.flink.connector.jdbc.JdbcConnectionOptions;
import org.apache.flink.connector.jdbc.JdbcExactlyOnceOptions;
import org.apache.flink.connector.jdbc.JdbcExecutionOptions;
import org.apache.flink.connector.jdbc.internal.GenericJdbcSinkFunction;
import org.apache.flink.connector.jdbc.internal.JdbcOutputFormat;
import org.apache.flink.connector.jdbc.internal.connection.SimpleJdbcConnectionProvider;
import org.apache.flink.connector.jdbc.xa.RdbSyncJdbcXaSinkFunction;
import org.apache.flink.connector.jdbc.xa.XaFacade;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.util.function.SerializableSupplier;
import vip.logz.rdbsync.common.job.RdbSyncEvent;
import vip.logz.rdbsync.connector.jdbc.job.RdbSyncBatchStatementExecutor;
import vip.logz.rdbsync.connector.jdbc.job.RdbSyncJdbcStatementBuilder;

import javax.sql.XADataSource;

/**
 * 数据同步JDBC出口工具
 *
 * @author logz
 * @date 2024-01-18
 */
public class RdbSyncJdbcSink {

    /**
     * 创建出口函数，一个事件至少会同步一次
     * @param upsertSql 更新或插入语句
     * @param deleteSql 删除语句
     * @param statementBuilder JDBC语句构建器，用于设置参数
     * @param connectionOptions JDBC连接选项
     * @return 返回出口函数
     */
    public static SinkFunction<RdbSyncEvent> sink(
            String upsertSql,
            String deleteSql,
            RdbSyncJdbcStatementBuilder<?> statementBuilder,
            JdbcConnectionOptions connectionOptions
    ) {
        return sink(upsertSql, deleteSql, statementBuilder, JdbcExecutionOptions.defaults(), connectionOptions);
    }

    /**
     * 创建出口函数，一个事件至少会同步一次
     * @param upsertSql 更新或插入语句
     * @param deleteSql 删除语句
     * @param statementBuilder JDBC语句构建器，用于设置参数
     * @param executionOptions JDBC执行选项
     * @param connectionOptions JDBC连接选项
     * @return 返回出口函数
     */
    public static SinkFunction<RdbSyncEvent> sink(
            String upsertSql,
            String deleteSql,
            RdbSyncJdbcStatementBuilder<?> statementBuilder,
            JdbcExecutionOptions executionOptions,
            JdbcConnectionOptions connectionOptions
    ) {
        return new GenericJdbcSinkFunction<>(
                new JdbcOutputFormat<>(
                        new SimpleJdbcConnectionProvider(connectionOptions),
                        executionOptions,
                        context -> new RdbSyncBatchStatementExecutor(upsertSql, deleteSql, statementBuilder),
                        JdbcOutputFormat.RecordExtractor.identity()
                )
        );
    }

    /**
     * 创建出口函数，一个事件精确同步一次
     * @param upsertSql 更新或插入语句
     * @param deleteSql 删除语句
     * @param statementBuilder JDBC语句构建器，用于设置参数
     * @param executionOptions JDBC执行选项
     * @param exactlyOnceOptions 精确同步一次选项
     * @param dataSourceSupplier 数据源供应者
     * @return 返回出口函数
     */
    public static SinkFunction<RdbSyncEvent> exactlyOnceSink(
            String upsertSql,
            String deleteSql,
            RdbSyncJdbcStatementBuilder<?> statementBuilder,
            JdbcExecutionOptions executionOptions,
            JdbcExactlyOnceOptions exactlyOnceOptions,
            SerializableSupplier<XADataSource> dataSourceSupplier
    ) {
        return new RdbSyncJdbcXaSinkFunction(
                upsertSql,
                deleteSql,
                statementBuilder,
                XaFacade.fromXaDataSourceSupplier(
                        dataSourceSupplier,
                        exactlyOnceOptions.getTimeoutSec(),
                        exactlyOnceOptions.isTransactionPerConnection()
                ),
                executionOptions,
                exactlyOnceOptions
        );
    }

    private RdbSyncJdbcSink() {
    }

}
