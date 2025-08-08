-- =====================================================
-- 房屋租赁管理系统 - 视图创建脚本
-- 版本: 1.0
-- 创建时间: 2025-08-06
-- 描述: 创建常用的数据库视图以简化查询
-- =====================================================

USE `rent_house`;

-- =====================================================
-- 1. 房间详情视图
-- =====================================================
DROP VIEW IF EXISTS `v_room_details`;
CREATE VIEW `v_room_details` AS
SELECT 
    r.id AS room_id,
    r.room_number,
    r.rent,
    r.default_deposit,
    r.rental_status,
    r.electricity_unit_price AS room_electricity_price,
    r.water_unit_price AS room_water_price,
    r.hot_water_unit_price AS room_hot_water_price,
    
    b.id AS building_id,
    b.building_name,
    b.landlord_name,
    b.electricity_unit_price AS building_electricity_price,
    b.water_unit_price AS building_water_price,
    b.hot_water_unit_price AS building_hot_water_price,
    b.rent_collection_method,
    
    -- 实际使用的单价（房间优先，否则使用楼宇设置）
    COALESCE(r.electricity_unit_price, b.electricity_unit_price) AS effective_electricity_price,
    COALESCE(r.water_unit_price, b.water_unit_price) AS effective_water_price,
    COALESCE(r.hot_water_unit_price, b.hot_water_unit_price) AS effective_hot_water_price,
    
    u.username AS created_by_username,
    u.full_name AS created_by_name,
    r.created_at,
    r.updated_at
FROM `rooms` r
LEFT JOIN `buildings` b ON r.building_id = b.id
LEFT JOIN `users` u ON r.created_by = u.id;

-- =====================================================
-- 2. 水电表记录详情视图
-- =====================================================
DROP VIEW IF EXISTS `v_utility_reading_details`;
CREATE VIEW `v_utility_reading_details` AS
SELECT 
    ur.id AS reading_id,
    ur.room_id,
    r.room_number,
    b.building_name,
    ur.reading_date,
    ur.reading_time,
    
    -- 电表信息
    ur.electricity_reading,
    ur.electricity_previous_reading,
    ur.electricity_usage,
    
    -- 水表信息
    ur.water_reading,
    ur.water_previous_reading,
    ur.water_usage,
    
    -- 热水表信息
    ur.hot_water_reading,
    ur.hot_water_previous_reading,
    ur.hot_water_usage,
    
    -- 抄表信息
    ur.meter_reader,
    ur.reading_type,
    ur.reading_status,
    ur.notes,
    ur.photos,
    
    -- 创建信息
    u.username AS created_by_username,
    u.full_name AS created_by_name,
    ur.created_at,
    ur.updated_at
FROM `utility_readings` ur
LEFT JOIN `rooms` r ON ur.room_id = r.id
LEFT JOIN `buildings` b ON r.building_id = b.id
LEFT JOIN `users` u ON ur.created_by = u.id;

-- =====================================================
-- 3. 账单详情视图
-- =====================================================
DROP VIEW IF EXISTS `v_bill_details`;
CREATE VIEW `v_bill_details` AS
SELECT 
    eb.id AS bill_id,
    eb.room_id,
    r.room_number,
    b.building_name,
    b.landlord_name,
    eb.bill_month,
    eb.bill_date,
    
    -- 费用明细
    eb.rent,
    eb.deposit,
    eb.electricity_unit_price,
    eb.electricity_usage,
    eb.electricity_amount,
    eb.water_unit_price,
    eb.water_usage,
    eb.water_amount,
    eb.hot_water_unit_price,
    eb.hot_water_usage,
    eb.hot_water_amount,
    eb.other_fees,
    eb.other_fees_description,
    eb.total_amount,
    
    -- 状态信息
    eb.bill_status,
    eb.notes,
    
    -- 创建信息
    u.username AS created_by_username,
    u.full_name AS created_by_name,
    eb.created_at,
    eb.updated_at
