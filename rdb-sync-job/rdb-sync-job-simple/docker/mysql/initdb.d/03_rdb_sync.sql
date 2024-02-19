-- ----------------------------
-- RDB Sync 配置库
-- 2024-02-18
-- ----------------------------
CREATE DATABASE rdb_sync;
USE rdb_sync;

-- ----------------------------
-- Table pipeline_dist
-- ----------------------------
create table `pipeline_dist` (
  `id` varchar(32) not null comment 'ID',
  `name` varchar(64) not null comment '名称',
  `protocol` varchar(32) not null comment '协议',
  primary key (`id`) using btree
) comment = '管道目标';

-- ----------------------------
-- Table pipeline_dist_mysql
-- ----------------------------
create table `pipeline_dist_mysql` (
  `id` varchar(32) not null comment 'ID',
  `host` varchar(255) comment '主机',
  `port` int(11) comment '端口',
  `database` varchar(255) not null comment '数据库名',
  `username` varchar(255) comment '用户名',
  `password` varchar(255) comment '密码',
  `exec_batch_interval_ms` bigint comment '执行批次间隔毫秒数',
  `exec_batch_size` int comment '执行批次最大容量',
  `exec_max_retries` int comment '执行最大重试次数',
  `conn_timeout_seconds` int comment '连接超时秒数',
  primary key (`id`) using btree
) comment = '管道目标-MySQL扩展';

-- ----------------------------
-- Table pipeline_dist_postgres
-- ----------------------------
create table `pipeline_dist_postgres` (
  `id` varchar(32) not null comment 'ID',
  `host` varchar(255) comment '主机',
  `port` int(11) comment '端口',
  `database` varchar(255) not null comment '数据库名',
  `schema` varchar(255) comment '模式名',
  `username` varchar(255) comment '用户名',
  `password` varchar(255) comment '密码',
  `exec_batch_interval_ms` bigint comment '执行批次间隔毫秒数',
  `exec_batch_size` int comment '执行批次最大容量',
  `exec_max_retries` int comment '执行最大重试次数',
  `conn_timeout_seconds` int comment '连接超时秒数',
  primary key (`id`) using btree
) comment = '管道目标-Postgres扩展';

-- ----------------------------
-- Table pipeline_dist_sqlserver
-- ----------------------------
create table `pipeline_dist_sqlserver` (
  `id` varchar(32) not null comment 'ID',
  `host` varchar(255) comment '主机',
  `port` int(11) comment '端口',
  `database` varchar(255) not null comment '数据库名',
  `schema` varchar(255) comment '模式名',
  `username` varchar(255) comment '用户名',
  `password` varchar(255) not null comment '密码',
  `exec_batch_interval_ms` bigint comment '执行批次间隔毫秒数',
  `exec_batch_size` int comment '执行批次最大容量',
  `exec_max_retries` int comment '执行最大重试次数',
  `conn_timeout_seconds` int comment '连接超时秒数',
  primary key (`id`) using btree
) comment = '管道目标-SQLServer扩展';

-- ----------------------------
-- Table pipeline_dist_starrocks
-- ----------------------------
create table `pipeline_dist_starrocks` (
  `id` varchar(32) not null comment 'ID',
  `host` varchar(255) comment 'BE-MySQL服务主机',
  `port` int(11) comment 'BE-MySQL服务端口',
  `load_host` varchar(255) comment 'FE-HTTP服务主机',
  `load_port` int(11) comment 'FE-HTTP服务端口',
  `database` varchar(255) not null comment '数据库名',
  `username` varchar(255) comment '用户名',
  `password` varchar(255) comment '密码',
  primary key (`id`) using btree
) comment = '管道目标-StarRocks扩展';

-- ----------------------------
-- Table pipeline_source
-- ----------------------------
create table `pipeline_source` (
  `id` varchar(32) not null comment 'ID',
  `name` varchar(64) not null comment '名称',
  `protocol` varchar(32) not null comment '协议',
  `parallelism` int comment '并行度',
  primary key (`id`) using btree
) comment = '管道来源';

-- ----------------------------
-- Table pipeline_source_mysql
-- ----------------------------
create table `pipeline_source_mysql` (
  `id` varchar(32) not null comment 'id',
  `host` varchar(255) comment '主机',
  `port` int(11) comment '端口',
  `database` varchar(255) not null comment '数据库名',
  `username` varchar(255) comment '用户名',
  `password` varchar(255) comment '密码',
  `server_id` varchar(32) comment '模拟服务端ID',
  `server_time_zone` varchar(64) comment '数据库的会话时区',
  `startup_mode` varchar(32) comment '启动模式',
  `startup_specific_offset_file` varchar(255) comment '启动参数：起始日志文件',
  `startup_specific_offset_pos` bigint(20) comment '启动参数：起始日志文件内位置',
  `startup_specific_offset_gtid_set` varchar(255) comment '启动参数：起始事务编码',
  `startup_timestamp_millis` bigint(20) comment '启动参数：起始时间戳',
  `split_size` int comment '快照属性：表快照的分块大小（行数）',
  `split_meta_group_size` int comment '快照属性：拆分元数据的分组大小',
  `distribution_factor_upper` double comment '快照属性：均匀分布因子的上限',
  `distribution_factor_lower` double comment '快照属性：均匀分布因子的下限',
  `fetch_size` int comment '快照属性：每次轮询所能获取的最大行数',
  `connect_timeout_seconds` bigint comment '连接超时秒数',
  `connect_max_retries` int comment '连接最大重试次数',
  `connection_pool_size` int comment '连接池大小',
  `heartbeat_interval_seconds` bigint comment '心跳检测间隔秒数',
  `jdbc_properties` text comment 'JDBC属性（JSON）',
  primary key (`id`) using btree
) comment = '管道来源-MySQL扩展';

