-- 修复rooms表的rental_status字段默认值
-- 将现有的NULL值设置为RENTED状态
UPDATE rooms SET rental_status = 'RENTED' WHERE rental_status IS NULL;

-- 修改字段为NOT NULL并设置默认值
ALTER TABLE rooms MODIFY COLUMN rental_status ENUM('VACANT', 'RENTED', 'MAINTENANCE', 'RESERVED') NOT NULL DEFAULT 'VACANT';
