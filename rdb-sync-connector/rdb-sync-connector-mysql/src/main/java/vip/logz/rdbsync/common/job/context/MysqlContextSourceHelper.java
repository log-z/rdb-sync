package vip.logz.rdbsync.common.job.context;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ververica.cdc.connectors.mysql.source.MySqlSource;
import com.ververica.cdc.connectors.mysql.table.StartupOptions;
import org.apache.flink.api.connector.source.Source;
import vip.logz.rdbsync.common.annotations.Scannable;
import vip.logz.rdbsync.common.exception.SourceException;
import vip.logz.rdbsync.common.job.debezium.DebeziumEvent;
import vip.logz.rdbsync.common.job.debezium.SimpleDebeziumDeserializationSchema;
import vip.logz.rdbsync.common.utils.JacksonUtils;
import vip.logz.rdbsync.connector.mysql.config.MysqlPipelineSourceProperties;
import vip.logz.rdbsync.connector.mysql.rule.Mysql;

import java.time.Duration;
import java.util.Properties;

/**
 * Mysql任务上下文来源辅助
 *
 * @author logz
 * @date 2024-01-09
 */
@Scannable
public class MysqlContextSourceHelper implements ContextSourceHelper<Mysql> {

    /** 对象转换器 */
    private final ObjectMapper objectMapper = JacksonUtils.createInstance();

    /**
     * 获取数据源
     * @param contextMeta 任务上下文元数据
     */
    @Override
    public Source<DebeziumEvent, ?, ?> getSource(ContextMeta contextMeta) {
        // 1. 获取配置
        MysqlPipelineSourceProperties pipelineProperties =
                (MysqlPipelineSourceProperties) contextMeta.getPipelineSourceProperties();

        // 构建启动模式
        StartupOptions startupOptions;
        String startupMode = pipelineProperties.getStartupMode();
        switch (startupMode) {
            // INITIAL：先做快照，再读取最新日志
            case MysqlPipelineSourceProperties.STARTUP_MODE_INITIAL:
                startupOptions = StartupOptions.initial();
                break;
            // EARLIEST：跳过快照，从最早可用位置读取日志
            case MysqlPipelineSourceProperties.STARTUP_MODE_EARLIEST:
                startupOptions = StartupOptions.earliest();
                break;
            // LATEST：跳过快照，仅读取最新日志
            case MysqlPipelineSourceProperties.STARTUP_MODE_LATEST:
                startupOptions = StartupOptions.latest();
                break;
            // OFFSET：跳过快照，从指定位置开始读取日志
            case MysqlPipelineSourceProperties.STARTUP_MODE_SPECIFIC_OFFSET:
                String gtidSet = pipelineProperties.getStartupSpecificOffsetGtidSet();
                Long pos = pipelineProperties.getStartupSpecificOffsetPos();
                String file = pipelineProperties.getStartupSpecificOffsetFile();
                if (gtidSet != null) {
                    startupOptions = StartupOptions.specificOffset(gtidSet);
                } else if (file != null) {
                    startupOptions = StartupOptions.specificOffset(file, pos);
                } else {
                    throw new SourceException("StartupMode '" +
                            MysqlPipelineSourceProperties.STARTUP_MODE_SPECIFIC_OFFSET +
                            "' Missing parameter.");
                }
                break;
            // TIMESTAMP：跳过快照，从指定时间戳开始读取日志
            case MysqlPipelineSourceProperties.STARTUP_MODE_TIMESTAMP:
                startupOptions = StartupOptions.timestamp(pipelineProperties.getStartupTimestampMillis());
                break;
            default:
                throw new SourceException("Unknown StartupMode: " + startupMode);
        }

        // 2. 构造数据源
        return MySqlSource.<DebeziumEvent>builder()
                .hostname(pipelineProperties.getHost())
                .port(pipelineProperties.getPort())
                .databaseList(pipelineProperties.getDatabase())
                .username(pipelineProperties.getUsername())
                .password(pipelineProperties.getPassword())
                .connectTimeout(Duration.ofSeconds(pipelineProperties.getConnectTimeoutSeconds()))
                .jdbcProperties(parseJdbcProperties(pipelineProperties.getJdbcProperties()))
                .serverId(pipelineProperties.getServerId())
                .startupOptions(startupOptions)
                .tableList(".*")
                .deserializer(new SimpleDebeziumDeserializationSchema())
                .build();
    }

    /**
     * 解析JDBC属性
     * @param json JSON对象
     */
    private Properties parseJdbcProperties(String json) {
        if (json == null || json.isEmpty()) {
            return new Properties();
        }

        try {
            return objectMapper.readValue(json, Properties.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
