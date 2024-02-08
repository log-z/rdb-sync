package vip.logz.rdbsync.common.job.debezium;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Debezium转换器回落常量
 *
 * @author logz
 * @date 2024-02-08
 */
public interface DebeziumConverterFallback {

    /** 日期：1970-01-01 */
    String DATE = DateTimeFormatter.ISO_LOCAL_DATE.format(LocalDate.ofEpochDay(0));

    /** 时间：00:00:00.0 */
    String TIME = "00:00:00.0";

    /** 日期时间：1970-01-01 00:00:00.0 */
    String DATETIME = Timestamp.from(Instant.EPOCH).toString();

}
