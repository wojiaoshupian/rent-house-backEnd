-- =====================================================
-- 房屋租赁管理系统 - 表结构创建脚本
-- 版本: 1.0
-- 创建时间: 2025-08-06
-- 描述: 创建所有数据表结构
-- PostgreSQL 16 兼容版本
-- =====================================================

-- 创建数据库（如果不存在）
-- CREATE DATABASE rent_house;

-- 连接到数据库
-- \c rent_house;

-- =====================================================
-- 1. 用户表 (users)
-- =====================================================
DROP TABLE IF EXISTS users CASCADE;
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(11) NOT NULL UNIQUE,
    email VARCHAR(100),
    full_name VARCHAR(50),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'INACTIVE', 'LOCKED')),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 创建索引
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_phone ON users(phone);
CREATE INDEX idx_users_status ON users(status);
CREATE INDEX idx_users_created_at ON users(created_at);

-- 添加注释
COMMENT ON TABLE users IS '用户表';
COMMENT ON COLUMN users.id IS '用户ID';
COMMENT ON COLUMN users.username IS '用户名';
COMMENT ON COLUMN users.password IS '密码（加密）';
COMMENT ON COLUMN users.phone IS '手机号码';
COMMENT ON COLUMN users.email IS '邮箱地址';
COMMENT ON COLUMN users.full_name IS '真实姓名';
COMMENT ON COLUMN users.status IS '用户状态';
COMMENT ON COLUMN users.created_at IS '创建时间';
COMMENT ON COLUMN users.updated_at IS '更新时间';

-- =====================================================
-- 2. 用户角色表 (user_roles)
-- =====================================================
DROP TABLE IF EXISTS user_roles CASCADE;
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role VARCHAR(50) NOT NULL,
    PRIMARY KEY (user_id, role),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

COMMENT ON TABLE user_roles IS '用户角色关联表';
COMMENT ON COLUMN user_roles.user_id IS '用户ID';
COMMENT ON COLUMN user_roles.role IS '角色名称';

-- =====================================================
-- 3. 楼宇表 (buildings)
-- =====================================================
DROP TABLE IF EXISTS buildings CASCADE;
CREATE TABLE buildings (
    id BIGSERIAL PRIMARY KEY,
    building_name VARCHAR(100) NOT NULL,
    landlord_name VARCHAR(50) NOT NULL,
    electricity_unit_price DECIMAL(10,2) NOT NULL,
    water_unit_price DECIMAL(10,2) NOT NULL,
    hot_water_unit_price DECIMAL(10,2),
    electricity_cost DECIMAL(10,2),
    water_cost DECIMAL(10,2),
    hot_water_cost DECIMAL(10,2),
    rent_collection_method VARCHAR(20) NOT NULL DEFAULT 'FIXED_MONTH_START' CHECK (rent_collection_method IN ('FIXED_MONTH_START', 'FLEXIBLE')),
    created_by BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES users(id)
);

-- 创建索引
CREATE INDEX idx_buildings_building_name ON buildings(building_name);
CREATE INDEX idx_buildings_created_by ON buildings(created_by);
CREATE INDEX idx_buildings_created_at ON buildings(created_at);

COMMENT ON TABLE buildings IS '楼宇表';
COMMENT ON COLUMN buildings.id IS '楼宇ID';
COMMENT ON COLUMN buildings.building_name IS '楼宇名称';
COMMENT ON COLUMN buildings.landlord_name IS '房东姓名';
COMMENT ON COLUMN buildings.electricity_unit_price IS '电费单价（元/度）';
COMMENT ON COLUMN buildings.water_unit_price IS '水费单价（元/吨）';
COMMENT ON COLUMN buildings.hot_water_unit_price IS '热水单价（元/吨）';
COMMENT ON COLUMN buildings.electricity_cost IS '电费成本（元/度）';
COMMENT ON COLUMN buildings.water_cost IS '水费成本（元/吨）';
COMMENT ON COLUMN buildings.hot_water_cost IS '热水费成本（元/吨）';
COMMENT ON COLUMN buildings.rent_collection_method IS '租金收取方式';
COMMENT ON COLUMN buildings.created_by IS '创建者用户ID';
COMMENT ON COLUMN buildings.created_at IS '创建时间';
COMMENT ON COLUMN buildings.updated_at IS '更新时间';

