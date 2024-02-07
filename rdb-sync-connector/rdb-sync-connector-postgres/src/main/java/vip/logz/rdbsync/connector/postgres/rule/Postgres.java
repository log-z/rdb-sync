package vip.logz.rdbsync.connector.postgres.rule;

import vip.logz.rdbsync.common.rule.Rdb;
import vip.logz.rdbsync.common.rule.data.*;

import java.math.BigDecimal;

/**
 * Postgres数据库
 *
 * @author logz
 * @date 2024-02-05
 */
public final class Postgres implements Rdb {

    private Postgres() {
    }


    /*
     数值类型
     */

    /** 字段类型：INT2 */
    public static FieldType<Postgres, Integer> INT2() {
        return new IntFieldType<>("INT2");
    }

    /** 字段类型：INT4 */
    public static FieldType<Postgres, Integer> INT4() {
        return new IntFieldType<>("INT4");
    }

    /** 字段类型：INT8 */
    public static FieldType<Postgres, Integer> INT8() {
        return new IntFieldType<>("INT8");
    }

    /** 字段类型：SMALLINT */
    public static FieldType<Postgres, Integer> SMALLINT() {
        return new IntFieldType<>("SMALLINT");
    }

    /** 字段类型：INTEGER */
    public static FieldType<Postgres, Integer> INTEGER() {
        return new IntFieldType<>("INTEGER");
    }

    /** 字段类型：BIGINT */
    public static FieldType<Postgres, Long> BIGINT() {
        return new BigintFieldType<>("BIGINT");
    }

    /** 字段类型：DECIMAL(p, s) */
    public static FieldType<Postgres, BigDecimal> DECIMAL(int p, int s) {
        return new DecimalFieldType<>("DECIMAL", p, s);
    }

    /** 字段类型：NUMERIC(p, s) */
    public static FieldType<Postgres, BigDecimal> NUMERIC(int p, int s) {
        return new DecimalFieldType<>("NUMERIC", p, s);
    }

    /** 字段类型：MONEY */
    public static FieldType<Postgres, BigDecimal> MONEY() {
        return new DecimalFieldType<>("MONEY");
    }

    /** 字段类型：FLOAT4 */
    public static FieldType<Postgres, Float> FLOAT4() {
        return new FloatFieldType<>("FLOAT4");
    }

    /** 字段类型：FLOAT8 */
    public static FieldType<Postgres, Double> FLOAT8() {
        return new DoubleFieldType<>("FLOAT8");
    }

    /** 字段类型：REAL */
    public static FieldType<Postgres, Float> REAL() {
        return new FloatFieldType<>("REAL");
    }


    /*
     布尔类型
     */

    /** 字段类型：BOOLEAN */
    public static FieldType<Postgres, Boolean> BOOLEAN() {
        return new BooleanFieldType<>("BOOLEAN");
    }


    /*
     日期时间类型
     */

    /** 字段类型：DATE */
    public static FieldType<Postgres, String> DATE() {
        return new DateFieldType<>("DATE");
    }

    /** 字段类型：TIME */
    public static FieldType<Postgres, String> TIME() {
        return new TimeFieldType<>("TIME");
    }

    /** 字段类型：TIME(p) */
    public static FieldType<Postgres, String> TIME(int p) {
        return new TimeFieldType<>("TIME", p);
    }

    /** 字段类型：TIMESTAMP */
    public static FieldType<Postgres, String> TIMESTAMP() {
        return new DatetimeFieldType<>("TIMESTAMP");
    }

    /** 字段类型：TIMESTAMP(p) */
    public static FieldType<Postgres, String> TIMESTAMP(int p) {
        return new DatetimeFieldType<>("TIMESTAMP", p);
    }


    /*
     字符串类型
     */

    /** 字段类型：CHAR(n) */
    public static FieldType<Postgres, String> CHAR(int n) {
        return new TextFieldType<>("CHAR", n);
    }

    /** 字段类型：VARCHAR(n) */
    public static FieldType<Postgres, String> VARCHAR(int n) {
        return new TextFieldType<>("VARCHAR", n);
    }

    /** 字段类型：BPCHAR */
    public static FieldType<Postgres, String> BPCHAR() {
        return new TextFieldType<>("BPCHAR");
    }

    /** 字段类型：BPCHAR(n) */
    public static FieldType<Postgres, String> BPCHAR(int n) {
        return new TextFieldType<>("BPCHAR", n);
    }

    /** 字段类型：TEXT */
    public static FieldType<Postgres, String> TEXT() {
        return new TextFieldType<>("TEXT");
    }


    /*
     网络地址类型
     */

    /** 字段类型：CIDR */
    public static FieldType<Postgres, String> CIDR() {
        return new TextFieldType<>("CIDR");
    }

    /** 字段类型：INET */
    public static FieldType<Postgres, String> INET() {
        return new TextFieldType<>("INET");
    }

    /** 字段类型：MACADDR */
    public static FieldType<Postgres, String> MACADDR() {
        return new TextFieldType<>("MACADDR");
    }

    /** 字段类型：MACADDR8 */
    public static FieldType<Postgres, String> MACADDR8() {
        return new TextFieldType<>("MACADDR8");
    }


    /*
     网络地址类型
     */

    /** 字段类型：UUID */
    public static FieldType<Postgres, String> UUID() {
        return new TextFieldType<>("UUID");
    }

}
