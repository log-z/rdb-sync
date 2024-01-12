package vip.logz.rdbsync.common.config.impl;

import vip.logz.rdbsync.common.config.PipelineDistPropertiesLoader;
import vip.logz.rdbsync.common.persistence.SqlSessionProxy;

/**
 * 持久化的管道目标属性加载器
 *
 * @author logz
 * @date 2024-01-09
 */
public abstract class PersistPipelineDistPropertiesLoader implements PipelineDistPropertiesLoader {

    /** SQL会话代理 */
    protected SqlSessionProxy sqlSessionProxy;

}
