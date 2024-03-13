package vip.logz.rdbsync.common.utils;

import org.apache.flink.configuration.Configuration;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;

/**
 * 配置工具
 *
 * @author logz
 * @date 2024-03-08
 */
public class Configurations {

    private Configurations() {
    }

    /**
     * 将映射解析为配置
     * @param map 映射
     * @return 返回配置
     */
    public static Configuration parse(Map<String, ?> map) {
        Configuration conf = new Configuration();

        map.forEach((key, val) -> {
            if (val == null) {
                return;
            }

            if (val instanceof Integer) {
                conf.setInteger(key, (int) val);
            } else if (val instanceof Long) {
                conf.setLong(key, (long) val);
            } else if (val instanceof Float) {
                conf.setFloat(key, (float) val);
            } else if (val instanceof Double) {
                conf.setDouble(key, (double) val);
            } else if (val instanceof BigInteger) {
                conf.setString(key, val.toString());
            } else if (val instanceof BigDecimal) {
                conf.setString(key, ((BigDecimal) val).toPlainString());
            } else if (val instanceof Boolean) {
                conf.setBoolean(key, (boolean) val);
            } else if (val instanceof String) {
                conf.setString(key, (String) val);
            } else if (val instanceof byte[]) {
                conf.setBytes(key, (byte[]) val);
            } else if (val instanceof Class) {
                conf.setClass(key, (Class<?>) val);
            }
        });

        return conf;
    }

}
