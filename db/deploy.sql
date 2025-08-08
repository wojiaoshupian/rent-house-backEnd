-- =====================================================
-- 房屋租赁管理系统 - 完整部署脚本
-- 版本: 1.0
-- 创建时间: 2025-08-06
-- 描述: 一键部署整个数据库系统
-- 使用方法: mysql -u root -p < deploy.sql
-- =====================================================

-- 执行所有SQL脚本
SOURCE 01_create_database.sql;
SOURCE 02_create_tables.sql;
SOURCE 03_insert_initial_data.sql;
SOURCE 04_create_indexes.sql;
SOURCE 05_create_views.sql;
SOURCE 06_create_procedures.sql;
SOURCE 07_create_triggers.sql;

-- 显示部署完成信息
SELECT '数据库部署完成！' AS message;
SELECT 'Database deployment completed!' AS message;

-- 显示数据库统计信息
USE `rent_house_management`;

SELECT 
    'Tables' AS type,
    COUNT(*) AS count
FROM information_schema.tables 
WHERE table_schema = 'rent_house_management'
UNION ALL
SELECT 
    'Views' AS type,
    COUNT(*) AS count
FROM information_schema.views 
WHERE table_schema = 'rent_house_management'
UNION ALL
SELECT 
    'Procedures' AS type,
    COUNT(*) AS count
FROM information_schema.routines 
WHERE routine_schema = 'rent_house_management' 
  AND routine_type = 'PROCEDURE'
UNION ALL
SELECT 
    'Triggers' AS type,
    COUNT(*) AS count
FROM information_schema.triggers 
WHERE trigger_schema = 'rent_house_management';

-- 显示初始用户信息
SELECT 
    '初始用户账号信息' AS info,
    '' AS username,
    '' AS password,
    '' AS description
UNION ALL
SELECT 
    '',
    'superadmin' AS username,
    '123456' AS password,
    '超级管理员' AS description
UNION ALL
SELECT 
    '',
    'admin' AS username,
    '123456' AS password,
    '系统管理员' AS description
UNION ALL
SELECT 
    '',
    'finance' AS username,
    '123456' AS password,
    '财务人员' AS description
UNION ALL
SELECT 
    '',
    'service' AS username,
    '123456' AS password,
    '客服人员' AS description;
