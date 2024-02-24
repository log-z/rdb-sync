package vip.logz.rdbsync.common.exception;

/**
 * 不支持的目标容错保证异常

 * @author logz
 * @date 2024-02-24
 */
public class UnsupportedDistGuaranteeException extends RuntimeException {

    /**
     * 构造器
     * @param guarantee 容错保证
     */
    public UnsupportedDistGuaranteeException(String guarantee) {
        super(guarantee);
    }

}
