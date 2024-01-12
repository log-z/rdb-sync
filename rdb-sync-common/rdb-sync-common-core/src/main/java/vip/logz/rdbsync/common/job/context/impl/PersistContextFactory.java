package vip.logz.rdbsync.common.job.context.impl;

import vip.logz.rdbsync.common.config.PipelineDistPropertiesLoader;
import vip.logz.rdbsync.common.config.PipelineSourcePropertiesLoader;
import vip.logz.rdbsync.common.config.StartupParameter;
import vip.logz.rdbsync.common.config.impl.PersistPipelineDistPropertiesLoaderProxy;
import vip.logz.rdbsync.common.config.impl.PersistPipelineSourcePropertiesLoaderProxy;
import vip.logz.rdbsync.common.job.context.ContextFactory;
import vip.logz.rdbsync.common.persistence.SqlSessionProxy;
import vip.logz.rdbsync.common.rule.Pipeline;

import java.util.HashMap;
import java.util.Map;

/**
 * 持久化的任务上下文工厂
 *
 * @author logz
 * @date 2024-01-09
 */
public class PersistContextFactory extends ContextFactory {

    /** SQL会话代理 */
    private final SqlSessionProxy sqlSessionProxy;

    /** 管道映射 [id -> Pipeline] */
    private final Map<String, Pipeline<?>> pipelineMap = new HashMap<>();

    /**
     * 构造器
     * @param startupParameter 启动参数
     */
    public PersistContextFactory(StartupParameter startupParameter) {
        super(startupParameter);
        this.sqlSessionProxy = new SqlSessionProxy(startupParameter.getEnv());
    }

    /**
     * 管道来源属性加载器
     */
    @Override
    protected PipelineSourcePropertiesLoader getPipelineSourcePropertiesLoader() {
        return new PersistPipelineSourcePropertiesLoaderProxy(sqlSessionProxy);
    }

    /**
     * 管道目标属性加载器
     */
    @Override
    protected PipelineDistPropertiesLoader getPipelineDistPropertiesLoader() {
        return new PersistPipelineDistPropertiesLoaderProxy(sqlSessionProxy);
    }

    /**
     * 获取管道
     */
    @Override
    protected Pipeline<?> getPipeline() {
        return pipelineMap.get(startupParameter.getPipeline());
    }

    /**
     * 注册管道
     * @param pipeline 管道
     * @return 返回当前对象
     */
    public PersistContextFactory register(Pipeline<?> pipeline) {
        pipelineMap.put(pipeline.getId(), pipeline);
        return this;
    }

}
