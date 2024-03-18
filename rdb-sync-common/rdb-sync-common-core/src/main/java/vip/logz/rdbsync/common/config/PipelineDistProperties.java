package vip.logz.rdbsync.common.config;

import java.util.Map;

/**
 * 管道目标属性
 *
 * @author logz
 * @date 2024-01-09
 */
public class PipelineDistProperties extends PipelineProperties {

    /**
     * 构造器
     */
    public PipelineDistProperties() {
    }

    /**
     * 构造器
     * @param props 初始属性
     */
    public PipelineDistProperties(Map<String, ?> props) {
        super(props);
    }

}