-- ----------------------------
-- Table pipeline_source_postgres
-- ----------------------------
create table `pipeline_source_postgres` (
  `id` varchar(32) not null comment 'id',
  `host` varchar(255) comment '主机',
  `port` int comment '端口',
  `database` varchar(255) not null comment '数据库名',
  `schema` varchar(255) comment '模式名',
  `username` varchar(255) comment '用户名',
  `password` varchar(255) comment '密码',
  `slot_name` varchar(255) not null comment '槽名称',
  `startup_mode` varchar(32) comment '启动模式',
  `decoding_plugin_name` varchar(64) comment '逻辑解码插件名称',
  `split_size` int comment '快照属性：表快照的分块大小（行数）',
  `split_meta_group_size` int comment '快照属性：拆分元数据的分组大小',
  `distribution_factor_upper` double comment '快照属性：均匀分布因子的上限',
  `distribution_factor_lower` double comment '快照属性：均匀分布因子的下限',
  `fetch_size` int comment '快照属性：每次轮询所能获取的最大行数',
  `connect_timeout_seconds` bigint comment '连接超时秒数',
  `connect_max_retries` int comment '连接最大重试次数',
  `connection_pool_size` int comment '连接池大小',
  `heartbeat_interval_seconds` bigint comment '心跳检测间隔秒数',
  primary key (`id`) using btree
) comment = '管道来源-Postgres扩展';

-- ----------------------------
-- Table pipeline_source_sqlserver
-- ----------------------------
create table `pipeline_source_sqlserver` (
  `id` varchar(32) not null comment 'id',
  `host` varchar(255) comment '主机',
  `port` int(11) comment '端口',
  `database` varchar(255) not null comment '数据库名',
  `schema` varchar(255) comment '模式名',
  `username` varchar(255) comment '用户名',
  `password` varchar(255) comment '密码',
  `server_time_zone` varchar(64) comment '数据库的会话时区',
  `startup_mode` varchar(32) comment '启动模式',
  `split_size` int comment '快照属性：表快照的分块大小（行数）',
  `split_meta_group_size` int comment '快照属性：拆分元数据的分组大小',
  `distribution_factor_upper` double comment '快照属性：均匀分布因子的上限',
  `distribution_factor_lower` double comment '快照属性：均匀分布因子的下限',
  `fetch_size` int comment '快照属性：每次轮询所能获取的最大行数',
  `connect_timeout_seconds` bigint comment '连接超时秒数',
  `connect_max_retries` int comment '连接最大重试次数',
  `connection_pool_size` int comment '连接池大小',
  primary key (`id`) using btree
) comment = '管道来源-SQLServer扩展';

-- ----------------------------
-- Records of pipeline_dist
-- ----------------------------
INSERT INTO `pipeline_dist` VALUES ('simple_dist_mysql', 'Simple Dist MySQL', 'mysql');
INSERT INTO `pipeline_dist` VALUES ('simple_dist_starrocks', 'Simple Dist StarRocks', 'starrocks');

-- ----------------------------
-- Records of pipeline_dist_mysql
-- ----------------------------
INSERT INTO `pipeline_dist_mysql` VALUES ('simple_dist_mysql', 'mysql', 3306, 'test_dist', 'rdb_sync', 'rdb_sync', NULL, NULL, NULL, NULL);

-- ----------------------------
-- Records of pipeline_dist_starrocks
-- ----------------------------
INSERT INTO `pipeline_dist_starrocks` VALUES ('simple_dist_starrocks', 'starrocks', 9030, 'starrocks', 8080, 'dw_ods', 'rdb_sync', 'rdb_sync');

-- ----------------------------
-- Records of pipeline_source
-- ----------------------------
INSERT INTO `pipeline_source` VALUES ('simple_source_mysql', 'Simple Source MySQL', 'mysql', 4);

-- ----------------------------
-- Records of pipeline_source_mysql
-- ----------------------------
INSERT INTO `pipeline_source_mysql` VALUES ('simple_source_mysql', 'mysql', 3306, 'test_src', 'rdb_sync', 'rdb_sync', '5000-5010', NULL, 'initial', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
