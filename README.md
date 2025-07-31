# Spring Boot 3 Demo 项目

这是一个整合了常用框架和类库的Spring Boot 3演示项目。

## 技术栈

- **Spring Boot 3.2.0** - 主框架
- **Spring Security** - 安全框架
- **Spring Data JPA** - 数据访问层
- **H2 Database** - 内存数据库
- **Redis** - 缓存
- **JWT** - 令牌认证
- **Swagger/OpenAPI** - API文档
- **MapStruct** - 对象映射

- **Maven** - 构建工具

## 功能特性

- ✅ 用户认证与授权 (JWT)
- ✅ 用户管理 (CRUD操作)
- ✅ 数据验证
- ✅ 缓存支持
- ✅ API文档 (Swagger)
- ✅ 全局异常处理
- ✅ 日志记录
- ✅ 健康检查 (Actuator)

## 快速开始

### 环境要求

- Java 17+
- Maven 3.6+
- Redis (可选，用于缓存)

### 运行项目

1. 克隆项目
```bash
git clone <repository-url>
cd spring-boot-demo
```

2. 编译项目
```bash
mvn clean compile
```

3. 运行项目
```bash
mvn spring-boot:run
```

4. 访问应用
- 应用地址: http://localhost:8080
- Swagger文档: http://localhost:8080/swagger-ui.html
- H2控制台: http://localhost:8080/h2-console
- Actuator: http://localhost:8080/actuator

### 数据库连接

H2数据库连接信息:
- JDBC URL: `jdbc:h2:mem:testdb`
- 用户名: `sa`
- 密码: (空)

## API接口

### 认证接口

#### 用户登录
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
```

#### 用户注册
```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "newuser",
  "password": "password123",
  "email": "newuser@example.com",
  "fullName": "新用户"
}
```

#### 验证令牌
```http
GET /api/auth/validate
Authorization: Bearer <your-jwt-token>
```

### 用户管理接口

#### 获取所有用户 (需要ADMIN权限)
```http
GET /api/users?page=0&size=10
Authorization: Bearer <your-jwt-token>
```

#### 根据ID获取用户
```http
GET /api/users/{id}
Authorization: Bearer <your-jwt-token>
```

#### 创建用户 (需要ADMIN权限)
```http
POST /api/users
Authorization: Bearer <your-jwt-token>
Content-Type: application/json

{
  "username": "newuser",
  "password": "password123",
  "email": "newuser@example.com",
  "fullName": "新用户",
  "roles": ["USER"]
}
```

#### 更新用户
```http
PUT /api/users/{id}
Authorization: Bearer <your-jwt-token>
Content-Type: application/json

{
  "fullName": "更新的用户名",
  "email": "updated@example.com"
}
```

#### 删除用户 (需要ADMIN权限)
```http
DELETE /api/users/{id}
Authorization: Bearer <your-jwt-token>
```

## 测试数据

应用启动时会自动创建以下测试用户:

| 用户名 | 密码 | 角色 | 状态 |
|--------|------|------|------|
| admin | admin123 | ADMIN, USER | ACTIVE |
| user1 | user123 | USER | ACTIVE |
| user2 | user123 | USER | ACTIVE |
| inactive | user123 | USER | INACTIVE |

## 项目结构

```
src/main/java/com/example/demo/
├── config/                 # 配置类
│   ├── DataInitializer.java
│   └── SecurityConfig.java
├── controller/             # 控制器
│   ├── AuthController.java
│   └── UserController.java
├── dto/                   # 数据传输对象
│   └── UserDto.java
├── entity/                # 实体类
│   └── User.java
├── exception/             # 异常处理
│   └── GlobalExceptionHandler.java
├── mapper/                # 对象映射
│   └── UserMapper.java
├── repository/            # 数据访问层
│   └── UserRepository.java
├── security/              # 安全相关
│   ├── CustomUserDetailsService.java
│   ├── JwtAuthenticationFilter.java
│   └── JwtTokenUtil.java
├── service/               # 业务逻辑层
│   └── UserService.java
└── DemoApplication.java   # 主启动类
```

## 配置说明

### 数据库配置
项目使用H2内存数据库，配置在 `application.yml` 中。

### Redis配置
如需使用Redis缓存，请确保Redis服务运行在localhost:6379。

### JWT配置
JWT密钥和过期时间在 `application.yml` 中配置。

## 开发指南

### 添加新的实体
1. 创建实体类 (entity包)
2. 创建DTO类 (dto包)
3. 创建Mapper接口 (mapper包)
4. 创建Repository接口 (repository包)
5. 创建Service类 (service包)
6. 创建Controller类 (controller包)

### 添加新的API
1. 在Controller中添加新的端点
2. 使用Swagger注解添加文档
3. 添加相应的权限控制

## 部署

### 打包
```bash
mvn clean package
```

### 运行JAR文件
```bash
java -jar target/spring-boot-demo-1.0.0.jar
```

## 监控和健康检查

访问 http://localhost:8080/actuator 查看应用健康状态和指标。

## 许可证

MIT License 