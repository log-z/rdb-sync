# RDB Sync - Postgres 连接器

## 适用范围
### 来源
- Postgres：9.6，10，11，12，13，14

### 目标
- 任何兼容 Postgres 协议的数据库


## 先决条件
### 来源
1. 必须将数据库参数 `wal_level` 设置为 `logical` ，若不生效请重启实例；
2. 角色至少对“来源表”具有 `Select` 权限 <sup>①</sup> ；
3. 角色至少具有 `REPLICATION` 属性。

```postgresql
-- 设置数据库参数
ALTER SYSTEM SET wal_level = logical;
```

> 详情参考 Debezium 教程中 [配置 Postgres](https://debezium.io/documentation/reference/1.9/connectors/postgresql.html#setting-up-postgresql) 这部分。

> 补充说明：
> 
> ① 如果所有绑定的来源表都是“等值表匹配”时，那么只需要这些表的 `Select` 权限即可，否则需要相关模式中的全部表的 `Select` 权限。

### 目标
1. 角色至少对“目标表”具有 `Select`、`Insert`、`Update` 和 `Delete` 权限；
2. （可选）当容错保证配置为 `exactly-once` 时，还需要确保数据库参数 [`max_prepared_transactions`](https://www.postgresql.org/docs/current/runtime-config-resource.html#GUC-MAX-PREPARED-TRANSACTIONS) 的大小足够，此参数只能在配置文件中更改，并在重启后才生效。


## 管道配置
### 来源
| 配置 | 类型 | 默认值 | 说明 |
|-|-|-|--|
| id | String | _*必填_ | 管道来源ID |
| name | String | _*必填_ | 管道来源名称 |
| protocol | String | _*必填_ | 管道来源协议，设置为 `postgres` 以使用此连接器 |
| parallelism | Integer | 1 | 进行快照阶段的并行度 |
| host | String | localhost | 主机 |
| port | Integer | 5432 | 端口 |
| database | String | _*必填_ | 数据库名 |
| schema | String | public | 模式名 |
| username | String | postgres | 用户名 |
| password | String | postgres | 密码 |
| slot_name | String | _*必填_ | 槽名称，与其它任务不可重复 |
| startup_mode | String | initial | 启动模式 <li>`initial`：先做快照，再读取最新日志；<li>`latest-offset`：跳过快照，仅读取最新日志。 |
| decoding_plugin_name | String | pgoutput | 逻辑解码插件名称 <br>可用取值有 `decoderbufs`、`wal2json`、`wal2json_rds`、`wal2json_streaming`、`wal2json_rds_streaming` 以及 `pgoutput` 这些插件名称。 |
| split_size | Integer | 8096 | 快照属性：表快照的分块大小（行数） |
| split_meta_group_size | Integer | 1000 | 快照属性：拆分元数据的分组大小 |
| distribution_factor_upper | Double | 1000.0 | 快照属性：均匀分布因子的上限 |
| distribution_factor_lower | Double | 0.05 | 快照属性：均匀分布因子的下限 |
| fetch_size | Integer | 1024 | 快照属性：每次轮询所能获取的最大行数 |
| connect_timeout_seconds | Long | 30 | 连接超时秒数 |
| connect_max_retries | Integer | 3 | 连接最大重试次数 |
| connection_pool_size | Integer | 20 | 连接池大小 |
| heartbeat_interval_seconds | Long | 30 | 心跳检测间隔秒数 |

### 目标
| 配置 | 类型 | 默认值 | 说明 |
|-|-|-|--|
| id | String | _*必填_ | 管道目标ID |
| name | String | _*必填_ | 管道目标名称 |
| protocol | String | _*必填_ | 管道目标协议，设置为 `postgres` 以使用此连接器 |
| host | String | localhost | 主机 |
| port | Integer | 5432 | 端口 |
| database | String | _*必填_ | 数据库名 |
| schema | String | public | 模式名 |
| username | String | postgres | 用户名 |
| password | String | postgres | 密码 |
| guarantee | String | at-least-once | 容错保证 <li>`at-least-once`：一个事件至少同步一次；<li>`exactly-once`：一个事件精确同步一次。 |
| exec_batch_interval_ms | Long | 0 | 执行批次间隔毫秒数 |
| exec_batch_size | Integer | 5000 | 执行批次最大容量 |
| exec_max_retries | Integer | 3 | 执行最大重试次数 <br>若容错保证是 `exactly-once` 时，将强制为零。 |
| conn_timeout_seconds | Integer | 30 | 连接超时秒数 |
| tx_max_commit_attempts | Integer | 3 | 精确一次属性：事务提交尝试次数 <br>仅当容错保证是 `exactly-once` 时生效。 |
| tx_timeout_seconds | Integer | | 精确一次属性：事务超时秒数 <br>仅当容错保证是 `exactly-once` 时生效。 |


## 注意事项
### 来源
#### 时间精度缺陷
对于 `TIME` 和 `TIMETZ` 类型的数据，在读取快照阶段，最多只能保留3位精度（毫秒级别）。而读取日志阶段，仍能保持完整精度（纳秒级别）。

这是由于 Postgres CDC 连接器在读取快照时，使用 `java.sql.Time` 传递时间数据导致的缺陷...

#### 处理 TOAST 存储的数据
对于大型数据元组，若超过了 `TOAST_TUPLE_THRESHOLD` 规定的大小（默认2KB）时，Postgres 将使用 TOAST 技术进行存储。一般情况下，这不会造成什么影响，但在逻辑复制中（读取日志阶段）却使我们无法获取确切的值。

外在的表现是，如果同步结果中出现 `__debezium_unavailable_value`（或其 Base64 解码后）这样的值，那就说明受到了 TOAST 的影响。这在 `TEXT` 和 `BYTEA` 之类的大型数据中尤为常见。

有个简单的解决办法是，对来源表添加 `REPLICA IDENTITY FULL` 属性：
```postgresql
ALTER TABLE table_name REPLICA IDENTITY FULL;
```

> 详情参考 Debezium 教程中 [被 TOAST 存储的值](https://debezium.io/documentation/reference/1.9/connectors/postgresql.html#postgresql-toasted-values) 这部分。


## 参考资料
- [Postgres CDC 连接器](https://github.com/ververica/flink-cdc-connectors/blob/master/docs/content/connectors/postgres-cdc.md) · _Ververica CDC Connectors_
- [Postgres 连接器](https://debezium.io/documentation/reference/1.9/connectors/postgresql.html) · _Debezium_
