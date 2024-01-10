package vip.logz.rdbsync.common.exception;

/**
 * 数据源异常

 * @author logz
 * @date 2024-01-09
 */
public class SourceException extends RuntimeException {

    /**
     * 构造器
     * @param message 错误信息
     */
    public SourceException(String message) {
        super(message);
    }

    /**
     * 构造器
     * @param message 错误信息
     * @param cause 原由
     */
    public SourceException(String message, Throwable cause) {
        super(message, cause);
    }

}