FROM `estimated_bills` eb
LEFT JOIN `rooms` r ON eb.room_id = r.id
LEFT JOIN `buildings` b ON r.building_id = b.id
LEFT JOIN `users` u ON eb.created_by = u.id;

-- =====================================================
-- 4. 楼宇统计视图
-- =====================================================
DROP VIEW IF EXISTS `v_building_statistics`;
CREATE VIEW `v_building_statistics` AS
SELECT
    b.id AS building_id,
    b.building_name,
    b.landlord_name,

    -- 房间统计
    COUNT(r.id) AS total_rooms,
    SUM(CASE WHEN r.rental_status = 'VACANT' THEN 1 ELSE 0 END) AS vacant_rooms,
    SUM(CASE WHEN r.rental_status = 'RENTED' THEN 1 ELSE 0 END) AS rented_rooms,
    SUM(CASE WHEN r.rental_status = 'MAINTENANCE' THEN 1 ELSE 0 END) AS maintenance_rooms,
    SUM(CASE WHEN r.rental_status = 'RESERVED' THEN 1 ELSE 0 END) AS reserved_rooms,

    -- 出租率
    ROUND(
        SUM(CASE WHEN r.rental_status = 'RENTED' THEN 1 ELSE 0 END) * 100.0 / COUNT(r.id),
        2
    ) AS occupancy_rate,

    -- 租金统计
    AVG(r.rent) AS avg_rent,
    MIN(r.rent) AS min_rent,
    MAX(r.rent) AS max_rent,
    SUM(CASE WHEN r.rental_status = 'RENTED' THEN r.rent ELSE 0 END) AS total_monthly_income,

    -- 押金统计
    AVG(r.default_deposit) AS avg_deposit,
    SUM(CASE WHEN r.rental_status = 'RENTED' THEN r.default_deposit ELSE 0 END) AS total_deposits,

    b.created_at,
    b.updated_at
FROM `buildings` b
LEFT JOIN `rooms` r ON b.id = r.building_id
GROUP BY b.id, b.building_name, b.landlord_name, b.created_at, b.updated_at;

-- =====================================================
-- 5. 月度账单汇总视图
-- =====================================================
DROP VIEW IF EXISTS `v_monthly_bill_summary`;
CREATE VIEW `v_monthly_bill_summary` AS
SELECT
    eb.bill_month,
    COUNT(eb.id) AS total_bills,
    SUM(CASE WHEN eb.bill_status = 'GENERATED' THEN 1 ELSE 0 END) AS generated_bills,
    SUM(CASE WHEN eb.bill_status = 'CONFIRMED' THEN 1 ELSE 0 END) AS confirmed_bills,
    SUM(CASE WHEN eb.bill_status = 'SENT' THEN 1 ELSE 0 END) AS sent_bills,
    SUM(CASE WHEN eb.bill_status = 'PAID' THEN 1 ELSE 0 END) AS paid_bills,
    SUM(CASE WHEN eb.bill_status = 'CANCELLED' THEN 1 ELSE 0 END) AS cancelled_bills,

    -- 金额统计
    SUM(eb.total_amount) AS total_amount,
    SUM(eb.rent) AS total_rent,
    SUM(eb.deposit) AS total_deposit,
    SUM(eb.electricity_amount) AS total_electricity,
    SUM(eb.water_amount) AS total_water,
    SUM(eb.hot_water_amount) AS total_hot_water,
    SUM(eb.other_fees) AS total_other_fees,

    -- 已支付金额
    SUM(CASE WHEN eb.bill_status = 'PAID' THEN eb.total_amount ELSE 0 END) AS paid_amount,

    -- 未支付金额
    SUM(CASE WHEN eb.bill_status != 'PAID' AND eb.bill_status != 'CANCELLED' THEN eb.total_amount ELSE 0 END) AS unpaid_amount

FROM `estimated_bills` eb
GROUP BY eb.bill_month
ORDER BY eb.bill_month DESC;
