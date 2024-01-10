package vip.logz.rdbsync.connector.starrocks.job.context;

import com.starrocks.connector.flink.StarRocksSink;
import com.starrocks.connector.flink.table.sink.StarRocksSinkOptions;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import vip.logz.rdbsync.connector.starrocks.config.StarrocksConnectDistProperties;
import vip.logz.rdbsync.common.enums.SideOutputOp;
import vip.logz.rdbsync.common.job.context.SideOutputContext;
import vip.logz.rdbsync.common.job.context.SideOutputTag;
import vip.logz.rdbsync.connector.starrocks.job.func.DebeziumEventToStarrocksMap;
import vip.logz.rdbsync.common.rule.Binding;
import vip.logz.rdbsync.common.rule.Channel;
import vip.logz.rdbsync.connector.starrocks.rule.Starrocks;

import java.util.HashMap;
import java.util.Map;

// TODO
public class StarrocksSideOutputContextHelper {

    public static Map<SideOutputTag, SideOutputContext<String>> generate(
            StarrocksConnectDistProperties starrocksConnectDistProperties,
            Channel<Starrocks> channel
    ) {
        Map<SideOutputTag, SideOutputContext<String>> sideOutputContextMap = new HashMap<>();

        for (Binding<Starrocks> binding : channel.getBindings()) {
            String distTable = binding.getDistTable();
            SideOutputTag outputTag = new SideOutputTag(distTable, SideOutputOp.BOTH);
            SideOutputContext<String> sideOutputContext = new SideOutputContext<>();
            sideOutputContextMap.put(outputTag, sideOutputContext);

            StarRocksSinkOptions options = StarRocksSinkOptions.builder()
                    .withProperty("jdbc-url", starrocksConnectDistProperties.getJdbcUrl())
                    .withProperty("load-url", starrocksConnectDistProperties.getLoadUrl())
                    .withProperty("database-name", starrocksConnectDistProperties.getDatabase())
                    .withProperty("table-name", distTable)
                    .withProperty("username", starrocksConnectDistProperties.getUsername())
                    .withProperty("password", starrocksConnectDistProperties.getPassword())
                    .withProperty("sink.properties.format", "json")
                    .withProperty("sink.properties.strip_outer_array", "true")
                    .build();
            SinkFunction<String> starrocksSink = StarRocksSink.sink(options);

            sideOutputContext.setTransformer(new DebeziumEventToStarrocksMap(binding.getMapping()));
            sideOutputContext.setSink(starrocksSink);
        }

        return sideOutputContextMap;
    }

}
