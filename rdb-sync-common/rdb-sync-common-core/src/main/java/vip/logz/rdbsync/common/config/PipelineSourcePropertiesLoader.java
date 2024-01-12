package vip.logz.rdbsync.common.config;

import java.util.Map;

/**
 * 管道来源属性加载器
 *
 * @author logz
 * @date 2024-01-09
 */
public interface PipelineSourcePropertiesLoader {

    /**
     * 加载所有
     * @return [id -> properties]
     */
    Map<String, PipelineSourceProperties> loadAll();

}
