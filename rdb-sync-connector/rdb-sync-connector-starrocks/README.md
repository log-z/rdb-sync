# RDB Sync - StarRocks 连接器


## 适用范围
### 来源
暂无支持...😅

### 目标
- StarRocks：2.x，3.x


## 先决条件
### 目标
1. 用户至少对“目标表”具有 `Insert`、`Update` 和 `Delete` 权限。


## 管道配置
### 目标
| 配置 | 类型 | 默认值 | 说明 |
|-|-|-|--|
| id | String | _*必填_ | 管道目标ID |
| name | String | _*必填_ | 管道目标名称 |
| protocol | String | _*必填_ | 管道目标协议，设置为 `starrocks` 以使用此连接器 |
| hosts | String | localhost | BE MySQL 服务主机列表（逗号分隔） |
| ports | String | 9030 | BE MySQL 服务端口列表（逗号分隔） |
| load_hosts | String | localhost | FE HTTP 服务主机列表列表（逗号分隔） |
| load_ports | String | 8030 | FE HTTP 服务端口列表（逗号分隔） |
| database | String | _*必填_ | 数据库名 |
| username | String | root | 用户名 |
| password | String | | 密码 |
| semantic | String | at-least-once | 语义保证 <li>`at-least-once`：一个事件至少同步一次；<li>`exactly-once`：一个事件精确同步一次，仅支持 StarRocks 2.5 或更高版本。 |
| label_prefix | String | | Stream Load 的标签前缀 <br>若语义保证是 `exactly-once` 时，推荐设置此值，具体请参考[官方文档](https://docs.starrocks.io/zh/docs/loading/Flink-connector-starrocks/#exactly-once)。 |


## 参考资料
- [从 Apache Flink® 持续导入](https://docs.starrocks.io/zh/docs/loading/Flink-connector-starrocks/) · _StarRocks_
