package vip.logz.rdbsync.common.job.context.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vip.logz.rdbsync.common.exception.UnsupportedDistProtocolException;
import vip.logz.rdbsync.common.job.context.ContextDistHelper;
import vip.logz.rdbsync.common.job.context.ContextMeta;
import vip.logz.rdbsync.common.job.context.SideOutputContext;
import vip.logz.rdbsync.common.job.context.SideOutputTag;
import vip.logz.rdbsync.common.job.func.process.DispatcherProcess;
import vip.logz.rdbsync.common.rule.Rdb;
import vip.logz.rdbsync.common.utils.ClassScanner;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * 任务上下文目标辅助的代理
 *
 * @author logz
 * @date 2024-01-09
 */
public class ContextDistHelperProxy implements ContextDistHelper<Rdb, Object> {

    /** 日志记录器 */
    private static final Logger log = LoggerFactory.getLogger(ContextDistHelperProxy.class);

    /** 原生辅助映射 [protocol -> helper] */
    private final Map<String, ContextDistHelper<Rdb, Object>> rawHelperMap = new HashMap<>();

    /**
     * 构造器
     */
    @SuppressWarnings({"unchecked"})
    public ContextDistHelperProxy() {
        // 在超类所在包中深度扫描
        Class<?> superclass = ContextDistHelper.class;
        for (Class<?> helperClass : ClassScanner.scanByClass(superclass, superclass.getPackageName())) {
            if (helperClass == superclass) {
                continue;
            }

            try {
                // 解析辅助的协议名
                Class<?> rdbType = extractRdbType(helperClass);
                String protocol = rdbType.getSimpleName().toLowerCase();
                // 实例化辅助
                Constructor<?> constructor = helperClass.getDeclaredConstructor();
                ContextDistHelper<Rdb, Object> loader = (ContextDistHelper<Rdb, Object>) constructor.newInstance();
                // 记录辅助实例
                rawHelperMap.put(protocol, loader);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException e) {
                log.info("Create " + helperClass + " instance failed.", e);
            }
        }
    }

    /**
     * 提取关系数据库类型
     * @param helperClass 辅助类
     * @return 返回 {@link Rdb} 的实现类
     */
    private static Class<?> extractRdbType(Class<?> helperClass) {
        // 遍历辅助类实现的接口
        for (Type genericInterface : helperClass.getGenericInterfaces()) {
            // 1. 排除非参数化类型
            if (!(genericInterface instanceof ParameterizedType)) {
                continue;
            }

            // 2. 排除原生类型不是ContextDistHelper的接口
            ParameterizedType paramType = (ParameterizedType) genericInterface;
            if (paramType.getRawType() != ContextDistHelper.class) {
                continue;
            }

            // 3. 获取ContextDistHelper的参数值，即Rdb实现类
            return (Class<?>) paramType.getActualTypeArguments()[0];
        }

        throw new IllegalArgumentException(helperClass + " is not ContextDistHelper implement class.");
    }

    /**
     * 获取旁路输出上下文映射
     * @param contextMeta 任务上下文元数据
     */
    @Override
    public Map<SideOutputTag, SideOutputContext<Object>> getSideOutContexts(ContextMeta contextMeta) {
        String protocol = contextMeta.getChannelDistProperties().getProtocol();
        ContextDistHelper<Rdb, Object> rawHelper = getRawHelper(protocol);
        return rawHelper.getSideOutContexts(contextMeta);
    }

    /**
     * 获取分发器
     * @param contextMeta 任务上下文元数据
     */
    @Override
    public DispatcherProcess getDispatcher(ContextMeta contextMeta) {
        String protocol = contextMeta.getChannelDistProperties().getProtocol();
        ContextDistHelper<Rdb, Object> rawHelper = getRawHelper(protocol);
        return rawHelper.getDispatcher(contextMeta);
    }

    /**
     * 获取原生辅助
     * @param protocol 协议名
     */
    private ContextDistHelper<Rdb, Object> getRawHelper(String protocol) {
        ContextDistHelper<Rdb, Object> helper = rawHelperMap.get(protocol.toLowerCase());

        if (helper == null) {
            throw new UnsupportedDistProtocolException(protocol);
        }
        return helper;
    }

}
