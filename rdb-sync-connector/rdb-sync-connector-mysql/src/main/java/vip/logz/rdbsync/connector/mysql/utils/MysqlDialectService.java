package vip.logz.rdbsync.connector.mysql.utils;

import vip.logz.rdbsync.common.utils.sql.GenericSqlDialectService;

/**
 * MySQL方言服务
 *
 * @author logz
 * @date 2024-01-29
 */
public class MysqlDialectService extends GenericSqlDialectService {

    /** 标志：字符串（单引号） */
    private static final String TOKEN_STRING = "'";

    /** 标志：标识符起始（反引号） */
    private static final String TOKEN_IDENTIFIER_BEGIN = "`";

    /** 标志：标识符结束（反引号） */
    private static final String TOKEN_IDENTIFIER_END = "`";

    /**
     * 构造器
     */
    public MysqlDialectService() {
        setTokenString(TOKEN_STRING);
        setTokenIdentifierBegin(TOKEN_IDENTIFIER_BEGIN);
        setTokenIdentifierEnd(TOKEN_IDENTIFIER_END);
    }

    /**
     * 获取字符串字面量
     * @param strings 若干个字符串
     * @return 返回一个数组，各个元素是对应字符串的字面量
     */
    public String[] stringLiteral(String... strings) {
        String[] literals = new String[strings.length];
        for (int i = 0; i < strings.length; i++) {
            literals[i] = stringLiteral(strings[i]);
        }

        return literals;
    }

}
