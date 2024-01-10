package vip.logz.rdbsync.simple;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import vip.logz.rdbsync.common.job.context.Context;
import vip.logz.rdbsync.common.job.context.ContextFactory;
import vip.logz.rdbsync.common.job.context.impl.PersistContextFactory;
import vip.logz.rdbsync.common.job.debezium.DebeziumEvent;
import vip.logz.rdbsync.common.rule.Channel;
import vip.logz.rdbsync.common.rule.ChannelBuilder;
import vip.logz.rdbsync.common.rule.table.Mapping;
import vip.logz.rdbsync.common.rule.table.MappingBuilder;
import vip.logz.rdbsync.connector.starrocks.rule.Starrocks;

public class Main {

    private final static Mapping<Starrocks> ordersMapping1 = MappingBuilder.<Starrocks>of()
            .field("order_id").type(Starrocks.INT()).nonNull().primaryKey().comment("订单ID").and()
            .field("order_date").type(Starrocks.DATETIME()).and()
            .field("customer_name").type(Starrocks.STRING()).and()
            .field("price").type(Starrocks.DECIMAL(10, 5)).and()
            .field("product_id").type(Starrocks.INT()).and()
            .field("order_status").type(Starrocks.INT()).and()
            .build();
//    private final static Mapping<Mysql> ordersMapping2 = MappingBuilder.ofMySQL()
//            .field("aaa").type(Mysql.INTEGER()).nonNull().primaryKey().comment("AA").and()
//            .field("bbb").type(Mysql.ENUM("e1", "e2", "e3")).and()
//            .field("ccc").type(Mysql.DECIMAL(10, 2)).and()
//            .build();
    private final static Channel<Starrocks> channel1 = ChannelBuilder.<Starrocks>of("simple_starrocks")
            .sourceId("simple_source")
            .distId("simple_dist")
            .binding("orders", "orders", ordersMapping1)
            .binding(t -> t.startsWith("orders_"), "orders", ordersMapping1)
            .build();

//    private final static Channel<Mysql> channel2 = ChannelBuilder.ofMySQL("simple_mysql")
//            .sourceId("simple_source")
//            .distId("simple_dist")
//            .binding(RegexTableMatcher.of("$orders_\\d+"), "orders", ordersMapping2)
//            .build();

    public static void main(String[] args) throws Exception {
        Configuration configuration = ParameterTool.fromArgs(args).getConfiguration();
        ContextFactory contextFactory = new PersistContextFactory(configuration)
                .register(channel1);
//                .register(channel2);
        Context<?> context = contextFactory.create();

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.enableCheckpointing(3000);

        SingleOutputStreamOperator<DebeziumEvent> dataStream =
                env.fromSource(context.getSource(), WatermarkStrategy.noWatermarks(), context.getSourceName())
                        .setParallelism(4)
                        .process(context.getDispatcher());

        context.getSideOutputContextMap().forEach((outputTag, sideOutputContext) ->
                dataStream.getSideOutput(outputTag)
                        .map(sideOutputContext.getTransformer())
                        .addSink(sideOutputContext.getSink())
                        .setParallelism(1)
                        .name("Sync to " + outputTag.getId())
        );

        env.execute("RDB Snapshot + Binlog");
    }
}
