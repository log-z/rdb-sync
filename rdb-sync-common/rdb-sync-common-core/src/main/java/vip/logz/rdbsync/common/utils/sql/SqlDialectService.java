package vip.logz.rdbsync.common.utils.sql;

/**
 * SQL方言服务
 *
 * @author logz
 * @date 2024-01-31
 */
public interface SqlDialectService {

    /*
     字符串相关
     */

    /**
     * 获取字符串字面量
     * @param string 字符串
     * @return 返回对应的字面量
     */
    String stringLiteral(String string);


    /*
     标识符相关
     */

    /**
     * 获取标识符字面量
     * @param name 标识符名称
     * @return 返回对应的字面量
     */
    String identifierLiteral(String name);

}
