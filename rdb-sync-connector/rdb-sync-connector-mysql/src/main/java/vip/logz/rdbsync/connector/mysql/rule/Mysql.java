package vip.logz.rdbsync.connector.mysql.rule;

import vip.logz.rdbsync.common.rule.Rdb;
import vip.logz.rdbsync.common.rule.data.*;
import vip.logz.rdbsync.common.utils.sql.SqlUtils;

import java.math.BigDecimal;

/**
 * MySQL数据库
 *
 * @author logz
 * @date 2024-01-10
 */
public final class Mysql implements Rdb {

    private Mysql() {
    }

    /** 字段类型：TINYINT */
    public static FieldType<Mysql, Integer> TINYINT() {
        return new IntFieldType<>("TINYINT");
    }

    /** 字段类型：TINYINT(n) */
    public static FieldType<Mysql, Integer> TINYINT(int n) {
        return new IntFieldType<>("TINYINT", n);
    }

    /** 字段类型：SMALLINT */
    public static FieldType<Mysql, Integer> SMALLINT() {
        return new IntFieldType<>("SMALLINT");
    }

    /** 字段类型：SMALLINT(n) */
    public static FieldType<Mysql, Integer> SMALLINT(int n) {
        return new IntFieldType<>("SMALLINT", n);
    }

    /** 字段类型：MEDIUMINT */
    public static FieldType<Mysql, Integer> MEDIUMINT() {
        return new IntFieldType<>("MEDIUMINT");
    }

    /** 字段类型：MEDIUMINT(n) */
    public static FieldType<Mysql, Integer> MEDIUMINT(int n) {
        return new IntFieldType<>("MEDIUMINT", n);
    }

    /** 字段类型：INTEGER */
    public static FieldType<Mysql, Integer> INTEGER() {
        return new IntFieldType<>("INTEGER");
    }

    /** 字段类型：INTEGER(n) */
    public static FieldType<Mysql, Integer> INTEGER(int n) {
        return new IntFieldType<>("INTEGER", n);
    }

    /** 字段类型：BIGINT */
    public static FieldType<Mysql, Long> BIGINT() {
        return new BigintFieldType<>("BIGINT");
    }

    /** 字段类型：BIGINT(n) */
    public static FieldType<Mysql, Long> BIGINT(int n) {
        return new BigintFieldType<>("BIGINT", n);
    }

    /** 字段类型：DECIMAL(p, s) */
    public static FieldType<Mysql, BigDecimal> DECIMAL(int p, int s) {
        return new DecimalFieldType<>("DECIMAL", p, s);
    }

    /** 字段类型：FLOAT */
    public static FieldType<Mysql, Float> FLOAT() {
        return new FloatFieldType<>("FLOAT");
    }

    /** 字段类型：DOUBLE */
    public static FieldType<Mysql, Double> DOUBLE() {
        return new DoubleFieldType<>("DOUBLE");
    }

    /** 字段类型：DATE */
    public static FieldType<Mysql, String> DATE() {
        return new DateFieldType<>("DATE");
    }

    /** 字段类型：TIME */
    public static FieldType<Mysql, String> TIME() {
        return new TimeFieldType<>("TIME");
    }

    /** 字段类型：DATETIME */
    public static FieldType<Mysql, String> DATETIME() {
        return new DatetimeFieldType<>("DATETIME");
    }

    /** 字段类型：TIMESTAMP */
    public static FieldType<Mysql, String> TIMESTAMP() {
        return new DatetimeFieldType<>("TIMESTAMP");
    }

    /** 字段类型：CHAR(n) */
    public static FieldType<Mysql, String> CHAR(int n) {
        return new TextFieldType<>("CHAR", n);
    }

    /** 字段类型：VARCHAR(n) */
    public static FieldType<Mysql, String> VARCHAR(int n) {
        return new TextFieldType<>("VARCHAR", n);
    }

    /** 字段类型：TINYTEXT */
    public static FieldType<Mysql, String> TINYTEXT() {
        return new TextFieldType<>("TINYTEXT");
    }

    /** 字段类型：TEXT */
    public static FieldType<Mysql, String> TEXT() {
        return new TextFieldType<>("TEXT");
    }

    /** 字段类型：MEDIUMTEXT */
    public static FieldType<Mysql, String> MEDIUMTEXT() {
        return new TextFieldType<>("MEDIUMTEXT");
    }

    /** 字段类型：LONGTEXT */
    public static FieldType<Mysql, String> LONGTEXT() {
        return new TextFieldType<>("LONGTEXT");
    }

    /** 字段类型：ENUM(...) */
    public static FieldType<Mysql, String> ENUM(String... elements) {
        return new TextFieldType<>("ENUM", (Object[]) SqlUtils.stringLiteral(elements));
    }

    /** 字段类型：SET(...) */
    public static FieldType<Mysql, String> SET(String... elements) {
        return new TextFieldType<>("SET", (Object[]) SqlUtils.stringLiteral(elements));
    }

}
