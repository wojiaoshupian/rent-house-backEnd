-- =====================================================
-- 房屋租赁管理系统 - 初始数据插入脚本
-- 版本: 1.0
-- 创建时间: 2025-08-06
-- 描述: 插入系统初始数据，包括管理员用户等
-- =====================================================

USE `rent_house_management`;

-- =====================================================
-- 1. 插入初始用户数据
-- =====================================================

-- 超级管理员用户
-- 密码: 123456 (BCrypt加密后的值)
INSERT INTO `users` (`username`, `password`, `phone`, `email`, `full_name`, `status`, `created_at`, `updated_at`) VALUES
('superadmin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKXIGfR8PQM6J6hOOqOaOOKOOqOa', '13800000001', 'superadmin@example.com', '超级管理员', 'ACTIVE', NOW(), NOW()),
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKXIGfR8PQM6J6hOOqOaOOKOOqOa', '13800000002', 'admin@example.com', '系统管理员', 'ACTIVE', NOW(), NOW()),
('content', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKXIGfR8PQM6J6hOOqOaOOKOOqOa', '13800000003', 'content@example.com', '内容管理员', 'ACTIVE', NOW(), NOW()),
('finance', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKXIGfR8PQM6J6hOOqOaOOKOOqOa', '13800000004', 'finance@example.com', '财务人员', 'ACTIVE', NOW(), NOW()),
('service', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKXIGfR8PQM6J6hOOqOaOOKOOqOa', '13800000005', 'service@example.com', '客服人员', 'ACTIVE', NOW(), NOW()),
('user1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKXIGfR8PQM6J6hOOqOaOOKOOqOa', '13800000006', 'user1@example.com', '张三', 'ACTIVE', NOW(), NOW()),
('user2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKXIGfR8PQM6J6hOOqOaOOKOOqOa', '13800000007', 'user2@example.com', '李四', 'ACTIVE', NOW(), NOW()),
('guest', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKXIGfR8PQM6J6hOOqOaOOKOOqOa', '13800000008', 'guest@example.com', '访客用户', 'ACTIVE', NOW(), NOW()),
('inactive', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKXIGfR8PQM6J6hOOqOaOOKOOqOa', '13800000009', 'inactive@example.com', '非活跃用户', 'INACTIVE', NOW(), NOW());

-- =====================================================
-- 2. 插入用户角色数据
-- =====================================================

-- 超级管理员角色
INSERT INTO `user_roles` (`user_id`, `role`) VALUES
(1, 'SUPER_ADMIN'),
(1, 'ADMIN'),
(1, 'USER');

-- 管理员角色
INSERT INTO `user_roles` (`user_id`, `role`) VALUES
(2, 'ADMIN'),
(2, 'USER');

-- 内容管理员角色
INSERT INTO `user_roles` (`user_id`, `role`) VALUES
(3, 'CONTENT_MANAGER'),
(3, 'USER');

-- 财务人员角色
INSERT INTO `user_roles` (`user_id`, `role`) VALUES
(4, 'FINANCE'),
(4, 'USER');

-- 客服人员角色
INSERT INTO `user_roles` (`user_id`, `role`) VALUES
(5, 'CUSTOMER_SERVICE'),
(5, 'USER');

-- 普通用户角色
INSERT INTO `user_roles` (`user_id`, `role`) VALUES
(6, 'USER'),
(7, 'USER');

-- 访客角色
INSERT INTO `user_roles` (`user_id`, `role`) VALUES
(8, 'GUEST');

-- 非活跃用户角色
INSERT INTO `user_roles` (`user_id`, `role`) VALUES
(9, 'USER');

-- =====================================================
-- 3. 插入示例楼宇数据（可选）
-- =====================================================

-- 示例楼宇1
INSERT INTO `buildings` (
    `building_name`, `landlord_name`, `electricity_unit_price`, `water_unit_price`, 
    `hot_water_unit_price`, `electricity_cost`, `water_cost`, `hot_water_cost`,
    `rent_collection_method`, `created_by`, `created_at`, `updated_at`
) VALUES (
    '阳光公寓', '王房东', 1.20, 3.50, 6.00, 0.80, 2.00, 4.00,
    'FIXED_MONTH_START', 1, NOW(), NOW()
);

-- 示例楼宇2
INSERT INTO `buildings` (
    `building_name`, `landlord_name`, `electricity_unit_price`, `water_unit_price`, 
    `hot_water_unit_price`, `electricity_cost`, `water_cost`, `hot_water_cost`,
    `rent_collection_method`, `created_by`, `created_at`, `updated_at`
) VALUES (
    '绿城花园', '李房东', 1.15, 3.20, 5.80, 0.75, 1.80, 3.80,
    'FIXED_MONTH_START', 1, NOW(), NOW()
);

-- =====================================================
-- 4. 插入用户楼宇关联数据
-- =====================================================

-- 超级管理员关联所有楼宇
INSERT INTO `user_buildings` (`user_id`, `building_id`, `created_at`) VALUES
(1, 1, NOW()),
(1, 2, NOW());

-- 管理员关联楼宇
INSERT INTO `user_buildings` (`user_id`, `building_id`, `created_at`) VALUES
(2, 1, NOW()),
(2, 2, NOW());

-- =====================================================
-- 5. 插入示例房间数据（可选）
-- =====================================================

-- 阳光公寓的房间
INSERT INTO `rooms` (
    `room_number`, `rent`, `default_deposit`, `electricity_unit_price`,
    `water_unit_price`, `hot_water_unit_price`, `building_id`,
    `rental_status`, `created_by`, `created_at`, `updated_at`
) VALUES
('101', 1200.00, 1500.00, NULL, NULL, NULL, 1, 'VACANT', 1, NOW(), NOW()),
('102', 1300.00, 1600.00, NULL, NULL, NULL, 1, 'VACANT', 1, NOW(), NOW()),
('103', 1250.00, 1550.00, NULL, NULL, NULL, 1, 'RENTED', 1, NOW(), NOW()),
('201', 1400.00, 1700.00, NULL, NULL, NULL, 1, 'VACANT', 1, NOW(), NOW()),
('202', 1350.00, 1650.00, NULL, NULL, NULL, 1, 'RENTED', 1, NOW(), NOW());

-- 绿城花园的房间
INSERT INTO `rooms` (
    `room_number`, `rent`, `default_deposit`, `electricity_unit_price`,
    `water_unit_price`, `hot_water_unit_price`, `building_id`,
    `rental_status`, `created_by`, `created_at`, `updated_at`
) VALUES
('A101', 1100.00, 1400.00, NULL, NULL, NULL, 2, 'VACANT', 1, NOW(), NOW()),
('A102', 1150.00, 1450.00, NULL, NULL, NULL, 2, 'RENTED', 1, NOW(), NOW()),
('A201', 1200.00, 1500.00, NULL, NULL, NULL, 2, 'VACANT', 1, NOW(), NOW()),
('A202', 1180.00, 1480.00, NULL, NULL, NULL, 2, 'MAINTENANCE', 1, NOW(), NOW());
