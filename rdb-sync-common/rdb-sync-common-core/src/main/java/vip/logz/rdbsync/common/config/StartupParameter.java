package vip.logz.rdbsync.common.config;

import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.ConfigOption;
import org.apache.flink.configuration.ConfigOptions;
import org.apache.flink.configuration.Configuration;

/**
 * 启动参数
 *
 * @author logz
 * @date 2024-01-12
 */
public class StartupParameter {

    /** 配置选项：运行环境 */
    private static final ConfigOption<String> CONFIG_OPTION_ENV =  ConfigOptions.key("env")
            .stringType()
            .defaultValue("dev");

    /** 配置选项：启动管道 */
    private static final ConfigOption<String> CONFIG_OPTION_PIPELINE =  ConfigOptions.key("pipeline")
            .stringType()
            .noDefaultValue();

    /** 配置 */
    private final Configuration configuration;

    /**
     * 构造器
     * @param parameter 参数
     */
    public StartupParameter(ParameterTool parameter) {
        configuration = parameter.getConfiguration();
    }

    /**
     * 工厂方法：从参数构建
     * @param args 主函数参数
     */
    public static StartupParameter fromArgs(String[] args) {
        return new StartupParameter(ParameterTool.fromArgs(args));
    }

    /**
     * 获取当前运行环境
     */
    public String getEnv() {
        return configuration.get(CONFIG_OPTION_ENV);
    }

    /**
     * 获取启动管道
     */
    public String getPipeline() {
        return configuration.get(CONFIG_OPTION_PIPELINE);
    }

}
