package vip.logz.rdbsync.connector.mysql.config;

import vip.logz.rdbsync.common.config.ChannelSourceProperties;
import vip.logz.rdbsync.connector.mysql.rule.Mysql;

/**
 * Mysql频道来源属性
 *
 * @author logz
 * @date 2024-01-09
 */
public class MysqlChannelSourceProperties extends ChannelSourceProperties {

    /** 启动模式：先做快照，再读取最新日志 */
    public static final String STARTUP_MODE_INITIAL = "initial";

    /** 启动模式：跳过快照，从最早可用位置读取日志 */
    public static final String STARTUP_MODE_EARLIEST = "earliest-offset";

    /** 启动模式：跳过快照，仅读取最新日志 */
    public static final String STARTUP_MODE_LATEST = "latest-offset";

    /** 启动模式：跳过快照，从指定位置开始读取日志 */
    public static final String STARTUP_MODE_SPECIFIC_OFFSET = "specific-offset";

    /** 启动模式：跳过快照，从指定时间戳开始读取日志 */
    public static final String STARTUP_MODE_TIMESTAMP = "timestamp-offset";

    /** 模拟服务端ID */
    private String serverId;

    /** 启动模式 */
    private String startupMode;

    /** 启动细节：起始日志文件 */
    private String startupSpecificOffsetFile;

    /** 启动细节：起始日志文件内位置 */
    private Long startupSpecificOffsetPos;

    /** 启动细节：起始GTID */
    private String startupSpecificOffsetGtidSet;

    /** 启动细节：起始时间戳 */
    private Long startupTimestampMillis;

    /**
     * 获取模拟服务端ID
     */
    public String getServerId() {
        return serverId;
    }

    /**
     * 设置模拟服务端ID
     * @param serverId 模拟服务端ID
     */
    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    /**
     * 获取启动模式
     */
    public String getStartupMode() {
        return startupMode != null ? startupMode : STARTUP_MODE_INITIAL;
    }

    /**
     * 设置启动模式
     * @param startupMode 启动模式
     */
    public void setStartupMode(String startupMode) {
        this.startupMode = startupMode;
    }

    /**
     * 获取启动细节：起始日志文件
     */
    public String getStartupSpecificOffsetFile() {
        return startupSpecificOffsetFile;
    }

    /**
     * 设置启动细节：起始日志文件
     * @param startupSpecificOffsetFile 启动细节：起始日志文件
     */
    public void setStartupSpecificOffsetFile(String startupSpecificOffsetFile) {
        this.startupSpecificOffsetFile = startupSpecificOffsetFile;
    }

    /**
     * 获取启动细节：起始日志文件内位置
     */
    public Long getStartupSpecificOffsetPos() {
        return startupSpecificOffsetPos != null ? startupSpecificOffsetPos : 0L;
    }

    /**
     * 设置启动细节：起始日志文件内位置
     * @param startupSpecificOffsetPos 启动细节：起始日志文件内位置
     */
    public void setStartupSpecificOffsetPos(Long startupSpecificOffsetPos) {
        this.startupSpecificOffsetPos = startupSpecificOffsetPos;
    }

    /**
     * 获取启动细节：起始GTID
     */
    public String getStartupSpecificOffsetGtidSet() {
        return startupSpecificOffsetGtidSet;
    }

    /**
     * 设置启动细节：起始GTID
     * @param startupSpecificOffsetGtidSet 启动细节：起始GTID
     */
    public void setStartupSpecificOffsetGtidSet(String startupSpecificOffsetGtidSet) {
        this.startupSpecificOffsetGtidSet = startupSpecificOffsetGtidSet;
    }

    /**
     * 获取启动细节：起始时间戳
     */
    public Long getStartupTimestampMillis() {
        return startupTimestampMillis;
    }

    /**
     * 设置启动细节：起始时间戳
     * @param startupTimestampMillis 启动细节：起始时间戳
     */
    public void setStartupTimestampMillis(Long startupTimestampMillis) {
        this.startupTimestampMillis = startupTimestampMillis;
    }

    /**
     * 获取协议
     */
    @Override
    public String getProtocol() {
        return Mysql.class.getSimpleName();
    }

}
