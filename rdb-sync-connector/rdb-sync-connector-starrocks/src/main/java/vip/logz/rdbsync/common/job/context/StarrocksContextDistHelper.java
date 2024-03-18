package vip.logz.rdbsync.common.job.context;

import com.starrocks.connector.flink.StarRocksSink;
import com.starrocks.connector.flink.table.sink.StarRocksSinkOptions;
import org.apache.flink.configuration.Configuration;
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
        StarrocksPipelineDistProperties pipelineProps =
                (StarrocksPipelineDistProperties) contextMeta.getPipelineDistProperties();

        // 2. 预处理StarRocks管道目标属性
        pretreatmentPipelineDistProps(pipelineProps);

        // 3. 构建所有旁路输出上下文
        Map<SideOutputTag, SideOutputContext<String>> sideOutputContextMap = new HashMap<>();
        for (Binding<Starrocks> binding : pipeline.getBindings()) {
            String distTable = binding.getDistTable();

            // 3.1. 旁路输出标签
            SideOutputTag outputTag = new SideOutputTag(distTable);

            // 3.2. 旁路输出上下文
            if (sideOutputContextMap.containsKey(outputTag)) {
                continue;
            }

            // 旁路输出上下文：与标签建立关联
            SideOutputContext<String> sideOutputContext = new SideOutputContext<>();
            sideOutputContextMap.put(outputTag, sideOutputContext);

            // 旁路输出上下文：配置出口
            Configuration sinkConf = pipelineProps.toConfiguration();
            sinkConf.set(StarRocksSinkOptions.TABLE_NAME, distTable);
            sinkConf.setString("sink.properties.format", "json");
            sinkConf.setString("sink.properties.strip_outer_array", "true");
            sinkConf.setString("sink.properties.max_filter_ratio", "0");
            sideOutputContext.setSink(
                    StarRocksSink.sink(new StarRocksSinkOptions(sinkConf, sinkConf.toMap()))
            );

            // 旁路输出上下文：配置转换器
            sideOutputContext.setTransformer(new DebeziumEventToStarrocksMap(binding.getMapping()));
        }

        return sideOutputContextMap;
    }

    /**
     * 预处理管道目标属性
     * @param pipelineProps StarRocks管道目标属性
     */
    private static void pretreatmentPipelineDistProps(StarrocksPipelineDistProperties pipelineProps) {
        // 检查FE-MySQL服务的URL，若不存在则构造它
        String jdbcUrl = pipelineProps.get(StarRocksSinkOptions.JDBC_URL);
        if (jdbcUrl == null || jdbcUrl.isEmpty()) {
            pipelineProps.set(StarRocksSinkOptions.JDBC_URL, PREFIX_JDBC_URL + parseDomains(
                    pipelineProps.get(StarrocksPipelineDistProperties.HOSTNAMES),
                    pipelineProps.get(StarrocksPipelineDistProperties.PORTS),
                    StarrocksOptions.DEFAULT_PORT,
                    ","
            ));
        }

        // 检查FE-HTTP服务的URL，若不存在则构造它
        List<String> loadUrl = pipelineProps.get(StarRocksSinkOptions.LOAD_URL);
        if (loadUrl == null || loadUrl.isEmpty()) {
            pipelineProps.setString(StarRocksSinkOptions.LOAD_URL, parseDomains(
                    pipelineProps.get(StarrocksPipelineDistProperties.LOAD_HOSTNAMES),
                    pipelineProps.get(StarrocksPipelineDistProperties.LOAD_PORTS),
                    StarrocksOptions.DEFAULT_LOAD_PORT,
                    ";"
            ));
        }

        // 检查数据库名，若不存在则抛出异常
        String database = pipelineProps.get(StarRocksSinkOptions.DATABASE_NAME);
        if (database == null || database.isEmpty()) {
            throw new IllegalArgumentException("Pipeline Dist [database-name] not specified.");
        }
    }

    /**
     * 解析域列表
     * @param hosts 主机列表
     * @param ports 端口列表
     * @param defaultPort 默认端口
     * @param domainsDelimiter 域分隔符
     */
    private static String parseDomains(List<String> hosts, List<Integer> ports, int defaultPort, String domainsDelimiter) {
        if (hosts.isEmpty()) {
            hosts = List.of(StarrocksOptions.DEFAULT_HOST);
        }

        // 构造域列表
        List<String> domains = new ArrayList<>();
        for (int i = 0; i < hosts.size(); i++) {
            String host = hosts.get(i).strip();
            int port = (i >= ports.size()) ?
                    defaultPort :
                    ports.get(i);

            domains.add(host + ":" + port);
        }

        return String.join(domainsDelimiter, domains);
    }

}
