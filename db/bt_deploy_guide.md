# 宝塔面板部署指南

## 📋 概述

本指南专门针对宝塔面板环境，提供详细的部署步骤和配置说明。

## 🔧 环境要求

### 服务器要求
- **操作系统**: CentOS 7+, Ubuntu 18+, Debian 9+
- **内存**: 至少 2GB RAM
- **磁盘**: 至少 20GB 可用空间
- **宝塔面板**: 7.0+ 版本

### 软件要求
- **MySQL**: 5.7+ 或 8.0+
- **Java**: JDK 17+ 或 JDK 11+
- **Nginx**: 1.18+
- **Redis**: 6.0+ (可选，用于缓存)

## 🚀 部署步骤

### 第一步：准备宝塔环境

#### 1.1 安装必要软件
在宝塔面板中安装以下软件：

1. **登录宝塔面板** → **软件商店**
2. 安装以下软件：
   - **MySQL 8.0** (或 5.7)
   - **Nginx 1.20**
   - **Java项目一键部署** (用于部署Spring Boot)
   - **Redis** (可选)
   - **phpMyAdmin** (数据库管理)

#### 1.2 配置MySQL
1. 点击 **MySQL** → **设置**
2. 修改配置文件，添加以下内容：
```ini
[mysqld]
character-set-server = utf8mb4
collation-server = utf8mb4_unicode_ci
default-time-zone = '+08:00'
max_connections = 200
innodb_buffer_pool_size = 512M
```
3. 重启MySQL服务

### 第二步：创建数据库

#### 2.1 通过宝塔面板创建数据库
1. 点击 **数据库** → **添加数据库**
2. 填写信息：
   - **数据库名**: `rent_house_management`
   - **用户名**: `rent_app`
   - **密码**: `设置一个强密码`
   - **访问权限**: `本地服务器`

#### 2.2 导入数据库结构
1. 点击数据库后面的 **管理** 按钮
2. 进入phpMyAdmin
3. 选择 `rent_house_management` 数据库
4. 点击 **导入** 标签
5. 按顺序导入以下SQL文件：
   - `01_create_database.sql` (跳过，数据库已创建)
   - `02_create_tables.sql`
   - `03_insert_initial_data.sql`
   - `04_create_indexes.sql`
   - `05_create_views.sql`
   - `06_create_procedures.sql`
   - `07_create_triggers.sql`

### 第三步：上传项目文件

#### 3.1 创建网站目录
1. 点击 **网站** → **添加站点**
2. 填写信息：
   - **域名**: 你的域名或IP
   - **根目录**: `/www/wwwroot/rent-house-management`
   - **PHP版本**: 纯静态 (因为是Java项目)

#### 3.2 上传项目文件
1. 点击网站后面的 **文件** 按钮
2. 上传你的Spring Boot项目JAR包到网站根目录
3. 上传配置文件 `application-production.properties`

### 第四步：配置Spring Boot应用

#### 4.1 创建应用配置文件
在网站根目录创建 `application-production.properties`：

```properties
# 服务器配置
server.port=8080

# 数据库配置 (根据宝塔实际配置修改)
spring.datasource.url=jdbc:mysql://localhost:3306/rent_house_management?useUnicode=true&characterEncoding=utf8mb4&useSSL=false&serverTimezone=Asia/Shanghai
spring.datasource.username=rent_app
spring.datasource.password=你的数据库密码
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA配置
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false

# 日志配置
logging.level.root=INFO
logging.file.name=/www/wwwroot/rent-house-management/logs/app.log

# JWT配置
jwt.secret=your_production_jwt_secret_key_must_be_very_long_and_secure
jwt.expiration=86400000

# 文件上传路径
file.upload.path=/www/wwwroot/rent-house-management/uploads/
```

