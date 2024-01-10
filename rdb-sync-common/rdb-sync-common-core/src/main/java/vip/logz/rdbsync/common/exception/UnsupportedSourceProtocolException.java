package vip.logz.rdbsync.common.exception;

/**
 * 不支持的来源协议异常

 * @author logz
 * @date 2024-01-09
 */
public class UnsupportedSourceProtocolException extends RuntimeException {

    /**
     * 构造器
     * @param protocol 协议名
     */
    public UnsupportedSourceProtocolException(String protocol) {
        super(protocol);
    }

}
