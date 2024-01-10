package vip.logz.rdbsync.common.config.impl;

import vip.logz.rdbsync.common.config.ConnectSourcePropertiesLoader;
import vip.logz.rdbsync.common.persistence.SqlSessionProxy;

/**
 * 持久化的连接来源属性加载器
 *
 * @author logz
 * @date 2024-01-09
 */
public abstract class PersistConnectSourcePropertiesLoader implements ConnectSourcePropertiesLoader {

    /** SQL会话代理 */
    protected SqlSessionProxy sqlSessionProxy;

}
