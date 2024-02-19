package vip.logz.rdbsync.common.job.context;

import com.ververica.cdc.connectors.base.options.StartupOptions;
import com.ververica.cdc.connectors.sqlserver.source.SqlServerSourceBuilder;
import org.apache.flink.api.connector.source.Source;
import vip.logz.rdbsync.common.annotations.Scannable;
import vip.logz.rdbsync.common.exception.SourceException;
import vip.logz.rdbsync.common.job.debezium.DebeziumEvent;
import vip.logz.rdbsync.common.job.debezium.SimpleDebeziumDeserializationSchema;
import vip.logz.rdbsync.common.rule.Binding;
import vip.logz.rdbsync.common.rule.Pipeline;
import vip.logz.rdbsync.common.rule.table.EqualTableMatcher;
import vip.logz.rdbsync.common.rule.table.TableMatcher;
import vip.logz.rdbsync.common.utils.BuildHelper;
import vip.logz.rdbsync.common.utils.sql.SqlGenerator;
import vip.logz.rdbsync.connector.sqlserver.config.SqlserverOptions;
import vip.logz.rdbsync.connector.sqlserver.config.SqlserverPipelineSourceProperties;
import vip.logz.rdbsync.connector.sqlserver.job.debezium.DateFormatConverter;
import vip.logz.rdbsync.connector.sqlserver.job.debezium.DatetimeFormatConverter;
import vip.logz.rdbsync.connector.sqlserver.job.debezium.TimeFormatConverter;
import vip.logz.rdbsync.connector.sqlserver.rule.Sqlserver;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.function.Function;

/**
 * SQLServer任务上下文来源辅助
 *
 * @author logz
 * @date 2024-01-29
 */
@Scannable
public class SqlserverContextSourceHelper implements ContextSourceHelper<Sqlserver> {

    /** 构建辅助工具 */
    private final BuildHelper buildHelper = BuildHelper.create();

    /**
     * 获取数据源
     * @param contextMeta 任务上下文元数据
     */
    @Override
    @SuppressWarnings("unchecked")
    public Source<DebeziumEvent, ?, ?> getSource(ContextMeta contextMeta) {
        // 1. 获取配置
        Pipeline<Sqlserver> pipeline = (Pipeline<Sqlserver>) contextMeta.getPipeline();
        SqlserverPipelineSourceProperties pipelineProperties =
                (SqlserverPipelineSourceProperties) contextMeta.getPipelineSourceProperties();

        // 2. 构造数据源
        SqlServerSourceBuilder<DebeziumEvent> builder = new SqlServerSourceBuilder<>();
        buildHelper
                // 主机
                .set(builder::hostname, pipelineProperties.getHost(), SqlserverOptions.DEFAULT_HOST)
                // 端口
                .set(builder::port, pipelineProperties.getPort(), SqlserverOptions.DEFAULT_PORT)
                // 数据库名【必须】
                .set(
                        (Function<? super String[], ?>) builder::databaseList,
                        new String[]{pipelineProperties.getDatabase()}
                )
                // 表名列表
                .set(
                        (Function<? super String[], ?>) builder::tableList,
                        pipelineProperties.getSchema(),
                        schema -> buildTableList(schema, pipeline),
                        buildTableList(SqlserverOptions.DEFAULT_SCHEMA, pipeline)
                )
                // 用户名
                .set(builder::username, pipelineProperties.getUsername(), SqlserverOptions.DEFAULT_USERNAME)
                // 密码【必须】
                .set(builder::password, pipelineProperties.getPassword())
                // 数据库的会话时区
                .setIfNotNull(builder::serverTimeZone, pipelineProperties.getServerTimeZone())
                // 启动模式
                .setIfNotNull(builder::startupOptions, buildStartupOptions(pipelineProperties))
                // 反序列化器
                .set(builder::deserializer, new SimpleDebeziumDeserializationSchema())
                // Debezium属性
                .set(builder::debeziumProperties, buildDebeziumProps())
                // 快照属性：表快照的分块大小（行数）
                .setIfNotNull(builder::splitSize, pipelineProperties.getSplitSize())
                // 快照属性：拆分元数据的分组大小
                .setIfNotNull(builder::splitMetaGroupSize, pipelineProperties.getSplitMetaGroupSize())
                // 快照属性：均匀分布因子的上限
                .setIfNotNull(builder::distributionFactorUpper, pipelineProperties.getDistributionFactorUpper())
                // 快照属性：均匀分布因子的下限
                .setIfNotNull(builder::distributionFactorLower, pipelineProperties.getDistributionFactorLower())
                // 快照属性：每次轮询所能获取的最大行数
                .setIfNotNull(builder::fetchSize, pipelineProperties.getFetchSize())
                // 连接超时时长
                .setIfNotNull(
                        builder::connectTimeout,
                        pipelineProperties.getConnectTimeoutSeconds(),
                        Duration::ofSeconds
                )
                // 连接最大重试次数
                .setIfNotNull(builder::connectMaxRetries, pipelineProperties.getConnectMaxRetries())
                // 连接池大小
                .setIfNotNull(builder::connectionPoolSize, pipelineProperties.getConnectionPoolSize());

        return builder.build();
    }

