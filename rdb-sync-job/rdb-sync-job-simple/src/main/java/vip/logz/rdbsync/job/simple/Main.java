package vip.logz.rdbsync.job.simple;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import vip.logz.rdbsync.common.job.context.Context;
import vip.logz.rdbsync.common.job.context.ContextFactory;
import vip.logz.rdbsync.common.job.context.impl.PersistContextFactory;
import vip.logz.rdbsync.common.job.debezium.DebeziumEvent;

/**
 * Flink作业从此启动
 *
 * @author logz
 * @date 2024-01-11
 */
public class Main {

    public static void main(String[] args) throws Exception {
        // 1. 任务上下文
        // 初始化任务上下文工厂
        Configuration configuration = ParameterTool.fromArgs(args).getConfiguration();
        ContextFactory contextFactory = new PersistContextFactory(configuration)
                .register(Channels.mysqlToMysqlChannel)
                .register(Channels.mysqlToStarrocksChannel);
        // 获取任务上下文
        Context<?> context = contextFactory.create();

        // 2. 配置作业
        // 初始化数据流环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.enableCheckpointing(3000);

        // 配置主线
        SingleOutputStreamOperator<DebeziumEvent> dataStream =
                env.fromSource(context.getSource(), WatermarkStrategy.noWatermarks(), context.getSourceName())
                        .setParallelism(4)
                        .process(context.getDispatcher());
        // 配置旁路输出
        context.getSideOutputContextMap().forEach((outputTag, sideOutputContext) ->
                dataStream.getSideOutput(outputTag)
                        .map(sideOutputContext.getTransformer())
                        .addSink(sideOutputContext.getSink())
                        .setParallelism(1)
                        .name("Sync to " + outputTag.getId())
        );

        // 3. 启动作业
        env.execute("RDB Snapshot + Binlog");
    }

}
