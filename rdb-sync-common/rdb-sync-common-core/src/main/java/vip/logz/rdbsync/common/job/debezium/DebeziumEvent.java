package vip.logz.rdbsync.common.job.debezium;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * Debezium事件
 *
 * @author logz
 * @date 2024-01-09
 */
public class DebeziumEvent {

    /** 来源信息 */
    private DebeziumEventSource source;

    /** 操作标识符 */
    private String op;

    /** 时间戳 */
    @JsonProperty("ts_ms")
    private Long tsMs;

    /** 记录变更前 */
    private Map<String, Object> before;

    /** 记录变更后 */
    private Map<String, Object> after;

    /**
     * 获取来源信息
     */
    public DebeziumEventSource getSource() {
        return source;
    }

    /**
     * 设置来源信息
     * @param source 来源信息
     */
    public void setSource(DebeziumEventSource source) {
        this.source = source;
    }

    /**
     * 获取操作标识符
     */
    public String getOp() {
        return op;
    }

    /**
     * 设置操作标识符
     * @param op 操作标识符
     */
    public void setOp(String op) {
        this.op = op;
    }

    /**
     * 获取时间戳
     */
    public Long getTsMs() {
        return tsMs;
    }

    /**
     * 设置时间戳
     * @param tsMs 时间戳
     */
    public void setTsMs(Long tsMs) {
        this.tsMs = tsMs;
    }

    /**
     * 获取记录变更前
     */
    public Map<String, Object> getBefore() {
        return before;
    }

    /**
     * 设置记录变更前
     * @param before 记录变更前
     */
    public void setBefore(Map<String, Object> before) {
        this.before = before;
    }

    /**
     * 获取记录变更后
     */
    public Map<String, Object> getAfter() {
        return after;
    }

    /**
     * 设置记录变更后
     * @param after 记录变更后
     */
    public void setAfter(Map<String, Object> after) {
        this.after = after;
    }

}
