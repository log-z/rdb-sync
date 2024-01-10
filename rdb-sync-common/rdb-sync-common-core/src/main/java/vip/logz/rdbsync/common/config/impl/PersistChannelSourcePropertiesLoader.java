package vip.logz.rdbsync.common.config.impl;

import vip.logz.rdbsync.common.config.ChannelSourcePropertiesLoader;
import vip.logz.rdbsync.common.persistence.SqlSessionProxy;

/**
 * 持久化的频道来源属性加载器
 *
 * @author logz
 * @date 2024-01-09
 */
public abstract class PersistChannelSourcePropertiesLoader implements ChannelSourcePropertiesLoader {

    /** SQL会话代理 */
    protected SqlSessionProxy sqlSessionProxy;

}
