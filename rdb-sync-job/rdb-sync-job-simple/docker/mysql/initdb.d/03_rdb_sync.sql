-- ----------------------------
-- RDB Sync 配置库
-- 2024-02-27
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
  `options` text comment '高级选项',
  primary key (`id`) using btree
) comment = '管道目标-MySQL扩展';

-- ----------------------------
-- Table pipeline_dist_postgres
-- ----------------------------
create table `pipeline_dist_postgres` (
  `id` varchar(32) not null comment 'ID',
  `hosts` text comment '主机列表',
  `ports` varchar(255) comment '端口列表',
  `database` varchar(255) not null comment '数据库名',
  `schema` varchar(255) comment '模式名',
  `username` varchar(255) comment '用户名',
  `password` varchar(255) comment '密码',
  `options` text comment '高级选项',
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
  `options` text comment '高级选项',
  primary key (`id`) using btree
) comment = '管道目标-SQLServer扩展';

-- ----------------------------
-- Table pipeline_dist_starrocks
-- ----------------------------
create table `pipeline_dist_starrocks` (
  `id` varchar(32) not null comment 'ID',
  `hosts` text comment 'FE-MySQL服务主机列表',
  `ports` varchar(255) comment 'FE-MySQL服务端口列表',
  `load_hosts` text comment 'FE-HTTP服务主机列表',
  `load_ports` varchar(255) comment 'FE-HTTP服务端口列表',
  `database` varchar(255) not null comment '数据库名',
  `username` varchar(255) comment '用户名',
  `password` varchar(255) comment '密码',
  `options` text comment '高级选项',
  primary key (`id`) using btree
) comment = '管道目标-StarRocks扩展';

-- ----------------------------
-- Table pipeline_source
-- ----------------------------
create table `pipeline_source` (
  `id` varchar(32) not null comment 'ID',
  `name` varchar(64) not null comment '名称',
  `protocol` varchar(32) not null comment '协议',
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
  `options` text comment '高级选项',
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
  `options` text comment '高级选项',
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
  `options` text comment '高级选项',
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
INSERT INTO `pipeline_dist_mysql` VALUES ('simple_dist_mysql', 'mysql', 3306, 'test_dist', 'rdb_sync', 'rdb_sync', NULL);

-- ----------------------------
-- Records of pipeline_dist_starrocks
-- ----------------------------
INSERT INTO `pipeline_dist_starrocks` VALUES ('simple_dist_starrocks', 'starrocks', '9030', 'starrocks', '8080', 'dw_ods', 'rdb_sync', 'rdb_sync', 'sink.semantic: exactly-once');

-- ----------------------------
-- Records of pipeline_source
-- ----------------------------
INSERT INTO `pipeline_source` VALUES ('simple_source_mysql', 'Simple Source MySQL', 'mysql');

-- ----------------------------
-- Records of pipeline_source_mysql
-- ----------------------------
INSERT INTO `pipeline_source_mysql` VALUES ('simple_source_mysql', 'mysql', 3306, 'test_src', 'rdb_sync', 'rdb_sync', 'parallelism: 4');
