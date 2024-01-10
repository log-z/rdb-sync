package vip.logz.rdbsync.common.config;

import java.util.Map;

/**
 * 频道目标属性加载器
 *
 * @author logz
 * @date 2024-01-09
 */
public interface ChannelDistPropertiesLoader {

    /**
     * 加载所有
     * @return [id -> properties]
     */
    Map<String, ChannelDistProperties> loadAll();

}
