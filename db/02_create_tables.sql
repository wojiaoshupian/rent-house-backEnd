-- =====================================================
-- 房屋租赁管理系统 - 表结构创建脚本
-- 版本: 1.0
-- 创建时间: 2025-08-06
-- 描述: 创建所有数据表结构
-- =====================================================

USE `rent_house_management`;

-- =====================================================
-- 1. 用户表 (users)
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
-- 2. 用户角色表 (user_roles)
-- =====================================================
DROP TABLE IF EXISTS `user_roles`;
CREATE TABLE `user_roles` (
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `role` VARCHAR(50) NOT NULL COMMENT '角色名称',
    PRIMARY KEY (`user_id`, `role`),
    CONSTRAINT `fk_user_roles_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';

-- =====================================================
-- 3. 楼宇表 (buildings)
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
-- 4. 用户楼宇关联表 (user_buildings)
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
-- 5. 房间表 (rooms)
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
-- 6. 水电表记录表 (utility_readings)
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
-- 7. 账单表 (estimated_bills)
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
