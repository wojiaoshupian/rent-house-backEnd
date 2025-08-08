-- =====================================================
-- 宝塔面板专用 - 数据库导入脚本
-- 版本: 1.0
-- 创建时间: 2025-08-08
-- 数据库名: rent_house
-- 用户名: rent_house
-- 密码: Qwesdx1245@
-- 描述: 适用于宝塔面板phpMyAdmin的完整导入脚本
-- 使用方法: 在phpMyAdmin中直接导入此文件
-- =====================================================

-- 设置字符集和时区
SET NAMES utf8mb4;
SET time_zone = '+08:00';
SET foreign_key_checks = 0;
SET sql_mode = 'STRICT_TRANS_TABLES,NO_ZERO_DATE,NO_ZERO_IN_DATE,ERROR_FOR_DIVISION_BY_ZERO';

-- =====================================================
-- 1. 创建用户表
-- =====================================================
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    `password` VARCHAR(255) NOT NULL COMMENT '密码（加密）',
    `phone` VARCHAR(11) NOT NULL UNIQUE COMMENT '手机号码',
    `email` VARCHAR(100) COMMENT '邮箱地址',
    `full_name` VARCHAR(50) COMMENT '真实姓名',
    `status` ENUM('ACTIVE', 'INACTIVE', 'LOCKED') NOT NULL DEFAULT 'ACTIVE' COMMENT '用户状态',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_username` (`username`),
    INDEX `idx_phone` (`phone`),
    INDEX `idx_status` (`status`),
    INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- =====================================================
-- 2. 创建用户角色表
-- =====================================================
DROP TABLE IF EXISTS `user_roles`;
CREATE TABLE `user_roles` (
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `role` VARCHAR(50) NOT NULL COMMENT '角色名称',
    PRIMARY KEY (`user_id`, `role`),
    CONSTRAINT `fk_user_roles_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';

