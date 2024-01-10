package vip.logz.rdbsync.common.job.context;

import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.util.OutputTag;
import vip.logz.rdbsync.common.enums.SideOutputOp;
import vip.logz.rdbsync.common.job.debezium.DebeziumEvent;

/**
 * 旁路输出标签
 *
 * @author logz
 * @date 2024-01-09
 */
public class SideOutputTag extends OutputTag<DebeziumEvent> {

    private static final long serialVersionUID = 1L;

    /** ID分隔符 */
    private static final String ID_DELIMITER = "|";

    /** 表名 */
    private final String table;

    /** 操作 */
    private final SideOutputOp op;

    /**
     * 构造器
     * @param table 表名
     * @param op 操作
     */
    public SideOutputTag(String table, SideOutputOp op) {
        super(op.name() + ID_DELIMITER + table, TypeInformation.of(DebeziumEvent.class));
        this.table = table;
        this.op = op;
    }

    /**
     * 通过ID解析旁路输出标签
     * @param id ID
     * @return 旁路输出标签，若格式不正确将抛出 {@link IllegalArgumentException} 异常
     */
    public static SideOutputTag of(String id) {
        for (SideOutputOp op : SideOutputOp.values()) {
            if (!id.startsWith(op.name())) {
                continue;
            }

            String prefix = op.name();
            String table = id.substring(prefix.length() + ID_DELIMITER.length());
            return new SideOutputTag(table, op);
        }

        throw new IllegalArgumentException(id);
    }

    /**
     * 获取操作
     */
    public SideOutputOp getOp() {
        return op;
    }

    /**
     * 获取表名
     */
    public String getTable() {
        return table;
    }

}
