# Token过期时间API文档

## 概述

现在所有返回JWT token的接口都会同时返回token的过期时间戳，方便前端动态读取和管理token的生命周期。

## 新增字段说明

### ApiResponse新增字段
- `tokenExpiresAt`: Long类型，token过期时间戳（毫秒），当响应包含token时会一并返回

## 接口详情

### 1. 用户登录
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "testuser123",
  "password": "123456"
}
```

**响应示例：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 23,
    "username": "testuser123",
    "password": null,
    "email": "testuser123@example.com",
    "fullName": "测试用户123",
    "status": "ACTIVE",
    "roles": ["USER"],
    "createdAt": "2025-08-04T09:25:44.907582",
    "updatedAt": "2025-08-04T09:25:44.907635"
  },
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0dXNlcjEyMyIsImlhdCI6MTc1NDI3MDc3OCwiZXhwIjoxNzU0MzU3MTc4fQ.CPLkGtXowxoA3yf_TBewNowBcR79Z2jYyX4WdUX63uQ",
  "tokenExpiresAt": 1754357178000,
  "timestamp": 1754270778123
}
```

### 2. 用户注册
```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "testuser123",
  "password": "123456",
  "phone": "13900000123",
  "email": "testuser123@example.com",
  "fullName": "测试用户123"
}
```

**响应示例：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 23,
    "username": "testuser123",
    "password": null,
    "email": "testuser123@example.com",
    "fullName": "测试用户123",
    "status": "ACTIVE",
    "roles": ["USER"],
    "createdAt": "2025-08-04T09:25:44.907582",
    "updatedAt": "2025-08-04T09:25:44.907635"
  },
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0dXNlcjEyMyIsImlhdCI6MTc1NDI3MDc0NCwiZXhwIjoxNzU0MzU3MTQ0fQ.lLs0Z3QFhumjCBjrQ93aXgtSYrpufFgHVBaryjxZMyU",
  "tokenExpiresAt": 1754357144000,
  "timestamp": 1754270744123
}
```

### 3. 刷新Token（新增接口）
```http
POST /api/auth/refresh
Authorization: Bearer <your-jwt-token>
```

**响应示例：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0dXNlcjEyMyIsImlhdCI6MTc1NDI3MDgxNCwiZXhwIjoxNzU0MzU3MjE0fQ.aPImKqX652HkQOKyDftDMYp4awWiujD_5jHMmQGB5o4",
    "expiresAt": 1754357214376
  },
  "token": null,
  "tokenExpiresAt": null,
  "timestamp": 1754270814123
}
```

### 4. 验证Token（增强版）
```http
GET /api/auth/validate
Authorization: Bearer <your-jwt-token>
```

**响应示例：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "valid": true,
    "username": "testuser123",
    "expiresAt": 1754357214000
  },
  "token": null,
  "tokenExpiresAt": null,
  "timestamp": 1754270814123
}
```

### 5. 获取当前用户信息（新增接口）
```http
GET /api/auth/me
Authorization: Bearer <your-jwt-token>
```

**成功响应示例：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 23,
    "username": "testuser123",
    "password": null,
    "email": "testuser123@example.com",
    "fullName": "测试用户123",
    "status": "ACTIVE",
    "roles": ["USER"],
    "createdAt": "2025-08-04T09:25:44.907582",
    "updatedAt": "2025-08-04T09:25:44.907635"
  },
  "token": null,
  "tokenExpiresAt": null,
  "timestamp": 1754277708298
}
```

**无效token响应示例：**
```json
{
  "code": 401,
  "message": "令牌无效或已过期",
  "data": null,
  "token": null,
  "tokenExpiresAt": null,
  "timestamp": 1754277715326
}
```

**缺少Authorization头响应示例：**
```json
{
  "code": 401,
  "message": "缺少有效的授权令牌",
  "data": null,
  "token": null,
  "tokenExpiresAt": null,
  "timestamp": 1754277722527
}
```

## 时间戳说明

- `tokenExpiresAt`: token过期时间戳（毫秒）
- `timestamp`: 响应生成时间戳（毫秒）
- 默认token有效期：24小时（86400000毫秒）

## 前端使用建议

### JavaScript示例
```javascript
// 登录后保存token和过期时间
const loginResponse = await fetch('/api/auth/login', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({ username: 'user', password: 'pass' })
});

const result = await loginResponse.json();
if (result.code === 200) {
  localStorage.setItem('token', result.token);
  localStorage.setItem('tokenExpiresAt', result.tokenExpiresAt);
}

// 检查token是否即将过期
function isTokenExpiringSoon() {
  const expiresAt = localStorage.getItem('tokenExpiresAt');
  if (!expiresAt) return true;
  
  const now = Date.now();
  const timeUntilExpiry = parseInt(expiresAt) - now;
  
  // 如果剩余时间少于5分钟，认为即将过期
  return timeUntilExpiry < 5 * 60 * 1000;
}

// 自动刷新token
async function refreshTokenIfNeeded() {
  if (isTokenExpiringSoon()) {
    const token = localStorage.getItem('token');
    const response = await fetch('/api/auth/refresh', {
      method: 'POST',
      headers: { 'Authorization': `Bearer ${token}` }
    });
    
    const result = await response.json();
    if (result.code === 200) {
      localStorage.setItem('token', result.data.token);
      localStorage.setItem('tokenExpiresAt', result.data.expiresAt);
    }
  }
}
```

## 配置说明

token过期时间在 `application.yml` 中配置：
```yaml
jwt:
  secret: your-secret-key-here-make-it-long-and-secure
  expiration: 86400000 # 24小时（毫秒）
```

## 更新内容总结

1. **ApiResponse类增强**：新增 `tokenExpiresAt` 字段
2. **JwtTokenUtil增强**：新增获取token过期时间的方法
3. **登录接口增强**：返回token过期时间
4. **注册接口增强**：返回token过期时间
5. **新增刷新token接口**：`POST /api/auth/refresh`
6. **验证token接口增强**：返回token过期时间
7. **新增获取当前用户接口**：`GET /api/auth/me`
8. **BuildingController增强**：token刷新时返回过期时间

所有涉及token的接口现在都会返回相应的过期时间信息，方便前端进行token生命周期管理。新增的 `/api/auth/me` 接口可以让前端获取当前登录用户的详细信息。
