package vip.logz.rdbsync.job.simple;

import vip.logz.rdbsync.common.config.StartupParameter;
import vip.logz.rdbsync.common.job.RdbSyncExecution;
import vip.logz.rdbsync.common.job.context.ContextFactory;
import vip.logz.rdbsync.common.job.context.impl.PersistContextFactory;

/**
 * Flink作业从此启动
 *
 * @author logz
 * @date 2024-01-11
 */
public class Main {

    public static void main(String[] args) throws Exception {
        // 初始化任务上下文工厂
        StartupParameter startupParameter = StartupParameter.fromArgs(args);
        ContextFactory contextFactory = new PersistContextFactory(startupParameter)
                .register(Pipelines.mysqlToMysqlPipeline)
                .register(Pipelines.mysqlToStarrocksPipeline);

        // 构造数据同步执行器，并启动它
        new RdbSyncExecution(contextFactory).start();
    }

}
