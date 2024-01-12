-- ----------------------------
-- 测试目标库
-- 2024-01-12
-- ----------------------------
CREATE DATABASE test_dist;
USE test_dist;

CREATE TABLE products (
  id INTEGER NOT NULL PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  description VARCHAR(512)
);

CREATE TABLE orders (
  order_id INTEGER NOT NULL PRIMARY KEY,
  order_date DATETIME NOT NULL,
  customer_name VARCHAR(255) NOT NULL,
  price DECIMAL(10, 5) NOT NULL,
  product_id INTEGER NOT NULL,
  order_status BOOLEAN NOT NULL -- Whether order has been placed
);
