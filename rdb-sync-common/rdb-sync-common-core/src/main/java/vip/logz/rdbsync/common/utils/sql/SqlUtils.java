package vip.logz.rdbsync.common.utils.sql;

/**
 * SQL工具
 *
 * @author logz
 * @date 2024-01-10
 */
public class SqlUtils {

    /*
     字符串相关
     */

    /** 标志：字符串（单引号） */
    private static final String TOKEN_STRING = "'";

    /** 标志：已转义的字符串（转义单引号） */
    private static final String TOKEN_STRING_ESCAPED = "\\'";

    /** 格式化器：字符串字面量 */
    private static final String FORMATTER_STRING_LITERAL = "'%s'";

    /**
     * 获取字符串字面量
     * @param strings 若干个字符串
     * @return 返回一个数组，各个元素是对应字符串的字面量
     */
    public static String[] stringLiteral(String... strings) {
        String[] literals = new String[strings.length];
        for (int i = 0; i < strings.length; i++) {
            literals[i] = stringLiteral(strings[i]);
        }

        return literals;
    }

    /**
     * 获取字符串字面量
     * @param string 字符串
     * @return 返回对应的字面量
     */
    public static String stringLiteral(String string) {
        String escaped = string.replace(TOKEN_STRING, TOKEN_STRING_ESCAPED);
        return String.format(FORMATTER_STRING_LITERAL, escaped);
    }


    /*
     标识符相关
     */

    /** 标志：标识符（反引号） */
    private static final String TOKEN_IDENTIFIER = "`";

    /** 标志：已转义的标识符（转义反引号） */
    private static final String TOKEN_IDENTIFIER_ESCAPED = "\\`";

    /** 格式化器：标识符字面量 */
    private static final String FORMATTER_IDENTIFIER_LITERAL = "`%s`";

    /**
     * 获取标识符字面量
     * @param name 标识符名称
     * @return 返回对应的字面量
     */
    public static String identifierLiteral(String name) {
        String escaped = name.replace(TOKEN_IDENTIFIER, TOKEN_IDENTIFIER_ESCAPED);
        return String.format(FORMATTER_IDENTIFIER_LITERAL, escaped);
    }

}
