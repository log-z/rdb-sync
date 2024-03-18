# RDB Sync - SQL Server 连接器


## 适用范围
- Sql Server：2012，2014，2016，2017，2019，2022


## 来源
### 先决条件
1. 必须对“来源表”逐个启用 CDC 功能，可监听全部或部分列；
2. 用户至少对“来源表”具有 `SELECT` 权限。
3. 用户至少对 `cdc` 模式具有 `SELECT` 权限。
4. 用户至少对“来源数据库”具有 `VIEW DATABASE PERFORMANCE STATE` 权限。

> 详情参考 Debezium 教程中 [配置 SQL Server](https://debezium.io/documentation/reference/1.9/connectors/sqlserver.html#setting-up-sqlserver) 这部分。

> 重要提示：修改表结构之后，此表的 CDC 监听列不会自动更新，你需要对此表重新启用 CDC 功能。 

### 管道配置
标记 `📌` 表示关键配置，在配置表中具有独立的字段，其余配置以 YAML 格式保存在 `options` 字段中。

| 配置                                             | 类型       | 默认值                | 说明                                                                |
|------------------------------------------------|----------|--------------------|-------------------------------------------------------------------|
| id `📌`                                        | String   | _*必填_              | 管道来源ID                                                            |
| name `📌`                                      | String   | _*必填_              | 管道来源名称                                                            |
| protocol `📌`                                  | String   | _*必填_              | 管道来源协议，设置为 `sqlserver` 以使用此连接器                                    |
| hostname `📌host`                              | String   | localhost          | 主机                                                                |
| port `📌`                                      | Integer  | 1433               | 端口                                                                |
| database-name `📌database`                     | String   | _*必填_              | 数据库名                                                              |
| schema-name `📌schema`                         | String   | dbo                | 模式名                                                               |
| username `📌`                                  | String   | sa                 | 用户名                                                               |
| password `📌`                                  | String   | _*必填_              | 密码                                                                |
| parallelism                                    | Integer  | 1                  | 进行快照阶段的并行度                                                        |
| server-time-zone                               | String   | UTC                | 数据库的会话时区 <br>例如 `Asia/Shanghai`，用于将 `TIMESTAMP` 类型转换为特定时区的字符串。    |
| scan.startup.mode                              | String   | initial            | 启动模式 <li>`initial`：先做快照，再读取最新日志；<li>`latest-offset`：跳过快照，仅读取最新日志。 |
| scan.snapshot.fetch.size                       | Integer  | 1024               | 快照属性：每次轮询所能获取的最大行数                                                |
| scan.incremental.snapshot.chunk.size           | Integer  | 8096               | 快照属性：表快照的分块大小（行数）                                                 |
| chunk-meta.group.size                          | Integer  | 1000               | 快照属性：拆分元数据的分组大小                                                   |
| chunk-key.even-distribution.factor.upper-bound | Double   | 1000.0             | 快照属性：均匀分布因子的上限                                                    |
| chunk-key.even-distribution.factor.lower-bound | Double   | 0.05               | 快照属性：均匀分布因子的下限                                                    |
| connect.timeout                                | Duration | 30s                | 连接超时，最小单位是秒                                                       |
| connect.max-retries                            | Integer  | 3                  | 连接最大重试次数                                                          |
| connection.pool.size                           | Integer  | 20                 | 连接池大小                                                             |
| security.sensitive.keys                        | String   | username; password | 安全属性：包含敏感信息的键名列表 <br>在输出日志时，它们的值会被脱敏。使用 `;` 分隔多个键名。               |

### 注意事项
#### 时间精度缺陷
对于 `TIME` 类型的数据，在读取日志阶段，最多只能保留3位精度（毫秒级别）。而读取快照阶段，仍能保持完整精度（纳秒级别）。

这是由于 SQLServer CDC 连接器在读取日志时，使用 `java.sql.Time` 传递时间数据导致的缺陷...


## 目标
### 先决条件
1. 用户至少对“目标表”具有 `SELECT`、`INSERT`、`UPDATE` 和 `DELETE` 权限；
2. （可选）当语义保证配置为 `exactly-once` 时，还需要确保在 master 数据库启用了 [XA 分布式事务](https://learn.microsoft.com/zh-cn/sql/connect/jdbc/understanding-xa-transactions)。

```sql
-- -------------------------------------------------------------------
-- SQLServer on Docker  [mcr.microsoft.com/mssql/server:2022-latest]
-- 启用 XA 分布式事务步骤参考
-- -------------------------------------------------------------------

-- 1. 进入 master 数据库
USE master
GO
-- 2. 启用 JDBC-XA 分布式事务组件
--    此存储过程仅在 SQL Server 2017 CU16 及更高版本可用，低版本请参考官方文档
EXEC sp_sqljdbc_xa_install
GO
-- 3. 创建数据库用户
--    这里用户名与登录名同名（假设都是 rdb_sync）
EXEC sp_grantdbaccess 'rdb_sync', 'rdb_sync';
GO
-- 4. 将数据库用户添加到 SqlJDBCXAUser 角色
EXEC sp_addrolemember [SqlJDBCXAUser], 'rdb_sync'
```

### 管道配置
标记 `📌` 表示关键配置，在配置表中具有独立的字段，其余配置以 YAML 格式保存在 `options` 字段中。

| 配置                                 | 类型       | 默认值                | 说明                                                                 |
|------------------------------------|----------|--------------------|--------------------------------------------------------------------|
| id `📌`                            | String   | _*必填_              | 管道目标ID                                                             |
| name `📌`                          | String   | _*必填_              | 管道目标名称                                                             |
| protocol `📌`                      | String   | _*必填_              | 管道目标协议，设置为 `sqlserver` 以使用此连接器                                     |
| hostname `📌host`                  | String   | localhost          | 主机                                                                 |
| port `📌`                          | Integer  | 1433               | 端口                                                                 |
| database-name `📌database`         | String   | _*必填_              | 数据库名                                                               |
| schema-name `📌schema`             | String   | dbo                | 模式名                                                                |
| username `📌`                      | String   | sa                 | 用户名                                                                |
| password `📌`                      | String   | _*必填_              | 密码                                                                 |
| connection.max-retry-timeout       | Duration | 60s                | 连接超时 <br>仅当语义保证是 `at-least-once` 时生效，最小单位是秒。                       |
| sink.semantic                      | String   | at-least-once      | 语义保证 <li>`at-least-once`：一个事件至少同步一次；<li>`exactly-once`：一个事件精确同步一次。 |
| sink.buffer-flush.interval         | Duration | 1s                 | 批量执行的时间间隔，最小单位是秒                                                   |
| sink.buffer-flush.max-rows         | Integer  | 100                | 批量执行的最大缓存记录数 <br>设置为 `0` 表示禁用缓存。                                   |
| sink.max-retries                   | Integer  | 3                  | 批量执行失败的最大重试次数 <br>若语义保证是 `exactly-once` 时，将强制为零。                   |
| sink.xa.max-commit-attempts        | Integer  | 3                  | 精确一次属性：XA事务提交的尝试次数 <br>仅当语义保证是 `exactly-once` 时生效。                 |
| sink.xa.timeout                    | Duration | 30s                | 精确一次属性：XA事务超时 <br>仅当语义保证是 `exactly-once` 时生效，最小单位是秒。               |
| sink.xa.transaction-per-connection | Boolean  | false              | 精确一次属性：同一连接是否可以用于多个XA事务 <br>仅当语义保证是 `exactly-once` 时生效。            |
| security.sensitive.keys            | String   | username; password | 安全属性：包含敏感信息的键名列表 <br>在输出日志时，它们的值会被脱敏。使用 `;` 分隔多个键名。                |


## 参考资料
- [SQLServer CDC 连接器](https://nightlies.apache.org/flink/flink-cdc-docs-release-3.0/docs/connectors/cdc-connectors/sqlserver-cdc/) · _Flink CDC_
- [SQLServer 连接器](https://debezium.io/documentation/reference/1.9/connectors/sqlserver.html) · _Debezium_
