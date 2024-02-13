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
1. 角色至少对“目标表”具有 `Select`、`Insert`、`Update` 和 `Delete` 权限。

## 参考资料
- [Postgres CDC 连接器](https://github.com/ververica/flink-cdc-connectors/blob/master/docs/content/connectors/postgres-cdc.md) · _Ververica CDC Connectors_
- [Postgres 连接器](https://debezium.io/documentation/reference/1.9/connectors/postgresql.html) · _Debezium_
