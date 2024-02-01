package vip.logz.rdbsync.common.job.context;

import vip.logz.rdbsync.common.config.*;
import vip.logz.rdbsync.common.job.context.impl.ContextDistHelperProxy;
import vip.logz.rdbsync.common.job.context.impl.ContextSourceHelperProxy;
import vip.logz.rdbsync.common.rule.Pipeline;
import vip.logz.rdbsync.common.rule.Rdb;
import vip.logz.rdbsync.common.utils.PropertiesUtils;

import java.util.Map;

/**
 * 任务上下文工厂
 *
 * @author logz
 * @date 2024-01-09
 */
public abstract class ContextFactory {

    /** 启动参数 */
    protected final StartupParameter startupParameter;

    /**
     * 构造器
     * @param startupParameter 启动参数
     */
    public ContextFactory(StartupParameter startupParameter) {
        this.startupParameter = startupParameter;
    }

    /**
     * 管道来源属性加载器
     */
    protected abstract PipelineSourcePropertiesLoader getPipelineSourcePropertiesLoader();

    /**
     * 管道目标属性加载器
     */
    protected abstract PipelineDistPropertiesLoader getPipelineDistPropertiesLoader();

    /**
     * 获取管道
     */
    protected abstract Pipeline<?> getPipeline();

    /**
     * 创建任务上下文
     */
    @SuppressWarnings("unchecked")
    public Context<?> create() {
        // 1. 获取元数据
        Pipeline<Rdb> pipeline = (Pipeline<Rdb>) getPipeline();
        if (pipeline == null) {
            throw new IllegalArgumentException("pipeline not found.");
        }

        PipelineSourceProperties pipelineSourceProperties = getPipelineSourcePropertiesLoader()
                .load(pipeline.getSourceId());
        PipelineDistProperties pipelineDistProperties = getPipelineDistPropertiesLoader()
                .load(pipeline.getDistId());
        if (pipelineSourceProperties == null) {
            throw new RuntimeException("pipeline source [" + pipeline.getSourceId() +  "] properties not found.");
        }
        if (pipelineDistProperties == null) {
            throw new RuntimeException("pipeline dist [" + pipeline.getDistId() +  "] properties not found.");
        }

        ContextMeta contextMeta = new ContextMeta(pipeline, pipelineSourceProperties, pipelineDistProperties);

        // 2. 构造任务上下文
        Context<Object> context = new Context<>();
        // 设置执行器属性
        ExecutionInfo executionInfo = getExecutionInfo(pipeline.getId());
        context.setExecutionInfo(executionInfo);
        // 设置来源
        ContextSourceHelperProxy contextSourceHelper = new ContextSourceHelperProxy();
        context.setSource(contextSourceHelper.getSource(contextMeta));
        context.setSourceName(pipelineSourceProperties.getName());
        context.setSourceParallelism(pipelineSourceProperties.getParallelism());
        // 设置去向
        ContextDistHelperProxy contextDistHelper = new ContextDistHelperProxy();
        context.setSideOutputContextMap(contextDistHelper.getSideOutContexts(contextMeta));
        context.setDispatcher(contextDistHelper.getDispatcher(contextMeta));

        return context;
    }

    /**
     * 获取执行器信息
     * @param jobName 作业名称
     */
    private ExecutionInfo getExecutionInfo(String jobName) {
        String env = startupParameter.getEnv();
        Map<String, String> config = PropertiesUtils.getFlatted(env);
        return ExecutionInfo.builder()
                .setJobName(jobName)
                .setConfig(config)
                .build();
    }

}
