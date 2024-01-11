GRANT Select ON `test_src_a`.* TO `flink_cdc`@`%`;

GRANT Insert, Update, Delete ON `test_dw_ods`.* TO `flink_cdc`@`%`;

GRANT Lock Tables, Replication Client, Replication Slave ON *.* TO `flink_cdc`@`%`;
