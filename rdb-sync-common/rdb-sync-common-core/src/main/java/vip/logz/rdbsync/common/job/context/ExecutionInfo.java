package vip.logz.rdbsync.common.job.context;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.environment.ExecutionCheckpointingOptions;

import java.time.Duration;
import java.util.Map;

/**
 * 执行器信息
 *
 * @author logz
 * @date 2024-01-12
 */
public class ExecutionInfo {

    /** 作业名称 */
    private String jobName;

    /** 执行器环境配置 */
    private Configuration config;

    /**
     * 获取作业名称
     */
    public String getJobName() {
        return jobName;
    }

    /**
     * 获取执行器环境配置
     */
    public Configuration getConfig() {
        return config;
    }

    /**
     * 获取一个新构建器
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * 构建器
     */
    public static class Builder {

        private static final Duration DEFAULT_CHECKPOINTING_INTERVAL = Duration.ofSeconds(3);

        /** 执行器信息 */
        private final ExecutionInfo info = new ExecutionInfo();

        /**
         * 设置作业名称
         * @param jobName 作业名称
         * @return 返回当前对象
         */
        public Builder setJobName(String jobName) {
            info.jobName = jobName;
            return this;
        }

        /**
         * 设置执行器环境配置
         * @param config 执行器环境配置
         * @return 返回当前对象
         */
        public Builder setConfig(Map<String, String> config) {
            info.config = Configuration.fromMap(config);

            // 补充必要的配置
            if (info.config.get(ExecutionCheckpointingOptions.CHECKPOINTING_INTERVAL) == null) {
                info.config.set(ExecutionCheckpointingOptions.CHECKPOINTING_INTERVAL, DEFAULT_CHECKPOINTING_INTERVAL);
            }

            return this;
        }

        /**
         * 构建
         * @return 返回执行器信息
         */
        public ExecutionInfo build() {
            return info;
        }

    }

}
