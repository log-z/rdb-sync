package vip.logz.rdbsync.common.rule.convert;

import java.io.Serializable;

/**
 * 字段值转换器
 *
 * @author logz
 * @date 2024-01-09
 */
public interface Converter<S, T> extends Serializable {

    /**
     * 转换
     * @param val 原始值
     * @return 返回转换后的值
     */
    T convert(S val);

    /**
     * 预定义转换：保持不变
     *
     * <P>用法：Converter::invariant
     */
    static <S> S invariant(S val) {
        return val;
    }

}
