-- =====================================================
-- 房屋租赁管理系统 - 数据库创建脚本
-- 版本: 1.0
-- 创建时间: 2025-08-06
-- 描述: 创建PostgreSQL数据库
-- PostgreSQL 16 兼容版本
-- =====================================================

-- 创建数据库（如果不存在）
-- 注意：PostgreSQL中创建数据库需要特殊权限，通常由DBA执行
-- 或者使用以下命令：
-- createdb -U postgres rent_house

-- 连接到数据库
-- \c rent_house;

-- 创建扩展（如果需要）
-- CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
-- CREATE EXTENSION IF NOT EXISTS "pg_trgm";

-- 设置时区
SET timezone = 'Asia/Shanghai';

-- 设置字符集
SET client_encoding = 'UTF8';

-- 显示当前数据库信息
SELECT current_database() as database_name, 
       current_user as current_user,
       version() as postgresql_version; 