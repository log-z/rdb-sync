package vip.logz.rdbsync.common.job.context.impl;

import org.apache.flink.configuration.ConfigOption;
import org.apache.flink.configuration.ConfigOptions;
import org.apache.flink.configuration.Configuration;
import vip.logz.rdbsync.common.config.ChannelDistPropertiesLoader;
import vip.logz.rdbsync.common.config.ChannelSourcePropertiesLoader;
import vip.logz.rdbsync.common.config.ConnectDistPropertiesLoader;
import vip.logz.rdbsync.common.config.ConnectSourcePropertiesLoader;
import vip.logz.rdbsync.common.config.impl.PersistChannelDistPropertiesLoaderProxy;
import vip.logz.rdbsync.common.config.impl.PersistChannelSourcePropertiesLoaderProxy;
import vip.logz.rdbsync.common.config.impl.PersistConnectDistPropertiesLoaderProxy;
import vip.logz.rdbsync.common.config.impl.PersistConnectSourcePropertiesLoaderProxy;
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

    /** 配置选项：运行环境 */
    private static final ConfigOption<String> CONFIG_OPTION_ENV =  ConfigOptions.key("env")
            .stringType()
            .defaultValue("dev");

    /** 配置选项：启动频道 */
    private static final ConfigOption<String> CONFIG_OPTION_CHANNEL =  ConfigOptions.key("channel")
            .stringType()
            .noDefaultValue();

    /** SQL会话代理 */
    private final SqlSessionProxy sqlSessionProxy;

    /** 启动频道ID */
    private final String startupChannelId;

    /** 频道映射 [id -> Channel] */
    private final Map<String, Channel<?>> channelMap = new HashMap<>();

    /**
     * 构造器
     * @param configuration 配置信息
     */
    public PersistContextFactory(Configuration configuration) {
        this.sqlSessionProxy = new SqlSessionProxy(configuration.getString(CONFIG_OPTION_ENV));
        this.startupChannelId = configuration.getString(CONFIG_OPTION_CHANNEL);
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
     * 获取连接来源属性加载器
     */
    @Override
    protected ConnectSourcePropertiesLoader getConnectSourcePropertiesLoader() {
        return new PersistConnectSourcePropertiesLoaderProxy(sqlSessionProxy);
    }

    /**
     * 获取连接目标属性加载器
     */
    @Override
    protected ConnectDistPropertiesLoader getConnectDistPropertiesLoader() {
        return new PersistConnectDistPropertiesLoaderProxy(sqlSessionProxy);
    }

    /**
     * 获取频道
     */
    @Override
    protected Channel<?> getChannel() {
        return channelMap.get(startupChannelId);
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
