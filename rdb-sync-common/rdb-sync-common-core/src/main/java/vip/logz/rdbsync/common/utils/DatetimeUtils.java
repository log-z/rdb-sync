package vip.logz.rdbsync.common.utils;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * 日期时间工具
 *
 * @author logz
 * @date 2024-01-10
 */
public class DatetimeUtils {

    /** 一秒中的毫秒数 */
    private static final long NANO_SECOND_NUM = 1000L;

    /**
     * 将UTC时间戳转换为日期时间
     * @param timestamp UTC时间戳
     * @return 返回日期时间
     */
    public static LocalDateTime ofUtc(Long timestamp) {
        long epochSecond = timestamp / NANO_SECOND_NUM;
        int nanoOfSecond = (int) (timestamp % NANO_SECOND_NUM);
        return LocalDateTime.ofEpochSecond(epochSecond, nanoOfSecond, ZoneOffset.UTC);
    }

}
