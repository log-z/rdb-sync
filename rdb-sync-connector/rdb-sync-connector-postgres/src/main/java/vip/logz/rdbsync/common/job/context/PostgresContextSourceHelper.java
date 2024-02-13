package vip.logz.rdbsync.common.job.context;

import com.ververica.cdc.connectors.base.options.StartupOptions;
import com.ververica.cdc.connectors.postgres.source.PostgresSourceBuilder;
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
import vip.logz.rdbsync.connector.postgres.config.PostgresOptions;
import vip.logz.rdbsync.connector.postgres.config.PostgresPipelineSourceProperties;
import vip.logz.rdbsync.connector.postgres.job.debezium.DateFormatConverter;
import vip.logz.rdbsync.connector.postgres.job.debezium.TimeFormatConverter;
import vip.logz.rdbsync.connector.postgres.job.debezium.TimestampFormatConverter;
import vip.logz.rdbsync.connector.postgres.rule.Postgres;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.function.Function;

/**
 * Postgres任务上下文来源辅助
 *
 * @author logz
 * @date 2024-02-05
 */
@Scannable
public class PostgresContextSourceHelper implements ContextSourceHelper<Postgres> {

    /** 默认值：逻辑解码插件名称 */
    private static final String DEFAULT_DECODING_PLUGIN_NAME = "pgoutput";

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
        Pipeline<Postgres> pipeline = (Pipeline<Postgres>) contextMeta.getPipeline();
        PostgresPipelineSourceProperties pipelineProperties =
                (PostgresPipelineSourceProperties) contextMeta.getPipelineSourceProperties();

        // 2. 构造数据源
        PostgresSourceBuilder<DebeziumEvent> builder = PostgresSourceBuilder.PostgresIncrementalSource.builder();
        buildHelper
                // 主机
                .set(builder::hostname, pipelineProperties.getHost(), PostgresOptions.DEFAULT_HOST)
                // 端口
                .set(builder::port, pipelineProperties.getPort(), PostgresOptions.DEFAULT_PORT)
                // 数据库名【必须】
                .set(builder::database, pipelineProperties.getDatabase())
                // 用户名
                .set(builder::username, pipelineProperties.getUsername(), PostgresOptions.DEFAULT_USERNAME)
                // 密码
                .set(builder::password, pipelineProperties.getPassword(), PostgresOptions.DEFAULT_PASSWORD)
                // 槽名称【必须】
                .set(builder::slotName, pipelineProperties.getSlotName())
                // 启动模式
                .setIfNotNull(builder::startupOptions, buildStartupOptions(pipelineProperties))
                // 模式名列表
                .set(
                        (Function<? super String[], ?>) builder::schemaList,
                        pipelineProperties.getSchema(),
                        schema -> new String[] {schema},
                        new String[] {PostgresOptions.DEFAULT_SCHEMA}
                )
                // 表名列表
                .set(
                        (Function<? super String[], ?>) builder::tableList,
                        pipelineProperties.getSchema(),
                        schema -> buildTableList(schema, pipeline),
                        buildTableList(PostgresOptions.DEFAULT_SCHEMA, pipeline)
                )
                // 反序列化器
                .set(builder::deserializer, new SimpleDebeziumDeserializationSchema())
                // Debezium属性
                .set(builder::debeziumProperties, buildDebeziumProps())
                // 逻辑解码插件名称
                .set(builder::decodingPluginName, pipelineProperties.getDecodingPluginName(), DEFAULT_DECODING_PLUGIN_NAME)
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
                .setIfNotNull(builder::connectionPoolSize, pipelineProperties.getConnectionPoolSize())
                // 心跳检测间隔时长
                .setIfNotNull(
                        builder::heartbeatInterval,
                        pipelineProperties.getHeartbeatIntervalSeconds(),
                        Duration::ofSeconds
                );

        return builder.build();
    }

    /**
     * 构建启动选项
     * @param pipelineProperties Postgres管道来源属性
     * @return 返回启动选项，若启动模式为null则返回null
     */
    private static StartupOptions buildStartupOptions(PostgresPipelineSourceProperties pipelineProperties) {
        String startupMode = pipelineProperties.getStartupMode();
        if (startupMode == null) {
            return null;
        }

        // 构建启动选项
        switch (startupMode) {
            // INITIAL：先做快照，再读取最新日志
            case PostgresPipelineSourceProperties.STARTUP_MODE_INITIAL:
                return StartupOptions.initial();
            // LATEST：跳过快照，仅读取最新日志
            case PostgresPipelineSourceProperties.STARTUP_MODE_LATEST:
                return StartupOptions.latest();
            default:
                throw new SourceException("Unknown StartupMode: " + startupMode);
        }
    }

    /**
     * 构建表名列表
     * @param schema 模式名
     * @param pipeline 管道
     * @return 返回表名数组。当所有绑定的来源表都是“等值表匹配”时，将返回切确的表名数组，否则匹配该模式下所有的表。
     */
    private static String[] buildTableList(String schema, Pipeline<Postgres> pipeline) {
        // 提前退出
        List<Binding<Postgres>> bindings = pipeline.getBindings();
        if (bindings.isEmpty()) {
            return new String[0];
        }

        // 切确的表名列表
        List<String> preciseTables = new ArrayList<>();

        for (Binding<Postgres> binding : bindings) {
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
     * 构建Debezium属性
     * @return 返回Debezium属性
     */
    private static Properties buildDebeziumProps() {
        Properties props = new Properties();
        props.put("converters", "date, time, timestamp");
        props.put("date.type", DateFormatConverter.class.getName());
        props.put("time.type", TimeFormatConverter.class.getName());
        props.put("timestamp.type", TimestampFormatConverter.class.getName());

        return props;
    }

}
