package vip.logz.rdbsync.common.job;

import vip.logz.rdbsync.common.enums.RdbSyncEventOp;

import java.io.Serializable;
import java.util.Map;

/**
 * 数据同步事件
 *
 * @author logz
 * @date 2024-01-17
 */
public class RdbSyncEvent implements Serializable {

    /** 操作 */
    private final RdbSyncEventOp op;

    /** 有意义的记录 */
    private final Map<String, Object> record;

    /**
     * 构造器
     * @param op 操作
     * @param record 有意义的记录
     */
    public RdbSyncEvent(RdbSyncEventOp op, Map<String, Object> record) {
        this.op = op;
        this.record = record;
    }

    /**
     * 获取操作
     */
    public RdbSyncEventOp getOp() {
        return op;
    }

    /**
     * 获取有意义的记录
     */
    public Map<String, Object> getRecord() {
        return record;
    }

}
