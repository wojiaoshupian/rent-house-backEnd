-- =====================================================
-- 房屋租赁管理系统 - 存储过程创建脚本
-- 版本: 1.0
-- 创建时间: 2025-08-06
-- 描述: 创建常用的存储过程以简化复杂操作
-- =====================================================

USE `rent_house_management`;

DELIMITER $$

-- =====================================================
-- 1. 自动更新水电表前期读数的存储过程
-- =====================================================
DROP PROCEDURE IF EXISTS `sp_update_previous_readings`$$
CREATE PROCEDURE `sp_update_previous_readings`(
    IN p_room_id BIGINT,
    IN p_reading_date DATE
)
BEGIN
    DECLARE v_prev_electricity DECIMAL(10,2) DEFAULT 0.00;
    DECLARE v_prev_water DECIMAL(10,2) DEFAULT 0.00;
    DECLARE v_prev_hot_water DECIMAL(10,2) DEFAULT 0.00;
    
    -- 获取上一次的读数
    SELECT 
        COALESCE(electricity_reading, 0.00),
        COALESCE(water_reading, 0.00),
        COALESCE(hot_water_reading, 0.00)
    INTO v_prev_electricity, v_prev_water, v_prev_hot_water
    FROM utility_readings 
    WHERE room_id = p_room_id 
      AND reading_date < p_reading_date
    ORDER BY reading_date DESC, reading_time DESC
    LIMIT 1;
    
    -- 更新当前记录的前期读数
    UPDATE utility_readings 
    SET 
        electricity_previous_reading = v_prev_electricity,
        water_previous_reading = v_prev_water,
        hot_water_previous_reading = v_prev_hot_water
    WHERE room_id = p_room_id 
      AND reading_date = p_reading_date;
      
END$$

-- =====================================================
-- 2. 批量生成月度账单的存储过程
-- =====================================================
DROP PROCEDURE IF EXISTS `sp_generate_monthly_bills`$$
CREATE PROCEDURE `sp_generate_monthly_bills`(
    IN p_bill_month VARCHAR(7),
    IN p_created_by BIGINT
)
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE v_room_id BIGINT;
    DECLARE v_rent DECIMAL(10,2);
    DECLARE v_deposit DECIMAL(10,2);
    DECLARE v_electricity_price DECIMAL(8,4);
    DECLARE v_water_price DECIMAL(8,4);
    DECLARE v_hot_water_price DECIMAL(8,4);
    
    -- 声明游标
    DECLARE room_cursor CURSOR FOR 
        SELECT 
            r.id,
            r.rent,
            CASE 
                WHEN (SELECT COUNT(*) FROM estimated_bills WHERE room_id = r.id) = 0 
                THEN r.default_deposit 
                ELSE 0.00 
            END as deposit,
            COALESCE(r.electricity_unit_price, b.electricity_unit_price) as electricity_price,
            COALESCE(r.water_unit_price, b.water_unit_price) as water_price,
            COALESCE(r.hot_water_unit_price, b.hot_water_unit_price) as hot_water_price
        FROM rooms r
        LEFT JOIN buildings b ON r.building_id = b.id
        WHERE r.rental_status = 'RENTED'
          AND NOT EXISTS (
              SELECT 1 FROM estimated_bills eb 
              WHERE eb.room_id = r.id AND eb.bill_month = p_bill_month
          );
    
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
    
    -- 开始事务
    START TRANSACTION;
    
    -- 打开游标
    OPEN room_cursor;
    
    read_loop: LOOP
        FETCH room_cursor INTO v_room_id, v_rent, v_deposit, v_electricity_price, v_water_price, v_hot_water_price;
        
        IF done THEN
            LEAVE read_loop;
        END IF;
        
        -- 插入账单记录
        INSERT INTO estimated_bills (
            room_id, bill_month, bill_date, rent, deposit,
            electricity_unit_price, water_unit_price, hot_water_unit_price,
            electricity_usage, electricity_amount,
            water_usage, water_amount,
            hot_water_usage, hot_water_amount,
            other_fees, total_amount, bill_status, created_by, created_at
        ) VALUES (
            v_room_id, p_bill_month, CURDATE(), v_rent, v_deposit,
            v_electricity_price, v_water_price, v_hot_water_price,
            0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00,
            v_rent + v_deposit, 'GENERATED', p_created_by, NOW()
        );
        
    END LOOP;
    
    -- 关闭游标
    CLOSE room_cursor;
    
    -- 提交事务
    COMMIT;
    
END$$

-- =====================================================
-- 3. 计算账单金额的存储过程
-- =====================================================
DROP PROCEDURE IF EXISTS `sp_calculate_bill_amount`$$
CREATE PROCEDURE `sp_calculate_bill_amount`(
    IN p_bill_id BIGINT
)
BEGIN
    DECLARE v_electricity_amount DECIMAL(10,2) DEFAULT 0.00;
    DECLARE v_water_amount DECIMAL(10,2) DEFAULT 0.00;
    DECLARE v_hot_water_amount DECIMAL(10,2) DEFAULT 0.00;
    DECLARE v_total_amount DECIMAL(10,2) DEFAULT 0.00;
    
    -- 计算各项费用
    SELECT 
        COALESCE(electricity_usage * electricity_unit_price, 0.00),
        COALESCE(water_usage * water_unit_price, 0.00),
        COALESCE(hot_water_usage * hot_water_unit_price, 0.00)
    INTO v_electricity_amount, v_water_amount, v_hot_water_amount
    FROM estimated_bills
    WHERE id = p_bill_id;
    
    -- 计算总金额
    SELECT 
        COALESCE(rent, 0.00) + COALESCE(deposit, 0.00) + 
        v_electricity_amount + v_water_amount + v_hot_water_amount + 
        COALESCE(other_fees, 0.00)
    INTO v_total_amount
    FROM estimated_bills
    WHERE id = p_bill_id;
    
    -- 更新账单
    UPDATE estimated_bills 
    SET 
        electricity_amount = v_electricity_amount,
        water_amount = v_water_amount,
        hot_water_amount = v_hot_water_amount,
        total_amount = v_total_amount,
        updated_at = NOW()
    WHERE id = p_bill_id;
    
END$$

DELIMITER ;