-- =====================================================
-- 4. 用户楼宇关联表 (user_buildings)
-- =====================================================
DROP TABLE IF EXISTS user_buildings CASCADE;
CREATE TABLE user_buildings (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    building_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, building_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (building_id) REFERENCES buildings(id) ON DELETE CASCADE
);

-- 创建索引
CREATE INDEX idx_user_buildings_user_id ON user_buildings(user_id);
CREATE INDEX idx_user_buildings_building_id ON user_buildings(building_id);

COMMENT ON TABLE user_buildings IS '用户楼宇关联表';
COMMENT ON COLUMN user_buildings.id IS '关联ID';
COMMENT ON COLUMN user_buildings.user_id IS '用户ID';
COMMENT ON COLUMN user_buildings.building_id IS '楼宇ID';
COMMENT ON COLUMN user_buildings.created_at IS '创建时间';

-- =====================================================
-- 5. 房间表 (rooms)
-- =====================================================
DROP TABLE IF EXISTS rooms CASCADE;
CREATE TABLE rooms (
    id BIGSERIAL PRIMARY KEY,
    room_number VARCHAR(50) NOT NULL,
    rent DECIMAL(10,2) NOT NULL,
    default_deposit DECIMAL(10,2) NOT NULL,
    electricity_unit_price DECIMAL(10,2),
    water_unit_price DECIMAL(10,2),
    hot_water_unit_price DECIMAL(10,2),
    building_id BIGINT NOT NULL,
    rental_status VARCHAR(20) NOT NULL DEFAULT 'VACANT' CHECK (rental_status IN ('VACANT', 'RENTED', 'MAINTENANCE', 'RESERVED')),
    created_by BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(room_number, building_id),
    FOREIGN KEY (building_id) REFERENCES buildings(id) ON DELETE CASCADE,
    FOREIGN KEY (created_by) REFERENCES users(id)
);

-- 创建索引
CREATE INDEX idx_rooms_building_id ON rooms(building_id);
CREATE INDEX idx_rooms_rental_status ON rooms(rental_status);
CREATE INDEX idx_rooms_created_by ON rooms(created_by);
CREATE INDEX idx_rooms_created_at ON rooms(created_at);

COMMENT ON TABLE rooms IS '房间表';
COMMENT ON COLUMN rooms.id IS '房间ID';
COMMENT ON COLUMN rooms.room_number IS '房间号';
COMMENT ON COLUMN rooms.rent IS '月租金（元）';
COMMENT ON COLUMN rooms.default_deposit IS '默认押金（元）';
COMMENT ON COLUMN rooms.electricity_unit_price IS '房间电费单价（元/度）';
COMMENT ON COLUMN rooms.water_unit_price IS '房间水费单价（元/吨）';
COMMENT ON COLUMN rooms.hot_water_unit_price IS '房间热水单价（元/吨）';
COMMENT ON COLUMN rooms.building_id IS '所属楼宇ID';
COMMENT ON COLUMN rooms.rental_status IS '出租状态';
COMMENT ON COLUMN rooms.created_by IS '创建者用户ID';
COMMENT ON COLUMN rooms.created_at IS '创建时间';
COMMENT ON COLUMN rooms.updated_at IS '更新时间';

