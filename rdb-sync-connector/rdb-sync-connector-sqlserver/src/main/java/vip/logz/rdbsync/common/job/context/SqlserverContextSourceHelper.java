package vip.logz.rdbsync.common.job.context;

import com.ververica.cdc.connectors.base.options.StartupOptions;
import com.ververica.cdc.connectors.sqlserver.source.SqlServerSourceBuilder;
import org.apache.flink.api.connector.source.Source;
import vip.logz.rdbsync.common.annotations.Scannable;
import vip.logz.rdbsync.common.exception.SourceException;
import vip.logz.rdbsync.common.job.debezium.DebeziumEvent;
import vip.logz.rdbsync.common.job.debezium.SimpleDebeziumDeserializationSchema;
import vip.logz.rdbsync.connector.sqlserver.config.SqlserverPipelineSourceProperties;
import vip.logz.rdbsync.connector.sqlserver.rule.Sqlserver;

/**
 * SQLServer任务上下文来源辅助
 *
 * @author logz
 * @date 2024-01-29
 */
@Scannable
public class SqlserverContextSourceHelper implements ContextSourceHelper<Sqlserver> {

    /**
     * 获取数据源
     * @param contextMeta 任务上下文元数据
     */
    @Override
    @SuppressWarnings("unchecked")
    public Source<DebeziumEvent, ?, ?> getSource(ContextMeta contextMeta) {
        // 1. 获取配置
        SqlserverPipelineSourceProperties pipelineProperties =
                (SqlserverPipelineSourceProperties) contextMeta.getPipelineSourceProperties();

        // 构建启动模式
        StartupOptions startupOptions;
        String startupMode = pipelineProperties.getStartupMode();
        switch (startupMode) {
            // INITIAL：先做快照，再读取最新日志
            case SqlserverPipelineSourceProperties.STARTUP_MODE_INITIAL:
                startupOptions = StartupOptions.initial();
                break;
            // LATEST：跳过快照，仅读取最新日志
            case SqlserverPipelineSourceProperties.STARTUP_MODE_LATEST:
                startupOptions = StartupOptions.latest();
                break;
            default:
                throw new SourceException("Unknown StartupMode: " + startupMode);
        }

        // 2. 构造数据源
        return new SqlServerSourceBuilder<DebeziumEvent>()
                .hostname(pipelineProperties.getHost())
                .port(pipelineProperties.getPort())
                .databaseList(pipelineProperties.getDatabase())
                .username(pipelineProperties.getUsername())
                .password(pipelineProperties.getPassword())
                .startupOptions(startupOptions)
                .tableList(pipelineProperties.getSchema() + ".*")
                .deserializer(new SimpleDebeziumDeserializationSchema())
                .build();
    }

}
