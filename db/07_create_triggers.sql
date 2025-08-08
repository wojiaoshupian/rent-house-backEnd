-- =====================================================
-- 房屋租赁管理系统 - 触发器创建脚本
-- 版本: 1.0
-- 创建时间: 2025-08-06
-- 描述: 创建数据库触发器以自动维护数据一致性
-- =====================================================

USE `rent_house`;

DELIMITER $$

-- =====================================================
-- 1. 水电表记录插入时自动更新前期读数
-- =====================================================
DROP TRIGGER IF EXISTS `tr_utility_readings_before_insert`$$
CREATE TRIGGER `tr_utility_readings_before_insert`
    BEFORE INSERT ON `utility_readings`
    FOR EACH ROW
BEGIN
    DECLARE v_prev_electricity DECIMAL(10,2) DEFAULT 0.00;
    DECLARE v_prev_water DECIMAL(10,2) DEFAULT 0.00;
    DECLARE v_prev_hot_water DECIMAL(10,2) DEFAULT 0.00;
    
    -- 获取该房间上一次的读数
    SELECT 
        COALESCE(electricity_reading, 0.00),
        COALESCE(water_reading, 0.00),
        COALESCE(hot_water_reading, 0.00)
    INTO v_prev_electricity, v_prev_water, v_prev_hot_water
    FROM utility_readings 
    WHERE room_id = NEW.room_id 
      AND reading_date < NEW.reading_date
    ORDER BY reading_date DESC, reading_time DESC
    LIMIT 1;
    
    -- 设置前期读数
    SET NEW.electricity_previous_reading = v_prev_electricity;
    SET NEW.water_previous_reading = v_prev_water;
    SET NEW.hot_water_previous_reading = v_prev_hot_water;
    
    -- 确保抄表时间不为空
    IF NEW.reading_time IS NULL THEN
        SET NEW.reading_time = NOW();
    END IF;
    
END$$

-- =====================================================
-- 2. 水电表记录更新时自动更新后续记录的前期读数
-- =====================================================
DROP TRIGGER IF EXISTS `tr_utility_readings_after_update`$$
CREATE TRIGGER `tr_utility_readings_after_update`
    AFTER UPDATE ON `utility_readings`
    FOR EACH ROW
BEGIN
    -- 如果电表读数发生变化，更新后续记录的前期电表读数
    IF OLD.electricity_reading != NEW.electricity_reading THEN
        UPDATE utility_readings 
        SET electricity_previous_reading = NEW.electricity_reading
        WHERE room_id = NEW.room_id 
          AND reading_date > NEW.reading_date
          AND electricity_previous_reading = OLD.electricity_reading;
    END IF;
    
    -- 如果水表读数发生变化，更新后续记录的前期水表读数
    IF OLD.water_reading != NEW.water_reading THEN
        UPDATE utility_readings 
        SET water_previous_reading = NEW.water_reading
        WHERE room_id = NEW.room_id 
          AND reading_date > NEW.reading_date
          AND water_previous_reading = OLD.water_reading;
    END IF;
    
    -- 如果热水表读数发生变化，更新后续记录的前期热水表读数
    IF OLD.hot_water_reading != NEW.hot_water_reading THEN
        UPDATE utility_readings 
        SET hot_water_previous_reading = NEW.hot_water_reading
        WHERE room_id = NEW.room_id 
          AND reading_date > NEW.reading_date
          AND hot_water_previous_reading = OLD.hot_water_reading;
    END IF;
    
END$$

-- =====================================================
-- 3. 账单插入时自动计算总金额
-- =====================================================
DROP TRIGGER IF EXISTS `tr_estimated_bills_before_insert`$$
CREATE TRIGGER `tr_estimated_bills_before_insert`
    BEFORE INSERT ON `estimated_bills`
    FOR EACH ROW
BEGIN
    -- 计算电费金额
    IF NEW.electricity_usage IS NOT NULL AND NEW.electricity_unit_price IS NOT NULL THEN
        SET NEW.electricity_amount = NEW.electricity_usage * NEW.electricity_unit_price;
    ELSE
        SET NEW.electricity_amount = 0.00;
    END IF;
    
    -- 计算水费金额
    IF NEW.water_usage IS NOT NULL AND NEW.water_unit_price IS NOT NULL THEN
        SET NEW.water_amount = NEW.water_usage * NEW.water_unit_price;
    ELSE
        SET NEW.water_amount = 0.00;
    END IF;
    
    -- 计算热水费金额
    IF NEW.hot_water_usage IS NOT NULL AND NEW.hot_water_unit_price IS NOT NULL THEN
        SET NEW.hot_water_amount = NEW.hot_water_usage * NEW.hot_water_unit_price;
    ELSE
        SET NEW.hot_water_amount = 0.00;
    END IF;
    
    -- 计算总金额
    SET NEW.total_amount = 
        COALESCE(NEW.rent, 0.00) + 
        COALESCE(NEW.deposit, 0.00) + 
        COALESCE(NEW.electricity_amount, 0.00) + 
        COALESCE(NEW.water_amount, 0.00) + 
        COALESCE(NEW.hot_water_amount, 0.00) + 
        COALESCE(NEW.other_fees, 0.00);
        
    -- 确保账单日期不为空
    IF NEW.bill_date IS NULL THEN
        SET NEW.bill_date = CURDATE();
    END IF;
    
END$$

-- =====================================================
-- 4. 账单更新时自动重新计算总金额
-- =====================================================
DROP TRIGGER IF EXISTS `tr_estimated_bills_before_update`$$
CREATE TRIGGER `tr_estimated_bills_before_update`
    BEFORE UPDATE ON `estimated_bills`
    FOR EACH ROW
BEGIN
    -- 计算电费金额
    IF NEW.electricity_usage IS NOT NULL AND NEW.electricity_unit_price IS NOT NULL THEN
        SET NEW.electricity_amount = NEW.electricity_usage * NEW.electricity_unit_price;
    ELSE
        SET NEW.electricity_amount = 0.00;
    END IF;
    
    -- 计算水费金额
    IF NEW.water_usage IS NOT NULL AND NEW.water_unit_price IS NOT NULL THEN
        SET NEW.water_amount = NEW.water_usage * NEW.water_unit_price;
    ELSE
        SET NEW.water_amount = 0.00;
    END IF;
    
    -- 计算热水费金额
    IF NEW.hot_water_usage IS NOT NULL AND NEW.hot_water_unit_price IS NOT NULL THEN
        SET NEW.hot_water_amount = NEW.hot_water_usage * NEW.hot_water_unit_price;
    ELSE
        SET NEW.hot_water_amount = 0.00;
    END IF;
    
    -- 计算总金额
    SET NEW.total_amount = 
        COALESCE(NEW.rent, 0.00) + 
        COALESCE(NEW.deposit, 0.00) + 
        COALESCE(NEW.electricity_amount, 0.00) + 
        COALESCE(NEW.water_amount, 0.00) + 
        COALESCE(NEW.hot_water_amount, 0.00) + 
        COALESCE(NEW.other_fees, 0.00);
        
END$$

DELIMITER ;
