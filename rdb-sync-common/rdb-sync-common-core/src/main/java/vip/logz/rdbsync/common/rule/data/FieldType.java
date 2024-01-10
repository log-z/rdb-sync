package vip.logz.rdbsync.common.rule.data;

import vip.logz.rdbsync.common.rule.Rdb;

import java.io.Serializable;

/**
 * 字段类型
 *
 * @author logz
 * @date 2024-01-09
 * @param <DB> 数据库实现
 * @param <T> 有效值类型
 */
public interface FieldType<DB extends Rdb, T> extends Serializable {

    /**
     * 获取名称
     */
    String getName();

    /**
     * 转换
     * @param val 原始值
     * @return 返回转换后的有效值
     * @param <S> 原始值类型
     */
    <S> T convart(S val);

}
