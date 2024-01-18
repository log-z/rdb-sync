package vip.logz.rdbsync.connector.starrocks.job.func;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.starrocks.connector.flink.row.sink.StarRocksSinkOP;
import vip.logz.rdbsync.common.exception.UnsupportedRdbSyncEventOpException;
import vip.logz.rdbsync.common.job.RdbSyncEvent;
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
     * 转换映射
     * @param event 数据同步事件
     * @return 返回转换结果
     */
    @Override
    protected String map(RdbSyncEvent event) {
        int op;
        switch (event.getOp()) {
            case UPSERT:
                op = StarRocksSinkOP.UPSERT.ordinal();
                break;
            case DELETE:
                op = StarRocksSinkOP.DELETE.ordinal();
                break;
            default:
                throw new UnsupportedRdbSyncEventOpException(
                        Starrocks.class.getSimpleName(), event.getOp()
                );
        }

        Map<String, Object> record = event.getRecord();
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
