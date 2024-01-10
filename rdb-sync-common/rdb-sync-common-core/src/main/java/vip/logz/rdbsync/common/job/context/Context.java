package vip.logz.rdbsync.common.job.context;

import org.apache.flink.api.connector.source.Source;
import vip.logz.rdbsync.common.job.debezium.DebeziumEvent;
import vip.logz.rdbsync.common.job.func.process.DispatcherProcess;

import java.util.Map;

/**
 * 任务上下文
 * @param <Mid> 中间数据类型
 */
public class Context<Mid> {

    /** 数据源 */
    private Source<DebeziumEvent, ?, ?> source;

    /** 数据源名称 */
    private String sourceName;

    /** 分发器 */
    private DispatcherProcess dispatcher;

    /** 旁路输出上下文映射 */
    private Map<SideOutputTag, SideOutputContext<Mid>> sideOutputContextMap;

    /**
     * 获取数据源
     */
    public Source<DebeziumEvent, ?, ?> getSource() {
        return source;
    }

    /**
     * 设置数据源
     * @param source 数据源
     */
    public void setSource(Source<DebeziumEvent, ?, ?> source) {
        this.source = source;
    }

    /**
     * 获取数据源名称
     */
    public String getSourceName() {
        return sourceName;
    }

    /**
     * 设置数据源名称
     * @param sourceName 数据名称
     */
    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    /**
     * 获取分发器
     */
    public DispatcherProcess getDispatcher() {
        return dispatcher;
    }

    /**
     * 设置分发器
     * @param dispatcher 分发器
     */
    public void setDispatcher(DispatcherProcess dispatcher) {
        this.dispatcher = dispatcher;
    }

    /**
     * 获取旁路输出上下文映射
     */
    public Map<SideOutputTag, SideOutputContext<Mid>> getSideOutputContextMap() {
        return sideOutputContextMap;
    }

    /**
     * 设置旁路输出上下文映射
     * @param sideOutputContextMap 旁路输出上下文映射
     */
    public void setSideOutputContextMap(Map<SideOutputTag, SideOutputContext<Mid>> sideOutputContextMap) {
        this.sideOutputContextMap = sideOutputContextMap;
    }

}
