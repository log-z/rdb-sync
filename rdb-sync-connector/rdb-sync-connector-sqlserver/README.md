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

## 参考资料
- [SQLServer CDC 连接器](https://github.com/ververica/flink-cdc-connectors/blob/master/docs/content/connectors/sqlserver-cdc.md) · _Ververica CDC Connectors_
- [SQLServer 连接器](https://debezium.io/documentation/reference/1.9/connectors/sqlserver.htm) · _Debezium_
