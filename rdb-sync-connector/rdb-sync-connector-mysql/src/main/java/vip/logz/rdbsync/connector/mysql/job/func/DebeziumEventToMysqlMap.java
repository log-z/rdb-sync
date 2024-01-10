package vip.logz.rdbsync.connector.mysql.job.func;

import vip.logz.rdbsync.common.job.func.map.AbstractDebeziumEventToSinkMap;
import vip.logz.rdbsync.common.rule.table.Mapping;
import vip.logz.rdbsync.connector.mysql.rule.Mysql;

import java.util.Map;

/**
 * Debezium事件同步到MySQL的转换映射
 *
 * @author logz
 * @date 2024-01-09
 */
public class DebeziumEventToMysqlMap extends AbstractDebeziumEventToSinkMap<Mysql, Map<String, Object>> {

    /**
     * 构造器
     * @param mapping 表映射
     */
    public DebeziumEventToMysqlMap(Mapping<Mysql> mapping) {
        super(mapping);
    }

    /**
     * 适配更新或新增的后续处理
     * @param record 变更后的记录
     * @return 返回转化结果
     */
    @Override
    protected Map<String, Object> adaptUpsert(Map<String, Object> record) {
        // 无需进一步处理
        return record;
    }

    /**
     * 适配删除的后续处理
     * @param record 删除前的记录
     * @return 返回转化结果
     */
    @Override
    protected Map<String, Object> adaptDelete(Map<String, Object> record) {
        // 无需进一步处理
        return record;
    }

}
