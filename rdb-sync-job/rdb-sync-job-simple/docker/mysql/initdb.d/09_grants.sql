-- Table
GRANT Select ON `test_src`.* TO `rdb_sync`@`%`;
GRANT Select, Insert, Update, Delete ON `test_dist`.* TO `rdb_sync`@`%`;
GRANT Select ON `rdb_sync`.* TO `rdb_sync`@`%`;

-- Database
GRANT Lock Tables, Replication Client, Replication Slave ON *.* TO `rdb_sync`@`%`;
