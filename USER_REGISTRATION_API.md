# 用户注册API功能说明

## 概述

根据用户需求，我们重新设计了用户注册功能，前端只需要传递必要的信息（用户名、密码、手机号码），后端会自动处理ID生成和重复校验。

## 主要改进

### 1. 新的注册DTO (`UserRegistrationDto`)

前端只需要传递以下字段：
- `username`: 用户名（必填，3-50字符）
- `password`: 密码（必填，最少6字符）
- `phone`: 手机号码（必填，符合中国手机号格式）
- `email`: 邮箱（可选，不进行重复校验）
- `fullName`: 姓名（可选，最多100字符）

**不再需要传递：**
- `id`: 由后端自动生成
- `status`: 默认为ACTIVE
- `roles`: 默认为USER角色
- `createdAt/updatedAt`: 由JPA自动管理

### 2. 重复校验功能

提供了三个独立的校验API：

#### 用户名校验
```
GET /api/auth/check-username/{username}
```
响应示例：
```json
{
  "username": "testuser",
  "available": false,
  "message": "用户名已存在"
}
```

#### 手机号码校验
```
GET /api/auth/check-phone/{phone}
```
响应示例：
```json
{
  "phone": "13900000001",
  "available": false,
  "message": "手机号码已存在"
}
```

#### 邮箱校验（可选）
```
GET /api/auth/check-email/{email}
```
响应示例：
```json
{
  "email": "test@example.com",
  "available": true,
  "message": "邮箱可用"
}
```

**注意：邮箱是可选的，不进行重复校验，多个用户可以使用相同的邮箱。**

### 3. 用户注册API

```
POST /api/auth/register
```

请求体示例：
```json
{
  "username": "newuser",
  "password": "123456",
  "phone": "13900000001",
  "email": "newuser@example.com",
  "fullName": "新用户"
}
```

成功响应示例：
```json
{
  "id": 11,
  "username": "newuser",
  "password": null,
  "email": "newuser@example.com",
  "fullName": "新用户",
  "status": "ACTIVE",
  "roles": ["USER"],
  "createdAt": "2025-07-31T09:52:15.029784",
  "updatedAt": "2025-07-31T09:52:15.029841"
}
```

### 4. 数据库字段更新

在`User`实体中添加了`phone`字段：
- 字段名：`phone`
- 类型：`String`
- 约束：`@NotBlank`、`@Pattern`（手机号格式）、`@Column(unique = true, nullable = false)`

邮箱字段：
- 字段名：`email`
- 类型：`String`
- 约束：`@Email`（格式验证）、`@Column`（无唯一性约束，允许重复）

### 5. 错误处理

注册时会进行以下校验：
1. 用户名是否已存在
2. 手机号码是否已存在
3. 邮箱格式是否正确（如果提供）

如果任何一项校验失败，会返回相应的错误信息。

## 测试结果

### 成功案例
1. ✅ 新用户注册成功
2. ✅ 注册后可以正常登录
3. ✅ 密码正确加密存储
4. ✅ 默认角色为USER
5. ✅ 邮箱可选，不进行重复校验
6. ✅ 多个用户可以使用相同邮箱

### 重复校验测试
1. ✅ 用户名重复检测
2. ✅ 手机号码重复检测
3. ✅ 邮箱不进行重复检测（允许多个用户使用相同邮箱）

### API功能测试
1. ✅ 用户名可用性检查API
2. ✅ 手机号码可用性检查API
3. ✅ 邮箱可用性检查API（可选）
4. ✅ 用户注册API
5. ✅ 用户登录API

## 使用建议

### 前端集成
1. 在注册表单中实时调用校验API
2. 在提交注册前进行最终校验
3. 根据API响应显示相应的错误信息
4. 邮箱字段可以设置为可选

### 安全考虑
1. 密码使用BCrypt加密存储
2. 手机号码和用户名都有唯一性约束
3. 邮箱格式验证但不强制唯一性
4. 所有输入都有格式验证

## 技术实现

### 新增文件
- `UserRegistrationDto.java`: 注册专用DTO
- 更新了`User.java`: 添加phone字段，移除邮箱唯一性约束
- 更新了`UserRepository.java`: 添加手机号相关查询方法
- 更新了`UserService.java`: 添加注册和校验逻辑，移除邮箱重复校验
- 更新了`AuthController.java`: 添加注册和校验API

### 数据库变更
- 添加了`phone`字段到`users`表
- 为`phone`字段添加了唯一索引
- 移除了`email`字段的唯一性约束

这个新的注册功能完全满足了用户的需求：前端只需要传递用户名、密码和手机号码，后端会自动处理ID生成和重复校验。邮箱是可选的，不进行重复校验。 