#### 4.2 使用Java项目一键部署
1. 点击 **软件商店** → **已安装** → **Java项目一键部署**
2. 点击 **设置**
3. 添加项目：
   - **项目名称**: `rent-house-management`
   - **项目路径**: `/www/wwwroot/rent-house-management`
   - **JAR包路径**: `/www/wwwroot/rent-house-management/your-app.jar`
   - **启动端口**: `8080`
   - **JVM参数**: `-Xms512m -Xmx1024m -Dspring.profiles.active=production`

### 第五步：配置Nginx反向代理

#### 5.1 修改网站配置
1. 点击网站后面的 **设置** 按钮
2. 点击 **配置文件** 标签
3. 替换配置内容：

```nginx
server {
    listen 80;
    server_name 你的域名或IP;
    
    # 静态文件处理
    location /static/ {
        alias /www/wwwroot/rent-house-management/static/;
        expires 30d;
    }
    
    location /uploads/ {
        alias /www/wwwroot/rent-house-management/uploads/;
        expires 7d;
    }
    
    # API请求代理到Spring Boot
    location / {
        proxy_pass http://127.0.0.1:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        
        # 超时设置
        proxy_connect_timeout 60s;
        proxy_send_timeout 60s;
        proxy_read_timeout 60s;
        
        # 缓冲设置
        proxy_buffering on;
        proxy_buffer_size 8k;
        proxy_buffers 8 8k;
    }
    
    # 健康检查
    location /actuator/health {
        proxy_pass http://127.0.0.1:8080/actuator/health;
        access_log off;
    }
}
```

### 第六步：启动应用

#### 6.1 启动Spring Boot应用
1. 在 **Java项目一键部署** 中点击项目后面的 **启动** 按钮
2. 查看启动日志，确保没有错误

#### 6.2 验证部署
1. 访问 `http://你的域名或IP/actuator/health`
2. 应该返回 `{"status":"UP"}`
3. 访问 `http://你的域名或IP` 查看应用是否正常

### 第七步：安全配置

#### 7.1 配置防火墙
1. 点击 **安全** → **防火墙**
2. 开放必要端口：
   - `80` (HTTP)
   - `443` (HTTPS，如果使用SSL)
   - `8080` (Spring Boot，仅内网访问)

#### 7.2 配置SSL证书 (推荐)
1. 点击网站后面的 **设置** → **SSL**
2. 选择证书类型：
   - **Let's Encrypt** (免费)
   - **其他证书** (如果有付费证书)
3. 开启 **强制HTTPS**

### 第八步：监控和维护

#### 8.1 设置监控
1. 点击 **监控** → **负载状态**
2. 监控CPU、内存、磁盘使用情况

#### 8.2 设置定时任务 (备份)
1. 点击 **计划任务** → **添加任务**
2. 创建数据库备份任务：
   - **任务类型**: `备份数据库`
   - **执行周期**: `每天 2:00`
   - **备份数据库**: `rent_house_management`

#### 8.3 日志管理
1. 应用日志位置: `/www/wwwroot/rent-house-management/logs/`
2. Nginx日志位置: `/www/wwwroot/rent-house-management/logs/`
3. 定期清理旧日志文件

## 🔍 故障排除

### 常见问题

#### 1. 应用启动失败
- 检查Java版本是否正确
- 检查数据库连接配置
- 查看应用启动日志

#### 2. 数据库连接失败
- 检查MySQL服务是否启动
- 检查数据库用户权限
- 检查防火墙设置

#### 3. 静态文件无法访问
- 检查文件路径权限
- 检查Nginx配置
- 重启Nginx服务

### 日志查看
```bash
# 应用日志
tail -f /www/wwwroot/rent-house-management/logs/app.log

# Nginx访问日志
tail -f /www/server/nginx/logs/access.log

# Nginx错误日志
tail -f /www/server/nginx/logs/error.log
```

## 📞 技术支持

如遇到问题，请检查：
1. 宝塔面板版本是否最新
2. 软件版本是否兼容
3. 服务器资源是否充足
4. 网络连接是否正常

---

**部署完成后，请及时修改默认密码！**
