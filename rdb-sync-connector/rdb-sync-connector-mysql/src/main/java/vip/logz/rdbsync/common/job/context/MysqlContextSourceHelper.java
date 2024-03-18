package vip.logz.rdbsync.common.job.context;

import com.ververica.cdc.connectors.mysql.source.MySqlSource;
import com.ververica.cdc.connectors.mysql.source.config.MySqlSourceOptions;
import com.ververica.cdc.connectors.mysql.table.StartupOptions;
import org.apache.flink.api.connector.source.Source;
import vip.logz.rdbsync.common.annotations.Scannable;
import vip.logz.rdbsync.common.exception.SourceException;
import vip.logz.rdbsync.common.job.debezium.DebeziumEvent;
import vip.logz.rdbsync.common.job.debezium.SimpleDebeziumDeserializationSchema;
import vip.logz.rdbsync.common.rule.Binding;
import vip.logz.rdbsync.common.rule.Pipeline;
import vip.logz.rdbsync.common.rule.table.EqualTableMatcher;
import vip.logz.rdbsync.common.rule.table.TableMatcher;
import vip.logz.rdbsync.common.utils.sql.DDLGenerator;
import vip.logz.rdbsync.connector.mysql.config.MysqlPipelineSourceProperties;
import vip.logz.rdbsync.connector.mysql.job.debezium.DateFormatConverter;
import vip.logz.rdbsync.connector.mysql.job.debezium.DatetimeFormatConverter;
import vip.logz.rdbsync.connector.mysql.job.debezium.TimeFormatConverter;
import vip.logz.rdbsync.connector.mysql.rule.Mysql;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Mysql任务上下文来源辅助
 *
 * @author logz
 * @date 2024-01-09
 */
@Scannable
public class MysqlContextSourceHelper implements ContextSourceHelper<Mysql> {

    /**
     * 获取数据源
     * @param contextMeta 任务上下文元数据
     */
    @Override
    @SuppressWarnings("unchecked")
    public Source<DebeziumEvent, ?, ?> getSource(ContextMeta contextMeta) {
        // 1. 获取配置
        Pipeline<Mysql> pipeline = (Pipeline<Mysql>) contextMeta.getPipeline();
        MysqlPipelineSourceProperties pipelineProps =
                (MysqlPipelineSourceProperties) contextMeta.getPipelineSourceProperties();

        // 2. 构造数据源
        return MySqlSource.<DebeziumEvent>builder()
                // 主机
                .hostname(pipelineProps.get(MysqlPipelineSourceProperties.HOSTNAME))
                // 端口
                .port(pipelineProps.get(MySqlSourceOptions.PORT))
                // 数据库名列表【必须】
                .databaseList(pipelineProps.getOptional(MySqlSourceOptions.DATABASE_NAME)
                        .map(db -> new String[]{db})
                        .orElseThrow(() -> new IllegalArgumentException("Pipeline Source [database-name] not specified.")))
                // 表名列表
                .tableList(buildTableList(pipelineProps.get(MySqlSourceOptions.DATABASE_NAME), pipeline))
                // 用户名
                .username(pipelineProps.get(MysqlPipelineSourceProperties.USERNAME))
                // 密码
                .password(pipelineProps.get(MysqlPipelineSourceProperties.PASSWORD))
                // 模拟服务端ID
                .serverId(pipelineProps.get(MySqlSourceOptions.SERVER_ID))
                // 数据库的会话时区
                .serverTimeZone(pipelineProps.getOptional(MySqlSourceOptions.SERVER_TIME_ZONE)
                        .orElseGet(() -> ZoneId.systemDefault().getId()))
                // 启动模式
                .startupOptions(buildStartupOptions(pipelineProps))
                // 反序列化器
                .deserializer(new SimpleDebeziumDeserializationSchema())
                // Debezium属性
                .debeziumProperties(buildDebeziumProps())
                // JDBC属性
                .jdbcProperties(buildJdbcProps(pipelineProps))
                // 快照属性：表快照的分块大小（行数）
                .splitSize(pipelineProps.get(MySqlSourceOptions.SCAN_INCREMENTAL_SNAPSHOT_CHUNK_SIZE))
                // 快照属性：拆分元数据的分组大小
                .splitMetaGroupSize(pipelineProps.get(MySqlSourceOptions.CHUNK_META_GROUP_SIZE))
                // 快照属性：均匀分布因子的上限
                .distributionFactorUpper(pipelineProps.get(MySqlSourceOptions.CHUNK_KEY_EVEN_DISTRIBUTION_FACTOR_UPPER_BOUND))
                // 快照属性：均匀分布因子的下限
                .distributionFactorLower(pipelineProps.get(MySqlSourceOptions.CHUNK_KEY_EVEN_DISTRIBUTION_FACTOR_LOWER_BOUND))
                // 快照属性：每次轮询所能获取的最大行数
                .fetchSize(pipelineProps.get(MySqlSourceOptions.SCAN_SNAPSHOT_FETCH_SIZE))
                // 连接超时时长
                .connectTimeout(pipelineProps.get(MySqlSourceOptions.CONNECT_TIMEOUT))
                // 连接最大重试次数
                .connectMaxRetries(pipelineProps.get(MySqlSourceOptions.CONNECT_MAX_RETRIES))
                // 连接池大小
                .connectionPoolSize(pipelineProps.get(MySqlSourceOptions.CONNECTION_POOL_SIZE))
                // 心跳检测间隔时长
                .heartbeatInterval(pipelineProps.get(MySqlSourceOptions.HEARTBEAT_INTERVAL))
                .build();
    }

