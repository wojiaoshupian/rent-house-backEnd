# 房屋租赁管理系统 - 数据库部署文档

## 📋 概述

本目录包含房屋租赁管理系统的完整数据库部署脚本，支持MySQL 5.7+和MySQL 8.0+。

## 📁 文件结构

```
db/
├── README.md                    # 本文档
├── deploy.sql                   # 一键部署脚本
├── 01_create_database.sql       # 数据库创建脚本
├── 02_create_tables.sql         # 表结构创建脚本
├── 03_insert_initial_data.sql   # 初始数据插入脚本
├── 04_create_indexes.sql        # 索引创建脚本
├── 05_create_views.sql          # 视图创建脚本
├── 06_create_procedures.sql     # 存储过程创建脚本
└── 07_create_triggers.sql       # 触发器创建脚本
```

## 🚀 快速部署

### 方法一：一键部署（推荐）

```bash
# 进入db目录
cd db

# 执行一键部署脚本
mysql -u root -p < deploy.sql
```

### 方法二：分步部署

```bash
# 1. 创建数据库
mysql -u root -p < 01_create_database.sql

# 2. 创建表结构
mysql -u root -p < 02_create_tables.sql

# 3. 插入初始数据
mysql -u root -p < 03_insert_initial_data.sql

# 4. 创建索引
mysql -u root -p < 04_create_indexes.sql

# 5. 创建视图
mysql -u root -p < 05_create_views.sql

# 6. 创建存储过程
mysql -u root -p < 06_create_procedures.sql

# 7. 创建触发器
mysql -u root -p < 07_create_triggers.sql
```

## 📊 数据库结构

### 核心表

| 表名 | 描述 | 主要字段 |
|------|------|----------|
| `users` | 用户表 | id, username, password, phone, email, status |
| `user_roles` | 用户角色表 | user_id, role |
| `buildings` | 楼宇表 | id, building_name, landlord_name, *_unit_price |
| `user_buildings` | 用户楼宇关联表 | user_id, building_id |
| `rooms` | 房间表 | id, room_number, rent, building_id, rental_status |
| `utility_readings` | 水电表记录表 | id, room_id, reading_date, *_reading, *_usage |
| `estimated_bills` | 账单表 | id, room_id, bill_month, total_amount, bill_status |

### 视图

| 视图名 | 描述 |
|--------|------|
| `v_room_details` | 房间详情视图（含楼宇信息） |
| `v_utility_reading_details` | 水电表记录详情视图 |
| `v_bill_details` | 账单详情视图 |
| `v_building_statistics` | 楼宇统计视图 |
| `v_monthly_bill_summary` | 月度账单汇总视图 |

### 存储过程

| 存储过程名 | 描述 | 参数 |
|------------|------|------|
| `sp_update_previous_readings` | 更新水电表前期读数 | room_id, reading_date |
| `sp_generate_monthly_bills` | 批量生成月度账单 | bill_month, created_by |
| `sp_calculate_bill_amount` | 计算账单金额 | bill_id |

## 👥 初始用户账号

部署完成后，系统将创建以下初始用户：

| 用户名 | 密码 | 角色 | 描述 |
|--------|------|------|------|
| `superadmin` | `123456` | 超级管理员 | 拥有所有权限 |
| `admin` | `123456` | 管理员 | 管理权限 |
| `finance` | `123456` | 财务人员 | 财务相关权限 |
| `service` | `123456` | 客服人员 | 客服相关权限 |
| `content` | `123456` | 内容管理员 | 内容管理权限 |
| `user1` | `123456` | 普通用户 | 基础权限 |
| `user2` | `123456` | 普通用户 | 基础权限 |
| `guest` | `123456` | 访客 | 只读权限 |

⚠️ **安全提醒**：部署到生产环境前，请务必修改所有默认密码！

## ⚙️ 配置要求

### 系统要求

- MySQL 5.7+ 或 MySQL 8.0+
- 至少 1GB 可用磁盘空间
- 建议 2GB+ 内存

### MySQL配置建议

```ini
# my.cnf 配置建议
[mysqld]
# 字符集设置
character-set-server = utf8mb4
collation-server = utf8mb4_unicode_ci

# InnoDB设置
innodb_buffer_pool_size = 1G
innodb_log_file_size = 256M
innodb_flush_log_at_trx_commit = 2

# 查询优化
max_connections = 200
query_cache_size = 256M
query_cache_type = ON

# 慢查询日志
slow_query_log = ON
long_query_time = 2
```

## 🔧 维护操作

### 备份数据库

```bash
# 完整备份
mysqldump -u root -p --single-transaction --routines --triggers rent_house_management > backup_$(date +%Y%m%d_%H%M%S).sql

# 仅备份数据
mysqldump -u root -p --no-create-info --single-transaction rent_house_management > data_backup_$(date +%Y%m%d_%H%M%S).sql
```

### 恢复数据库

```bash
# 恢复完整备份
mysql -u root -p rent_house_management < backup_20250806_120000.sql

# 恢复数据
mysql -u root -p rent_house_management < data_backup_20250806_120000.sql
```

### 性能监控

```sql
-- 查看表大小
SELECT 
    table_name,
    ROUND(((data_length + index_length) / 1024 / 1024), 2) AS 'Size (MB)'
FROM information_schema.tables 
WHERE table_schema = 'rent_house_management'
ORDER BY (data_length + index_length) DESC;

-- 查看慢查询
SELECT * FROM mysql.slow_log ORDER BY start_time DESC LIMIT 10;
```

## 🐛 故障排除

### 常见问题

1. **字符集问题**
   ```sql
   -- 检查字符集
   SHOW VARIABLES LIKE 'character_set%';
   
   -- 修改表字符集
   ALTER TABLE table_name CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```

2. **权限问题**
   ```sql
   -- 创建应用用户
   CREATE USER 'rent_app'@'%' IDENTIFIED BY 'your_password';
   GRANT ALL PRIVILEGES ON rent_house_management.* TO 'rent_app'@'%';
   FLUSH PRIVILEGES;
   ```

3. **连接问题**
   ```bash
   # 检查MySQL服务状态
   systemctl status mysql
   
   # 检查端口
   netstat -tlnp | grep 3306
   ```

## 📞 技术支持

如遇到部署问题，请检查：

1. MySQL版本是否符合要求
2. 用户权限是否足够
3. 磁盘空间是否充足
4. 字符集配置是否正确

---

**版本**: 1.0  
**更新时间**: 2025-08-06  
**兼容性**: MySQL 5.7+, MySQL 8.0+
