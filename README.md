# RDB Sync
基于 Apache Flink® `1.18.x` 与 Ververica CDC Connectors 的数据同步解决方案。

## 演练场
### 第一阶段（MySQL --> MySQL）
1. 启动位于 [rdb-sync-job/rdb-sync-job-simple/docker](rdb-sync-job/rdb-sync-job-simple/docker) 的 Docker Compose 服务，里面包含了 MySQL、StarRocks 以及一个最小的 Flink 集群；
2. 编译整个源码并打包；
3. 通过 Web UI <sup>①</sup> 把 [rdb-sync-job/rdb-sync-job-simple/](rdb-sync-job/rdb-sync-job-simple)target 目录中刚打包好的 rdb-sync-job-simple-0.1.0-SNAPSHOT-jar-with-dependencies.jar 文件上传到 Flink 集群；
4. 在 Flink 集群中启动 JAR 任务，同时传递实参 `-e dev -p mysql_to_mysql` 以指定运行环境 `-e` 和管道 `-p`；
5. 通过软件连接到 MySQL <sup>②</sup> 上，观察 `test_src.orders` 表是否已经实时同步到 `test_dist.orders` 表中 <sup>③</sup> 。

> 补充说明：
> 
> ① Flink Web UI 已经映射到 `8081` 端口。
>
> ② MySQL 已映射到 `3306` 端口，root 用户密码是 `root` 。
>
> ③ Docker Compose 已经自动初始化了 MySQL 的库表和用户。

### 第二阶段（MySQL --> StarRocks）
1. 通过软件连接到 StarRocks <sup>①</sup> 上，并创建 `dw_ods` 数据库 <sup>②</sup> ；
2. 在 StarRocks 运行下面的 3 行 SQL 创建数据同步专用用户，并赋予必要权限；
    ```mysql
    CREATE USER 'rdb_sync'@'%' IDENTIFIED BY 'rdb_sync';
    GRANT INSERT, UPDATE, DELETE ON ALL TABLES IN DATABASE dw_ods TO USER 'rdb_sync'@'%';
    SHOW GRANTS FOR 'rdb_sync'@'%';
    ```
3. 运行 [rdb-sync-job/rdb-sync-job-simple](rdb-sync-job/rdb-sync-job-simple) 模块中的 [vip.logz.rdbsync.job.simple.Pipelines](rdb-sync-job/rdb-sync-job-simple/src/main/java/vip/logz/rdbsync/job/simple/Pipelines.java) 主函数，得到 Starrocks 语法的 `orders` 的建表语句；
4. 在 StarRocks 的 `dw_ods` 数据库中创建 `orders` 表；
5. 在 Flink 集群中启动 JAR 任务，同时传递实参 `-e dev -p mysql_to_starrocks` 以指定运行环境和另一个管道 <sup>③</sup> ；
6. 观察 MySQL 中的 `test_src.orders` 表是否已经实时同步到 StarRocks 中的 `dw_ods.orders` 表中。

> 补充说明：
>
> ① StarRocks 基本兼容 MySQL 协议，任何 MySQL 客户端均可连接，它的连接端口已经映射到 `9030` 上，root 用户没有密码。
>
> ② Docker Compose 没有初始化 StarRocks 的库表和用户，所以要手动创建它们。
>
> ③ 由于第一和第二阶段都使用了相同的 MySQL 来源，而且 Server ID 重复，所以两个任务不能同时运行。

## 教程

### 连接器
根据同步的数据库，按需引入连接器依赖。其它**必要依赖**请参考示例项目的 [pom.xml](rdb-sync-job/rdb-sync-job-simple/pom.xml) 文件。

| 协议 | 连接器 | 管道 |
|---|---|---|
| MySQL | [rdb-sync-connector-mysql](rdb-sync-connector/rdb-sync-connector-mysql) | 来源、目标 |
| StarRocks | [rdb-sync-connector-starrocks](rdb-sync-connector/rdb-sync-connector-starrocks) | 目标 |

### 表映射 & 管道
表映射：定义目标表结构，依据此进行类型转换和建表语句生成。

管道：确定来源与目标的连接配置，定义来源表与目标表的绑定关系。

