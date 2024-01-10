package vip.logz.rdbsync.common.rule;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 频道
 *
 * @author logz
 * @date 2024-01-10
 * @param <DistDB> 目标数据库实现
 */
public class Channel<DistDB extends Rdb> implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    private String id;

    /**
     * 来源ID
     * @see vip.logz.rdbsync.common.config.ChannelSourceProperties
     */
    private String sourceId;

    /**
     * 目标ID
     * @see vip.logz.rdbsync.common.config.ChannelDistProperties
     */
    private String distId;

    /**
     * 来源与目标表的绑定列表
     */
    private final List<Binding<DistDB>> bindings = new ArrayList<>();

    /**
     * 获取ID
     */
    public String getId() {
        return id;
    }

    /**
     * 设置ID
     * @param id ID
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 获取来源ID
     * @see vip.logz.rdbsync.common.config.ChannelSourceProperties
     */
    public String getSourceId() {
        return sourceId;
    }

    /**
     * 设置来源ID
     * @param sourceId 来源ID
     * @see vip.logz.rdbsync.common.config.ChannelSourceProperties
     */
    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    /**
     * 获取目标ID
     * @see vip.logz.rdbsync.common.config.ChannelDistProperties
     */
    public String getDistId() {
        return distId;
    }

    /**
     * 设置目标ID
     * @param distId 目标ID
     * @see vip.logz.rdbsync.common.config.ChannelDistProperties
     */
    public void setDistId(String distId) {
        this.distId = distId;
    }

    /**
     * 获取来源与目标表的绑定列表
     */
    public List<Binding<DistDB>> getBindings() {
        return bindings;
    }

}
