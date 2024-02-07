package vip.logz.rdbsync.common.utils;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 构建辅助工具
 *
 * @author logz
 * @date 2024-02-05
 */
public class BuildHelper {

    private BuildHelper() {
    }

    /**
     * 工厂方法
     * @return 返回一个新的构建辅助工具
     */
    public static BuildHelper create() {
        return new BuildHelper();
    }

    /**
     * 设置值
     * @param setter 设置方法
     * @param value 值
     * @return 返回当前对象
     */
    public <V> BuildHelper set(Consumer<V> setter, V value) {
        setter.accept(value);
        return this;
    }

    /**
     * 设置值
     * @param setter 设置方法
     * @param value 值
     * @return 返回当前对象
     */
    public <V> BuildHelper set(Function<V, ?> setter, V value) {
        setter.apply(value);
        return this;
    }

    /**
     * 设置值
     * @param setter 设置方法
     * @param valueSupplier 值的供应者
     * @return 返回当前对象
     */
    public <V> BuildHelper set(Consumer<V> setter, Supplier<V> valueSupplier) {
        return set(setter, valueSupplier.get());
    }

    /**
     * 设置值
     * @param setter 设置方法
     * @param valueSupplier 值的供应者
     * @return 返回当前对象
     */
    public <V> BuildHelper set(Function<V, ?> setter, Supplier<V> valueSupplier) {
        return set(setter, valueSupplier.get());
    }

    /**
     * 设置值
     * @param setter 设置方法
     * @param value 值
     * @param defaultValue 默认值，若value为null则使用该值
     * @return 返回当前对象
     */
    public <V> BuildHelper set(Consumer<V> setter, V value, V defaultValue) {
        if (value != null) {
            setter.accept(value);
        } else {
            setter.accept(defaultValue);
        }
        return this;
    }

    /**
     * 设置值
     * @param setter 设置方法
     * @param value 值
     * @param defaultValue 默认值，若value为null则使用该值
     * @return 返回当前对象
     */
    public <V> BuildHelper set(Function<V, ?> setter, V value, V defaultValue) {
        if (value != null) {
            setter.apply(value);
        } else {
            setter.apply(defaultValue);
        }
        return this;
    }

    /**
     * 设置值
     * @param setter 设置方法
     * @param observedValue 观察值
     * @param mapper 将观察值映射为最终值
     * @param defaultValue 默认值，若observedValue为null则使用该值
     * @return 返回当前对象
     */
    public <V, T> BuildHelper set(Consumer<V> setter, T observedValue, Function<T, V> mapper, V defaultValue) {
        if (observedValue != null) {
            setter.accept(mapper.apply(observedValue));
        } else {
            setter.accept(defaultValue);
        }
        return this;
    }

    /**
     * 设置值
     * @param setter 设置方法
     * @param observedValue 观察值
     * @param mapper 将观察值映射为最终值
     * @param defaultValue 默认值，若observedValue为null则使用该值
     * @return 返回当前对象
     */
    public <V, T> BuildHelper set(Function<V, ?> setter, T observedValue, Function<T, V> mapper, V defaultValue) {
        if (observedValue != null) {
            setter.apply(mapper.apply(observedValue));
        } else {
            setter.apply(defaultValue);
        }
        return this;
    }

    /**
     * 设置值，仅当值不为null时
     * @param setter 设置方法
     * @param value 值
     * @return 返回当前对象
     */
    public <V> BuildHelper setIfNotNull(Consumer<V> setter, V value) {
        if (value != null) {
            setter.accept(value);
        }
        return this;
    }

    /**
     * 设置值，仅当值不为null时
     * @param setter 设置方法
     * @param value 值
     * @return 返回当前对象
     */
    public <V> BuildHelper setIfNotNull(Function<V, ?> setter, V value) {
        if (value != null) {
            setter.apply(value);
        }
        return this;
    }

    /**
     * 设置值，仅当观察值不为null时
     * @param setter 设置方法
     * @param observedValue 观察值
     * @param mapper 将观察值映射为最终值
     * @return 返回当前对象
     */
    public <V, T> BuildHelper setIfNotNull(Consumer<V> setter, T observedValue, Function<T, V> mapper) {
        if (observedValue != null) {
            setter.accept(mapper.apply(observedValue));
        }
        return this;
    }

    /**
     * 设置值，仅当观察值不为null时
     * @param setter 设置方法
     * @param observedValue 观察值
     * @param mapper 将观察值映射为最终值
     * @return 返回当前对象
     */
    public <V, T> BuildHelper setIfNotNull(Function<V, ?> setter, T observedValue, Function<T, V> mapper) {
        if (observedValue != null) {
            setter.apply(mapper.apply(observedValue));
        }
        return this;
    }

}
