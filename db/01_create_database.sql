-- =====================================================
-- 房屋租赁管理系统 - 数据库创建脚本
-- 版本: 1.0
-- 创建时间: 2025-08-06
-- 描述: 创建数据库和基础配置
-- =====================================================

-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS `rent_house_management` 
DEFAULT CHARACTER SET utf8mb4 
DEFAULT COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE `rent_house_management`;

-- 设置时区
SET time_zone = '+08:00';

-- 设置字符集
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;
