package vip.logz.rdbsync.common.annotations;

import java.lang.annotation.*;

/**
 * 可被扫描的标志
 *
 * @author logz
 * @date 2024-01-09
 * @see vip.logz.rdbsync.common.utils.ClassScanner
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Scannable {
}
