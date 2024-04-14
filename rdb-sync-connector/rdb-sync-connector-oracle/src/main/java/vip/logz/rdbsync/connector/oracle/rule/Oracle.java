package vip.logz.rdbsync.connector.oracle.rule;

import vip.logz.rdbsync.common.rule.Rdb;
import vip.logz.rdbsync.common.rule.data.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

/**
 * Oracle数据库
 *
 * @author logz
 * @date 2024-03-18
 */
public final class Oracle implements Rdb {

    private Oracle() {
    }


    /*
     数值类型
     */

    /** 字段类型：NUMBER(precision) */
    public static FieldType<Oracle, BigDecimal> NUMBER(int precision) {
        return new DecimalFieldType<>("NUMBER", precision);
    }

    /** 字段类型：NUMBER(precision, scale) */
    public static FieldType<Oracle, BigDecimal> NUMBER(int precision, int scale) {
        return new DecimalFieldType<>("NUMBER", precision, scale);
    }

    /** 字段类型：FLOAT(precision) */
    public static FieldType<Oracle, BigDecimal> FLOAT(int precision) {
        return new DecimalFieldType<>("FLOAT", precision);
    }

    /** 字段类型：SMALLINT */
    public static FieldType<Oracle, Integer> SMALLINT() {
        return new IntFieldType<>("SMALLINT");
    }

    /** 字段类型：INTEGER */
    public static FieldType<Oracle, Integer> INTEGER() {
        return new IntFieldType<>("INTEGER");
    }

    /** 字段类型：BINARY_FLOAT */
    public static FieldType<Oracle, Float> BINARY_FLOAT() {
        return new FloatFieldType<>("BINARY_FLOAT");
    }

    /** 字段类型：BINARY_DOUBLE */
    public static FieldType<Oracle, Double> BINARY_DOUBLE() {
        return new DoubleFieldType<>("BINARY_DOUBLE");
    }


    /*
     日期时间类型
     */

    /** 字段类型：DATE */
    public static FieldType<Oracle, LocalDate> DATE() {
        return new DateFieldTypeNext<>("DATE");
    }

    /** 字段类型：TIMESTAMP */
    public static FieldType<Oracle, LocalDateTime> TIMESTAMP() {
        return new TimestampFieldType<>("TIMESTAMP");
    }

    /** 字段类型：TIMESTAMP(fractional_seconds_precision) */
    public static FieldType<Oracle, LocalDateTime> TIMESTAMP(int fractionalSecondsPrecision) {
        return new TimestampFieldType<>("TIMESTAMP", fractionalSecondsPrecision);
    }

    /** 字段类型：TIMESTAMP WITH TIME ZONE */
    public static FieldType<Oracle, OffsetDateTime> TIMESTAMP_WITH_TIME_ZONE() {
        return new TimestampTZFieldType<>("TIMESTAMP WITH TIME ZONE");
    }

    /** 字段类型：TIMESTAMP(fractional_seconds_precision) WITH TIME ZONE */
    public static FieldType<Oracle, OffsetDateTime> TIMESTAMP_WITH_TIME_ZONE(int fractionalSecondsPrecision) {
        return new TimestampTZFieldType<>("TIMESTAMP{} WITH TIME ZONE", fractionalSecondsPrecision);
    }

    /** 字段类型：TIMESTAMP WITH LOCAL TIME ZONE */
    public static FieldType<Oracle, OffsetDateTime> TIMESTAMP_WITH_LOCAL_TIME_ZONE() {
        return new TimestampTZFieldType<>("TIMESTAMP WITH LOCAL TIME ZONE");
    }

    /** 字段类型：TIMESTAMP(fractional_seconds_precision) WITH LOCAL TIME ZONE */
    public static FieldType<Oracle, OffsetDateTime> TIMESTAMP_WITH_LOCAL_TIME_ZONE(int fractionalSecondsPrecision) {
        return new TimestampTZFieldType<>("TIMESTAMP{} WITH LOCAL TIME ZONE", fractionalSecondsPrecision);
    }


    /*
     字符串类型
     */

    /** 字段类型：CHAR(n) */
    public static FieldType<Oracle, String> CHAR(int n) {
        return new TextFieldType<>("CHAR", n);
    }

    /** 字段类型：VARCHAR(n) */
    public static FieldType<Oracle, String> VARCHAR(int n) {
        return new TextFieldType<>("VARCHAR", n);
    }

    /** 字段类型：VARCHAR2(n) */
    public static FieldType<Oracle, String> VARCHAR2(int n) {
        return new TextFieldType<>("VARCHAR2", n);
    }

    /** 字段类型：CLOB */
    public static FieldType<Oracle, String> CLOB() {
        return new TextFieldType<>("CLOB");
    }

    /** 字段类型：NCHAR(n) */
    public static FieldType<Oracle, String> NCHAR(int n) {
        return new TextFieldType<>("NCHAR", n);
    }

    /** 字段类型：NVARCHAR(n) */
    public static FieldType<Oracle, String> NVARCHAR(int n) {
        return new TextFieldType<>("NVARCHAR", n);
    }

    /** 字段类型：NVARCHAR2(n) */
    public static FieldType<Oracle, String> NVARCHAR2(int n) {
        return new TextFieldType<>("NVARCHAR2", n);
    }

    /** 字段类型：NCLOB */
    public static FieldType<Oracle, String> NCLOB() {
        return new TextFieldType<>("NCLOB");
    }

    /** 字段类型：LONG */
    public static FieldType<Oracle, String> LONG() {
        return new TextFieldType<>("LONG");
    }


    /*
     二进制类型
     */

    /** 字段类型：RAW(size) */
    public static FieldType<Oracle, byte[]> RAW(int size) {
        return new BytesFieldType<>("RAW", size);
    }

    /** 字段类型：LONG RAW */
    public static FieldType<Oracle, byte[]> LONG_RAW() {
        return new BytesFieldType<>("LONG RAW");
    }

    /** 字段类型：BLOB */
    public static FieldType<Oracle, byte[]> BLOB() {
        return new BytesFieldType<>("BLOB");
    }

}
