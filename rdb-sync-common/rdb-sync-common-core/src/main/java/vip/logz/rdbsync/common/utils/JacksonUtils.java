package vip.logz.rdbsync.common.utils;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Jackson工具
 *
 * @author logz
 * @date 2024-01-10
 */
public class JacksonUtils {

    /**
     * 创建实例
     */
    public static ObjectMapper createInstance() {
        return createInstance(null);
    }

    /**
     * 创建实例
     * @param jf JSON工厂
     */
    public static ObjectMapper createInstance(JsonFactory jf) {
        ObjectMapper objectMapper = new ObjectMapper(jf);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        return objectMapper;
    }

}
