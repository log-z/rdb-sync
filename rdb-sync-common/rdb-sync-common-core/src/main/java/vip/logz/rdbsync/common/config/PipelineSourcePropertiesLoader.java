package vip.logz.rdbsync.common.config;

/**
 * 管道来源属性加载器
 *
 * @author logz
 * @date 2024-01-09
 */
public interface PipelineSourcePropertiesLoader {

    /**
     * 加载
     * @param id ID
     * @return 管道来源属性
     */
    PipelineSourceProperties load(String id);

}