-- =====================================================
-- 6. 水电表记录表 (utility_readings)
-- =====================================================
DROP TABLE IF EXISTS utility_readings CASCADE;
CREATE TABLE utility_readings (
    id BIGSERIAL PRIMARY KEY,
    room_id BIGINT NOT NULL,
    reading_date DATE NOT NULL,
    reading_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- 电表相关
    electricity_reading DECIMAL(10,2) NOT NULL,
    electricity_previous_reading DECIMAL(10,2) DEFAULT 0.00,
    electricity_usage DECIMAL(10,2),

    -- 水表相关
    water_reading DECIMAL(10,2) NOT NULL,
    water_previous_reading DECIMAL(10,2) DEFAULT 0.00,
    water_usage DECIMAL(10,2),

    -- 热水表相关
    hot_water_reading DECIMAL(10,2) DEFAULT 0.00,
    hot_water_previous_reading DECIMAL(10,2) DEFAULT 0.00,
    hot_water_usage DECIMAL(10,2),

    -- 抄表信息
    meter_reader VARCHAR(100) NOT NULL,
    reading_type VARCHAR(20) DEFAULT 'MANUAL' CHECK (reading_type IN ('MANUAL', 'AUTO', 'ESTIMATED')),
    reading_status VARCHAR(20) DEFAULT 'PENDING' CHECK (reading_status IN ('PENDING', 'CONFIRMED', 'DISPUTED')),
    notes TEXT,
    photos TEXT, -- 将 JSON 转为 TEXT

    -- 系统字段
    created_by BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    UNIQUE(room_id, reading_date),
    FOREIGN KEY (room_id) REFERENCES rooms(id) ON DELETE CASCADE,
    FOREIGN KEY (created_by) REFERENCES users(id)
);

-- 创建索引
CREATE INDEX idx_utility_readings_room_id ON utility_readings(room_id);
CREATE INDEX idx_utility_readings_reading_date ON utility_readings(reading_date);
CREATE INDEX idx_utility_readings_reading_time ON utility_readings(reading_time);
CREATE INDEX idx_utility_readings_room_date ON utility_readings(room_id, reading_date);
CREATE INDEX idx_utility_readings_reading_status ON utility_readings(reading_status);
CREATE INDEX idx_utility_readings_created_by ON utility_readings(created_by);

COMMENT ON TABLE utility_readings IS '水电表记录表';
COMMENT ON COLUMN utility_readings.id IS '记录ID';
COMMENT ON COLUMN utility_readings.room_id IS '房间ID';
COMMENT ON COLUMN utility_readings.reading_date IS '抄表日期';
COMMENT ON COLUMN utility_readings.reading_time IS '抄表时间';
COMMENT ON COLUMN utility_readings.electricity_reading IS '电表读数（度）';
COMMENT ON COLUMN utility_readings.electricity_previous_reading IS '上次电表读数（度）';
COMMENT ON COLUMN utility_readings.electricity_usage IS '本期用电量（度）';
COMMENT ON COLUMN utility_readings.water_reading IS '水表读数（吨）';
COMMENT ON COLUMN utility_readings.water_previous_reading IS '上次水表读数（吨）';
COMMENT ON COLUMN utility_readings.water_usage IS '本期用水量（吨）';
COMMENT ON COLUMN utility_readings.hot_water_reading IS '热水表读数（吨）';
COMMENT ON COLUMN utility_readings.hot_water_previous_reading IS '上次热水表读数（吨）';
COMMENT ON COLUMN utility_readings.hot_water_usage IS '本期热水用量（吨）';
COMMENT ON COLUMN utility_readings.meter_reader IS '抄表人';
COMMENT ON COLUMN utility_readings.reading_type IS '抄表类型';
COMMENT ON COLUMN utility_readings.reading_status IS '读数状态';
COMMENT ON COLUMN utility_readings.notes IS '备注信息';
COMMENT ON COLUMN utility_readings.photos IS '抄表照片URL列表';
COMMENT ON COLUMN utility_readings.created_by IS '创建者用户ID';
COMMENT ON COLUMN utility_readings.created_at IS '创建时间';
COMMENT ON COLUMN utility_readings.updated_at IS '更新时间';

