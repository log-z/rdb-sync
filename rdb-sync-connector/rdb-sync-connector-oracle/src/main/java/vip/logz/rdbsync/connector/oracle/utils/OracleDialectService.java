package vip.logz.rdbsync.connector.oracle.utils;

import vip.logz.rdbsync.common.utils.sql.GenericSqlDialectService;

/**
 * Oracle转换服务
 *
 * @author logz
 * @date 2024-03-18
 */
public class OracleDialectService extends GenericSqlDialectService {

    /** 标志：字符串（单引号） */
    private static final String TOKEN_STRING = "'";

    /** 标志：标识符起始（双引号） */
    private static final String TOKEN_IDENTIFIER_BEGIN = "\"";

    /** 标志：标识符结束（双引号） */
    private static final String TOKEN_IDENTIFIER_END = "\"";

    /**
     * 构造器
     */
    public OracleDialectService() {
        setTokenString(TOKEN_STRING);
        setTokenIdentifierBegin(TOKEN_IDENTIFIER_BEGIN);
        setTokenIdentifierEnd(TOKEN_IDENTIFIER_END);
    }

}
