package vip.logz.rdbsync.connector.starrocks.job.func;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.starrocks.connector.flink.row.sink.StarRocksSinkOP;
import vip.logz.rdbsync.common.job.func.map.AbstractDebeziumEventToSinkMap;
import vip.logz.rdbsync.common.rule.table.Mapping;
import vip.logz.rdbsync.common.utils.JacksonUtils;
import vip.logz.rdbsync.connector.starrocks.rule.Starrocks;

import java.util.Map;

/**
 * Debezium事件同步到StarRocks的转换映射
 *
 * @author logz
 * @date 2024-01-09
 */
public class DebeziumEventToStarrocksMap extends AbstractDebeziumEventToSinkMap<Starrocks, String> {

    /** 对象转换器 */
    protected final ObjectMapper objectMapper = JacksonUtils.createInstance();

    /**
     * 构造器
     * @param mapping 表映射
     */
    public DebeziumEventToStarrocksMap(Mapping<Starrocks> mapping) {
        super(mapping);
    }

    /**
     * 适配更新或新增的后续处理
     * @param record 变更后的记录
     * @return 返回转化结果
     */
    @Override
    protected String adaptUpsert(Map<String, Object> record) {
        int op = StarRocksSinkOP.UPSERT.ordinal();
        record.put(StarRocksSinkOP.COLUMN_KEY, op);
        return toJsonString(record);
    }

    /**
     * 适配删除的后续处理
     * @param record 删除前的记录
     * @return 返回转化结果
     */
    @Override
    protected String adaptDelete(Map<String, Object> record) {
        int op = StarRocksSinkOP.DELETE.ordinal();
        record.put(StarRocksSinkOP.COLUMN_KEY, op);
        return toJsonString(record);
    }

    /**
     * 转化为JSON字符串
     * @param val 任何值
     */
    private String toJsonString(Object val) {
        try {
            return objectMapper.writeValueAsString(val);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }

}
