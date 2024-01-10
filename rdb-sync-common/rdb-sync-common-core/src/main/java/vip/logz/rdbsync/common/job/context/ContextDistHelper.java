package vip.logz.rdbsync.common.job.context;

import vip.logz.rdbsync.common.job.func.process.DispatcherProcess;
import vip.logz.rdbsync.common.rule.Rdb;

import java.util.Map;

/**
 * 任务上下文目标辅助
 *
 * @author logz
 * @date 2024-01-09
 * @param <DistDB> 目标数据库实现
 * @param <Mid> 中间数据类型
 */
public interface ContextDistHelper<DistDB extends Rdb, Mid> {

    /**
     * 获取旁路输出上下文映射
     * @param contextMeta 任务上下文元数据
     */
    Map<SideOutputTag, SideOutputContext<Mid>> getSideOutContexts(ContextMeta contextMeta);

    /**
     * 获取分发器
     * @param contextMeta 任务上下文元数据
     */
    DispatcherProcess getDispatcher(ContextMeta contextMeta);

}
