package vip.logz.rdbsync.common.job.context;

import com.starrocks.connector.flink.StarRocksSink;
import com.starrocks.connector.flink.table.sink.StarRocksSinkOptions;
import vip.logz.rdbsync.common.annotations.Scannable;
import vip.logz.rdbsync.common.enums.SideOutputOp;
import vip.logz.rdbsync.common.job.func.process.DispatcherProcess;
import vip.logz.rdbsync.common.rule.Binding;
import vip.logz.rdbsync.common.rule.Channel;
import vip.logz.rdbsync.connector.starrocks.config.StarrocksConnectDistProperties;
import vip.logz.rdbsync.connector.starrocks.job.func.DebeziumEventToStarrocksMap;
import vip.logz.rdbsync.connector.starrocks.rule.Starrocks;

import java.util.HashMap;
import java.util.Map;

/**
 * Starrocks任务上下文目标辅助
 *
 * @author logz
 * @date 2024-01-09
 */
@Scannable
public class StarrocksContextDistHelper implements ContextDistHelper<Starrocks, String> {

    /**
     * 获取旁路输出上下文映射
     * @param contextMeta 任务上下文元数据
     */
    @Override
    @SuppressWarnings("unchecked")
    public Map<SideOutputTag, SideOutputContext<String>> getSideOutContexts(ContextMeta contextMeta) {
        // 1. 提取元数据
        Channel<Starrocks> channel = (Channel<Starrocks>) contextMeta.getChannel();
        StarrocksConnectDistProperties connectDistProperties =
                (StarrocksConnectDistProperties) contextMeta.getConnectDistProperties();

        // 2. 构建所有旁路输出上下文
        Map<SideOutputTag, SideOutputContext<String>> sideOutputContextMap = new HashMap<>();
        for (Binding<Starrocks> binding : channel.getBindings()) {
            String distTable = binding.getDistTable();
            // 旁路输出标签
            SideOutputTag outputTag = new SideOutputTag(distTable, SideOutputOp.BOTH);
            // 旁路输出上下文
            SideOutputContext<String> sideOutputContext = new SideOutputContext<>();
            sideOutputContextMap.put(outputTag, sideOutputContext);

            // 旁路输出上下文：初始化Sink
            StarRocksSinkOptions options = StarRocksSinkOptions.builder()
                    .withProperty("jdbc-url", connectDistProperties.getJdbcUrl())
                    .withProperty("load-url", connectDistProperties.getLoadUrl())
                    .withProperty("database-name", connectDistProperties.getDatabase())
                    .withProperty("table-name", distTable)
                    .withProperty("username", connectDistProperties.getUsername())
                    .withProperty("password", connectDistProperties.getPassword())
                    .withProperty("sink.properties.format", "json")
                    .withProperty("sink.properties.strip_outer_array", "true")
                    .build();
            sideOutputContext.setSink(StarRocksSink.sink(options));

            // 旁路输出上下文：初始化转换器
            sideOutputContext.setTransformer(new DebeziumEventToStarrocksMap(binding.getMapping()));
        }

        return sideOutputContextMap;
    }

    /**
     * 获取分发器
     * @param contextMeta 任务上下文元数据
     */
    @Override
    public DispatcherProcess getDispatcher(ContextMeta contextMeta) {
        return new DispatcherProcess(contextMeta.getChannel(), false);
    }

}
