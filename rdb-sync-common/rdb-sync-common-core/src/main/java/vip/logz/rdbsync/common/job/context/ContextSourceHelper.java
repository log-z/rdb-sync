package vip.logz.rdbsync.common.job.context;

import org.apache.flink.api.connector.source.Source;
import vip.logz.rdbsync.common.job.debezium.DebeziumEvent;
import vip.logz.rdbsync.common.rule.Rdb;

/**
 * 任务上下文来源辅助
 *
 * @author logz
 * @date 2024-01-09
 * @param <SourceDB> 来源数据库实现
 */
public interface ContextSourceHelper<SourceDB extends Rdb> {

    /**
     * 获取数据源
     * @param contextMeta 任务上下文元数据
     */
    Source<DebeziumEvent, ?, ?> getSource(ContextMeta contextMeta);

}
