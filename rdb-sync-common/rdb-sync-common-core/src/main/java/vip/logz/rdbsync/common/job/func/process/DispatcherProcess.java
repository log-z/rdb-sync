package vip.logz.rdbsync.common.job.func.process;

import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;
import vip.logz.rdbsync.common.enums.DebeziumEventOp;
import vip.logz.rdbsync.common.enums.SideOutputOp;
import vip.logz.rdbsync.common.exception.UnsupportedDebeziumEventOpException;
import vip.logz.rdbsync.common.job.context.SideOutputTag;
import vip.logz.rdbsync.common.job.debezium.DebeziumEvent;
import vip.logz.rdbsync.common.rule.Binding;
import vip.logz.rdbsync.common.rule.Pipeline;

import java.util.Comparator;

/**
 * 分发器过程
 *
 * <p>负责转发Debezium事件到合适的旁路，选择的旁路取决于来源表
 *
 * @author logz
 * @date 2024-01-09
 */
public class DispatcherProcess extends ProcessFunction<DebeziumEvent, DebeziumEvent> {

    /** 管道 */
    private final Pipeline<?> pipeline;

    /** 是否分离“更新或新增”和“删除”操作为两个旁路 */
    private final boolean separate;

    /**
     * 构造器
     * @param pipeline 管道
     * @param separate 是否分离“更新或新增”和“删除”操作为两个旁路
     */
    public DispatcherProcess(Pipeline<?> pipeline, boolean separate) {
        this.pipeline = pipeline;
        this.separate = separate;

        // 按匹配器优先级调整绑定顺序
        pipeline.getBindings().sort(
                Comparator.comparingInt(binding -> binding.getSourceTableMatcher().order())
        );
    }

    /**
     * 处理Debezium事件
     * @param event Debezium事件
     * @param ctx 过程上下文
     * @param out 默认输出收集器
     */
    @Override
    public void processElement(DebeziumEvent event,
                               ProcessFunction<DebeziumEvent, DebeziumEvent>.Context ctx,
                               Collector<DebeziumEvent> out) {
        // 来源表名
        String sourceTable = event.getSource().getTable();

        // 查找匹配的绑定
        for (Binding<?> binding : pipeline.getBindings()) {
            if (!binding.getSourceTableMatcher().match(sourceTable)) {
                continue;
            }

            // 确定旁路输出操作
            SideOutputOp op;
            switch (DebeziumEventOp.parse(event.getOp())) {
                case READ:
                case CREATE:
                case UPDATE: {
                    op = separate ? SideOutputOp.UPSERT : SideOutputOp.BOTH;
                    break;
                }
                case DELETE: {
                    op = separate ? SideOutputOp.DELETE : SideOutputOp.BOTH;
                    break;
                }
                default: {
                    throw new UnsupportedDebeziumEventOpException(event.getOp());
                }
            }

            // 构建旁路输出标签
            SideOutputTag outputTag = new SideOutputTag(binding.getDistTable(), op);
            // 转发到旁路输出
            ctx.output(outputTag, event);
            return;
        }
    }

}
