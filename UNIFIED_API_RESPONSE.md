# 统一API响应格式

## 概述

为了提供一致的API体验，我们实现了统一的响应格式 `ApiResponse<T>`，所有API接口都使用这个格式返回数据。

## 响应格式结构

```json
{
  "code": 200,           // 状态码
  "message": "操作成功",   // 消息
  "data": {...},         // 数据（泛型）
  "token": "jwt_token",  // JWT令牌（可选）
  "timestamp": 1753930187589  // 时间戳
}
```

## 状态码说明

- `200`: 操作成功
- `400`: 请求错误（如参数错误、业务逻辑错误）
- `401`: 未授权（需要登录）
- `403`: 禁止访问（权限不足）
- `500`: 服务器内部错误

## 成功响应示例

### 1. 用户注册成功
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 14,
    "username": "testuser7",
    "password": null,
    "email": "testuser7@example.com",
    "fullName": "测试用户7",
    "status": "ACTIVE",
    "roles": ["USER"],
    "createdAt": "2025-07-31T10:55:25.688639",
    "updatedAt": "2025-07-31T10:55:25.688684"
  },
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0dXNlcjciLCJpYXQiOjE3NTM5MzA1MjUsImV4cCI6MTc1NDAxNjkyNX0.q9CMOgxdT-TJp4gDBY9sVsCJZh07rwe_YXC7JBQLbOc",
  "timestamp": 1753930525721
}
```

### 2. 用户登录成功（包含token）
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 12,
    "username": "testuser6",
    "password": null,
    "email": "testuser6@example.com",
    "fullName": "测试用户6",
    "status": "ACTIVE",
    "roles": ["USER"],
    "createdAt": "2025-07-31T10:49:39.999021",
    "updatedAt": "2025-07-31T10:49:39.999066"
  },
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0dXNlcjYiLCJpYXQiOjE3NTM5MzAxODcsImV4cCI6MTc1NDAxNjU4N30.7EXR0h_LsFKSmOblHJ85gNJjx-nujlwWyqmANBtmjxk",
  "timestamp": 1753930187589
}
```

### 3. 用户名可用性检查
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "available": true,
    "message": "用户名可用",
    "username": "testuser7"
  },
  "token": null,
  "timestamp": 1753930192788
}
```

### 4. 角色验证
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "role": "ADMIN",
    "valid": true,
    "message": "角色有效"
  },
  "token": null,
  "timestamp": 1753930213535
}
```

### 5. 获取所有角色
```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    "SUPER_ADMIN",
    "FINANCE",
    "CONTENT_MANAGER",
    "GUEST",
    "ADMIN",
    "CUSTOMER_SERVICE",
    "USER"
  ],
  "token": null,
  "timestamp": 1753930229209
}
```

## 错误响应示例

### 1. 用户名已存在
```json
{
  "code": 400,
  "message": "用户名已存在",
  "data": null,
  "token": null,
  "timestamp": 1753930206377
}
```

### 2. 手机号码已存在
```json
{
  "code": 400,
  "message": "手机号码已存在",
  "data": null,
  "token": null,
  "timestamp": 1753930206377
}
```

### 3. 登录失败
```json
{
  "code": 400,
  "message": "用户名或密码错误",
  "data": null,
  "token": null,
  "timestamp": 1753930206377
}
```

## API接口列表

### 认证相关接口

#### 用户注册
```
POST /api/auth/register
```
- 成功：返回用户信息和JWT token
- 失败：返回错误信息

#### 用户登录
```
POST /api/auth/login
```
- 成功：返回用户信息和JWT token
- 失败：返回错误信息

#### 用户名可用性检查
```
GET /api/auth/check-username/{username}
```
- 返回可用性状态

#### 手机号码可用性检查
```
GET /api/auth/check-phone/{phone}
```