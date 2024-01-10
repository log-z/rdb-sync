package vip.logz.rdbsync.common.job.func.map;

import org.apache.flink.api.common.functions.MapFunction;
import vip.logz.rdbsync.common.enums.DebeziumEventOp;
import vip.logz.rdbsync.common.exception.UnsupportedDebeziumEventOpException;
import vip.logz.rdbsync.common.job.debezium.DebeziumEvent;
import vip.logz.rdbsync.common.rule.Rdb;
import vip.logz.rdbsync.common.rule.table.Mapping;
import vip.logz.rdbsync.common.rule.table.MappingField;

import java.util.Map;

/**
 * 抽象的Debezium事件到出口的转换映射
 *
 * @author logz
 * @date 2024-01-09
 */
public abstract class AbstractDebeziumEventToSinkMap<DistDB extends Rdb, T> implements MapFunction<DebeziumEvent, T> {

    /** 表映射 */
    private final Mapping<DistDB> mapping;

    /**
     * 构造器
     * @param mapping 表映射
     */
    public AbstractDebeziumEventToSinkMap(Mapping<DistDB> mapping) {
        this.mapping = mapping;
    }

    /**
     * 转化映射
     * @param event Debezium事件
     * @return 返回转化结果
     * @throws Exception 参考 {@link MapFunction#map(Object)}
     */
    @Override
    public T map(DebeziumEvent event) throws Exception {
        // 是否为“更新或删除”的标志
        boolean isUpsert;
        // 有意义的记录
        Map<String, Object> record;

        switch (DebeziumEventOp.parse(event.getOp())) {
            // 快照、新增、更新操作：等价于“更新或新增”
            case READ:
            case CREATE:
            case UPDATE: {
                isUpsert = true;
                record = event.getAfter();  // 变更后的记录有意义
                break;
            }
            // 删除操作
            case DELETE: {
                isUpsert = false;
                record = event.getBefore();  // 删除前的记录有意义
                break;
            }
            default: {
                throw new UnsupportedDebeziumEventOpException(event.getOp());
            }
        }

        // 逐个字段进行转换
        for (MappingField<?> field : mapping.getFields()) {
            String fieldName = field.getName();
            Object val = record.remove(fieldName);
            Object valFinal = field.getType().convart(val);
            record.put(fieldName, valFinal);
        }

        // 视情况进行后续处理
        return isUpsert ? adaptUpsert(record) : adaptDelete(record);
    }

    /**
     * 适配更新或新增的后续处理
     * @param record 变更后的记录
     * @return 返回转化结果
     */
    protected abstract T adaptUpsert(Map<String, Object> record);

    /**
     * 适配删除的后续处理
     * @param record 删除前的记录
     * @return 返回转化结果
     */
    protected abstract T adaptDelete(Map<String, Object> record);

}
