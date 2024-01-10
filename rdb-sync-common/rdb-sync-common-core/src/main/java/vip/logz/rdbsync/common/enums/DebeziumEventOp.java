package vip.logz.rdbsync.common.enums;

/**
 * Debezium事件操作

 * @author logz
 * @date 2024-01-09
 */
public enum DebeziumEventOp {

    /** 新增 */
    CREATE("c"),

    /** 更新 */
    UPDATE("u"),

    /** 删除 */
    DELETE("d"),

    /** 读取（快照） */
    READ("r"),

    /** 截断 */
    TRUNCATE("t"),

    /** 发送消息 */
    MESSAGE("m"),

    /** 未知 */
    UNKNOWN(null),
    ;


    /** 标识符 */
    private final String tag;

    /**
     * 构造器
     * @param tag 标识符
     */
    DebeziumEventOp(String tag) {
        this.tag = tag;
    }

    /**
     * 获取标识符
     */
    public String getTag() {
        return tag;
    }

    /**
     * 解析
     * @param tag 标识符
     * @return 返回匹配的Debezium事件操作，若都不匹配则返回 {@link #UNKNOWN}
     */
    public static DebeziumEventOp parse(String tag) {
        for (DebeziumEventOp op : values()) {
            if (op.tag.equals(tag)) {
                return op;
            }
        }

        return UNKNOWN;
    }

}
