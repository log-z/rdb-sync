# RDB Sync
基于 Apache Flink® 与 Ververica CDC Connectors 的数据同步解决方案。

## 演练场
### 第一阶段（MySQL --> MySQL）
1. 启动位于 [rdb-sync-job/rdb-sync-job-simple/docker](rdb-sync-job/rdb-sync-job-simple/docker) 的 Docker Compose 服务，里面包含了 MySQL、StarRocks 以及一个最小的 Flink 集群；
2. 编译整个源码并打包；
3. 通过 Web UI 把 [rdb-sync-job/rdb-sync-job-simple/](rdb-sync-job/rdb-sync-job-simple)target 目录中刚打包好的 rdb-sync-job-simple-0.1.0-SNAPSHOT-jar-with-dependencies.jar 文件上传到 Flink 集群；
4. 在 Flink 集群中启动 JAR 任务，同时传递实参 `-e dev -p mysql_to_mysql` 以指定运行环境 `-e` 和管道 `-p`；
5. 通过软件连接到 MySQL 上，观察 `test_src.orders` 表是否已经实时同步到 `test_dist.orders` 表中。

> Flink Web UI 已经映射到 `8081` 端口。
> 
> MySQL 已映射到 `3306` 端口，root 用户密码是 `root` 。
> 
> Docker Compose 已经自动初始化了 MySQL 的库表和用户。

### 第二阶段（MySQL --> StarRocks）
1. 通过软件连接到 StarRocks 上，并创建 `dw_ods` 数据库。
2. 在 StarRocks 运行下面的 3 行 SQL 创建数据同步专用用户，并赋予必要权限。
    ```mysql
    CREATE USER 'rdb_sync'@'%' IDENTIFIED BY 'rdb_sync';
    GRANT INSERT, UPDATE, DELETE ON ALL TABLES IN DATABASE dw_ods TO USER 'rdb_sync'@'%';
    SHOW GRANTS FOR 'rdb_sync'@'%';
    ```
3. 运行 [rdb-sync-job/rdb-sync-job-simple](rdb-sync-job/rdb-sync-job-simple) 模块中的 [vip.logz.rdbsync.job.simple.Pipelines](rdb-sync-job/rdb-sync-job-simple/src/main/java/vip/logz/rdbsync/job/simple/Pipelines.java) 主函数，得到 Starrocks 语法的 `orders` 的建表语句。
4. 在 StarRocks 的 `dw_ods` 数据库中创建 `orders` 表。
5. 在 Flink 集群中启动 JAR 任务，同时传递实参 `-e dev -p mysql_to_starrocks` 以指定运行环境和另一个管道；
6. 观察 MySQL 中的 `test_src.orders` 表是否已经实时同步到 StarRocks 中的 `dw_ods.orders` 表中。

> Docker Compose 没有初始化 StarRocks 的库表和用户，所以要手动创建它们。
>
> StarRocks 基本兼容 MySQL 协议，任何 MySQL 客户端均可连接，它的连接端口已经映射到 `9030` 上，root 用户没有密码。
> 
> 由于第一和第二阶段都使用了相同的 MySQL 来源，而且 Server ID 重复，所以两个任务不能同时运行。

## 教程

### 表映射 & 管道
TODO...

### 配置
TODO...
