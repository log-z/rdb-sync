package vip.logz.rdbsync.common.rule.data;

import vip.logz.rdbsync.common.rule.Rdb;
import vip.logz.rdbsync.common.rule.convert.ConverterRegistrar;

import java.util.Base64;

/**
 * BYTES字段类型
 *
 * @author logz
 * @date 2024-02-19
 */
public class BytesFieldType<DB extends Rdb> extends AbstractFieldType<DB, byte[]> {

    /**
     * 构造器
     * @param name 具体名称
     * @param args 参数列表
     */
    public BytesFieldType(String name, Object... args) {
        super(name, args);
    }

    /**
     * 初始化配置
     * @param converterRegistrar 转换器注册器
     */
    @Override
    public void config(ConverterRegistrar<byte[]> converterRegistrar) {
        converterRegistrar.withString(val -> Base64.getDecoder().decode(val));
    }

}