-- =====================================================
-- 3. 创建楼宇表
-- =====================================================
DROP TABLE IF EXISTS `buildings`;
CREATE TABLE `buildings` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '楼宇ID',
    `building_name` VARCHAR(100) NOT NULL COMMENT '楼宇名称',
    `landlord_name` VARCHAR(50) NOT NULL COMMENT '房东姓名',
    `electricity_unit_price` DECIMAL(10,2) NOT NULL COMMENT '电费单价（元/度）',
    `water_unit_price` DECIMAL(10,2) NOT NULL COMMENT '水费单价（元/吨）',
    `hot_water_unit_price` DECIMAL(10,2) COMMENT '热水单价（元/吨）',
    `electricity_cost` DECIMAL(10,2) COMMENT '电费成本（元/度）',
    `water_cost` DECIMAL(10,2) COMMENT '水费成本（元/吨）',
    `hot_water_cost` DECIMAL(10,2) COMMENT '热水费成本（元/吨）',
    `rent_collection_method` ENUM('FIXED_MONTH_START', 'FLEXIBLE') NOT NULL DEFAULT 'FIXED_MONTH_START' COMMENT '租金收取方式',
    `created_by` BIGINT NOT NULL COMMENT '创建者用户ID',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_building_name` (`building_name`),
    INDEX `idx_created_by` (`created_by`),
    INDEX `idx_created_at` (`created_at`),
    CONSTRAINT `fk_buildings_created_by` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='楼宇表';

-- =====================================================
-- 4. 创建用户楼宇关联表
-- =====================================================
DROP TABLE IF EXISTS `user_buildings`;
CREATE TABLE `user_buildings` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '关联ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `building_id` BIGINT NOT NULL COMMENT '楼宇ID',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_building` (`user_id`, `building_id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_building_id` (`building_id`),
    CONSTRAINT `fk_user_buildings_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_user_buildings_building_id` FOREIGN KEY (`building_id`) REFERENCES `buildings` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户楼宇关联表';

-- =====================================================
-- 5. 创建房间表
-- =====================================================
DROP TABLE IF EXISTS `rooms`;
CREATE TABLE `rooms` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '房间ID',
    `room_number` VARCHAR(50) NOT NULL COMMENT '房间号',
    `rent` DECIMAL(10,2) NOT NULL COMMENT '月租金（元）',
    `default_deposit` DECIMAL(10,2) NOT NULL COMMENT '默认押金（元）',
    `electricity_unit_price` DECIMAL(10,2) COMMENT '房间电费单价（元/度）',
    `water_unit_price` DECIMAL(10,2) COMMENT '房间水费单价（元/吨）',
    `hot_water_unit_price` DECIMAL(10,2) COMMENT '房间热水单价（元/吨）',
    `building_id` BIGINT NOT NULL COMMENT '所属楼宇ID',
    `rental_status` ENUM('VACANT', 'RENTED', 'MAINTENANCE', 'RESERVED') NOT NULL DEFAULT 'VACANT' COMMENT '出租状态',
    `created_by` BIGINT NOT NULL COMMENT '创建者用户ID',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_room_building` (`room_number`, `building_id`),
    INDEX `idx_building_id` (`building_id`),
    INDEX `idx_rental_status` (`rental_status`),
    INDEX `idx_created_by` (`created_by`),
    INDEX `idx_created_at` (`created_at`),
    CONSTRAINT `fk_rooms_building_id` FOREIGN KEY (`building_id`) REFERENCES `buildings` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_rooms_created_by` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='房间表';

-- =====================================================
-- 6. 创建水电表记录表
-- =====================================================
DROP TABLE IF EXISTS `utility_readings`;
CREATE TABLE `utility_readings` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `room_id` BIGINT NOT NULL COMMENT '房间ID',
    `reading_date` DATE NOT NULL COMMENT '抄表日期',
    `reading_time` DATETIME NOT NULL COMMENT '抄表时间',

    -- 电表相关
    `electricity_reading` DECIMAL(10,2) NOT NULL COMMENT '电表读数（度）',
    `electricity_previous_reading` DECIMAL(10,2) DEFAULT 0.00 COMMENT '上次电表读数（度）',
    `electricity_usage` DECIMAL(10,2) GENERATED ALWAYS AS (`electricity_reading` - `electricity_previous_reading`) STORED COMMENT '本期用电量（度）',

    -- 水表相关
    `water_reading` DECIMAL(10,2) NOT NULL COMMENT '水表读数（吨）',
    `water_previous_reading` DECIMAL(10,2) DEFAULT 0.00 COMMENT '上次水表读数（吨）',
    `water_usage` DECIMAL(10,2) GENERATED ALWAYS AS (`water_reading` - `water_previous_reading`) STORED COMMENT '本期用水量（吨）',

    -- 热水表相关
    `hot_water_reading` DECIMAL(10,2) DEFAULT 0.00 COMMENT '热水表读数（吨）',
    `hot_water_previous_reading` DECIMAL(10,2) DEFAULT 0.00 COMMENT '上次热水表读数（吨）',
    `hot_water_usage` DECIMAL(10,2) GENERATED ALWAYS AS (`hot_water_reading` - `hot_water_previous_reading`) STORED COMMENT '本期热水用量（吨）',

    -- 抄表信息
    `meter_reader` VARCHAR(100) NOT NULL COMMENT '抄表人',
    `reading_type` ENUM('MANUAL', 'AUTO', 'ESTIMATED') DEFAULT 'MANUAL' COMMENT '抄表类型',
    `reading_status` ENUM('PENDING', 'CONFIRMED', 'DISPUTED') DEFAULT 'PENDING' COMMENT '读数状态',
    `notes` TEXT COMMENT '备注信息',
    `photos` JSON COMMENT '抄表照片URL列表',

    -- 系统字段
    `created_by` BIGINT NOT NULL COMMENT '创建者用户ID',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_room_date` (`room_id`, `reading_date`),
    INDEX `idx_room_id` (`room_id`),
    INDEX `idx_reading_date` (`reading_date`),
    INDEX `idx_reading_time` (`reading_time`),
    INDEX `idx_room_date` (`room_id`, `reading_date`),
    INDEX `idx_reading_status` (`reading_status`),
    INDEX `idx_created_by` (`created_by`),
    CONSTRAINT `fk_utility_readings_room_id` FOREIGN KEY (`room_id`) REFERENCES `rooms` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_utility_readings_created_by` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='水电表记录表';

-- =====================================================
-- 7. 创建账单表
-- =====================================================
DROP TABLE IF EXISTS `estimated_bills`;
CREATE TABLE `estimated_bills` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '账单ID',
    `room_id` BIGINT NOT NULL COMMENT '房间ID',
    `bill_month` VARCHAR(7) NOT NULL COMMENT '账单年月（YYYY-MM）',
    `bill_date` DATE NOT NULL COMMENT '账单生成日期',

    -- 费用项目
    `rent` DECIMAL(10,2) COMMENT '房租（元）',
    `deposit` DECIMAL(10,2) DEFAULT 0.00 COMMENT '押金（元）',

    -- 电费相关
    `electricity_unit_price` DECIMAL(8,4) COMMENT '电费单价（元/度）',
    `electricity_usage` DECIMAL(10,2) COMMENT '用电量（度）',
    `electricity_amount` DECIMAL(10,2) COMMENT '电费金额（元）',

    -- 水费相关
    `water_unit_price` DECIMAL(8,4) COMMENT '水费单价（元/吨）',
    `water_usage` DECIMAL(10,2) COMMENT '用水量（吨）',
    `water_amount` DECIMAL(10,2) COMMENT '水费金额（元）',

    -- 热水费相关
    `hot_water_unit_price` DECIMAL(8,4) COMMENT '热水单价（元/吨）',
    `hot_water_usage` DECIMAL(10,2) COMMENT '热水用量（吨）',
    `hot_water_amount` DECIMAL(10,2) COMMENT '热水费金额（元）',

    -- 其他费用
    `other_fees` DECIMAL(10,2) DEFAULT 0.00 COMMENT '其他费用（元）',
    `other_fees_description` TEXT COMMENT '其他费用说明',

    -- 总计
    `total_amount` DECIMAL(10,2) COMMENT '总金额（元）',

    -- 账单状态
    `bill_status` ENUM('GENERATED', 'CONFIRMED', 'SENT', 'PAID', 'CANCELLED') DEFAULT 'GENERATED' COMMENT '账单状态',
    `notes` TEXT COMMENT '备注信息',

    -- 系统字段
    `created_by` BIGINT COMMENT '创建者用户ID',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_room_month` (`room_id`, `bill_month`),
    INDEX `idx_room_id` (`room_id`),
    INDEX `idx_bill_month` (`bill_month`),
    INDEX `idx_bill_date` (`bill_date`),
    INDEX `idx_bill_status` (`bill_status`),
    INDEX `idx_created_by` (`created_by`),
    INDEX `idx_created_at` (`created_at`),
    CONSTRAINT `fk_estimated_bills_room_id` FOREIGN KEY (`room_id`) REFERENCES `rooms` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_estimated_bills_created_by` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='账单表';

-- =====================================================
-- 8. 插入初始用户数据
-- =====================================================

-- 插入初始用户（密码: 123456 的BCrypt加密值）
INSERT INTO `users` (`username`, `password`, `phone`, `email`, `full_name`, `status`, `created_at`, `updated_at`) VALUES
('superadmin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKXIGfR8PQM6J6hOOqOaOOKOOqOa', '13800000001', 'superadmin@example.com', '超级管理员', 'ACTIVE', NOW(), NOW()),
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKXIGfR8PQM6J6hOOqOaOOKOOqOa', '13800000002', 'admin@example.com', '系统管理员', 'ACTIVE', NOW(), NOW()),
('finance', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKXIGfR8PQM6J6hOOqOaOOKOOqOa', '13800000004', 'finance@example.com', '财务人员', 'ACTIVE', NOW(), NOW()),
('service', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKXIGfR8PQM6J6hOOqOaOOKOOqOa', '13800000005', 'service@example.com', '客服人员', 'ACTIVE', NOW(), NOW()),
('user1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKXIGfR8PQM6J6hOOqOaOOKOOqOa', '13800000006', 'user1@example.com', '张三', 'ACTIVE', NOW(), NOW()),
('guest', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKXIGfR8PQM6J6hOOqOaOOKOOqOa', '13800000008', 'guest@example.com', '访客用户', 'ACTIVE', NOW(), NOW());

-- 插入用户角色
INSERT INTO `user_roles` (`user_id`, `role`) VALUES
-- 超级管理员角色
(1, 'SUPER_ADMIN'), (1, 'ADMIN'), (1, 'USER'),
-- 管理员角色
(2, 'ADMIN'), (2, 'USER'),
-- 财务人员角色
(3, 'FINANCE'), (3, 'USER'),
-- 客服人员角色
(4, 'CUSTOMER_SERVICE'), (4, 'USER'),
-- 普通用户角色
(5, 'USER'),
-- 访客角色
(6, 'GUEST');

-- =====================================================
-- 9. 插入示例楼宇数据
-- =====================================================

INSERT INTO `buildings` (
    `building_name`, `landlord_name`, `electricity_unit_price`, `water_unit_price`,
    `hot_water_unit_price`, `electricity_cost`, `water_cost`, `hot_water_cost`,
    `rent_collection_method`, `created_by`, `created_at`, `updated_at`
) VALUES
('阳光公寓', '王房东', 1.20, 3.50, 6.00, 0.80, 2.00, 4.00, 'FIXED_MONTH_START', 1, NOW(), NOW()),
('绿城花园', '李房东', 1.15, 3.20, 5.80, 0.75, 1.80, 3.80, 'FIXED_MONTH_START', 1, NOW(), NOW());

-- 插入用户楼宇关联
INSERT INTO `user_buildings` (`user_id`, `building_id`, `created_at`) VALUES
(1, 1, NOW()), (1, 2, NOW()), (2, 1, NOW()), (2, 2, NOW());

-- =====================================================
-- 10. 插入示例房间数据
-- =====================================================

INSERT INTO `rooms` (
    `room_number`, `rent`, `default_deposit`, `electricity_unit_price`,
    `water_unit_price`, `hot_water_unit_price`, `building_id`,
    `rental_status`, `created_by`, `created_at`, `updated_at`
) VALUES
-- 阳光公寓的房间
('101', 1200.00, 1500.00, NULL, NULL, NULL, 1, 'VACANT', 1, NOW(), NOW()),
('102', 1300.00, 1600.00, NULL, NULL, NULL, 1, 'RENTED', 1, NOW(), NOW()),
('103', 1250.00, 1550.00, NULL, NULL, NULL, 1, 'RENTED', 1, NOW(), NOW()),
-- 绿城花园的房间
('A101', 1100.00, 1400.00, NULL, NULL, NULL, 2, 'VACANT', 1, NOW(), NOW()),
('A102', 1150.00, 1450.00, NULL, NULL, NULL, 2, 'RENTED', 1, NOW(), NOW());

-- 恢复外键检查
SET foreign_key_checks = 1;

-- 显示导入完成信息
SELECT '数据库导入完成！' AS message,
       '默认管理员账号: superadmin' AS admin_account,
       '默认密码: 123456' AS admin_password,
       '请及时修改默认密码！' AS security_notice;
