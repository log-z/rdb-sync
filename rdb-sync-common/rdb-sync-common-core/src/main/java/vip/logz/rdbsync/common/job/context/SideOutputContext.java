package vip.logz.rdbsync.common.job.context;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import vip.logz.rdbsync.common.job.debezium.DebeziumEvent;

/**
 * 旁路输出上下文
 *
 * @author logz
 * @date 2024-01-09
 * @param <Mid> 中间数据类型
 */
public class SideOutputContext<Mid> {

    /** 转换器 */
    private MapFunction<DebeziumEvent, Mid> transformer;

    /** 出口函数 */
    private SinkFunction<Mid> sink;

    /**
     * 获取转换器
     */
    public MapFunction<DebeziumEvent, Mid> getTransformer() {
        return transformer;
    }

    /**
     * 设置转换器
     * @param transformer 转换器
     */
    public void setTransformer(MapFunction<DebeziumEvent, Mid> transformer) {
        this.transformer = transformer;
    }

    /**
     * 获取出口函数
     */
    public SinkFunction<Mid> getSink() {
        return sink;
    }

    /**
     * 设置出口函数
     * @param sink 出口函数
     */
    public void setSink(SinkFunction<Mid> sink) {
        this.sink = sink;
    }

}
