# RDB Sync - Postgres 连接器

## 适用范围
- Postgres：9.6，10，11，12，13，14，15，16


## 来源
### 先决条件
1. 必须将数据库参数 `wal_level` 设置为 `logical` ，若不生效请重启实例；
2. 角色至少对“来源表”具有 `Select` 权限 <sup>①</sup> ；
3. 角色至少对“来源数据库”具有 `Connect` 权限；
4. 角色至少具有 `REPLICATION` 和 `LOGIN` 属性；
5. （按需）当逻辑解码插件是 **pgoutput**（默认值）时，请使用“超级用户”手动创建 `FOR ALL TABLES` 发布，否则以“普通用户”进行同步将会提示权限不足，无法自动创建。 

```postgresql
-- 设置数据库参数
ALTER SYSTEM SET wal_level = logical;
```

```postgresql
-- 使用“超级用户”手动创建 FOR ALL TABLES 发布
-- 先进入来源数据库，再执行此语句
CREATE PUBLICATION dbz_publication FOR ALL TABLES;
```

> 详情参考 Debezium 教程中 [配置 Postgres](https://debezium.io/documentation/reference/1.9/connectors/postgresql.html#setting-up-postgresql) 这部分。

> 补充说明：
> 
> ① 如果所有绑定的来源表都是“等值表匹配”时，那么只需要这些表的 `Select` 权限即可，否则需要相关模式中的全部表的 `Select` 权限。

### 管道配置
标记 `📌` 表示关键配置，在配置表中具有独立的字段，其余配置以 YAML 格式保存在 `options` 字段中。

| 配置                                             | 类型       | 默认值                | 说明                                                                                                                             |
|------------------------------------------------|----------|--------------------|--------------------------------------------------------------------------------------------------------------------------------|
| id `📌`                                        | String   | _*必填_              | 管道来源ID                                                                                                                         |
| name `📌`                                      | String   | _*必填_              | 管道来源名称                                                                                                                         |
| protocol `📌`                                  | String   | _*必填_              | 管道来源协议，设置为 `postgres` 以使用此连接器                                                                                                  |
| hostname `📌host`                              | String   | localhost          | 主机                                                                                                                             |
| port `📌`                                      | Integer  | 5432               | 端口                                                                                                                             |
| database-name `📌database`                     | String   | _*必填_              | 数据库名                                                                                                                           |
| schema-name `📌schema`                         | String   | public             | 模式名                                                                                                                            |
| username `📌`                                  | String   | postgres           | 用户名                                                                                                                            |
| password `📌`                                  | String   | postgres           | 密码                                                                                                                             |
| slot.name `📌slot_name`                        | String   | _*必填_              | 槽名称，与其它任务不可重复                                                                                                                  |
| parallelism                                    | Integer  | 1                  | 进行快照阶段的并行度                                                                                                                     |
| decoding.plugin.name                           | String   | pgoutput           | 逻辑解码插件名称 <br>可用取值有 `decoderbufs`、`wal2json`、`wal2json_rds`、`wal2json_streaming`、`wal2json_rds_streaming` 以及 `pgoutput` 这些插件名称。 |
| scan.startup.mode                              | String   | initial            | 启动模式 <li>`initial`：先做快照，再读取最新日志；<li>`latest-offset`：跳过快照，仅读取最新日志。                                                              |
| scan.snapshot.fetch.size                       | Integer  | 1024               | 快照属性：每次轮询所能获取的最大行数                                                                                                             |
| scan.incremental.snapshot.chunk.size           | Integer  | 8096               | 快照属性：表快照的分块大小（行数）                                                                                                              |
| chunk-meta.group.size                          | Integer  | 1000               | 快照属性：拆分元数据的分组大小                                                                                                                |
| chunk-key.even-distribution.factor.upper-bound | Double   | 1000.0             | 快照属性：均匀分布因子的上限                                                                                                                 |
| chunk-key.even-distribution.factor.lower-bound | Double   | 0.05               | 快照属性：均匀分布因子的下限                                                                                                                 |
| connect.timeout                                | Duration | 30s                | 连接超时，最小单位是秒                                                                                                                    |
| connect.max-retries                            | Integer  | 3                  | 连接最大重试次数                                                                                                                       |
| connection.pool.size                           | Integer  | 20                 | 连接池大小                                                                                                                          |
| heartbeat.interval                             | Duration | 30s                | 心跳检测间隔，最小单位是毫秒                                                                                                                 |
| security.sensitive.keys                        | String   | username; password | 安全属性：包含敏感信息的键名列表 <br>在输出日志时，它们的值会被脱敏。使用 `;` 分隔多个键名。                                                                            |

### 注意事项
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


## 目标
### 先决条件
1. 角色至少对“目标表”具有 `Select`、`Insert`、`Update` 和 `Delete` 权限；
2. 角色至少对“来源数据库”具有 `Connect` 权限；
3. 角色至少具有 `LOGIN` 属性；
4. （可选）当语义保证配置为 `exactly-once` 时，还需要确保数据库参数 [`max_prepared_transactions`](https://www.postgresql.org/docs/current/runtime-config-resource.html#GUC-MAX-PREPARED-TRANSACTIONS) 的大小足够，此参数只能在配置文件中更改，并在重启后才生效。

### 管道配置
标记 `📌` 表示关键配置，在配置表中具有独立的字段，其余配置以 YAML 格式保存在 `options` 字段中。

| 配置                           | 类型       | 默认值                | 说明                                                                 |
|------------------------------|----------|--------------------|--------------------------------------------------------------------|
| id `📌`                      | String   | _*必填_              | 管道目标ID                                                             |
| name `📌`                    | String   | _*必填_              | 管道目标名称                                                             |
| protocol `📌`                | String   | _*必填_              | 管道目标协议，设置为 `postgres` 以使用此连接器                                      |
| hostnames `📌hosts`          | String   | localhost          | 主机名列表 <br>使用 `;` 分隔多个主机名。                                          |
| ports `📌`                   | String   | 5432               | 端口列表 <br>使用 `;` 分隔多个端口。                                            |
| database-name `📌database`   | String   | _*必填_              | 数据库名                                                               |
| schema-name `📌schema`       | String   | public             | 模式名                                                                |
| username `📌`                | String   | postgres           | 用户名                                                                |
| password `📌`                | String   | postgres           | 密码                                                                 |
| connection.max-retry-timeout | Duration | 60s                | 连接超时，最小单位是秒                                                        |
| sink.semantic                | String   | at-least-once      | 语义保证 <li>`at-least-once`：一个事件至少同步一次；<li>`exactly-once`：一个事件精确同步一次。 |
| sink.buffer-flush.interval   | Duration | 1s                 | 批量执行的时间间隔，最小单位是秒                                                   |
| sink.buffer-flush.max-rows   | Integer  | 100                | 批量执行的最大缓存记录数 <br>设置为 `0` 表示禁用缓存。                                   |
| sink.max-retries             | Integer  | 3                  | 批量执行失败的最大重试次数 <br>若语义保证是 `exactly-once` 时，将强制为零。                   |
| sink.xa.max-commit-attempts  | Integer  | 3                  | 精确一次属性：XA事务提交的尝试次数 <br>仅当语义保证是 `exactly-once` 时生效。                 |
| sink.xa.timeout              | Duration | 30s                | 精确一次属性：XA事务超时 <br>仅当语义保证是 `exactly-once` 时生效，最小单位是秒。               |
| security.sensitive.keys      | String   | username; password | 安全属性：包含敏感信息的键名列表 <br>在输出日志时，它们的值会被脱敏。使用 `;` 分隔多个键名。                |


## 参考资料
- [Postgres CDC 连接器](https://nightlies.apache.org/flink/flink-cdc-docs-release-3.0/docs/connectors/cdc-connectors/postgres-cdc/) · _Flink CDC_
- [Postgres 连接器](https://debezium.io/documentation/reference/1.9/connectors/postgresql.html) · _Debezium_
