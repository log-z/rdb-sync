# RDB Sync - MySQL 连接器


## 适用范围
- MySQL：5.6，5.7，8.0.x
- RDS MySQL：5.6，5.7，8.0.x
- PolarDB MySQL：5.6，5.7，8.0.x
- Aurora MySQL：5.6，5.7，8.0.x
- MariaDB：10.x
- PolarDB X：2.0.1


## 来源
### 先决条件
1. 必须启用 BinLog 功能；
2. 用户至少对“来源表”具有 `Select` 权限；
3. 用户至少对“来源数据库”具有 `Lock Tables`、`Replication Client` 和 `Replication Slave` 权限。

> 详情参考 Debezium 教程中 [配置 MySQL](https://debezium.io/documentation/reference/1.9/connectors/mysql.html#setting-up-mysql) 这部分。

### 管道配置
标记 `📌` 表示关键配置，在配置表中具有独立的字段，其余配置以 YAML 格式保存在 `options` 字段中。

| 配置                                             | 类型       | 默认值                        | 说明                                                                                                                                                                                       |
|------------------------------------------------|----------|----------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| id `📌`                                        | String   | _*必填_                      | 管道来源ID                                                                                                                                                                                   |
| name `📌`                                      | String   | _*必填_                      | 管道来源名称                                                                                                                                                                                   |
| protocol `📌`                                  | String   | _*必填_                      | 管道来源协议，设置为 `mysql` 以使用此连接器                                                                                                                                                               |
| hostname `📌host`                              | String   | localhost                  | 主机                                                                                                                                                                                       |
| port `📌`                                      | Integer  | 3306                       | 端口                                                                                                                                                                                       |
| database-name `📌database`                     | String   | _*必填_                      | 数据库名                                                                                                                                                                                     |
| username `📌`                                  | String   | root                       | 用户名                                                                                                                                                                                      |
| password `📌`                                  | String   | root                       | 密码                                                                                                                                                                                       |
| parallelism                                    | Integer  | 1                          | 进行快照阶段的并行度                                                                                                                                                                               |
| server-id                                      | String   | 从 `5400` 到 `6400` 之间随机选择一个 | 模拟服务端ID <br>与其它任务不可重复，强烈推荐设置为明确的值。<li>单个：例如 `5000`；<li>范围：例如 `5000-5004`（共5个）。<br>请注意，需要的个数取决于 `parallelism` 的值。                                                                         |
| server-time-zone                               | String   | 系统默认时区                     | 数据库的会话时区 <br>例如 `Asia/Shanghai`，用于将 `TIMESTAMP` 类型转换为特定时区的字符串。                                                                                                                           |
| scan.startup.mode                              | String   | initial                    | 启动模式 <li>`initial`：先做快照，再读取最新日志；<li>`earliest-offset`：跳过快照，从最早可用位置读取日志；<li>`latest-offset`：跳过快照，仅读取最新日志；<li>`specific-offset`：跳过快照，从指定位置开始读取日志；<li>`timestamp-offset`：跳过快照，从指定时间戳开始读取日志。 |
| scan.startup.specific-offset.file              | String   |                            | 启动参数：起始日志文件 <br>仅当启动模式是 `specific-offset` 时生效，需要与 `scan.startup.specific-offset.pos` 搭配。                                                                                                 |
| scan.startup.specific-offset.pos               | Long     |                            | 启动参数：起始日志文件内位置 <br>仅当启动模式是 `specific-offset` 时生效，需要与 `scan.startup.specific-offset.file` 搭配。                                                                                             |
| scan.startup.specific-offset.gtid-set          | String   |                            | 启动参数：起始事务编码 <br>仅当启动模式是 `specific-offset` 时生效，比上两个配置项的优先级更高。                                                                                                                             |
| scan.startup.timestamp-millis                  | Long     |                            | 启动参数：起始时间戳                                                                                                                                                                               |
| scan.snapshot.fetch.size                       | Integer  | 1024                       | 快照属性：每次轮询所能获取的最大行数                                                                                                                                                                       |
| scan.incremental.snapshot.chunk.size           | Integer  | 8096                       | 快照属性：表快照的分块大小（行数）                                                                                                                                                                        |
| chunk-meta.group.size                          | Integer  | 1000                       | 快照属性：拆分元数据的分组大小                                                                                                                                                                          |
| chunk-key.even-distribution.factor.upper-bound | Double   | 1000.0                     | 快照属性：均匀分布因子的上限                                                                                                                                                                           |
| chunk-key.even-distribution.factor.lower-bound | Double   | 0.05                       | 快照属性：均匀分布因子的下限                                                                                                                                                                           |
| connect.timeout                                | Duration | 30s                        | 连接超时，最小单位是秒                                                                                                                                                                              |
| connect.max-retries                            | Integer  | 3                          | 连接最大重试次数                                                                                                                                                                                 |
| connection.pool.size                           | Integer  | 20                         | 连接池大小                                                                                                                                                                                    |
| heartbeat.interval                             | Duration | 30s                        | 心跳检测间隔，最小单位是毫秒                                                                                                                                                                           |
| jdbc-props                                     | String   |                            | JDBC 属性 <br>即连接字符串 URL 的参数部分，例如：`k1=v1&k2=v2` 。                                                                                                                                          |
| security.sensitive.keys                        | String   | username; password         | 安全属性：包含敏感信息的键名列表 <br>在输出日志时，它们的值会被脱敏。使用 `;` 分隔多个键名。                                                                                                                                      |


## 目标
### 先决条件
1. 用户至少对“目标表”具有 `Select`、`Insert`、`Update` 和 `Delete` 权限；
2. （可选）当语义保证配置为 `exactly-once` 时，对于 MySQL 8 及以上版本，用户还需具有 `XA_RECOVER_ADMIN` 的服务器权限。

### 管道配置
标记 `📌` 表示关键配置，在配置表中具有独立的字段，其余配置以 YAML 格式保存在 `options` 字段中。

| 配置                           | 类型       | 默认值                | 说明                                                                 |
|------------------------------|----------|--------------------|--------------------------------------------------------------------|
| id `📌`                      | String   | _*必填_              | 管道目标ID                                                             |
| name `📌`                    | String   | _*必填_              | 管道目标名称                                                             |
| protocol `📌`                | String   | _*必填_              | 管道目标协议，设置为 `mysql` 以使用此连接器                                         |
| hostname `📌host`            | String   | localhost          | 主机                                                                 |
| port `📌`                    | Integer  | 3306               | 端口                                                                 |
| database-name `📌database`   | String   | _*必填_              | 数据库名                                                               |
| username `📌`                | String   | root               | 用户名                                                                |
| password `📌`                | String   | root               | 密码                                                                 |
| connection.max-retry-timeout | Duration | 60s                | 连接超时，最小单位是秒                                                        |
| sink.semantic                | String   | at-least-once      | 语义保证 <li>`at-least-once`：一个事件至少同步一次；<li>`exactly-once`：一个事件精确同步一次。 |
| sink.buffer-flush.interval   | Duration | 1s                 | 批量执行的时间间隔，最小单位是秒                                                   |
| sink.buffer-flush.max-rows   | Integer  | 100                | 批量执行的最大缓存记录数 <br>设置为 `0` 表示禁用缓存。                                   |
| sink.max-retries             | Integer  | 3                  | 批量执行失败的最大重试次数 <br>若语义保证是 `exactly-once` 时，将强制为零。                   |
| sink.xa.max-commit-attempts  | Integer  | 3                  | 精确一次属性：XA事务提交的尝试次数 <br>仅当语义保证是 `exactly-once` 时生效。                 |
| sink.xa.timeout              | Duration | 30s                | 精确一次属性：XA事务超时 <br>仅当语义保证是 `exactly-once` 时生效，最小单位是秒。               |
| security.sensitive.keys      | String   | username; password | 安全属性：包含敏感信息的键名列表 <br>在输出日志时，它们的值会被脱敏。使用 `;` 分隔多个键名。                |


## 参考资料
- [MySQL CDC 连接器](https://nightlies.apache.org/flink/flink-cdc-docs-release-3.0/docs/connectors/cdc-connectors/mysql-cdc/) · _Flink CDC_
- [MySQL 连接器](https://debezium.io/documentation/reference/1.9/connectors/mysql.html) · _Debezium_
