package vip.logz.rdbsync.connector.starrocks.utils;

import vip.logz.rdbsync.common.utils.sql.GenericSqlDialectService;

/**
 * StarRocks方言服务
 *
 * @author logz
 * @date 2024-01-31
 */
public class StarrocksDialectService extends GenericSqlDialectService {

    /** 标志：字符串（单引号） */
    private static final String TOKEN_STRING = "'";

    /** 标志：标识符起始（反引号） */
    private static final String TOKEN_IDENTIFIER_BEGIN = "`";

    /** 标志：标识符结束（反引号） */
    private static final String TOKEN_IDENTIFIER_END = "`";

    /**
     * 构造器
     */
    public StarrocksDialectService() {
        setTokenString(TOKEN_STRING);
        setTokenIdentifierBegin(TOKEN_IDENTIFIER_BEGIN);
        setTokenIdentifierEnd(TOKEN_IDENTIFIER_END);
    }

}
