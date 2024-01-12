package vip.logz.rdbsync.common.job.context.impl;

import vip.logz.rdbsync.common.config.ChannelDistPropertiesLoader;
import vip.logz.rdbsync.common.config.ChannelSourcePropertiesLoader;
import vip.logz.rdbsync.common.config.StartupParameter;
import vip.logz.rdbsync.common.config.impl.PersistChannelDistPropertiesLoaderProxy;
import vip.logz.rdbsync.common.config.impl.PersistChannelSourcePropertiesLoaderProxy;
import vip.logz.rdbsync.common.job.context.ContextFactory;
import vip.logz.rdbsync.common.persistence.SqlSessionProxy;
import vip.logz.rdbsync.common.rule.Channel;

import java.util.HashMap;
import java.util.Map;

/**
 * 持久化的任务上下文工厂
 *
 * @author logz
 * @date 2024-01-09
 */
public class PersistContextFactory extends ContextFactory {

    /** SQL会话代理 */
    private final SqlSessionProxy sqlSessionProxy;

    /** 频道映射 [id -> Channel] */
    private final Map<String, Channel<?>> channelMap = new HashMap<>();

    /**
     * 构造器
     * @param startupParameter 启动参数
     */
    public PersistContextFactory(StartupParameter startupParameter) {
        super(startupParameter);
        this.sqlSessionProxy = new SqlSessionProxy(startupParameter.getEnv());
    }

    /**
     * 频道来源属性加载器
     */
    @Override
    protected ChannelSourcePropertiesLoader getChannelSourcePropertiesLoader() {
        return new PersistChannelSourcePropertiesLoaderProxy(sqlSessionProxy);
    }

    /**
     * 频道目标属性加载器
     */
    @Override
    protected ChannelDistPropertiesLoader getChannelDistPropertiesLoader() {
        return new PersistChannelDistPropertiesLoaderProxy(sqlSessionProxy);
    }

    /**
     * 获取频道
     */
    @Override
    protected Channel<?> getChannel() {
        return channelMap.get(startupParameter.getChannel());
    }

    /**
     * 注册频道
     * @param channel 频道
     * @return 返回当前对象
     */
    public PersistContextFactory register(Channel<?> channel) {
        channelMap.put(channel.getId(), channel);
        return this;
    }

}
