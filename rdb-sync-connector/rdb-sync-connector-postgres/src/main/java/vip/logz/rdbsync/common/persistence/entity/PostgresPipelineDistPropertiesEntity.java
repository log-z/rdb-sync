package vip.logz.rdbsync.common.persistence.entity;

import vip.logz.rdbsync.connector.jdbc.persistence.entity.JdbcPipelineDistPropertiesEntity;

/**
 * Postgres管道目标属性实体
 *
 * @author logz
 * @date 2024-02-05
 */
public class PostgresPipelineDistPropertiesEntity extends JdbcPipelineDistPropertiesEntity {

    /** 主机列表 */
    private String hosts;

    /** 端口列表 */
    private String ports;

    /**
     * 获取主机列表
     */
    public String getHosts() {
        return hosts;
    }

    /**
     * 设置主机列表
     * @param hosts 主机列表
     */
    public void setHosts(String hosts) {
        this.hosts = hosts;
    }

    /**
     * 获取端口列表
     */
    public String getPorts() {
        return ports;
    }

    /**
     * 设置端口列表
     * @param ports 端口列表
     */
    public void setPorts(String ports) {
        this.ports = ports;
    }

}
