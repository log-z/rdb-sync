package vip.logz.rdbsync.common.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.IOException;
import java.net.URL;

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

}
