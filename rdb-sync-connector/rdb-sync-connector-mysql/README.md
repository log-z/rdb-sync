# RDB Sync - MySQL 连接器


## 适用范围
### 来源
- MySQL：5.6，5.7，8.0.x
- RDS MySQL：5.6，5.7，8.0.x
- PolarDB MySQL：5.6，5.7，8.0.x
- Aurora MySQL：5.6，5.7，8.0.x
- MariaDB：10.x
- PolarDB X：2.0.1

### 目标
- 任何兼容 MySQL 协议的数据库


## 先决条件
### 来源
1. 必须启用 BinLog 功能；
2. 用户至少对“来源表”具有 `Select` 权限；
3. 用户至少对“来源数据库”具有 `Lock Tables`、`Replication Client` 和 `Replication Slave` 权限。

> 详情参考 Debezium 教程中 [配置 MySQL](https://debezium.io/documentation/reference/1.9/connectors/mysql.html#setting-up-mysql) 这部分。

### 目标
1. 用户至少对“目标表”具有 `Select`、`Insert`、`Update` 和 `Delete` 权限。


## 管道配置
### 来源
| 配置 | 类型 | 默认值 | 说明 |
|-|-|-|--|
| id | String | _*必填_ | 管道来源ID |
| name | String | _*必填_ | 管道来源名称 |
| protocol | String | _*必填_ | 管道来源协议，设置为 `mysql` 以使用此连接器 |
| parallelism | Integer | 1 | 进行快照阶段的并行度 |
| host | String | localhost | 主机 |
| port | Integer | 3306 | 端口 |
| database | String | _*必填_ | 数据库名 |
| username | String | root | 用户名 |
| password | String | root | 密码 |
| server_id | String | 从 `5400` 到 `6400` 之间随机选择一个 | 模拟服务端ID <br>与其它任务不可重复，强烈推荐设置为明确的值。<li>单个：例如 `5000`；<li>范围：例如 `5000-5004`（共5个）。<br>请注意，需要的个数取决于 `parallelism` 的值。 |
| server_time_zone | String | 系统默认时区 | 数据库的会话时区 <br>例如 `Asia/Shanghai`，用于将 `TIMESTAMP` 类型转换为特定时区的字符串。 |
| startup_mode | String | initial | 启动模式 <li>`initial`：先做快照，再读取最新日志；<li>`earliest-offset`：跳过快照，从最早可用位置读取日志；<li>`latest-offset`：跳过快照，仅读取最新日志；<li>`specific-offset`：跳过快照，从指定位置开始读取日志；<li>`timestamp-offset`：跳过快照，从指定时间戳开始读取日志。 |
| startup_specific_offset_file | String | | 启动参数：起始日志文件 <br>仅当启动模式是 `specific-offset` 时生效，需要与 `startup_specific_offset_pos` 搭配。 |
| startup_specific_offset_pos | Long | | 启动参数：起始日志文件内位置 <br>仅当启动模式是 `specific-offset` 时生效，需要与 `startup_specific_offset_file` 搭配。 |
| startup_specific_offset_gtid_set | String | | 启动参数：起始事务编码 <br>仅当启动模式是 `specific-offset` 时生效，比上两个配置项的优先级更高。  |
| startup_timestamp_millis | Long | | 启动参数：起始时间戳 |
| split_size | Integer | 8096 | 快照属性：表快照的分块大小（行数） |
| split_meta_group_size | Integer | 1000 | 快照属性：拆分元数据的分组大小 |
| distribution_factor_upper | Double | 1000.0 | 快照属性：均匀分布因子的上限 |
| distribution_factor_lower | Double | 0.05 | 快照属性：均匀分布因子的下限 |
| fetch_size | Integer | 1024 | 快照属性：每次轮询所能获取的最大行数 |
| connect_timeout_seconds | Long | 30 | 连接超时秒数 |
| connect_max_retries | Integer | 3 | 连接最大重试次数 |
| connection_pool_size | Integer | 20 | 连接池大小 |
| heartbeat_interval_seconds | Long | 30 | 心跳检测间隔秒数 |
| jdbc_properties | String | | JDBC属性（JSON） |

### 目标
| 配置 | 类型 | 默认值 | 说明 |
|-|-|-|--|
| id | String | _*必填_ | 管道目标ID |
| name | String | _*必填_ | 管道目标名称 |
| protocol | String | _*必填_ | 管道目标协议，设置为 `mysql` 以使用此连接器 |
| host | String | localhost | 主机 |
| port | Integer | 3306 | 端口 |
| database | String | _*必填_ | 数据库名 |
| username | String | root | 用户名 |
| password | String | root | 密码 |
| exec_batch_interval_ms | Long | 0 | 执行批次间隔毫秒数 |
| exec_batch_size | Integer | 5000 | 执行批次最大容量 |
| exec_max_retries | Integer | 3 | 执行最大重试次数 |
| conn_timeout_seconds | Integer | 30 | 连接超时秒数 |


## 参考资料
- [MySQL CDC 连接器](https://github.com/ververica/flink-cdc-connectors/blob/master/docs/content/connectors/mysql-cdc(ZH).md) · _Ververica CDC Connectors_
- [MySQL 连接器](https://debezium.io/documentation/reference/1.9/connectors/mysql.html) · _Debezium_
