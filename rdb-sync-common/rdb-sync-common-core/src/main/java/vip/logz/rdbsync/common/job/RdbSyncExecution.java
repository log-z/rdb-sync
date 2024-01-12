package vip.logz.rdbsync.common.job;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import vip.logz.rdbsync.common.job.context.Context;
import vip.logz.rdbsync.common.job.context.ContextFactory;
import vip.logz.rdbsync.common.job.debezium.DebeziumEvent;

/**
 * 数据同步执行器
 *
 * @author logz
 * @date 2024-01-12
 */
public class RdbSyncExecution {

    /** 出口并行度（限制为1以保证执行顺序不变） */
    private static final int SINK_PARALLELISM = 1;

    /** 出口名前缀 */
    private static final String SINK_NAME_PREFIX = "Sync to ";

    /** 任务上下文工厂 */
    private final ContextFactory contextFactory;

    /**
     * 构造器
     * @param contextFactory 任务上下文工厂
     */
    public RdbSyncExecution(ContextFactory contextFactory) {
        this.contextFactory = contextFactory;
    }

    /**
     * 启动
     * @throws Exception 启动失败时抛出此异常
     */
    public void start() throws Exception {
        // 1. 获取任务上下文
        Context<?> context = contextFactory.create();

        // 2. 配置作业
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.configure(context.getExecutionInfo().getConfig());

        // 配置主线
        SingleOutputStreamOperator<DebeziumEvent> dataStream =
                env.fromSource(context.getSource(), WatermarkStrategy.noWatermarks(), context.getSourceName())
                        .setParallelism(context.getSourceParallelism())
                        .process(context.getDispatcher());
        // 配置旁路输出
        context.getSideOutputContextMap().forEach((outputTag, sideOutputContext) ->
                dataStream.getSideOutput(outputTag)
                        .map(sideOutputContext.getTransformer())
                        .addSink(sideOutputContext.getSink())
                        .setParallelism(SINK_PARALLELISM)
                        .name(SINK_NAME_PREFIX + outputTag.getId())
        );

        // 3. 启动作业
        env.execute(context.getExecutionInfo().getJobName());
    }

}