    /**
     * 构建表名列表
     * @param database 当前数据库名，用于确定表的完全限定名称
     * @param pipeline 管道
     * @return 返回表名数组。当所有绑定的来源表都是“等值表匹配”时，将返回切确的表名数组，否则匹配当前数据库下所有的表。
     */
    private static String[] buildTableList(String database, Pipeline<Mysql> pipeline) {
        // 提前退出
        List<Binding<Mysql>> bindings = pipeline.getBindings();
        if (bindings.isEmpty()) {
            return new String[0];
        }

        // 切确的表名列表
        List<String> preciseTables = new ArrayList<>();

        for (Binding<Mysql> binding : bindings) {
            TableMatcher sourceTableMatcher = binding.getSourceTableMatcher();
            if (sourceTableMatcher instanceof EqualTableMatcher) {
                String table = ((EqualTableMatcher) sourceTableMatcher).getTable();
                preciseTables.add(database + DDLGenerator.TOKEN_REF_DELIMITER + table);
                continue;
            }

            // 使用了其它来源表匹配器
            preciseTables.clear();
            break;
        }

        return preciseTables.isEmpty() ?
                new String[] {".*"} :  // 匹配当前数据库下所有的表
                preciseTables.toArray(String[]::new);  // 切确的表名数组
    }

    /**
     * 构建启动选项
     * @param pipelineProperties MySQL管道来源属性
     * @return 返回启动选项，若启动模式为null则返回null
     */
    private static StartupOptions buildStartupOptions(MysqlPipelineSourceProperties pipelineProperties) {
        String startupMode = pipelineProperties.get(MySqlSourceOptions.SCAN_STARTUP_MODE);

        // 构建启动选项
        switch (startupMode) {
            // INITIAL：先做快照，再读取最新日志
            case MysqlPipelineSourceProperties.StartupMode.INITIAL:
                return StartupOptions.initial();
            // EARLIEST：跳过快照，从最早可用位置读取日志
            case MysqlPipelineSourceProperties.StartupMode.EARLIEST:
                return StartupOptions.earliest();
            // LATEST：跳过快照，仅读取最新日志
            case MysqlPipelineSourceProperties.StartupMode.LATEST:
                return StartupOptions.latest();
            // SPECIFIC_OFFSET：跳过快照，从指定位置开始读取日志
            case MysqlPipelineSourceProperties.StartupMode.SPECIFIC_OFFSET:
                String gtidSet = pipelineProperties.get(MySqlSourceOptions.SCAN_STARTUP_SPECIFIC_OFFSET_GTID_SET);
                Long pos = pipelineProperties.get(MySqlSourceOptions.SCAN_STARTUP_SPECIFIC_OFFSET_POS);
                String file = pipelineProperties.get(MySqlSourceOptions.SCAN_STARTUP_SPECIFIC_OFFSET_FILE);
                if (gtidSet != null) {
                    return StartupOptions.specificOffset(gtidSet);
                } else if (file != null && pos != null) {
                    return StartupOptions.specificOffset(file, pos);
                } else {
                    throw new SourceException("StartupMode '" +
                            MysqlPipelineSourceProperties.StartupMode.SPECIFIC_OFFSET +
                            "' Missing parameter.");
                }
            // TIMESTAMP：跳过快照，从指定时间戳开始读取日志
            case MysqlPipelineSourceProperties.StartupMode.TIMESTAMP:
                return StartupOptions.timestamp(pipelineProperties.get(MySqlSourceOptions.SCAN_STARTUP_TIMESTAMP_MILLIS));
            default:
                throw new SourceException("Unknown StartupMode: " + startupMode);
        }
    }

    /**
     * 构建Debezium属性
     * @return 返回Debezium属性
     */
    private static Properties buildDebeziumProps() {
        Properties props = new Properties();
        props.put("converters", "date, time, datetime");
        props.put("date.type", DateFormatConverter.class.getName());
        props.put("time.type", TimeFormatConverter.class.getName());
        props.put("datetime.type", DatetimeFormatConverter.class.getName());

        return props;
    }

    /** JDBC属性之间的分隔符 */
    private static final String JDBC_PROPS_DELIMITER = "&";

    /** JDBC属性中键和值的分隔符 */
    private static final String JDBC_PROPS_KV_DELIMITER = "=";

    /**
     * 构建JDBC属性
     * @param pipelineProperties MySQL管道来源属性
     */
    private Properties buildJdbcProps(MysqlPipelineSourceProperties pipelineProperties) {
        Properties pros = new Properties();
        String jdbcProps = pipelineProperties.get(MysqlPipelineSourceProperties.JDBC_PROPS);
        if (jdbcProps == null || jdbcProps.isEmpty()) {
            return pros;
        }

        for (String kv : jdbcProps.split(JDBC_PROPS_DELIMITER)) {
            String[] kvTuple = kv.split(JDBC_PROPS_KV_DELIMITER, 2);
            if (kvTuple.length == 2) {
                String key = URLDecoder.decode(kvTuple[0].strip(), StandardCharsets.UTF_8);
                String value = URLDecoder.decode(kvTuple[1].strip(), StandardCharsets.UTF_8);
                pros.put(key, value);
            }
        }

        return pros;
    }

}
