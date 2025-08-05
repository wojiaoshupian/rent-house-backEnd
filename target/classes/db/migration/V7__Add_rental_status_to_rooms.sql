-- 为rooms表添加出租状态字段
ALTER TABLE rooms
ADD COLUMN rental_status ENUM('VACANT', 'RENTED', 'MAINTENANCE', 'RESERVED')
DEFAULT 'VACANT'
COMMENT '出租状态: VACANT-空置, RENTED-已出租, MAINTENANCE-维修中, RESERVED-已预订';

-- 将现有房间设置为已出租状态（假设现有房间都已出租）
UPDATE rooms SET rental_status = 'RENTED' WHERE id > 0;
