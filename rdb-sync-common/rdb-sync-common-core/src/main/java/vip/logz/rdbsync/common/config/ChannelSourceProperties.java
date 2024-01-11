package vip.logz.rdbsync.common.config;

/**
 * 频道来源属性
 *
 * @author logz
 * @date 2024-01-09
 */
public class ChannelSourceProperties {

    /** ID */
    private String id;

    /** 名称 */
    private String name;

    /** 协议 */
    private String protocol;

    /**
     * 获取ID
     */
    public String getId() {
        return id;
    }

    /**
     * 设置ID
     * @param id ID
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 获取名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置名称
     * @param name 名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取协议
     */
    public String getProtocol() {
        return protocol;
    }

    /**
     * 设置协议
     * @param protocol 协议
     */
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

}
