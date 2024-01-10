package vip.logz.rdbsync.connector.starrocks.config;

import vip.logz.rdbsync.common.config.ChannelDistProperties;
import vip.logz.rdbsync.connector.starrocks.rule.Starrocks;

/**
 * Starrocks频道目标属性
 *
 * @author logz
 * @date 2024-01-09
 */
public class StarrocksChannelDistProperties extends ChannelDistProperties {

    /**
     * 获取协议
     */
    @Override
    public String getProtocol() {
        return Starrocks.class.getSimpleName();
    }

}
