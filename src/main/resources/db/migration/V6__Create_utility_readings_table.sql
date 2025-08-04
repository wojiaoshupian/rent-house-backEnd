-- 创建水电表记录表
CREATE TABLE utility_readings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    room_id BIGINT NOT NULL COMMENT '房间ID',
    reading_date DATE NOT NULL COMMENT '抄表日期',
    reading_time DATETIME NOT NULL COMMENT '抄表时间',
    
    -- 电表读数
    electricity_reading DECIMAL(10,2) NOT NULL COMMENT '电表读数(度)',
    electricity_previous_reading DECIMAL(10,2) DEFAULT 0 COMMENT '上次电表读数(度)',
    electricity_usage DECIMAL(10,2) GENERATED ALWAYS AS (electricity_reading - electricity_previous_reading) STORED COMMENT '本期用电量(度)',
    
    -- 水表读数
    water_reading DECIMAL(10,2) NOT NULL COMMENT '水表读数(吨)',
    water_previous_reading DECIMAL(10,2) DEFAULT 0 COMMENT '上次水表读数(吨)',
    water_usage DECIMAL(10,2) GENERATED ALWAYS AS (water_reading - water_previous_reading) STORED COMMENT '本期用水量(吨)',
    
    -- 热水表读数（可选）
    hot_water_reading DECIMAL(10,2) DEFAULT NULL COMMENT '热水表读数(吨)',
    hot_water_previous_reading DECIMAL(10,2) DEFAULT 0 COMMENT '上次热水表读数(吨)',
    hot_water_usage DECIMAL(10,2) GENERATED ALWAYS AS (
        CASE 
            WHEN hot_water_reading IS NOT NULL 
            THEN hot_water_reading - hot_water_previous_reading 
            ELSE NULL 
        END
    ) STORED COMMENT '本期热水用量(吨)',
    
    -- 抄表信息
    meter_reader VARCHAR(100) NOT NULL COMMENT '抄表人',
    reading_type ENUM('MANUAL', 'AUTO', 'ESTIMATED') DEFAULT 'MANUAL' COMMENT '抄表类型：手动/自动/估算',
    reading_status ENUM('PENDING', 'CONFIRMED', 'DISPUTED') DEFAULT 'PENDING' COMMENT '读数状态：待确认/已确认/有争议',
    
    -- 备注信息
    notes TEXT COMMENT '备注信息',
    photos JSON COMMENT '抄表照片URL列表',
    
    -- 审计字段
    created_by BIGINT NOT NULL COMMENT '创建人ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    -- 索引
    INDEX idx_room_id (room_id),
    INDEX idx_reading_date (reading_date),
    INDEX idx_reading_time (reading_time),
    INDEX idx_room_date (room_id, reading_date),
    
    -- 外键约束
    FOREIGN KEY (room_id) REFERENCES rooms(id) ON DELETE CASCADE,
    FOREIGN KEY (created_by) REFERENCES users(id),
    
    -- 唯一约束：同一房间同一天只能有一条记录
    UNIQUE KEY uk_room_date (room_id, reading_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='水电表记录表';

-- 创建触发器：自动更新上次读数
DELIMITER $$

CREATE TRIGGER tr_utility_readings_before_insert
BEFORE INSERT ON utility_readings
FOR EACH ROW
BEGIN
    DECLARE last_electricity_reading DECIMAL(10,2) DEFAULT 0;
    DECLARE last_water_reading DECIMAL(10,2) DEFAULT 0;
    DECLARE last_hot_water_reading DECIMAL(10,2) DEFAULT 0;
    
    -- 获取该房间最近一次的读数
    SELECT 
        COALESCE(electricity_reading, 0),
        COALESCE(water_reading, 0),
        COALESCE(hot_water_reading, 0)
    INTO 
        last_electricity_reading,
        last_water_reading,
        last_hot_water_reading
    FROM utility_readings 
    WHERE room_id = NEW.room_id 
    ORDER BY reading_date DESC, reading_time DESC 
    LIMIT 1;
    
    -- 设置上次读数
    SET NEW.electricity_previous_reading = last_electricity_reading;
    SET NEW.water_previous_reading = last_water_reading;
    SET NEW.hot_water_previous_reading = last_hot_water_reading;
END$$

DELIMITER ;
