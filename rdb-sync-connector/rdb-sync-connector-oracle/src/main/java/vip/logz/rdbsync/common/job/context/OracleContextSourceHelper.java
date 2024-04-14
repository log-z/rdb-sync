package vip.logz.rdbsync.common.job.context;

import com.ververica.cdc.connectors.base.options.StartupOptions;
import com.ververica.cdc.connectors.oracle.source.OracleSourceBuilder;
import com.ververica.cdc.connectors.oracle.source.config.OracleSourceOptions;
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
import vip.logz.rdbsync.connector.oracle.config.OraclePipelineSourceProperties;
import vip.logz.rdbsync.connector.oracle.job.debezium.DateFormatConverter;
import vip.logz.rdbsync.connector.oracle.job.debezium.TimestampFormatConverter;
import vip.logz.rdbsync.connector.oracle.rule.Oracle;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Oracle任务上下文来源辅助
 *
 * @author logz
 * @date 2024-03-19
 */
@Scannable
public class OracleContextSourceHelper implements ContextSourceHelper<Oracle> {

    /**
     * 获取数据源
     * @param contextMeta 任务上下文元数据
     */
    @Override
    @SuppressWarnings("unchecked")
    public Source<DebeziumEvent, ?, ?> getSource(ContextMeta contextMeta) {
        // 1. 获取配置
        Pipeline<Oracle> pipeline = (Pipeline<Oracle>) contextMeta.getPipeline();
        OraclePipelineSourceProperties pipelineProps =
                (OraclePipelineSourceProperties) contextMeta.getPipelineSourceProperties();

        // 2. 构造数据源
        return OracleSourceBuilder.OracleIncrementalSource.<DebeziumEvent>builder()
                // URL
                .url(pipelineProps.get(OracleSourceOptions.URL))
                // 主机
                .hostname(pipelineProps.get(OraclePipelineSourceProperties.HOSTNAME))
                // 端口
                .port(pipelineProps.get(OracleSourceOptions.PORT))
                // 数据库名
                .databaseList(new String[]{
                        pipelineProps.get(OraclePipelineSourceProperties.DATABASE_NAME)
                })
                // 表名列表【必须】
                .schemaList(new String[] {
                        pipelineProps.getOptional(OracleSourceOptions.SCHEMA_NAME)
                                .orElseThrow(() -> new IllegalArgumentException("Pipeline Source [schema-name] not specified."))
                })
                // 表名列表
                .tableList(buildTableList(
                        pipelineProps.get(OracleSourceOptions.SCHEMA_NAME),
                        pipeline
                ))
                // 用户名【必须】
                .username(pipelineProps.getOptional(OracleSourceOptions.USERNAME)
                        .orElseThrow(() -> new IllegalArgumentException("Pipeline Source [username] not specified.")))
                // 密码【必须】
                .password(pipelineProps.getOptional(OracleSourceOptions.PASSWORD)
                        .orElseThrow(() -> new IllegalArgumentException("Pipeline Source [password] not specified.")))
                // 数据库的会话时区
                .serverTimeZone(pipelineProps.get(OracleSourceOptions.SERVER_TIME_ZONE))
                // 启动模式
                .startupOptions(buildStartupOptions(pipelineProps))
                // 反序列化器
                .deserializer(new SimpleDebeziumDeserializationSchema())
                // Debezium属性
                .debeziumProperties(buildDebeziumProps(pipelineProps))
                // 快照属性：表快照的分块大小（行数）
                .splitSize(pipelineProps.get(OracleSourceOptions.SCAN_INCREMENTAL_SNAPSHOT_CHUNK_SIZE))
                // 快照属性：拆分元数据的分组大小
                .splitMetaGroupSize(pipelineProps.get(OracleSourceOptions.CHUNK_META_GROUP_SIZE))
                // 快照属性：均匀分布因子的上限
                .distributionFactorUpper(pipelineProps.get(OracleSourceOptions.SPLIT_KEY_EVEN_DISTRIBUTION_FACTOR_UPPER_BOUND))
                // 快照属性：均匀分布因子的下限
                .distributionFactorLower(pipelineProps.get(OracleSourceOptions.SPLIT_KEY_EVEN_DISTRIBUTION_FACTOR_LOWER_BOUND))
                // 快照属性：每次轮询所能获取的最大行数
                .fetchSize(pipelineProps.get(OracleSourceOptions.SCAN_SNAPSHOT_FETCH_SIZE))
                // 连接超时时长
                .connectTimeout(pipelineProps.get(OracleSourceOptions.CONNECT_TIMEOUT))
                // 连接最大重试次数
                .connectMaxRetries(pipelineProps.get(OracleSourceOptions.CONNECT_MAX_RETRIES))
                // 连接池大小
                .connectionPoolSize(pipelineProps.get(OracleSourceOptions.CONNECTION_POOL_SIZE))
                .build();
    }

    /**
     * 构建表名列表
     * @param schema 模式名
     * @param pipeline 管道
     * @return 返回表名数组。当所有绑定的来源表都是“等值表匹配”时，将返回切确的表名数组，否则匹配该模式下所有的表。
     */
    private static String[] buildTableList(String schema, Pipeline<Oracle> pipeline) {
        // 提前退出
        List<Binding<Oracle>> bindings = pipeline.getBindings();
        if (bindings.isEmpty()) {
            return new String[0];
        }

        // 切确的表名列表
        List<String> preciseTables = new ArrayList<>();

        for (Binding<Oracle> binding : bindings) {
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
     * @param pipelineProps Oracle管道来源属性
     * @return 返回启动选项，若启动模式为null则返回null
     */
    private static StartupOptions buildStartupOptions(OraclePipelineSourceProperties pipelineProps) {
        String startupMode = pipelineProps.get(OracleSourceOptions.SCAN_STARTUP_MODE);
        if (startupMode == null) {
            return null;
        }

        // 构建启动模式
        switch (startupMode) {
            // INITIAL：先做快照，再读取最新日志
            case OraclePipelineSourceProperties.StartupMode.INITIAL:
                return StartupOptions.initial();
            // LATEST：跳过快照，仅读取最新日志
            case OraclePipelineSourceProperties.StartupMode.LATEST:
                return StartupOptions.latest();
            default:
                throw new SourceException("Unknown StartupMode: " + startupMode);
        }
    }

    /**
     * 构建Debezium属性
     * @return 返回Debezium属性
     */
    private static Properties buildDebeziumProps(OraclePipelineSourceProperties pipelineProps) {
        // 基础属性
        Properties props = new Properties();
        props.put("log.mining.strategy", "online_catalog");
        props.put("binary.handling.mode", "base64");
        props.put("decimal.handling.mode", "string");
        props.put("interval.handling.mode", "string");
        props.put("lob.enabled", "true");
        props.put("converters", "date, timestamp");
        props.put("date.type", DateFormatConverter.class.getName());
        props.put("timestamp.type", TimestampFormatConverter.class.getName());

        // 可选属性
        pipelineProps.getOptional(OraclePipelineSourceProperties.DEBEZIUM_DATABASE_PDB_NAME)
                .ifPresent(val -> props.put("database.pdb.name", val));

        return props;
    }

}
