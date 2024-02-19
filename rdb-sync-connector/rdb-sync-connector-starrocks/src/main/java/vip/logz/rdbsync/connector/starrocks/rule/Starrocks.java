package vip.logz.rdbsync.connector.starrocks.rule;

import vip.logz.rdbsync.common.rule.Rdb;
import vip.logz.rdbsync.common.rule.data.DateFieldType;
import vip.logz.rdbsync.common.rule.data.DatetimeFieldType;
import vip.logz.rdbsync.common.rule.data.FieldType;
import vip.logz.rdbsync.common.rule.data.PlainFieldType;

/**
 * StarRocks数据仓库
 *
 * @author logz
 * @date 2024-01-10
 */
public class Starrocks implements Rdb {

    private Starrocks() {
    }


    /*
     数值类型
     */

    /** 字段类型：SMALLINT */
    public static FieldType<Starrocks, ?> SMALLINT() {
        return new PlainFieldType<>("SMALLINT");
    }

    /** 字段类型：INT */
    public static FieldType<Starrocks, ?> INT() {
        return new PlainFieldType<>("INT");
    }

    /** 字段类型：BIGINT(m) */
    public static FieldType<Starrocks, ?> BIGINT(int m) {
        return new PlainFieldType<>("SMALLINT", m);
    }

    /** 字段类型：LARGEINT */
    public static FieldType<Starrocks, ?> LARGEINT() {
        return new PlainFieldType<>("LARGEINT");
    }

    /** 字段类型：DECIMAL(p, s) */
    public static FieldType<Starrocks, ?> DECIMAL(int p, int s) {
        return new PlainFieldType<>("DECIMAL", p, s);
    }

    /** 字段类型：FLOAT */
    public static FieldType<Starrocks, ?> FLOAT() {
        return new PlainFieldType<>("FLOAT");
    }

    /** 字段类型：DOUBLE */
    public static FieldType<Starrocks, ?> DOUBLE() {
        return new PlainFieldType<>("DOUBLE");
    }


    /*
     布尔类型
     */

    /** 字段类型：BOOLEAN */
    public static FieldType<Starrocks, ?> BOOLEAN() {
        return new PlainFieldType<>("BOOLEAN");
    }


    /*
     日期时间类型
     */

    /** 字段类型：DATE */
    public static FieldType<Starrocks, ?> DATE() {
        return new DateFieldType<>("DATE");
    }

    /** 字段类型：DATETIME */
    public static FieldType<Starrocks, ?> DATETIME() {
        return new DatetimeFieldType<>("DATETIME");
    }


    /*
     字符串类型
     */

    /** 字段类型：STRING */
    public static FieldType<Starrocks, ?> STRING() {
        return new PlainFieldType<>("STRING");
    }

    /** 字段类型：CHAR(m) */
    public static FieldType<Starrocks, ?> CHAR(int m) {
        return new PlainFieldType<>("CHAR", m);
    }

    /** 字段类型：VARCHAR(m) */
    public static FieldType<Starrocks, ?> VARCHAR(int m) {
        return new PlainFieldType<>("VARCHAR", m);
    }

}
