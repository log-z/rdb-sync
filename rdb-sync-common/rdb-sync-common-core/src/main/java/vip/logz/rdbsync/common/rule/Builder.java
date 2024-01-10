package vip.logz.rdbsync.common.rule;

/**
 * 构建器
 *
 * @param <Outside> 外围构建器。若当前构建器已是最外围时，此参数是它本身。
 * @author logz
 * @date 2024-01-10
 */
public interface Builder<Outside extends Builder<Outside>> {

    /**
     * 退出当前构建器，回到外围构建器
     * @return 返回外围构建器
     */
    Outside and();

}
