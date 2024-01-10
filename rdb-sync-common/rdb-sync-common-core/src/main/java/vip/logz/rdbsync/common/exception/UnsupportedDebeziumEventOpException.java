package vip.logz.rdbsync.common.exception;

/**
 * 不支持的Debezium事件操作异常

 * @author logz
 * @date 2024-01-09
 */
public class UnsupportedDebeziumEventOpException extends RuntimeException {

    /**
     * 构造器
     * @param message 错误信息
     */
    public UnsupportedDebeziumEventOpException(String message) {
        super(message);
    }

}
