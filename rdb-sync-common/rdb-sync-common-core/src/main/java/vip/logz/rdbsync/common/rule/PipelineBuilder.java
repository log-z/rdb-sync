package vip.logz.rdbsync.common.rule;

import vip.logz.rdbsync.common.rule.table.EqualTableMatcher;
import vip.logz.rdbsync.common.rule.table.Mapping;
import vip.logz.rdbsync.common.rule.table.TableMatcher;

/**
 * 管道构建器
 *
 * @author logz
 * @date 2024-01-10
 * @param <DistDB> 目标数据库实现
 */
public class PipelineBuilder<DistDB extends Rdb> implements Builder<PipelineBuilder<DistDB>> {

    /** 管道 */
    private final Pipeline<DistDB> pipeline = new Pipeline<>();

    /**
     * 构建器
     * @param pipelineId 管道ID
     */
    private PipelineBuilder(String pipelineId) {
        this.pipeline.setId(pipelineId);
    }

    /**
     * 设置来源ID
     * @param sourceId 来源ID
     * @return 返回当前对象
     * @see vip.logz.rdbsync.common.config.PipelineSourceProperties
     */
    public PipelineBuilder<DistDB> sourceId(String sourceId) {
        pipeline.setSourceId(sourceId);
        return this;
    }

    /**
     * 设置目标ID
     * @param distId 目标ID
     * @return 返回当前对象
     * @see vip.logz.rdbsync.common.config.PipelineDistProperties
     */
    public PipelineBuilder<DistDB> distId(String distId) {
        pipeline.setDistId(distId);
        return this;
    }

    /**
     * 设置来源与目标表的绑定
     * @param sourceTable 来源表名
     * @param distTable 目标表名
     * @param mapping 表映射
     * @return 返回当前对象
     */
    public PipelineBuilder<DistDB> binding(String sourceTable, String distTable, Mapping<DistDB> mapping) {
        return binding(EqualTableMatcher.of(sourceTable), distTable, mapping);
    }

    /**
     * 设置来源与目标表的绑定
     * @param sourceTableMatcher 来源表匹配器
     * @param distTable 目标表名
     * @param mapping 表映射
     * @return 返回当前对象
     */
    public PipelineBuilder<DistDB> binding(TableMatcher sourceTableMatcher, String distTable, Mapping<DistDB> mapping) {
        Binding<DistDB> binding = new Binding<>(sourceTableMatcher, distTable, mapping);
        pipeline.getBindings().add(binding);
        return this;
    }

    /**
     * 退出当前构建器，回到外围构建器
     * @return 当前构建器已经是最外围，将返回它本身
     */
    @Override
    public PipelineBuilder<DistDB> and() {
        return this;
    }

    /**
     * 执行构建
     * @return 返回管道
     */
    public Pipeline<DistDB> build() {
        return pipeline;
    }

    /**
     * 工厂方法
     * @param pipelineId 管道ID
     * @return 返回一个新的管道构建器
     * @param <DistDB> 目标数据库实现
     */
    public static <DistDB extends Rdb> PipelineBuilder<DistDB> of(String pipelineId) {
        return new PipelineBuilder<>(pipelineId);
    }

}
