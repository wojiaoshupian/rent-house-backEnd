-- =====================================================
-- 房屋租赁管理系统 - 索引和优化脚本
-- 版本: 1.0
-- 创建时间: 2025-08-06
-- 描述: 创建额外的索引以优化查询性能
-- =====================================================

USE `rent_house_management`;

-- =====================================================
-- 1. 复合索引优化
-- =====================================================

-- 用户表复合索引
CREATE INDEX `idx_users_status_created` ON `users` (`status`, `created_at`);
CREATE INDEX `idx_users_username_status` ON `users` (`username`, `status`);

-- 楼宇表复合索引
CREATE INDEX `idx_buildings_created_by_created_at` ON `buildings` (`created_by`, `created_at`);

-- 房间表复合索引
CREATE INDEX `idx_rooms_building_status` ON `rooms` (`building_id`, `rental_status`);
CREATE INDEX `idx_rooms_status_created` ON `rooms` (`rental_status`, `created_at`);

-- 水电表记录表复合索引
CREATE INDEX `idx_utility_readings_room_status` ON `utility_readings` (`room_id`, `reading_status`);
CREATE INDEX `idx_utility_readings_date_status` ON `utility_readings` (`reading_date`, `reading_status`);
CREATE INDEX `idx_utility_readings_created_by_date` ON `utility_readings` (`created_by`, `reading_date`);

-- 账单表复合索引
CREATE INDEX `idx_estimated_bills_room_status` ON `estimated_bills` (`room_id`, `bill_status`);
CREATE INDEX `idx_estimated_bills_month_status` ON `estimated_bills` (`bill_month`, `bill_status`);
CREATE INDEX `idx_estimated_bills_created_by_month` ON `estimated_bills` (`created_by`, `bill_month`);

-- =====================================================
-- 2. 全文搜索索引（如果需要）
-- =====================================================

-- 楼宇名称全文搜索
-- ALTER TABLE `buildings` ADD FULLTEXT(`building_name`);

-- 房间备注全文搜索
-- ALTER TABLE `utility_readings` ADD FULLTEXT(`notes`);

-- 账单备注全文搜索
-- ALTER TABLE `estimated_bills` ADD FULLTEXT(`notes`);

-- =====================================================
-- 3. 数据库配置优化建议
-- =====================================================

-- 设置InnoDB缓冲池大小（建议设置为系统内存的70-80%）
-- SET GLOBAL innodb_buffer_pool_size = 1073741824; -- 1GB

-- 设置查询缓存（MySQL 8.0已移除查询缓存）
-- 对于MySQL 5.7及以下版本：
-- SET GLOBAL query_cache_size = 268435456; -- 256MB
-- SET GLOBAL query_cache_type = ON;

-- 设置慢查询日志
-- SET GLOBAL slow_query_log = 'ON';
-- SET GLOBAL long_query_time = 2;

-- =====================================================
-- 4. 分区表建议（大数据量时使用）
-- =====================================================

-- 水电表记录表按月分区（示例，需要重建表）
/*
ALTER TABLE `utility_readings` 
PARTITION BY RANGE (YEAR(reading_date) * 100 + MONTH(reading_date)) (
    PARTITION p202501 VALUES LESS THAN (202502),
    PARTITION p202502 VALUES LESS THAN (202503),
    PARTITION p202503 VALUES LESS THAN (202504),
    PARTITION p202504 VALUES LESS THAN (202505),
    PARTITION p202505 VALUES LESS THAN (202506),
    PARTITION p202506 VALUES LESS THAN (202507),
    PARTITION p202507 VALUES LESS THAN (202508),
    PARTITION p202508 VALUES LESS THAN (202509),
    PARTITION p202509 VALUES LESS THAN (202510),
    PARTITION p202510 VALUES LESS THAN (202511),
    PARTITION p202511 VALUES LESS THAN (202512),
    PARTITION p202512 VALUES LESS THAN (202601),
    PARTITION p_future VALUES LESS THAN MAXVALUE
);
*/

-- 账单表按年分区（示例，需要重建表）
/*
ALTER TABLE `estimated_bills` 
PARTITION BY RANGE (YEAR(STR_TO_DATE(CONCAT(bill_month, '-01'), '%Y-%m-%d'))) (
    PARTITION p2024 VALUES LESS THAN (2025),
    PARTITION p2025 VALUES LESS THAN (2026),
    PARTITION p2026 VALUES LESS THAN (2027),
    PARTITION p2027 VALUES LESS THAN (2028),
    PARTITION p_future VALUES LESS THAN MAXVALUE
);
*/
