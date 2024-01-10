-- MySQL
CREATE DATABASE test_dw_ods;
USE test_dw_ods;

CREATE TABLE a$products (
  id INTEGER NOT NULL PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  description VARCHAR(512)
);

CREATE TABLE a$orders (
  order_id INTEGER NOT NULL PRIMARY KEY,
  order_date DATETIME NOT NULL,
  customer_name VARCHAR(255) NOT NULL,
  price DECIMAL(10, 5) NOT NULL,
  product_id INTEGER NOT NULL,
  order_status BOOLEAN NOT NULL -- Whether order has been placed
);
