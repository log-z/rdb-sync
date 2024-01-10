package vip.logz.rdbsync.common.rule;

import vip.logz.rdbsync.common.rule.table.EqualTableMatcher;
import vip.logz.rdbsync.common.rule.table.Mapping;
import vip.logz.rdbsync.common.rule.table.TableMatcher;

/**
 * 频道构建器
 *
 * @author logz
 * @date 2024-01-10
 * @param <DistDB> 目标数据库实现
 */
public class ChannelBuilder<DistDB extends Rdb> implements Builder<ChannelBuilder<DistDB>> {

    /** 频道 */
    private final Channel<DistDB> channel = new Channel<>();

    /**
     * 构建器
     * @param channelId 频道ID
     */
    private ChannelBuilder(String channelId) {
        this.channel.setId(channelId);
    }

    /**
     * 设置来源ID
     * @param sourceId 来源ID
     * @return 返回当前对象
     * @see vip.logz.rdbsync.common.config.ChannelSourceProperties
     */
    public ChannelBuilder<DistDB> sourceId(String sourceId) {
        channel.setSourceId(sourceId);
        return this;
    }

    /**
     * 设置目标ID
     * @param distId 目标ID
     * @return 返回当前对象
     * @see vip.logz.rdbsync.common.config.ChannelDistProperties
     */
    public ChannelBuilder<DistDB> distId(String distId) {
        channel.setDistId(distId);
        return this;
    }

    /**
     * 设置来源与目标表的绑定
     * @param sourceTable 来源表名
     * @param distTable 目标表名
     * @param mapping 表映射
     * @return 返回当前对象
     */
    public ChannelBuilder<DistDB> binding(String sourceTable, String distTable, Mapping<DistDB> mapping) {
        return binding(EqualTableMatcher.of(sourceTable), distTable, mapping);
    }

    /**
     * 设置来源与目标表的绑定
     * @param sourceTableMatcher 来源表匹配器
     * @param distTable 目标表名
     * @param mapping 表映射
     * @return 返回当前对象
     */
    public ChannelBuilder<DistDB> binding(TableMatcher sourceTableMatcher, String distTable, Mapping<DistDB> mapping) {
        Binding<DistDB> binding = new Binding<>(sourceTableMatcher, distTable, mapping);
        channel.getBindings().add(binding);
        return this;
    }

    /**
     * 退出当前构建器，回到外围构建器
     * @return 当前构建器已经是最外围，将返回它本身
     */
    @Override
    public ChannelBuilder<DistDB> and() {
        return this;
    }

    /**
     * 执行构建
     * @return 返回频道
     */
    public Channel<DistDB> build() {
        return channel;
    }

    /**
     * 工厂方法
     * @param channelId 频道ID
     * @return 返回一个新的频道构建器
     * @param <DistDB> 目标数据库实现
     */
    public static <DistDB extends Rdb> ChannelBuilder<DistDB> of(String channelId) {
        return new ChannelBuilder<>(channelId);
    }

}
