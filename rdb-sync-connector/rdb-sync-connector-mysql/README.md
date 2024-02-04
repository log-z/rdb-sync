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

## 参考资料
- [MySQL CDC 连接器](https://github.com/ververica/flink-cdc-connectors/blob/master/docs/content/connectors/mysql-cdc(ZH).md) · _Ververica CDC Connectors_
- [MySQL 连接器](https://debezium.io/documentation/reference/1.9/connectors/mysql.html) · _Debezium_
