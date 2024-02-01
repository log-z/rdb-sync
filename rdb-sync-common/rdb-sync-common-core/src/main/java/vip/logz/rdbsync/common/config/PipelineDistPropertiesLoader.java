package vip.logz.rdbsync.common.config;

/**
 * 管道目标属性加载器
 *
 * @author logz
 * @date 2024-01-09
 */
public interface PipelineDistPropertiesLoader {

    /**
     * 加载
     * @param id ID
     * @return 管道目标属性
     */
    PipelineDistProperties load(String id);

}
