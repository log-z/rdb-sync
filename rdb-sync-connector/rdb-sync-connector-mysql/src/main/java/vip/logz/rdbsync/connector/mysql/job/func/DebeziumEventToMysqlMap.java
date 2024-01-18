package vip.logz.rdbsync.connector.mysql.job.func;

import vip.logz.rdbsync.common.job.RdbSyncEvent;
import vip.logz.rdbsync.common.job.func.map.AbstractDebeziumEventToSinkMap;
import vip.logz.rdbsync.common.rule.table.Mapping;
import vip.logz.rdbsync.connector.mysql.rule.Mysql;

/**
 * Debezium事件同步到MySQL的转换映射
 *
 * @author logz
 * @date 2024-01-09
 */
public class DebeziumEventToMysqlMap extends AbstractDebeziumEventToSinkMap<Mysql, RdbSyncEvent> {

    /**
     * 构造器
     * @param mapping 表映射
     */
    public DebeziumEventToMysqlMap(Mapping<Mysql> mapping) {
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
