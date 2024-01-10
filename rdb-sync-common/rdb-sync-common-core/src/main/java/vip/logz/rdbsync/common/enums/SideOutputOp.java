package vip.logz.rdbsync.common.enums;

/**
 * 旁路输出操作
 *
 * @author logz
 * @date 2024-01-09
 */
public enum SideOutputOp {

    /** 更新或插入 */
    UPSERT,

    /** 删除 */
    DELETE,

    /** 二者通用 */
    BOTH,

}
