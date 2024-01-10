package vip.logz.rdbsync.common.job.context;

import vip.logz.rdbsync.common.config.ChannelDistProperties;
import vip.logz.rdbsync.common.config.ChannelSourceProperties;
import vip.logz.rdbsync.common.config.ConnectDistProperties;
import vip.logz.rdbsync.common.config.ConnectSourceProperties;
import vip.logz.rdbsync.common.rule.Channel;

/**
 * 任务上下文元数据
 *
 * @author logz
 * @date 2024-01-09
 */
public class ContextMeta {

    /** 频道 */
    private final Channel<?> channel;

    /** 频道来源属性 */
    private final ChannelSourceProperties channelSourceProperties;

    /** 频道目标属性 */
    private final ChannelDistProperties channelDistProperties;

    /** 连接来源属性 */
    private final ConnectSourceProperties connectSourceProperties;

    /** 连接目标属性 */
    private final ConnectDistProperties connectDistProperties;

    /**
     * 构造器
     * @param channel 频道
     * @param channelSourceProperties 频道来源属性
     * @param channelDistProperties 频道目标属性
     * @param connectSourceProperties 连接来源属性
     * @param connectDistProperties 连接目标属性
     */
    public ContextMeta(Channel<?> channel,
                       ChannelSourceProperties channelSourceProperties,
                       ChannelDistProperties channelDistProperties,
                       ConnectSourceProperties connectSourceProperties,
                       ConnectDistProperties connectDistProperties) {
        this.channel = channel;
        this.channelSourceProperties = channelSourceProperties;
        this.channelDistProperties = channelDistProperties;
        this.connectSourceProperties = connectSourceProperties;
        this.connectDistProperties = connectDistProperties;
    }

    /**
     * 获取频道
     */
    public Channel<?> getChannel() {
        return channel;
    }

    /**
     * 获取频道来源属性
     */
    public ChannelSourceProperties getChannelSourceProperties() {
        return channelSourceProperties;
    }

    /**
     * 获取频道目标属性
     */
    public ChannelDistProperties getChannelDistProperties() {
        return channelDistProperties;
    }

    /**
     * 获取连接来源属性
     */
    public ConnectSourceProperties getConnectSourceProperties() {
        return connectSourceProperties;
    }

    /**
     * 获取连接目标属性
     */
    public ConnectDistProperties getConnectDistProperties() {
        return connectDistProperties;
    }

}
