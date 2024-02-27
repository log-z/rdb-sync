package vip.logz.rdbsync.common.job.context;

import com.starrocks.connector.flink.StarRocksSink;
import com.starrocks.connector.flink.table.sink.StarRocksSinkOptions;
import vip.logz.rdbsync.common.annotations.Scannable;
import vip.logz.rdbsync.common.rule.Binding;
import vip.logz.rdbsync.common.rule.Pipeline;
import vip.logz.rdbsync.connector.starrocks.config.StarrocksOptions;
import vip.logz.rdbsync.connector.starrocks.config.StarrocksPipelineDistProperties;
import vip.logz.rdbsync.connector.starrocks.job.func.DebeziumEventToStarrocksMap;
import vip.logz.rdbsync.connector.starrocks.rule.Starrocks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Starrocks任务上下文目标辅助
 *
 * @author logz
 * @date 2024-01-09
 */
@Scannable
public class StarrocksContextDistHelper implements ContextDistHelper<Starrocks, String> {

    /** 前缀：JDBC-URL */
    private static final String PREFIX_JDBC_URL = "jdbc:mysql://";

    /**
     * 获取旁路输出上下文映射
     * @param contextMeta 任务上下文元数据
     */
    @Override
    @SuppressWarnings("unchecked")
    public Map<SideOutputTag, SideOutputContext<String>> getSideOutContexts(ContextMeta contextMeta) {
        // 1. 提取元数据
        Pipeline<Starrocks> pipeline = (Pipeline<Starrocks>) contextMeta.getPipeline();
        StarrocksPipelineDistProperties pipelineProperties =
                (StarrocksPipelineDistProperties) contextMeta.getPipelineDistProperties();

        // 2. 构建所有旁路输出上下文
        Map<SideOutputTag, SideOutputContext<String>> sideOutputContextMap = new HashMap<>();
        for (Binding<Starrocks> binding : pipeline.getBindings()) {
            String distTable = binding.getDistTable();

            // 2.1. 旁路输出标签
            SideOutputTag outputTag = new SideOutputTag(distTable);

            // 2.2. 旁路输出上下文
            if (sideOutputContextMap.containsKey(outputTag)) {
                continue;
            }

            // 旁路输出上下文：与标签建立关联
            SideOutputContext<String> sideOutputContext = new SideOutputContext<>();
            sideOutputContextMap.put(outputTag, sideOutputContext);

            // 旁路输出上下文：配置出口
            String jdbcUrl = PREFIX_JDBC_URL + parseDomains(
                    pipelineProperties.getHosts(),
                    pipelineProperties.getPorts(),
                    StarrocksOptions.DEFAULT_PORT,
                    ","
            );
            String loadUrl = parseDomains(
                    pipelineProperties.getLoadHosts(),
                    pipelineProperties.getLoadPorts(),
                    StarrocksOptions.DEFAULT_LOAD_PORT,
                    ";"
            );
            StarRocksSinkOptions.Builder optionsBuilder = StarRocksSinkOptions.builder()
                    .withProperty("jdbc-url", jdbcUrl)
                    .withProperty("load-url", loadUrl)
                    .withProperty("database-name", pipelineProperties.getDatabase())
                    .withProperty("table-name", distTable)
                    .withProperty("username", pipelineProperties.getUsername())
                    .withProperty("password", pipelineProperties.getPassword())
                    .withProperty("sink.properties.format", "json")
                    .withProperty("sink.properties.strip_outer_array", "true");
            if (pipelineProperties.getSemantic() != null) {
                optionsBuilder.withProperty("sink.semantic", pipelineProperties.getSemantic());
            }
            if (pipelineProperties.getLabelPrefix() != null) {
                optionsBuilder.withProperty("sink.label-prefix", pipelineProperties.getLabelPrefix());
            }
            sideOutputContext.setSink(
                    StarRocksSink.sink(optionsBuilder.build())
            );

            // 旁路输出上下文：配置转换器
            sideOutputContext.setTransformer(new DebeziumEventToStarrocksMap(binding.getMapping()));
        }

        return sideOutputContextMap;
    }

    /** 多个值的分隔符 */
    private static final String MULTI_VALUES_DELIMITER = ",";

    /**
     * 解析域列表
     * @param hosts 主机列表
     * @param ports 端口列表
     * @param defaultPort 默认端口
     * @param domainsDelimiter 域分隔符
     */
    private static String parseDomains(String hosts, String ports, int defaultPort, String domainsDelimiter) {
        // 解析主机列表
        String[] hostList = (hosts == null || hosts.isEmpty()) ?
                new String[] {StarrocksOptions.DEFAULT_HOST} :
                hosts.split(MULTI_VALUES_DELIMITER);

        // 解析端口列表
        String[] portList = (ports == null || ports.isEmpty()) ?
                new String[0] :
                ports.split(MULTI_VALUES_DELIMITER);

        // 构造域列表
        List<String> domains = new ArrayList<>();
        for (int i = 0; i < hostList.length; i++) {
            String host = hostList[i].strip();

            int port;
            if (i >= portList.length) {
                port = defaultPort;
            } else {
                try {
                    port = Integer.parseInt(portList[i].strip());
                } catch (NumberFormatException e) {
                    throw new RuntimeException("解析 StarRocks 管道目标端口列表失败，格式不正确", e);
                }
            }

            domains.add(host + ":" + port);
        }

        return String.join(domainsDelimiter, domains);
    }

}
