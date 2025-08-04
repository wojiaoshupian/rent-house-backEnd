-- 创建房间表
CREATE TABLE rooms (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '房间ID',
    room_number VARCHAR(50) NOT NULL COMMENT '房号',
    rent DECIMAL(10,2) NOT NULL COMMENT '租金(元/月)',
    default_deposit DECIMAL(10,2) NOT NULL COMMENT '默认押金(元)',
    electricity_unit_price DECIMAL(10,2) COMMENT '电费单价(元/度)，为空则使用楼宇设置',
    water_unit_price DECIMAL(10,2) COMMENT '水费单价(元/吨)，为空则使用楼宇设置',
    hot_water_unit_price DECIMAL(10,2) COMMENT '热水单价(元/吨)，为空则使用楼宇设置',
    building_id BIGINT NOT NULL COMMENT '楼宇ID',
    created_by BIGINT NOT NULL COMMENT '创建者用户ID',
    created_at DATETIME NOT NULL COMMENT '创建时间',
    updated_at DATETIME COMMENT '更新时间',
    
    -- 索引
    INDEX idx_building_id (building_id),
    INDEX idx_room_number (room_number),
    INDEX idx_created_by (created_by),
    INDEX idx_created_at (created_at),
    
    -- 唯一约束：同一楼宇中房号不能重复
    UNIQUE KEY uk_room_building (room_number, building_id),
    
    -- 外键约束
    FOREIGN KEY (building_id) REFERENCES buildings(id) ON DELETE CASCADE,
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE RESTRICT
) COMMENT '房间信息表';

-- 插入示例数据
INSERT INTO rooms (room_number, rent, default_deposit, electricity_unit_price, water_unit_price, hot_water_unit_price, building_id, created_by, created_at) VALUES
-- 楼宇1的房间（使用自定义费用单价）
('101', 1500.00, 3000.00, 1.2, 3.5, 6.0, 3, 1, NOW()),
('102', 1600.00, 3200.00, NULL, NULL, NULL, 3, 1, NOW()), -- 使用楼宇设置
('103', 1550.00, 3100.00, 1.3, NULL, 5.5, 3, 1, NOW()), -- 部分使用自定义设置

-- 楼宇2的房间
('201', 1800.00, 3600.00, NULL, NULL, NULL, 4, 1, NOW()),
('202', 1750.00, 3500.00, 1.1, 3.2, NULL, 4, 1, NOW()),
('203', 1900.00, 3800.00, NULL, 3.8, 6.2, 4, 1, NOW()),

-- 楼宇3的房间
('301', 2000.00, 4000.00, NULL, NULL, NULL, 5, 1, NOW()),
('302', 2100.00, 4200.00, 1.4, NULL, NULL, 5, 1, NOW()),

-- 楼宇4的房间
('A01', 1400.00, 2800.00, NULL, NULL, NULL, 6, 1, NOW()),
('A02', 1450.00, 2900.00, 1.0, 3.0, 5.0, 6, 1, NOW()),
('B01', 1500.00, 3000.00, NULL, NULL, NULL, 6, 1, NOW()),

-- 楼宇5的房间
('1001', 2500.00, 5000.00, NULL, NULL, NULL, 7, 1, NOW()),
('1002', 2600.00, 5200.00, 1.5, 4.0, 7.0, 7, 1, NOW()),
('1003', 2550.00, 5100.00, NULL, 3.8, NULL, 7, 1, NOW());
