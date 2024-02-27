package vip.logz.rdbsync.common.config;

/**
 * 语义保证选项
 *
 * @author logz
 * @date 2024-02-24
 */
public interface SemanticOptions {

    /** 至少一次 */
    String AT_LEAST_ONCE = "at-least-once";

    /** 精确一次 */
    String EXACTLY_ONCE = "exactly-once";

}
