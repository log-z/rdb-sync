package vip.logz.rdbsync.connector.sqlserver.utils;

import vip.logz.rdbsync.common.utils.sql.GenericSqlDialectService;

/**
 * SQLServer转换服务
 *
 * @author logz
 * @date 2024-01-29
 */
public class SqlserverDialectService extends GenericSqlDialectService {

    /** 标志：字符串（单引号） */
    private static final String TOKEN_STRING = "'";

    /** 标志：标识符起始（方括号头） */
    private static final String TOKEN_IDENTIFIER_BEGIN = "[";

    /** 标志：标识符结束（方括号尾） */
    private static final String TOKEN_IDENTIFIER_END = "]";

    /** 前缀：字符串字面量（表示使用Unicode编码） */
    private static final String PREFIX_STRING_LITERAL = "N";

    /**
     * 构造器
     */
    public SqlserverDialectService() {
        setTokenString(TOKEN_STRING);
        setTokenIdentifierBegin(TOKEN_IDENTIFIER_BEGIN);
        setTokenIdentifierEnd(TOKEN_IDENTIFIER_END);
    }

    /**
     * 获取字符串字面量
     * @param string 字符串，支持Unicode字符
     * @return 返回对应的字面量
     */
    @Override
    public String stringLiteral(String string) {
        return PREFIX_STRING_LITERAL + super.stringLiteral(string);
    }

}
