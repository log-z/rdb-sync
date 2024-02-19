package vip.logz.rdbsync.connector.sqlserver.rule;

import vip.logz.rdbsync.common.rule.Rdb;
import vip.logz.rdbsync.common.rule.data.*;

import java.math.BigDecimal;

/**
 * SQLServer数据库
 *
 * @author logz
 * @date 2024-01-27
 */
public final class Sqlserver implements Rdb {

    private Sqlserver() {
    }


    /*
     数值类型
     */

    /** 字段类型：BIT */
    public static FieldType<Sqlserver, Integer> BIT() {
        return new IntFieldType<>("BIT");
    }

    /** 字段类型：TINYINT */
    public static FieldType<Sqlserver, Integer> TINYINT() {
        return new IntFieldType<>("TINYINT");
    }

    /** 字段类型：SMALLINT */
    public static FieldType<Sqlserver, Integer> SMALLINT() {
        return new IntFieldType<>("SMALLINT");
    }

    /** 字段类型：INT */
    public static FieldType<Sqlserver, Integer> INT() {
        return new IntFieldType<>("INT");
    }

    /** 字段类型：BIGINT */
    public static FieldType<Sqlserver, Long> BIGINT() {
        return new BigintFieldType<>("BIGINT");
    }

    /** 字段类型：DECIMAL(p, s) */
    public static FieldType<Sqlserver, BigDecimal> DECIMAL(int p, int s) {
        return new DecimalFieldType<>("DECIMAL", p, s);
    }

    /** 字段类型：NUMERIC(p, s) */
    public static FieldType<Sqlserver, BigDecimal> NUMERIC(int p, int s) {
        return new DecimalFieldType<>("NUMERIC", p, s);
    }

    /** 字段类型：SMALLMONEY */
    public static FieldType<Sqlserver, BigDecimal> SMALLMONEY() {
        return new DecimalFieldType<>("SMALLMONEY");
    }

    /** 字段类型：MONEY */
    public static FieldType<Sqlserver, BigDecimal> MONEY() {
        return new DecimalFieldType<>("MONEY");
    }

    /** 字段类型：REAL */
    public static FieldType<Sqlserver, Float> REAL() {
        return new FloatFieldType<>("REAL");
    }

    /** 字段类型：FLOAT(n) */
    public static FieldType<Sqlserver, Double> FLOAT(int n) {
        return new DoubleFieldType<>("FLOAT", n);
    }


    /*
     日期时间类型
     */

    /** 字段类型：DATE */
    public static FieldType<Sqlserver, String> DATE() {
        return new DateFieldType<>("DATE");
    }

    /** 字段类型：TIME */
    public static FieldType<Sqlserver, String> TIME() {
        return new TimeFieldType<>("TIME");
    }

    /** 字段类型：TIME(fractional_seconds_precision) */
    public static FieldType<Sqlserver, String> TIME(int fractionalSecondsPrecision) {
        return new TimeFieldType<>("TIME", fractionalSecondsPrecision);
    }

    /** 字段类型：SMALLDATETIME */
    public static FieldType<Sqlserver, String> SMALLDATETIME() {
        return new DatetimeFieldType<>("SMALLDATETIME");
    }

    /** 字段类型：DATETIME */
    public static FieldType<Sqlserver, String> DATETIME() {
        return new DatetimeFieldType<>("DATETIME");
    }

    /** 字段类型：DATETIME2 */
    public static FieldType<Sqlserver, String> DATETIME2() {
        return new DatetimeFieldType<>("DATETIME2");
    }

    /** 字段类型：DATETIMEOFFSET */
    public static FieldType<Sqlserver, String> DATETIMEOFFSET() {
        return new DatetimeFieldType<>("DATETIMEOFFSET");
    }

    /** 字段类型：DATETIMEOFFSET(fractional_seconds_precision) */
    public static FieldType<Sqlserver, String> DATETIMEOFFSET(int fractionalSecondsPrecision) {
        return new DatetimeFieldType<>("DATETIMEOFFSET", fractionalSecondsPrecision);
    }


    /*
     字符串类型
     */

    /** 字段类型：CHAR(n) */
    public static FieldType<Sqlserver, String> CHAR(int n) {
        return new TextFieldType<>("CHAR", n);
    }

    /** 字段类型：VARCHAR(n) */
    public static FieldType<Sqlserver, String> VARCHAR(int n) {
        return new TextFieldType<>("VARCHAR", n);
    }

    /** 字段类型：TEXT */
    public static FieldType<Sqlserver, String> TEXT() {
        return new TextFieldType<>("TEXT");
    }

    /** 字段类型：NCHAR(n) */
    public static FieldType<Sqlserver, String> NCHAR(int n) {
        return new TextFieldType<>("NCHAR", n);
    }

    /** 字段类型：NVARCHAR(n) */
    public static FieldType<Sqlserver, String> NVARCHAR(int n) {
        return new TextFieldType<>("NVARCHAR", n);
    }

    /** 字段类型：NTEXT */
    public static FieldType<Sqlserver, String> NTEXT() {
        return new TextFieldType<>("NTEXT");
    }


    /*
     二进制类型
     */

    /** 字段类型：BINARY(n) */
    public static FieldType<Sqlserver, byte[]> BINARY(int n) {
        return new BytesFieldType<>("BINARY", n);
    }

    /** 字段类型：VARBINARY(n) */
    public static FieldType<Sqlserver, byte[]> VARBINARY(int n) {
        return new BytesFieldType<>("VARBINARY", n);
    }

    /** 字段类型：VARBINARY(MAX) */
    public static FieldType<Sqlserver, byte[]> VARBINARY_MAX() {
        return new BytesFieldType<>("VARBINARY", "MAX");
    }

    /** 字段类型：IMAGE */
    public static FieldType<Sqlserver, byte[]> IMAGE() {
        return new BytesFieldType<>("IMAGE");
    }


    /*
     其它类型
     */

    /** 字段类型：HIERARCHYID */
    public static FieldType<Sqlserver, String> HIERARCHYID() {
        return new TextFieldType<>("HIERARCHYID");
    }

    /** 字段类型：UNIQUEIDENTIFIER */
    public static FieldType<Sqlserver, String> UNIQUEIDENTIFIER() {
        return new TextFieldType<>("UNIQUEIDENTIFIER");
    }

}
