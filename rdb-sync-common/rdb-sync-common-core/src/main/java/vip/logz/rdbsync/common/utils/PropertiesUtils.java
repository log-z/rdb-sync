package vip.logz.rdbsync.common.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 配置工具
 *
 * @author logz
 * @date 2024-01-10
 */
public class PropertiesUtils {

    /** 配置文件名格式器 */
    private final static String FORMATTER_FILENAME = "application-%s.yaml";

    /** 配置路径分隔符 */
    private final static String PATH_DELIMITER = "\\.";

    /**
     * 获取配置
     * @param env 运行环境
     * @param propertiesCls 配置类型
     * @return 返回反序列化的配置
     * @param <T> 配置类型
     */
    public static <T> T get(String env, Class<T> propertiesCls) {
        return get(env, null, propertiesCls);
    }

    /**
     * 获取配置
     * @param env 运行环境
     * @param path 配置路径
     * @param propertiesCls 配置类型
     * @return 返回反序列化的配置
     * @param <T> 配置类型
     */
    public static <T> T get(String env, String path, Class<T> propertiesCls) {
        // 1. 获取配置文件
        String filename = String.format(FORMATTER_FILENAME, env);
        URL url = PropertiesUtils.class.getClassLoader().getResource(filename);

        // 2. 读取配置文件
        // 初始化YAML解析器
        ObjectMapper objectMapper = JacksonUtils.createInstance(new YAMLFactory());
        // 读取YAML文件
        JsonNode jsonNode;
        try {
            jsonNode = objectMapper.readTree(url);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // 3. 根据路径从根导航到关键位置
        if (path != null) {
            for (String field : path.split(PATH_DELIMITER)) {
                if (field.isEmpty()) continue;
                jsonNode = jsonNode.path(field);
            }
        }

        // 4. 反序列化配置
        return objectMapper.convertValue(jsonNode, propertiesCls);
    }

    /**
     * 获取扁平化配置
     * @param env 运行环境
     * @return 返回反序列化的配置
     */
    public static Map<String, String> getFlatted(String env) {
        return getFlatted(env, null);
    }

    /**
     * 获取扁平化配置
     * @param env 运行环境
     * @param path 配置路径
     * @return 返回反序列化的配置
     */
    @SuppressWarnings("unchecked")
    public static Map<String, String> getFlatted(String env, String path) {
        return nestedToFlatted(get(env, path, LinkedHashMap.class));
    }

    /** 键分隔符 */
    private static final String KEY_DELIMITER = ".";

    /**
     * 将嵌套Map转换为扁平Map
     *
     * <pre>{"a": {"b": {"c": 123}}} --> {"a.b.c": 123}
     * </pre>
     *
     * @param nestedMap 嵌套Map
     * @return 返回扁平Map
     */
    @SuppressWarnings("unchecked")
    private static Map<String, String> nestedToFlatted(Map<String, ?> nestedMap) {
        // 扁平化的Map
        Map<String, String> flatedMap = new LinkedHashMap<>();

        nestedMap.forEach((key, val) -> {
            Map<String, String> innerFlatedMap;
            if (val instanceof Map) {
                // 情形1：内部值是Map，递归处理它
                innerFlatedMap = nestedToFlatted((Map<String, ?>) val);
            } else if (val instanceof Iterable) {
                // 情形2：内部值可迭代，转换为Map后按情形1处理
                // [7, 8, 9] --> {"0": 7, "1": 8, "2": 9}
                int i = 0;
                Map<String, Object> innerNestedMap = new LinkedHashMap<>();
                for (Object ele : ((Iterable<?>) val)) {
                    innerNestedMap.put(Integer.toString(i++), ele);
                }
                innerFlatedMap = nestedToFlatted(innerNestedMap);
            } else {
                // 情形3：内部值不可展开，直接记录该值
                flatedMap.put(key, val.toString());
                return;
            }

            innerFlatedMap.forEach((innerKey, innerVal) ->
                    flatedMap.put(key + KEY_DELIMITER + innerKey, innerVal)
            );
        });

        return flatedMap;
    }

}
