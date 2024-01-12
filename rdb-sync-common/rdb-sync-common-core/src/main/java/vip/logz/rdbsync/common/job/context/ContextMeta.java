package vip.logz.rdbsync.common.job.context;

import vip.logz.rdbsync.common.config.PipelineDistProperties;
import vip.logz.rdbsync.common.config.PipelineSourceProperties;
import vip.logz.rdbsync.common.rule.Pipeline;

/**
 * 任务上下文元数据
 *
 * @author logz
 * @date 2024-01-09
 */
public class ContextMeta {

    /** 管道 */
    private final Pipeline<?> pipeline;

    /** 管道来源属性 */
    private final PipelineSourceProperties pipelineSourceProperties;

    /** 管道目标属性 */
    private final PipelineDistProperties pipelineDistProperties;

    /**
     * 构造器
     * @param pipeline 管道
     * @param pipelineSourceProperties 管道来源属性
     * @param pipelineDistProperties 管道目标属性
     */
    public ContextMeta(Pipeline<?> pipeline,
                       PipelineSourceProperties pipelineSourceProperties,
                       PipelineDistProperties pipelineDistProperties) {
        this.pipeline = pipeline;
        this.pipelineSourceProperties = pipelineSourceProperties;
        this.pipelineDistProperties = pipelineDistProperties;
    }

    /**
     * 获取管道
     */
    public Pipeline<?> getPipeline() {
        return pipeline;
    }

    /**
     * 获取管道来源属性
     */
    public PipelineSourceProperties getPipelineSourceProperties() {
        return pipelineSourceProperties;
    }

    /**
     * 获取管道目标属性
     */
    public PipelineDistProperties getPipelineDistProperties() {
        return pipelineDistProperties;
    }

}
