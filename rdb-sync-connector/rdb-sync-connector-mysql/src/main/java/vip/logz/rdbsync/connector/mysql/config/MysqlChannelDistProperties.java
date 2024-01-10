package vip.logz.rdbsync.connector.mysql.config;

import vip.logz.rdbsync.common.config.ChannelDistProperties;
import vip.logz.rdbsync.connector.mysql.rule.Mysql;

/**
 * Mysql频道目标属性
 *
 * @author logz
 * @date 2024-01-09
 */
public class MysqlChannelDistProperties extends ChannelDistProperties {

    /**
     * 获取协议
     */
    @Override
    public String getProtocol() {
        return Mysql.class.getSimpleName();
    }

}
