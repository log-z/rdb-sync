# RDB Sync - SQL Server 连接器


## 适用范围
### 来源
- Sql Server：2012，2014，2016，2017，2019，2022

### 目标
- 任何兼容 SQL Server 协议的数据库


## 先决条件
### 来源
1. 必须对“来源表”逐个启用 CDC 功能，可监听全部或部分列；
2. 用户至少对“来源表”具有 `SELECT` 权限。
3. 用户至少对 `cdc` 模式具有 `SELECT` 权限。
4. 用户至少对“来源数据库”具有 `VIEW DATABASE PERFORMANCE STATE` 权限。

> 详情参考 Debezium 教程中 [配置 SQL Server](https://debezium.io/documentation/reference/1.9/connectors/sqlserver.html#setting-up-sqlserver) 这部分。

> 重要提示：修改表结构之后，此表的 CDC 监听列不会自动更新，你需要对此表重新启用 CDC 功能。 

### 目标
1. 用户至少对“目标表”具有 `SELECT`、`INSERT`、`UPDATE` 和 `DELETE` 权限。


## 管道配置
### 来源
| 配置 | 类型 | 默认值 | 说明 |
|-|-|-|--|
| id | String | _*必填_ | 管道来源ID |
| name | String | _*必填_ | 管道来源名称 |
| protocol | String | _*必填_ | 管道来源协议，设置为 `sqlserver` 以使用此连接器 |
| parallelism | Integer | 1 | 进行快照阶段的并行度 |
| host | String | localhost | 主机 |
| port | Integer | 1433 | 端口 |
| database | String | _*必填_ | 数据库名 |
| schema | String | dbo | 模式名 |
| username | String | sa | 用户名 |
| password | String | _*必填_ | 密码 |
| server_time_zone | String | UTC | 数据库的会话时区 <br>例如 `Asia/Shanghai`，用于将 `TIMESTAMP` 类型转换为特定时区的字符串。 |
| startup_mode | String | initial | 启动模式 <li>`initial`：先做快照，再读取最新日志；<li>`latest-offset`：跳过快照，仅读取最新日志。 |
| split_size | Integer | 8096 | 快照属性：表快照的分块大小（行数） |
| split_meta_group_size | Integer | 1000 | 快照属性：拆分元数据的分组大小 |
| distribution_factor_upper | Double | 1000.0 | 快照属性：均匀分布因子的上限 |
| distribution_factor_lower | Double | 0.05 | 快照属性：均匀分布因子的下限 |
| fetch_size | Integer | 1024 | 快照属性：每次轮询所能获取的最大行数 |
| connect_timeout_seconds | Long | 30 | 连接超时秒数 |
| connect_max_retries | Integer | 3 | 连接最大重试次数 |
| connection_pool_size | Integer | 20 | 连接池大小 |

### 目标
| 配置 | 类型 | 默认值 | 说明 |
|-|-|-|--|
| id | String | _*必填_ | 管道目标ID |
| name | String | _*必填_ | 管道目标名称 |
| protocol | String | _*必填_ | 管道目标协议，设置为 `sqlserver` 以使用此连接器 |
| host | String | localhost | 主机 |
| port | Integer | 1433 | 端口 |
| database | String | _*必填_ | 数据库名 |
| schema | String | dbo | 模式名 |
| username | String | sa | 用户名 |
| password | String | _*必填_ | 密码 |
| exec_batch_interval_ms | Long | 0 | 执行批次间隔毫秒数 |
| exec_batch_size | Integer | 5000 | 执行批次最大容量 |
| exec_max_retries | Integer | 3 | 执行最大重试次数 |
| conn_timeout_seconds | Integer | 30 | 连接超时秒数 |


## 注意事项
### 来源
#### 时间精度缺陷
对于 `TIME` 类型的数据，在读取日志阶段，最多只能保留3位精度（毫秒级别）。而读取快照阶段，仍能保持完整精度（纳秒级别）。

这是由于 SQLServer CDC 连接器在读取日志时，使用 `java.sql.Time` 传递时间数据导致的缺陷...


## 参考资料
- [SQLServer CDC 连接器](https://github.com/ververica/flink-cdc-connectors/blob/master/docs/content/connectors/sqlserver-cdc.md) · _Ververica CDC Connectors_
- [SQLServer 连接器](https://debezium.io/documentation/reference/1.9/connectors/sqlserver.html) · _Debezium_
