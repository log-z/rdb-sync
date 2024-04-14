package vip.logz.rdbsync.common.rule.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vip.logz.rdbsync.common.rule.Rdb;
import vip.logz.rdbsync.common.rule.convert.Converter;
import vip.logz.rdbsync.common.rule.convert.ConverterRegistrar;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 抽象的字段类型
 *
 * @author logz
 * @date 2024-01-09
 * @param <DB> 数据库实现
 * @param <T> 有效值类型
 */
public abstract class AbstractFieldType<DB extends Rdb, T> implements FieldType<DB, T> {

    private static final long serialVersionUID = 1L;

    /** 日志记录器 */
    private static final Logger LOG = LoggerFactory.getLogger(AbstractFieldType.class);

    /** 参数分隔符 */
    private static final String ARG_DELIMITER = ", ";

    /** 参数列表开始 */
    private static final String ARGS_BEGIN = "(";

    /** 参数列表结束 */
    private static final String ARGS_END = ")";

    /** 占位符：任意 */
    private static final String PLACEHOLDER_ANY = "{}";

    /** 占位符匹配器 */
    private static final Pattern PATTERN_PLACEHOLDER = Pattern.compile("\\{(.*?)}");

    /** 具体名称 */
    private final String name;

    /** 参数列表 */
    private final Object[] args;

    /** 转换器映射 [srcType -> Converter] */
    private final Map<Class<?>, Converter<?, T>> converterMap;

    /**
     * 构造器
     * @param name 具体名称
     * @param args 参数列表
     */
    public AbstractFieldType(String name, Object... args) {
        this.name = name;
        this.args = args;

        // 获取字段的所有转换器，并记录
        ConverterRegistrar<T> converterRegistrar = new ConverterRegistrar<>();
        config(converterRegistrar);
        converterMap = converterRegistrar.getConverterMap();
    }

    /**
     * 为不同的字段进行具体配置，比如注册转换器
     * @param converterRegistrar 转换器注册器
     */
    protected abstract void config(ConverterRegistrar<T> converterRegistrar);

    /**
     * 转换
     * @param val 原始值
     * @return 返回转换后的有效值
     * @param <S> 原始值类型
     */
    @Override
    @SuppressWarnings("unchecked")
    public <S> T convart(S val) {
        if (val == null) {
            return null;
        }

        Converter<S, T> converter = (Converter<S, T>) converterMap.get(val.getClass());
        try {
            return converter.convert(val);
        } catch (Exception e) {
            String msg = String.format("can not cast %s[%s] to %s", val.getClass(), val, getName());
            LOG.warn(msg, e);
            return null;
        }
    }

    /**
     * 获取名称
     */
    public String getName() {
        // 清除所有占位符
        return PATTERN_PLACEHOLDER.matcher(name).replaceAll("");
    }

    /**
     * 转换为字符串
     */
    @Override
    public String toString() {
        Matcher matcher = PATTERN_PLACEHOLDER.matcher(name);

        // 默认情况下，即未指定参数位置时，在名称末尾追加参数
        String expr = args.length > 0 && !matcher.find() ?
                name + PLACEHOLDER_ANY :
                name;

        // 替换占位符
        return PATTERN_PLACEHOLDER.matcher(expr).replaceAll(matchResult -> {
            IdxRange idxRange = new IdxRange(matchResult.group(1));
            int begin = idxRange.begin;
            int end = Math.min(idxRange.end, args.length);
            if (begin >= end) {
                return "";
            }

            StringBuilder sb = new StringBuilder(ARGS_BEGIN);
            for (int i = begin; i < end; i++) {
                sb.append(args[i]);
                if (end - i > 1) {
                    sb.append(ARG_DELIMITER);
                }
            }

            return sb.append(ARGS_END).toString();
        });
    }

    /**
     * 索引范围
     * <li> {@code ""} 与 {@code ":"} 表示[0,+∞)
     * <li> {@code "1"} 表示 [1,1]
     * <li> {@code "1:3"} 表示 [1,3)
     * <li> {@code "1:"} 表示 [1,+∞)
     * <li> {@code ":3"} 表示 [0,3)
     */
    private static class IdxRange {

        /** 分隔符 */
        private static final String DELIMITER = ":";

        /** 起始 */
        final Integer begin;

        /** 结束 */
        final Integer end;

        IdxRange(String expr) {
            if (expr.isEmpty()) {
                begin = 0;
                end = Integer.MAX_VALUE;
                return;
            }

            if (!expr.contains(DELIMITER)) {
                begin = Integer.valueOf(expr);
                end = Integer.parseInt(expr) + 1;
                return;
            }

            String[] range = expr.split(DELIMITER);
            if (range.length == 0) {
                begin = 0;
                end = Integer.MAX_VALUE;
            } else if (range.length == 1) {
                begin = Integer.valueOf(range[0]);
                end = Integer.MAX_VALUE;
            } else {
                begin = range[0].isEmpty() ? 0 : Integer.parseInt(range[0]);
                end = Integer.valueOf(range[1]);
            }
        }

    }

}
