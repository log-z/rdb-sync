package vip.logz.rdbsync.common.exception;

import vip.logz.rdbsync.common.enums.RdbSyncEventOp;

/**
 * 不支持的数据同步事件操作异常

 * @author logz
 * @date 2024-01-09
 */
public class UnsupportedRdbSyncEventOpException extends RuntimeException {

    /**
     * 构造器
     * @param protocol 协议名
     * @param op 数据同步事件操作
     */
    public UnsupportedRdbSyncEventOpException(String protocol, RdbSyncEventOp op) {
        super(String.format(
                "Dist DB protocol [%s] is not support operation %s.",
                protocol,
                op
        ));
    }

}
