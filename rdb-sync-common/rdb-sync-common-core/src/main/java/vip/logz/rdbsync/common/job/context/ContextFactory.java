package vip.logz.rdbsync.common.job.context;

import vip.logz.rdbsync.common.config.ChannelDistProperties;
import vip.logz.rdbsync.common.config.ChannelDistPropertiesLoader;
import vip.logz.rdbsync.common.config.ChannelSourceProperties;
import vip.logz.rdbsync.common.config.ChannelSourcePropertiesLoader;
import vip.logz.rdbsync.common.job.context.impl.ContextDistHelperProxy;
import vip.logz.rdbsync.common.job.context.impl.ContextSourceHelperProxy;
import vip.logz.rdbsync.common.rule.Channel;
import vip.logz.rdbsync.common.rule.Rdb;

/**
 * 任务上下文工厂
 *
 * @author logz
 * @date 2024-01-09
 */
public abstract class ContextFactory {

    /**
     * 频道来源属性加载器
     */
    protected abstract ChannelSourcePropertiesLoader getChannelSourcePropertiesLoader();

    /**
     * 频道目标属性加载器
     */
    protected abstract ChannelDistPropertiesLoader getChannelDistPropertiesLoader();

    /**
     * 获取频道
     */
    protected abstract Channel<?> getChannel();

    /**
     * 创建任务上下文
     */
    @SuppressWarnings("unchecked")
    public Context<?> create() {
        // 1. 获取元数据
        Channel<Rdb> channel = (Channel<Rdb>) getChannel();
        ChannelSourceProperties channelSourceProperties = getChannelSourcePropertiesLoader()
                .loadAll()
                .get(channel.getSourceId());
        ChannelDistProperties channelDistProperties = getChannelDistPropertiesLoader()
                .loadAll()
                .get(channel.getDistId());
        if (channelSourceProperties == null) {
            throw new RuntimeException("channel source [" + channel.getSourceId() +  "] properties not found.");
        }
        if (channelDistProperties == null) {
            throw new RuntimeException("channel dist [" + channel.getDistId() +  "] properties not found.");
        }

        ContextMeta contextMeta = new ContextMeta(channel, channelSourceProperties, channelDistProperties);

        // 2. 构造任务上下文
        Context<Object> context = new Context<>();
        // 设置来源
        ContextSourceHelperProxy contextSourceHelper = new ContextSourceHelperProxy();
        context.setSource(contextSourceHelper.getSource(contextMeta));
        context.setSourceName(channelSourceProperties.getName());
        // 设置去向
        ContextDistHelperProxy contextDistHelper = new ContextDistHelperProxy();
        context.setSideOutputContextMap(contextDistHelper.getSideOutContexts(contextMeta));
        context.setDispatcher(contextDistHelper.getDispatcher(contextMeta));

        return context;
    }

}