    /**
     * 构建表名列表
     * @param schema 模式名
     * @param pipeline 管道
     * @return 返回表名数组。当所有绑定的来源表都是“等值表匹配”时，将返回切确的表名数组，否则匹配该模式下所有的表。
     */
    private static String[] buildTableList(String schema, Pipeline<Sqlserver> pipeline) {
        // 提前退出
        List<Binding<Sqlserver>> bindings = pipeline.getBindings();
        if (bindings.isEmpty()) {
            return new String[0];
        }

        // 切确的表名列表
        List<String> preciseTables = new ArrayList<>();

        for (Binding<Sqlserver> binding : bindings) {
            TableMatcher sourceTableMatcher = binding.getSourceTableMatcher();
            if (sourceTableMatcher instanceof EqualTableMatcher) {
                String table = ((EqualTableMatcher) sourceTableMatcher).getTable();
                preciseTables.add(schema + SqlGenerator.TOKEN_REF_DELIMITER + table);
                continue;
            }

            // 使用了其它来源表匹配器
            preciseTables.clear();
            break;
        }

        return preciseTables.isEmpty() ?
                new String[] {schema + "\\..*"} :  // 匹配该模式下所有的表
                preciseTables.toArray(String[]::new);  // 切确的表名数组
    }

    /**
     * 构建启动选项
     * @param pipelineProperties SQLServer管道来源属性
     * @return 返回启动选项，若启动模式为null则返回null
     */
    private static StartupOptions buildStartupOptions(SqlserverPipelineSourceProperties pipelineProperties) {
        String startupMode = pipelineProperties.getStartupMode();
        if (startupMode == null) {
            return null;
        }

        // 构建启动模式
        switch (startupMode) {
            // INITIAL：先做快照，再读取最新日志
            case SqlserverPipelineSourceProperties.STARTUP_MODE_INITIAL:
                return StartupOptions.initial();
            // LATEST：跳过快照，仅读取最新日志
            case SqlserverPipelineSourceProperties.STARTUP_MODE_LATEST:
                return StartupOptions.latest();
            default:
                throw new SourceException("Unknown StartupMode: " + startupMode);
        }
    }

    /**
     * 构建Debezium属性
     * @return 返回Debezium属性
     */
    private Properties buildDebeziumProps() {
        Properties props = new Properties();
        props.put("converters", "date, time, datetime");
        props.put("date.type", DateFormatConverter.class.getName());
        props.put("time.type", TimeFormatConverter.class.getName());
        props.put("datetime.type", DatetimeFormatConverter.class.getName());

        return props;
    }

}
