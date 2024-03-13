package vip.logz.rdbsync.common.job.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    /** 日志记录器 */
    private static final Logger LOG = LoggerFactory.getLogger(ContextFactory.class);

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
            throw new IllegalArgumentException("Pipeline not found.");
        }

        PipelineSourceProperties pipelineSourceProps = getPipelineSourcePropertiesLoader().load(pipeline.getSourceId());
        PipelineDistProperties pipelineDistProps = getPipelineDistPropertiesLoader().load(pipeline.getDistId());
        if (pipelineSourceProps == null) {
            throw new RuntimeException("Pipeline Source [" + pipeline.getSourceId() +  "] properties not found.");
        }
        if (pipelineDistProps == null) {
            throw new RuntimeException("Pipeline Dist [" + pipeline.getDistId() +  "] properties not found.");
        }
        LOG.info("Pipeline Source properties:\n" + pipelineSourceProps);
        LOG.info("Pipeline Dist properties:\n" + pipelineDistProps);

        ContextMeta contextMeta = new ContextMeta(pipeline, pipelineSourceProps, pipelineDistProps);

        // 2. 构造任务上下文
        Context<Object> context = new Context<>();
        // 设置执行器属性
        ExecutionInfo executionInfo = getExecutionInfo(pipeline.getId());
        context.setExecutionInfo(executionInfo);
        // 设置来源
        ContextSourceHelperProxy contextSourceHelper = new ContextSourceHelperProxy();
        context.setSource(contextSourceHelper.getSource(contextMeta));
        context.setSourceName(pipelineSourceProps.get(PipelineSourceProperties.NAME));
        context.setSourceParallelism(pipelineSourceProps.get(PipelineSourceProperties.PARALLELISM));
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
        Map<String, ?> config = PropertiesUtils.getFlatted(env);
        return ExecutionInfo.builder()
                .setJobName(jobName)
                .setConfig(config)
                .build();
    }

}
