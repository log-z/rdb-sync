package vip.logz.rdbsync.common.config.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vip.logz.rdbsync.common.utils.ClassScanner;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

/**
 * 持久化的属性加载器工具
 *
 * @author logz
 * @date 2024-01-09
 */
public class PersistPropertiesLoaders {

    /** 日志记录器 */
    private static final Logger LOG = LoggerFactory.getLogger(PersistPropertiesLoaders.class);

    /**
     * 创建加载器
     * @param superclass 指定加载器的超类，以此为依据扫描其具体实现
     * @return 返回所有符合条件的加载器
     * @param <T> 加载器的超类
     */
    @SuppressWarnings("unchecked")
    public static <T> Set<T> createLoaders(Class<T> superclass) {
        Set<T> loaderSet = new HashSet<>();

        // 在超类所在包中深度扫描
        for (Class<?> loaderClass : ClassScanner.scanByClass(superclass, superclass.getPackageName())) {
            try {
                // 通过默认构造器实例化
                Constructor<?> constructor = loaderClass.getDeclaredConstructor();
                T loader = (T) constructor.newInstance();
                loaderSet.add(loader);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException e) {
                LOG.info("Create " + loaderClass + " instance failed.", e);
            }
        }

        return loaderSet;
    }

}