-- =====================================================
-- 7. 账单表 (estimated_bills)
-- =====================================================
DROP TABLE IF EXISTS estimated_bills CASCADE;
CREATE TABLE estimated_bills (
    id BIGSERIAL PRIMARY KEY,
    room_id BIGINT NOT NULL,
    bill_month VARCHAR(7) NOT NULL,
    bill_date DATE NOT NULL,

    -- 费用项目
    rent DECIMAL(10,2),
    deposit DECIMAL(10,2) DEFAULT 0.00,

    -- 电费相关
    electricity_unit_price DECIMAL(8,4),
    electricity_usage DECIMAL(10,2),
    electricity_amount DECIMAL(10,2),

    -- 水费相关
    water_unit_price DECIMAL(8,4),
    water_usage DECIMAL(10,2),
    water_amount DECIMAL(10,2),

    -- 热水费相关
    hot_water_unit_price DECIMAL(8,4),
    hot_water_usage DECIMAL(10,2),
    hot_water_amount DECIMAL(10,2),

    -- 其他费用
    other_fees DECIMAL(10,2) DEFAULT 0.00,
    other_fees_description TEXT,

    -- 总计
    total_amount DECIMAL(10,2),

    -- 账单状态
    bill_status VARCHAR(20) DEFAULT 'GENERATED' CHECK (bill_status IN ('GENERATED', 'CONFIRMED', 'SENT', 'PAID', 'CANCELLED')),
    notes TEXT,

    -- 系统字段
    created_by BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    UNIQUE(room_id, bill_month),
    FOREIGN KEY (room_id) REFERENCES rooms(id) ON DELETE CASCADE,
    FOREIGN KEY (created_by) REFERENCES users(id)
);

-- 创建索引
CREATE INDEX idx_estimated_bills_room_id ON estimated_bills(room_id);
CREATE INDEX idx_estimated_bills_bill_month ON estimated_bills(bill_month);
CREATE INDEX idx_estimated_bills_bill_date ON estimated_bills(bill_date);
CREATE INDEX idx_estimated_bills_bill_status ON estimated_bills(bill_status);
CREATE INDEX idx_estimated_bills_created_by ON estimated_bills(created_by);
CREATE INDEX idx_estimated_bills_created_at ON estimated_bills(created_at);

COMMENT ON TABLE estimated_bills IS '账单表';
COMMENT ON COLUMN estimated_bills.id IS '账单ID';
COMMENT ON COLUMN estimated_bills.room_id IS '房间ID';
COMMENT ON COLUMN estimated_bills.bill_month IS '账单年月（YYYY-MM）';
COMMENT ON COLUMN estimated_bills.bill_date IS '账单生成日期';
COMMENT ON COLUMN estimated_bills.rent IS '房租（元）';
COMMENT ON COLUMN estimated_bills.deposit IS '押金（元）';
COMMENT ON COLUMN estimated_bills.electricity_unit_price IS '电费单价（元/度）';
COMMENT ON COLUMN estimated_bills.electricity_usage IS '用电量（度）';
COMMENT ON COLUMN estimated_bills.electricity_amount IS '电费金额（元）';
COMMENT ON COLUMN estimated_bills.water_unit_price IS '水费单价（元/吨）';
COMMENT ON COLUMN estimated_bills.water_usage IS '用水量（吨）';
COMMENT ON COLUMN estimated_bills.water_amount IS '水费金额（元）';
COMMENT ON COLUMN estimated_bills.hot_water_unit_price IS '热水单价（元/吨）';
COMMENT ON COLUMN estimated_bills.hot_water_usage IS '热水用量（吨）';
COMMENT ON COLUMN estimated_bills.hot_water_amount IS '热水费金额（元）';
COMMENT ON COLUMN estimated_bills.other_fees IS '其他费用（元）';
COMMENT ON COLUMN estimated_bills.other_fees_description IS '其他费用说明';
COMMENT ON COLUMN estimated_bills.total_amount IS '总金额（元）';
COMMENT ON COLUMN estimated_bills.bill_status IS '账单状态';
COMMENT ON COLUMN estimated_bills.notes IS '备注信息';
COMMENT ON COLUMN estimated_bills.created_by IS '创建者用户ID';
COMMENT ON COLUMN estimated_bills.created_at IS '创建时间';
COMMENT ON COLUMN estimated_bills.updated_at IS '更新时间'; 