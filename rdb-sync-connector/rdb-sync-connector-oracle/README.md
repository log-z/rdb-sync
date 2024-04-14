# RDB Sync - Oracle 连接器


## 适用范围
- Oracle: 11, 12, 19, 21


## 来源
### 先决条件
1. 开启归档日志；
2. 调整库表属性；
3. 创建同步专用表空间；
4. 创建同步专用用户，并赋予权限。

> 下面演示基本步骤，详情参考 Flink CDC 教程中 [配置 Oracle](https://nightlies.apache.org/flink/flink-cdc-docs-release-3.0/zh/docs/connectors/legacy-flink-cdc-sources/oracle-cdc/#setup-oracle) 这部分，以及 Debezium 教程中 [配置 Oracle](https://debezium.io/documentation/reference/1.9/connectors/oracle.html#setting-up-oracle) 这部分。

1）开启归档日志。

```sql
ORACLE_SID=ORCLCDB
export ORACLE_SID

sqlplus /nolog
  CONNECT sys/password AS SYSDBA
  -- 1. 开启归档日志
  alter system set db_recovery_file_dest_size = 10G;
  -- 请留意目录是否存在，如不存在请创建它
  alter system set db_recovery_file_dest = '/opt/oracle/oradata/recovery_area' scope=spfile;
  shutdown immediate
  startup mount
  alter database archivelog;
  alter database open;
  
  -- 2. 检查是否开启成功
  -- 如果成功应该显示 "Database log mode: Archive Mode"
  archive log list
  exit;
```

2）调整库表属性，使之能够在重做日志中保存数据修改前的状态。

```sql
-- 1. 在数据库级别开启补充日志
ALTER DATABASE ADD SUPPLEMENTAL LOG DATA;

-- 2. 在表级别开启补充日志
-- 注意：请为每个来源表都执行此操作
ALTER TABLE schemaname.tablename ADD SUPPLEMENTAL LOG DATA (ALL) COLUMNS;
```

_接下来，请根据你的实际情况，按不同数据库模型进行后续操作。_

#### 对于 PDB 数据库
3）创建同步专用表空间 `RDB_SYNC_TBS` 。

```sql
sqlplus sys/password@host:port/SID as sysdba
  CREATE TABLESPACE RDB_SYNC_TBS DATAFILE '/opt/oracle/oradata/SID/rdb_sync_tbs.dbf' SIZE 25M REUSE AUTOEXTEND ON MAXSIZE UNLIMITED;
  exit;
```

4）创建同步专用用户 `RDB_SYNC` ，并赋予权限。

```sql
sqlplus sys/password@host:port/SID as sysdba
  -- 1. 创建同步专用用户
  CREATE USER "RDB_SYNC" IDENTIFIED BY "password";
  ALTER USER "RDB_SYNC" DEFAULT TABLESPACE RDB_SYNC_TBS QUOTA UNLIMITED ON RDB_SYNC_TBS;
  
  -- 2. 为专用用户授权
  -- 2.1. 常规权限
  GRANT CREATE SESSION TO "RDB_SYNC";
  GRANT SET CONTAINER TO "RDB_SYNC";
  GRANT SELECT ON V_$DATABASE to "RDB_SYNC";
  GRANT FLASHBACK ANY TABLE TO "RDB_SYNC";
  GRANT SELECT ANY TABLE TO "RDB_SYNC";
  GRANT SELECT_CATALOG_ROLE TO "RDB_SYNC";
  GRANT EXECUTE_CATALOG_ROLE TO "RDB_SYNC";
  GRANT SELECT ANY TRANSACTION TO "RDB_SYNC";
  GRANT LOGMINING TO "RDB_SYNC";
  GRANT ANALYZE ANY TO "RDB_SYNC";
  GRANT CREATE TABLE TO "RDB_SYNC";
  
  -- 2.2. LogMiner 相关权限
  GRANT EXECUTE ON DBMS_LOGMNR TO "RDB_SYNC";
  GRANT EXECUTE ON DBMS_LOGMNR_D TO "RDB_SYNC";
  
  -- 2.3. 日志视图相关权限
  GRANT SELECT ON V_$LOG TO "RDB_SYNC";
  GRANT SELECT ON V_$LOG_HISTORY TO "RDB_SYNC";
  GRANT SELECT ON V_$LOGMNR_LOGS TO "RDB_SYNC";
  GRANT SELECT ON V_$LOGMNR_CONTENTS TO "RDB_SYNC";
  GRANT SELECT ON V_$LOGMNR_PARAMETERS TO "RDB_SYNC";
  GRANT SELECT ON V_$LOGFILE TO "RDB_SYNC";
  GRANT SELECT ON V_$ARCHIVED_LOG TO "RDB_SYNC";
  GRANT SELECT ON V_$ARCHIVE_DEST_STATUS TO "RDB_SYNC";
  exit;
```

#### 对于 CDB 数据库
3）创建同步专用表空间 `RDB_SYNC_TBS` 。

```sql
-- 1. 在 CDB 创建表空间
sqlplus sys/password@//localhost:1521/ORCLCDB as sysdba
  CREATE TABLESPACE RDB_SYNC_TBS DATAFILE '/opt/oracle/oradata/ORCLCDB/rdb_sync_tbs.dbf' SIZE 25M REUSE AUTOEXTEND ON MAXSIZE UNLIMITED;
  exit;

-- 2. 在每个 PDB 都创建表空间
sqlplus sys/password@//localhost:1521/ORCLPDB1 as sysdba
  CREATE TABLESPACE RDB_SYNC_TBS DATAFILE '/opt/oracle/oradata/ORCLCDB/ORCLPDB1/rdb_sync_tbs.dbf' SIZE 25M REUSE AUTOEXTEND ON MAXSIZE UNLIMITED;
  exit;
```

4）创建同步专用用户 `C##RDB_SYNC` ，并赋予权限。

```sql
sqlplus sys/password@//localhost:1521/ORCLCDB as sysdba
  -- 1. 创建同步专用用户
  CREATE USER "C##RDB_SYNC" IDENTIFIED BY "password";
  ALTER USER "C##RDB_SYNC" DEFAULT TABLESPACE RDB_SYNC_TBS QUOTA UNLIMITED ON RDB_SYNC_TBS CONTAINER=ALL;
  
  -- 2. 为专用用户授权
  -- 2.1. 常规权限
  GRANT CREATE SESSION TO "C##RDB_SYNC" CONTAINER=ALL;
  GRANT SET CONTAINER TO "C##RDB_SYNC" CONTAINER=ALL;
  GRANT SELECT ON V_$DATABASE to "C##RDB_SYNC" CONTAINER=ALL;
  GRANT FLASHBACK ANY TABLE TO "C##RDB_SYNC" CONTAINER=ALL;
  GRANT SELECT ANY TABLE TO "C##RDB_SYNC" CONTAINER=ALL;
  GRANT SELECT_CATALOG_ROLE TO "C##RDB_SYNC" CONTAINER=ALL;
  GRANT EXECUTE_CATALOG_ROLE TO "C##RDB_SYNC" CONTAINER=ALL;
  GRANT SELECT ANY TRANSACTION TO "C##RDB_SYNC" CONTAINER=ALL;
  GRANT LOGMINING TO "C##RDB_SYNC" CONTAINER=ALL;
  GRANT CREATE TABLE TO "C##RDB_SYNC" CONTAINER=ALL;
  
  -- 2.2. LogMiner 相关权限
  GRANT EXECUTE ON DBMS_LOGMNR TO "C##RDB_SYNC" CONTAINER=ALL;
  GRANT EXECUTE ON DBMS_LOGMNR_D TO "C##RDB_SYNC" CONTAINER=ALL;
  
  -- 2.3. 日志视图相关权限
  GRANT SELECT ON V_$LOG TO "C##RDB_SYNC" CONTAINER=ALL;
  GRANT SELECT ON V_$LOG_HISTORY TO "C##RDB_SYNC" CONTAINER=ALL;
  GRANT SELECT ON V_$LOGMNR_LOGS TO "C##RDB_SYNC" CONTAINER=ALL;
  GRANT SELECT ON V_$LOGMNR_CONTENTS TO "C##RDB_SYNC" CONTAINER=ALL;
  GRANT SELECT ON V_$LOGMNR_PARAMETERS TO "C##RDB_SYNC" CONTAINER=ALL;
  GRANT SELECT ON V_$LOGFILE TO "C##RDB_SYNC" CONTAINER=ALL;
  GRANT SELECT ON V_$ARCHIVED_LOG TO "C##RDB_SYNC" CONTAINER=ALL;
  GRANT SELECT ON V_$ARCHIVE_DEST_STATUS TO "C##RDB_SYNC" CONTAINER=ALL;
  exit;
```

### 管道配置
标记 `📌` 表示关键配置，在配置表中具有独立的字段，其余配置以 YAML 格式保存在 `options` 字段中。

> 默认情况下使用 JDBC Thin 驱动，通过配置 `url` 你可以选择其它驱动方式。

| 配置                                             | 类型       | 默认值                                                 | 说明                                                                               |
|------------------------------------------------|----------|-----------------------------------------------------|----------------------------------------------------------------------------------|
| id `📌`                                        | String   | _*必填_                                               | 管道来源ID                                                                           |
| name `📌`                                      | String   | _*必填_                                               | 管道来源名称                                                                           |
| protocol `📌`                                  | String   | _*必填_                                               | 管道来源协议，设置为 `oracle` 以使用此连接器                                                      |
| hostname `📌host`                              | String   | localhost                                           | 主机                                                                               |
| port `📌`                                      | Integer  | 1521                                                | 端口                                                                               |
| database-name `📌database`                     | String   | ORCLCDB                                             | 数据库名                                                                             |
| url                                            | String   | jdbc:oracle:thin:@{hostname}:{port}:{database-name} | JDBC 连接字符串 <br>_注意：如果使用了此配置，将会覆盖 `hostname`、`port` 和 `database-name` 这三个“简化”配置。_ |
| schema-name `📌schema`                         | String   | _*必填_                                               | 模式名                                                                              |
| username `📌`                                  | String   | _*必填_                                               | 用户名                                                                              |
| password `📌`                                  | String   | _*必填_                                               | 密码                                                                               |
| parallelism                                    | Integer  | 1                                                   | 进行快照阶段的并行度                                                                       |
| server-time-zone                               | String   | UTC                                                 | 数据库的会话时区 <br>例如 `Asia/Shanghai`，用于将 `TIMESTAMP` 类型转换为特定时区的字符串。                   |
| scan.startup.mode                              | String   | initial                                             | 启动模式 <li>`initial`：先做快照，再读取最新日志；<li>`latest-offset`：跳过快照，仅读取最新日志。                |
| scan.snapshot.fetch.size                       | Integer  | 1024                                                | 快照属性：每次轮询所能获取的最大行数                                                               |
| scan.incremental.snapshot.chunk.size           | Integer  | 8096                                                | 快照属性：表快照的分块大小（行数）                                                                |
| chunk-meta.group.size                          | Integer  | 1000                                                | 快照属性：拆分元数据的分组大小                                                                  |
| chunk-key.even-distribution.factor.upper-bound | Double   | 1000.0                                              | 快照属性：均匀分布因子的上限                                                                   |
| chunk-key.even-distribution.factor.lower-bound | Double   | 0.05                                                | 快照属性：均匀分布因子的下限                                                                   |
| connect.timeout                                | Duration | 30s                                                 | 连接超时，最小单位是秒                                                                      |
| connect.max-retries                            | Integer  | 3                                                   | 连接最大重试次数                                                                         |
| connection.pool.size                           | Integer  | 20                                                  | 连接池大小                                                                            |
| debezium.database.pdb.name                     | String   |                                                     | 指定一个 PDB 名称 <br>仅当使用 CDB + PDB 模型时，您才需要此配置。                                      |
| security.sensitive.keys                        | String   | username; password                                  | 安全属性：包含敏感信息的键名列表 <br>在输出日志时，它们的值会被脱敏。使用 `;` 分隔多个键名。                              |

### 注意事项
#### 限制
1. 一个同步专用用户，目前仅支持为一个作业提供来源（TODO）；
2. 扫描表快照时无法执行检查点。

> 详情参考 Flink CDC 教程中 [限制](https://nightlies.apache.org/flink/flink-cdc-docs-release-3.0/zh/docs/connectors/legacy-flink-cdc-sources/oracle-cdc/#limitation) 这部分。

#### 巨大数据类型
已知 `CLOB`、`NCLOB`、`LONG`、`LONGRAW` 和 `BLOB` 在读取日志阶段基本不可用，无法获取有效值。这是 Debezium 1.9 的兼容性问题。

#### ORA-01291: 缺少日志文件
这个异常一般在 Flink 作业重启后出现，表示 LogMiner 进度异常，可按下面步骤修复。
1. 停止 Flink 作业；
2. 删除同步专用用户的 `LOG_MINING_FLUSH` 表，注意这将删除同步进度；
3. 启动 Flink 作业。

```sql
-- 对于 PDB 数据库
DROP TABLE RDB_SYNC.LOG_MINING_FLUSH;

-- 对于 CDB 数据库
DROP TABLE C##RDB_SYNC.LOG_MINING_FLUSH;
```


## 目标
### 先决条件
1. 用户至少对“目标表”具有 `INSERT`、`UPDATE` 和 `DELETE` 权限。

### 管道配置
标记 `📌` 表示关键配置，在配置表中具有独立的字段，其余配置以 YAML 格式保存在 `options` 字段中。

> 默认情况下使用 JDBC Thin 驱动，通过配置 `url` 你可以选择其它驱动方式。

| 配置                                 | 类型       | 默认值                                                 | 说明                                                                               |
|------------------------------------|----------|-----------------------------------------------------|----------------------------------------------------------------------------------|
| id `📌`                            | String   | _*必填_                                               | 管道目标ID                                                                           |
| name `📌`                          | String   | _*必填_                                               | 管道目标名称                                                                           |
| protocol `📌`                      | String   | _*必填_                                               | 管道目标协议，设置为 `oracle` 以使用此连接器                                                      |
| hostname `📌host`                  | String   | localhost                                           | 主机                                                                               |
| port `📌`                          | Integer  | 1521                                                | 端口                                                                               |
| database-name `📌database`         | String   | ORCLCDB                                             | 数据库名                                                                             |
| url                                | String   | jdbc:oracle:thin:@{hostname}:{port}:{database-name} | JDBC 连接字符串 <br>_注意：如果使用了此配置，将会覆盖 `hostname`、`port` 和 `database-name` 这三个“简化”配置。_ |
| schema-name `📌schema`             | String   | _*必填_                                               | 模式名                                                                              |
| username `📌`                      | String   | _*必填_                                               | 用户名                                                                              |
| password `📌`                      | String   | _*必填_                                               | 密码                                                                               |
| connection.max-retry-timeout       | Duration | 60s                                                 | 连接超时 <br>仅当语义保证是 `at-least-once` 时生效，最小单位是秒。                                     |
| sink.semantic                      | String   | at-least-once                                       | 语义保证 <li>`at-least-once`：一个事件至少同步一次；<li>`exactly-once`：一个事件精确同步一次。               |
| sink.buffer-flush.interval         | Duration | 1s                                                  | 批量执行的时间间隔，最小单位是秒                                                                 |
| sink.buffer-flush.max-rows         | Integer  | 100                                                 | 批量执行的最大缓存记录数 <br>设置为 `0` 表示禁用缓存。                                                 |
| sink.max-retries                   | Integer  | 3                                                   | 批量执行失败的最大重试次数 <br>若语义保证是 `exactly-once` 时，将强制为零。                                 |
| sink.xa.max-commit-attempts        | Integer  | 3                                                   | 精确一次属性：XA事务提交的尝试次数 <br>仅当语义保证是 `exactly-once` 时生效。                               |
| sink.xa.timeout                    | Duration | 30s                                                 | 精确一次属性：XA事务超时 <br>仅当语义保证是 `exactly-once` 时生效，最小单位是秒。                             |
| sink.xa.transaction-per-connection | Boolean  | false                                               | 精确一次属性：同一连接是否可以用于多个XA事务 <br>仅当语义保证是 `exactly-once` 时生效。                          |
| security.sensitive.keys            | String   | username; password                                  | 安全属性：包含敏感信息的键名列表 <br>在输出日志时，它们的值会被脱敏。使用 `;` 分隔多个键名。                              |


## 参考资料
- [Oracle CDC 连接器](https://nightlies.apache.org/flink/flink-cdc-docs-release-3.0/docs/connectors/cdc-connectors/oracle-cdc/) · _Flink CDC_
- [Oracle 连接器](https://debezium.io/documentation/reference/1.9/connectors/oracle.html) · _Debezium_
- [Oracle环境准备（LogMiner）](https://help.fanruan.com/finedatalink/doc-view-98.html) · _FineDataLink_
