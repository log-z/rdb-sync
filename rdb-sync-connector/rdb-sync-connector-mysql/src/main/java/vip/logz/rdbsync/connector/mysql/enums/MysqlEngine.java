package vip.logz.rdbsync.connector.mysql.enums;

/**
 * Mysql引擎
 *
 * @author logz
 * @date 2024-01-10
 */
public enum MysqlEngine {

    /** InnoDB */
    INNO_DB("InnoDB"),

    /** MyISAM */
    MY_ISAM("MyISAM"),
    ;


    /** 名称 */
    private final String name;

    /**
     * 构造器
     * @param name 名称
     */
    MysqlEngine(String name) {
        this.name = name;
    }

    /** 转化为字符串 */
    @Override
    public String toString() {
        return name;
    }

}
