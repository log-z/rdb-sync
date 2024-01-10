package vip.logz.rdbsync.common.job.debezium;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Debezium事件来源信息
 *
 * @author logz
 * @date 2024-01-09
 */
public class DebeziumEventSource {

    /** 连接器版本 */
    private String version;

    /** 连接器名称 */
    private String connector;

    /** 数据源名称 */
    private String name;

    /** 时间戳 */
    @JsonProperty("ts_ms")
    private Long tsMs;

    /** 是否快照中 */
    private String snapshot;

    /** 数据库名 */
    private String db;

    /** 序列名 */
    private String sequence;

    /** 表名 */
    private String table;

    /** 模拟服务端ID */
    @JsonProperty("server_id")
    private Long serverId;

    /**
     * 获取连接器版本
     */
    public String getVersion() {
        return version;
    }

    /**
     * 设置连接器版本
     * @param version 连接器版本
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * 获取连接器名称
     */
    public String getConnector() {
        return connector;
    }

    /**
     * 设置连接器名称
     * @param connector 连接器名称
     */
    public void setConnector(String connector) {
        this.connector = connector;
    }

    /**
     * 获取数据源名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置数据源名称
     * @param name 数据源名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取时间戳
     */
    public Long getTsMs() {
        return tsMs;
    }

    /**
     * 设置时间戳
     * @param tsMs 时间戳
     */
    public void setTsMs(Long tsMs) {
        this.tsMs = tsMs;
    }

    /**
     * 获取是否快照中
     */
    public String getSnapshot() {
        return snapshot;
    }

    /**
     * 设置是否快照中
     * @param snapshot 是否快照中
     */
    public void setSnapshot(String snapshot) {
        this.snapshot = snapshot;
    }

    /**
     * 获取数据库名
     */
    public String getDb() {
        return db;
    }

    /**
     * 设置数据库名
     * @param db 数据库名
     */
    public void setDb(String db) {
        this.db = db;
    }

    /**
     * 获取序列名
     */
    public String getSequence() {
        return sequence;
    }

    /**
     * 设置序列名
     * @param sequence 序列名
     */
    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    /**
     * 获取表名
     */
    public String getTable() {
        return table;
    }

    /**
     * 设置表名
     * @param table 表名
     */
    public void setTable(String table) {
        this.table = table;
    }

    /**
     * 获取模拟服务端ID
     */
    public Long getServerId() {
        return serverId;
    }

    /**
     * 设置模拟服务端ID
     * @param serverId 模拟服务端ID
     */
    public void setServerId(Long serverId) {
        this.serverId = serverId;
    }

}
