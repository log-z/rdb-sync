package vip.logz.rdbsync.common.utils.sql;

/**
 * 通用SQL方言服务
 *
 * @author logz
 * @date 2024-01-29
 */
public class GenericSqlDialectService implements SqlDialectService {

    /** 转义字符 */
    private static final String ESCAPE = "\\";


    /*
     字符串相关
     */

    /** 标志：字符串 */
    private String tokenString = "\"";

    /**
     * 设置标志：字符串
     * @param tokenString 标志：字符串
     */
    protected void setTokenString(String tokenString) {
        this.tokenString = tokenString;
    }

    /**
     * 获取字符串字面量
     * @param string 字符串
     * @return 返回对应的字面量
     */
    @Override
    public String stringLiteral(String string) {
        String escaped = string.replace(tokenString, ESCAPE + tokenString);
        String formatter = tokenString + "%s" + tokenString;
        return String.format(formatter, escaped);
    }

    /*
     标识符相关
     */

    /** 标志：标识符起始 */
    private String tokenIdentifierBegin = "\"";

    /** 标志：标识符结束 */
    private String tokenIdentifierEnd = "\"";

    /**
     * 设置标志：标识符起始
     * @param tokenIdentifierBegin 标志：标识符起始
     */
    protected void setTokenIdentifierBegin(String tokenIdentifierBegin) {
        this.tokenIdentifierBegin = tokenIdentifierBegin;
    }

    /**
     * 设置标志：标识符结束
     * @param tokenIdentifierEnd 标志：标识符结束
     */
    protected void setTokenIdentifierEnd(String tokenIdentifierEnd) {
        this.tokenIdentifierEnd = tokenIdentifierEnd;
    }

    /**
     * 获取标识符字面量
     * @param name 标识符名称
     * @return 返回对应的字面量
     */
    @Override
    public String identifierLiteral(String name) {
        String escaped = name.replace(tokenIdentifierBegin, ESCAPE + tokenIdentifierBegin)
                .replace(tokenIdentifierEnd, ESCAPE + tokenIdentifierEnd);
        String formatter = tokenIdentifierBegin + "%s" + tokenIdentifierEnd;
        return String.format(formatter, escaped);
    }

}
