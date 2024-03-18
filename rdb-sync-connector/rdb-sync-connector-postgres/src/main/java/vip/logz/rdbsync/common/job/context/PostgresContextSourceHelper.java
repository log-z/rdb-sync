package vip.logz.rdbsync.common.job.context;

import com.ververica.cdc.connectors.base.options.StartupOptions;
import com.ververica.cdc.connectors.postgres.source.PostgresSourceBuilder;
import com.ververica.cdc.connectors.postgres.source.config.PostgresSourceOptions;
import org.apache.flink.api.connector.source.Source;
import vip.logz.rdbsync.common.annotations.Scannable;
import vip.logz.rdbsync.common.exception.SourceException;
import vip.logz.rdbsync.common.job.debezium.DebeziumEvent;
import vip.logz.rdbsync.common.job.debezium.SimpleDebeziumDeserializationSchema;
import vip.logz.rdbsync.common.rule.Binding;
import vip.logz.rdbsync.common.rule.Pipeline;
import vip.logz.rdbsync.common.rule.table.EqualTableMatcher;
import vip.logz.rdbsync.common.rule.table.TableMatcher;
import vip.logz.rdbsync.common.utils.sql.SqlGenerator;
import vip.logz.rdbsync.connector.postgres.config.PostgresPipelineSourceProperties;
import vip.logz.rdbsync.connector.postgres.job.debezium.DateFormatConverter;
import vip.logz.rdbsync.connector.postgres.job.debezium.TimeFormatConverter;
import vip.logz.rdbsync.connector.postgres.job.debezium.TimestampFormatConverter;
import vip.logz.rdbsync.connector.postgres.rule.Postgres;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Postgres任务上下文来源辅助
 *
 * @author logz
 * @date 2024-02-05
 */
@Scannable
public class PostgresContextSourceHelper implements ContextSourceHelper<Postgres> {

    /**
     * 获取数据源
     * @param contextMeta 任务上下文元数据
     */
    @Override
    @SuppressWarnings("unchecked")
    public Source<DebeziumEvent, ?, ?> getSource(ContextMeta contextMeta) {
        // 1. 获取配置
        Pipeline<Postgres> pipeline = (Pipeline<Postgres>) contextMeta.getPipeline();
        PostgresPipelineSourceProperties pipelineProps =
                (PostgresPipelineSourceProperties) contextMeta.getPipelineSourceProperties();

        // 2. 构造数据源
        return PostgresSourceBuilder.PostgresIncrementalSource.<DebeziumEvent>builder()
                // 主机
                .hostname(pipelineProps.get(PostgresPipelineSourceProperties.HOSTNAME))
                // 端口
                .port(pipelineProps.get(PostgresSourceOptions.PG_PORT))
                // 数据库名【必须】
                .database(pipelineProps.getOptional(PostgresSourceOptions.DATABASE_NAME)
                        .orElseThrow(() -> new IllegalArgumentException("Pipeline Source [database-name] not specified.")))
                // 用户名
                .username(pipelineProps.get(PostgresPipelineSourceProperties.USERNAME))
                // 密码
                .password(pipelineProps.get(PostgresPipelineSourceProperties.PASSWORD))
                // 槽名称【必须】
                .slotName(pipelineProps.getOptional(PostgresSourceOptions.SLOT_NAME)
                        .orElseThrow(() -> new IllegalArgumentException("Pipeline Source [slot.name] not specified.")))
                // 启动模式
                .startupOptions(buildStartupOptions(pipelineProps))
                // 模式名列表
                .schemaList(new String[] {
                        pipelineProps.get(PostgresPipelineSourceProperties.SCHEMA_NAME)
                })
                // 表名列表
                .tableList(buildTableList(
                        pipelineProps.get(PostgresPipelineSourceProperties.SCHEMA_NAME),
                        pipeline
                ))
                // 反序列化器
                .deserializer(new SimpleDebeziumDeserializationSchema())
                // Debezium属性
                .debeziumProperties(buildDebeziumProps())
                // 逻辑解码插件名称
                .decodingPluginName(pipelineProps.get(PostgresPipelineSourceProperties.DECODING_PLUGIN_NAME))
                // 快照属性：表快照的分块大小（行数）
                .splitSize(pipelineProps.get(PostgresSourceOptions.SCAN_INCREMENTAL_SNAPSHOT_CHUNK_SIZE))
                // 快照属性：拆分元数据的分组大小
                .splitMetaGroupSize(pipelineProps.get(PostgresSourceOptions.CHUNK_META_GROUP_SIZE))
                // 快照属性：均匀分布因子的上限
                .distributionFactorUpper(pipelineProps.get(PostgresSourceOptions.SPLIT_KEY_EVEN_DISTRIBUTION_FACTOR_UPPER_BOUND))
                // 快照属性：均匀分布因子的下限
                .distributionFactorLower(pipelineProps.get(PostgresSourceOptions.SPLIT_KEY_EVEN_DISTRIBUTION_FACTOR_LOWER_BOUND))
                // 快照属性：每次轮询所能获取的最大行数
                .fetchSize(pipelineProps.get(PostgresSourceOptions.SCAN_SNAPSHOT_FETCH_SIZE))
                // 连接超时时长
                .connectTimeout(pipelineProps.get(PostgresSourceOptions.CONNECT_TIMEOUT))
                // 连接最大重试次数
                .connectMaxRetries(pipelineProps.get(PostgresSourceOptions.CONNECT_MAX_RETRIES))
                // 连接池大小
                .connectionPoolSize(pipelineProps.get(PostgresSourceOptions.CONNECTION_POOL_SIZE))
                // 心跳检测间隔时长
                .heartbeatInterval(pipelineProps.get(PostgresPipelineSourceProperties.HEARTBEAT_INTERVAL))
                .build();
    }

    /**
     * 构建启动选项
     * @param pipelineProperties Postgres管道来源属性
     * @return 返回启动选项，若启动模式为null则返回null
     */
    private static StartupOptions buildStartupOptions(PostgresPipelineSourceProperties pipelineProperties) {
        String startupMode = pipelineProperties.get(PostgresSourceOptions.SCAN_STARTUP_MODE);

        // 构建启动选项
        switch (startupMode) {
            // INITIAL：先做快照，再读取最新日志
            case PostgresPipelineSourceProperties.StartupMode.INITIAL:
                return StartupOptions.initial();
            // LATEST：跳过快照，仅读取最新日志
            case PostgresPipelineSourceProperties.StartupMode.LATEST:
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