> 不需要定义来源表结构，只需要目标表的结构与来源表匹配即可。
```java
// 表映射：订单表的Starrocks结构（目标表）
Mapping<Starrocks> ordersToStarrocksMapping = MappingBuilder.<Starrocks>of()
        .field("order_id").type(Starrocks.INT()).nonNull().primaryKey().comment("订单ID").and()
        .field("order_date").type(Starrocks.DATETIME()).comment("下单日期").and()
        .field("customer_name").type(Starrocks.STRING()).comment("客户名称").and()
        .field("price").type(Starrocks.DECIMAL(10, 5)).comment("价格").and()
        .field("product_id").type(Starrocks.INT()).comment("商品ID").and()
        .field("order_status").type(Starrocks.INT()).comment("订单状态").and()
        .build();

// 管道：Mysql同步到Starrocks，管道ID是“mysql_to_starrocks”
Pipeline<Starrocks> mysqlToStarrocksPipeline = PipelineBuilder.<Starrocks>of("mysql_to_starrocks")
        .sourceId("simple_source_mysql")
        .distId("simple_dist_starrocks")
        // 来源表等值匹配
        .binding("orders", "orders", ordersToStarrocksMapping)
        // 来源表正则匹配
        .binding(RegexTableMatcher.of("$orders_\\d+"), "orders", ordersToMysqlMapping)
        // 来源表自定义匹配
        .binding(t -> t.startsWith("orders_"), "orders", ordersToMysqlMapping)
        .build();
```

通过各种 `DDLGenerator` 为管道批量生成目标表的建表语句。
```java
public static void printDDL() {
    StarrocksDDLGenerator ddlGenerator = new StarrocksDDLGenerator();
    for (String ddl : ddlGenerator.generate(mysqlToStarrocksPipeline)) {
        System.out.println(ddl);
    }
}
```

启动 Flink 作业的主方法。
```java
public static void main(String[] args) throws Exception {
    // 初始化任务上下文工厂
    StartupParameter startupParameter = StartupParameter.fromArgs(args);
    ContextFactory contextFactory = new PersistContextFactory(startupParameter)
            // 注册管道（一个或多个），后续在启动时通过参数指定需要启动的管道
            .register(mysqlToStarrocksPipeline);
    
    // 构造数据同步执行器，并启动它
    new RdbSyncExecution(contextFactory).start();
}
```

### 配置
RDB Sync 的关键配置保存在数据库中，下面以 MySQL 存储为例。

推荐使用专用数据库 `rdb_sync` 和专用用户，执行 [doc/sql/rdb_sync.sql](doc/sql/rdb_sync.sql) 可生成全部配置表 <sup>①</sup> ：
* pipeline_source `管道来源`
  * pipeline_source_mysql `管道来源-MySQL扩展`
* pipeline_dist `管道目标`
  * pipeline_dist_mysql `管道目标-MySQL扩展`
  * pipeline_dist_starrocks `管道目标-StarRocks扩展`

```
pipeline_source.id <--> PipelineBuilder.sourceId(..)
pipeline_dist.id <--> PipelineBuilder.distId(..)
```

在 Job 源码的配置文件 `application-${env}.yaml` <sup>②</sup> 中可以指定 `rdb_sync` 数据库的连接信息：
```yaml
rdb-sync:
  datasource:
    driver: com.mysql.jdbc.Driver
    url: jdbc:mysql://mysql:3306/rdb_sync
    username: ...
    password: ...
```

配置文件还支持管理 [Flink 配置](https://nightlies.apache.org/flink/flink-docs-release-1.18/zh/docs/deployment/config/)，这将覆盖 Flink 集群的默认配置。
```yaml
# 此处演示 state.checkpoints.dir 配置项
state:
  checkpoints:
    dir: hdfs://...
```

> 补充说明：
>
> ① 启动 Flink 作业前，请按实际情况录入数据。
>
> ② 文件名中的 `${env}` 是运行环境的意思，其值可以是 `dev` 、`test` 和 `prod` 之类的。

### 启动参数
| 参数 | 默认值 | 描述 |
|---|---|---|
| `-e`<br/>`--env` | dev | 指定运行环境 |
| `-p`<br/>`--pipeline` | 必填 | 指定启动的管道ID |
