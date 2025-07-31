-- 创建楼宇表
CREATE TABLE buildings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '楼宇ID',
    building_name VARCHAR(100) NOT NULL COMMENT '楼宇名称',
    landlord_name VARCHAR(50) NOT NULL COMMENT '房东名称',
    electricity_unit_price DECIMAL(10,2) NOT NULL COMMENT '电费单价(元/度)',
    water_unit_price DECIMAL(10,2) NOT NULL COMMENT '水费单价(元/吨)',
    hot_water_unit_price DECIMAL(10,2) COMMENT '热水单价(元/吨)',
    electricity_cost DECIMAL(10,2) COMMENT '电费成本(元/度)',
    water_cost DECIMAL(10,2) COMMENT '水费成本(元/吨)',
    hot_water_cost DECIMAL(10,2) COMMENT '热水费成本(元/吨)',
    rent_collection_method ENUM('FIXED_MONTH_START', 'RENTAL_START_DATE') NOT NULL DEFAULT 'FIXED_MONTH_START' COMMENT '收租方式',
    created_by BIGINT NOT NULL COMMENT '创建者用户ID',
    created_at DATETIME NOT NULL COMMENT '创建时间',
    updated_at DATETIME COMMENT '更新时间',
    INDEX idx_created_by (created_by),
    INDEX idx_building_name (building_name),
    INDEX idx_landlord_name (landlord_name)
) COMMENT '楼宇信息表';

-- 创建用户楼宇关联表
CREATE TABLE user_buildings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '关联ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    building_id BIGINT NOT NULL COMMENT '楼宇ID',
    created_at DATETIME NOT NULL COMMENT '创建时间',
    UNIQUE KEY uk_user_building (user_id, building_id),
    INDEX idx_user_id (user_id),
    INDEX idx_building_id (building_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (building_id) REFERENCES buildings(id) ON DELETE CASCADE
) COMMENT '用户楼宇关联表'; 