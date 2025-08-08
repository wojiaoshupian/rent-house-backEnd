-- =====================================================
-- 创建数据库和用户脚本
-- 数据库名: rent_house
-- 用户名: rent_house
-- 密码: Qwesdx1245@
-- 创建时间: 2025-08-08
-- =====================================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS `rent_house` 
DEFAULT CHARACTER SET utf8mb4 
DEFAULT COLLATE utf8mb4_unicode_ci;

-- 创建用户（如果不存在）
CREATE USER IF NOT EXISTS 'rent_house'@'localhost' IDENTIFIED BY 'Qwesdx1245@';
CREATE USER IF NOT EXISTS 'rent_house'@'%' IDENTIFIED BY 'Qwesdx1245@';

-- 授予权限
GRANT ALL PRIVILEGES ON `rent_house`.* TO 'rent_house'@'localhost';
GRANT ALL PRIVILEGES ON `rent_house`.* TO 'rent_house'@'%';

-- 刷新权限
FLUSH PRIVILEGES;

-- 显示创建结果
SELECT 'Database and user created successfully!' AS message;
SELECT 'Database: rent_house' AS database_info;
SELECT 'Username: rent_house' AS user_info;
SELECT 'Password: Qwesdx1245@' AS password_info;
