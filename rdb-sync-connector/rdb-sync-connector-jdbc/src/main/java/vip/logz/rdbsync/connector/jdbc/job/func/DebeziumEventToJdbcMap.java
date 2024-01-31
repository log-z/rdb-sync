package vip.logz.rdbsync.connector.jdbc.job.func;

import vip.logz.rdbsync.common.job.RdbSyncEvent;
import vip.logz.rdbsync.common.job.func.map.AbstractDebeziumEventToSinkMap;
import vip.logz.rdbsync.common.rule.Rdb;
import vip.logz.rdbsync.common.rule.table.Mapping;

/**
 * Debezium事件同步到JDBC的转换映射
 *
 * @author logz
 * @date 2024-01-09
 */
public class DebeziumEventToJdbcMap<DistDB extends Rdb> extends AbstractDebeziumEventToSinkMap<DistDB, RdbSyncEvent> {

    /**
     * 构造器
     * @param mapping 表映射
     */
    public DebeziumEventToJdbcMap(Mapping<DistDB> mapping) {
        super(mapping);
    }

    /**
     * 转换映射
     * @param event 数据同步事件
     * @return 返回转换结果
     */
    @Override
    protected RdbSyncEvent map(RdbSyncEvent event) {
        // 无需进一步处理
        return event;
    }

}
