package vip.logz.rdbsync.common.exception;

/**
 * 不支持的目标协议异常

 * @author logz
 * @date 2024-01-09
 */
public class UnsupportedDistProtocolException extends RuntimeException {

    /**
     * 构造器
     * @param protocol 协议名
     */
    public UnsupportedDistProtocolException(String protocol) {
        super(protocol);
    }

}
