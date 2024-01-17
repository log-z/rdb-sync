package vip.logz.rdbsync.common.utils;

/**
 * 字符串工具
 *
 * @author logz
 * @date 2024-01-17
 */
public class StringUtils {

    /**
     * 安全地移除末尾字符串
     * @param str 字符串
     * @param deleting 需要删除的内容
     * @return 返回已截短的字符串
     */
    public static String removeEnd(String str, String deleting) {
        if (str.isEmpty() || deleting.isEmpty()) {
            return str;
        }

        if (str.endsWith(deleting)) {
            int strLen = str.length();
            int tailLen = deleting.length();
            return str.substring(0, strLen - tailLen);
        } else {
            return str;
        }
    }

    /**
     * 安全地移除末尾字符串
     * @param sb 字符串构建器
     * @param deleting 需要删除的内容
     * @return 返回已截短的字符串构建器
     */
    public static StringBuilder removeEnd(StringBuilder sb, String deleting) {
        int strLen = sb.length();
        int tailLen = deleting.length();

        if (strLen == 0 || deleting.isEmpty()) {
            return sb;
        }

        String tail = sb.substring(strLen - tailLen);
        if (tail.equals(deleting)) {
            return sb.delete(strLen - tailLen, strLen);
        } else {
            return sb;
        }
    }

}
