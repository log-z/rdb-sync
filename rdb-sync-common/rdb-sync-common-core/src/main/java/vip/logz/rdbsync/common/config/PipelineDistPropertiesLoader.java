package vip.logz.rdbsync.common.config;

import java.util.Map;

/**
 * 管道目标属性加载器
 *
 * @author logz
 * @date 2024-01-09
 */
public interface PipelineDistPropertiesLoader {

    /**
     * 加载所有
     * @return [id -> properties]
     */
    Map<String, PipelineDistProperties> loadAll();

}
