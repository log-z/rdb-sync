package vip.logz.rdbsync.common.exception;

/**
 * 不支持的目标语义保证异常

 * @author logz
 * @date 2024-02-24
 */
public class UnsupportedDistSemanticException extends RuntimeException {

    /**
     * 构造器
     * @param semantic 语义保证
     */
    public UnsupportedDistSemanticException(String semantic) {
        super(semantic);
    }

}
