package vip.logz.rdbsync.common.rule.table;

import vip.logz.rdbsync.common.rule.Rdb;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 表映射
 *
 * @author logz
 * @date 2024-01-10
 * @param <DistDB> 目标数据库实现
 */
public class Mapping<DistDB extends Rdb> implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 字段列表 */
    private final List<MappingField<DistDB>> fields = new ArrayList<>();

    /**
     * 获取字段列表
     */
    public List<MappingField<DistDB>> getFields() {
        return fields;
    }

}
