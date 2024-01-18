package vip.logz.rdbsync.common.job.context;

import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.util.OutputTag;
import vip.logz.rdbsync.common.job.debezium.DebeziumEvent;

/**
 * 旁路输出标签
 *
 * @author logz
 * @date 2024-01-09
 */
public class SideOutputTag extends OutputTag<DebeziumEvent> {

    private static final long serialVersionUID = 2L;

    /** 表名 */
    private final String table;

    /**
     * 构造器
     * @param table 表名
     */
    public SideOutputTag(String table) {
        super(table, TypeInformation.of(DebeziumEvent.class));
        this.table = table;
    }

    /**
     * 通过ID解析旁路输出标签
     * @param id ID
     * @return 旁路输出标签
     */
    public static SideOutputTag of(String id) {
        return new SideOutputTag(id);
    }

    /**
     * 获取表名
     */
    public String getTable() {
        return table;
    }

}
