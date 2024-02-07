package vip.logz.rdbsync.connector.postgres.utils;

import vip.logz.rdbsync.common.utils.sql.GenericSqlDialectService;

/**
 * Postgres方言服务
 *
 * @author logz
 * @date 2024-02-06
 */
public class PostgresDialectService extends GenericSqlDialectService {

    /** 标志：字符串（单引号） */
    private static final String TOKEN_STRING = "'";

    /** 标志：标识符起始（双引号） */
    private static final String TOKEN_IDENTIFIER_BEGIN = "\"";

    /** 标志：标识符结束（双引号） */
    private static final String TOKEN_IDENTIFIER_END = "\"";

    /**
     * 构造器
     */
    public PostgresDialectService() {
        setTokenString(TOKEN_STRING);
        setTokenIdentifierBegin(TOKEN_IDENTIFIER_BEGIN);
        setTokenIdentifierEnd(TOKEN_IDENTIFIER_END);
    }

}
