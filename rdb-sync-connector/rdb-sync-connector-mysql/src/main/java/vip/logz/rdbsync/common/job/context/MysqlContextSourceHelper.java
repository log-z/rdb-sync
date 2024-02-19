package vip.logz.rdbsync.common.job.context;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ververica.cdc.connectors.mysql.source.MySqlSource;
import com.ververica.cdc.connectors.mysql.source.MySqlSourceBuilder;
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
import vip.logz.rdbsync.common.utils.BuildHelper;
import vip.logz.rdbsync.common.utils.JacksonUtils;
import vip.logz.rdbsync.common.utils.sql.DDLGenerator;
import vip.logz.rdbsync.connector.mysql.config.MysqlOptions;
import vip.logz.rdbsync.connector.mysql.config.MysqlPipelineSourceProperties;
import vip.logz.rdbsync.connector.mysql.job.debezium.DateFormatConverter;
import vip.logz.rdbsync.connector.mysql.job.debezium.DatetimeFormatConverter;
import vip.logz.rdbsync.connector.mysql.job.debezium.TimeFormatConverter;
import vip.logz.rdbsync.connector.mysql.rule.Mysql;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.function.Function;

/**
 * Mysql任务上下文来源辅助
 *
 * @author logz
 * @date 2024-01-09
 */
@Scannable
public class MysqlContextSourceHelper implements ContextSourceHelper<Mysql> {

    /** 构建辅助工具 */
    private final BuildHelper buildHelper = BuildHelper.create();

    /** 对象转换器 */
    private final ObjectMapper objectMapper = JacksonUtils.createInstance();

    /**
     * 获取数据源
     * @param contextMeta 任务上下文元数据
     */
    @Override
    @SuppressWarnings("unchecked")
    public Source<DebeziumEvent, ?, ?> getSource(ContextMeta contextMeta) {
        // 1. 获取配置
        Pipeline<Mysql> pipeline = (Pipeline<Mysql>) contextMeta.getPipeline();
        MysqlPipelineSourceProperties pipelineProperties =
                (MysqlPipelineSourceProperties) contextMeta.getPipelineSourceProperties();

        // 2. 构造数据源
        MySqlSourceBuilder<DebeziumEvent> builder = MySqlSource.builder();
        buildHelper
                // 主机
                .set(builder::hostname, pipelineProperties.getHost(), MysqlOptions.DEFAULT_HOST)
                // 端口
                .set(builder::port, pipelineProperties.getPort(), MysqlOptions.DEFAULT_PORT)
                // 数据库名列表【必须】
                .set(
                        (Function<? super String[], ?>) builder::databaseList,
                        new String[]{pipelineProperties.getDatabase()}
                )
                // 表名列表
                .set(
                        (Function<? super String[], ?>) builder::tableList,
                        buildTableList(pipelineProperties.getDatabase(), pipeline)
                )
                // 用户名
                .set(builder::username, pipelineProperties.getUsername(), MysqlOptions.DEFAULT_USERNAME)
                // 密码
                .set(builder::password, pipelineProperties.getPassword(), MysqlOptions.DEFAULT_PASSWORD)
                // 模拟服务端ID
                .setIfNotNull(builder::serverId, pipelineProperties.getServerId())
                // 数据库的会话时区
                .setIfNotNull(builder::serverTimeZone, pipelineProperties.getServerTimeZone())
                // 启动模式
                .setIfNotNull(builder::startupOptions, buildStartupOptions(pipelineProperties))
                // 反序列化器
                .set(builder::deserializer, new SimpleDebeziumDeserializationSchema())
                // Debezium属性
                .set(builder::debeziumProperties, buildDebeziumProps())
                // JDBC属性
                .setIfNotNull(builder::jdbcProperties, buildJdbcProps(pipelineProperties.getJdbcProperties()))
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
        String startupMode = pipelineProperties.getStartupMode();
        if (startupMode == null) {
            return null;
        }

        // 构建启动选项
        switch (startupMode) {
            // INITIAL：先做快照，再读取最新日志
            case MysqlPipelineSourceProperties.STARTUP_MODE_INITIAL:
                return StartupOptions.initial();
            // EARLIEST：跳过快照，从最早可用位置读取日志
            case MysqlPipelineSourceProperties.STARTUP_MODE_EARLIEST:
                return StartupOptions.earliest();
            // LATEST：跳过快照，仅读取最新日志
            case MysqlPipelineSourceProperties.STARTUP_MODE_LATEST:
                return StartupOptions.latest();
            // OFFSET：跳过快照，从指定位置开始读取日志
            case MysqlPipelineSourceProperties.STARTUP_MODE_SPECIFIC_OFFSET:
                String gtidSet = pipelineProperties.getStartupSpecificOffsetGtidSet();
                Long pos = pipelineProperties.getStartupSpecificOffsetPos();
                String file = pipelineProperties.getStartupSpecificOffsetFile();
                if (gtidSet != null) {
                    return StartupOptions.specificOffset(gtidSet);
                } else if (file != null) {
                    return StartupOptions.specificOffset(file, pos);
                } else {
                    throw new SourceException("StartupMode '" +
                            MysqlPipelineSourceProperties.STARTUP_MODE_SPECIFIC_OFFSET +
                            "' Missing parameter.");
                }
            // TIMESTAMP：跳过快照，从指定时间戳开始读取日志
            case MysqlPipelineSourceProperties.STARTUP_MODE_TIMESTAMP:
                return StartupOptions.timestamp(pipelineProperties.getStartupTimestampMillis());
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

    /**
     * 构建JDBC属性
     * @param json JSON对象
     */
    private Properties buildJdbcProps(String json) {
        if (json == null || json.isEmpty()) {
            return null;
        }

        try {
            return objectMapper.readValue(json, Properties.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
