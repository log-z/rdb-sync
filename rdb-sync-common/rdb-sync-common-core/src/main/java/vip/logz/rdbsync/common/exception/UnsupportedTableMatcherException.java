package vip.logz.rdbsync.common.exception;

/**
 * 不支持的表匹配器异常

 * @author logz
 * @date 2024-01-29
 */
public class UnsupportedTableMatcherException extends RuntimeException {

    /**
     * 构造器
     * @param msg 错误信息
     */
    public UnsupportedTableMatcherException(String msg) {
        super(msg);
    }

}
