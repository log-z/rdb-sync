package vip.logz.rdbsync.common.config;

import org.apache.flink.configuration.ConfigOption;
import org.apache.flink.configuration.ConfigOptions;

import java.util.Map;

/**
 * 管道来源属性
 *
 * @author logz
 * @date 2024-01-09
 */
public abstract class PipelineSourceProperties extends PipelineProperties {

    /** 属性定义：并行度 */
    public static final ConfigOption<Integer> PARALLELISM = ConfigOptions.key("parallelism")
            .intType()
            .defaultValue(1);

    /**
     * 构造器
     */
    public PipelineSourceProperties() {
    }

    /**
     * 构造器
     * @param props 初始属性
     */
    public PipelineSourceProperties(Map<String, ?> props) {
        super(props);
    }

}
