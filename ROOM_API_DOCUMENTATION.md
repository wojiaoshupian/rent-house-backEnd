# 房间管理API文档

## 概述

房间管理API提供了完整的房间CRUD操作，包括创建、查询、更新、删除房间等功能。房间与楼宇是多对一的关系，一个楼宇可以有多个房间，一个房间只能属于一个楼宇。

## 数据模型

### Room实体

```java
public class Room {
    private Long id;                    // 房间ID
    private String roomNumber;          // 房号（必填）
    private BigDecimal rent;            // 租金（必填）
    private BigDecimal defaultDeposit;  // 默认押金（必填）
    private BigDecimal electricityUnitPrice;  // 电费单价（可选，为空则使用楼宇设置）
    private BigDecimal waterUnitPrice;        // 水费单价（可选，为空则使用楼宇设置）
    private BigDecimal hotWaterUnitPrice;     // 热水费单价（可选，为空则使用楼宇设置）
    private Long buildingId;            // 楼宇ID（必填）
    private Long createdBy;             // 创建者用户ID
    private LocalDateTime createdAt;    // 创建时间
    private LocalDateTime updatedAt;    // 更新时间
}
```

### RoomDto

```java
public class RoomDto {
    private Long id;
    private String roomNumber;
    private BigDecimal rent;
    private BigDecimal defaultDeposit;
    private BigDecimal electricityUnitPrice;
    private BigDecimal waterUnitPrice;
    private BigDecimal hotWaterUnitPrice;
    private Long buildingId;
    private String buildingName;        // 楼宇名称（显示用）
    private String landlordName;        // 房东姓名（显示用）
    private BigDecimal effectiveElectricityUnitPrice;  // 有效电费单价
    private BigDecimal effectiveWaterUnitPrice;        // 有效水费单价
    private BigDecimal effectiveHotWaterUnitPrice;     // 有效热水费单价
    private Long createdBy;
    private String createdByUsername;   // 创建者用户名
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

## API接口

### 1. 创建房间

```http
POST /api/rooms
Authorization: Bearer <your-jwt-token>
Content-Type: application/json
```

**请求体：**
```json
{
  "roomNumber": "101",
  "rent": 1500.00,
  "defaultDeposit": 3000.00,
  "electricityUnitPrice": 1.2,
  "waterUnitPrice": 3.5,
  "hotWaterUnitPrice": 6.0,
  "buildingId": 3
}
```

**成功响应：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "roomNumber": "101",
    "rent": 1500.00,
    "defaultDeposit": 3000.00,
    "electricityUnitPrice": 1.2,
    "waterUnitPrice": 3.5,
    "hotWaterUnitPrice": 6.0,
    "buildingId": 3,
    "buildingName": "阳光小区1号楼",
    "landlordName": "张三",
    "effectiveElectricityUnitPrice": 1.2,
    "effectiveWaterUnitPrice": 3.5,
    "effectiveHotWaterUnitPrice": 6.0,
    "createdBy": 1,
    "createdByUsername": "superadmin",
    "createdAt": "2025-08-04T13:45:00",
    "updatedAt": null
  },
  "token": "new-jwt-token",
  "tokenExpiresAt": 1754372478208,
  "timestamp": 1754286078214
}
```

### 2. 获取房间列表

```http
GET /api/rooms
GET /api/rooms?buildingId=3
GET /api/rooms?userId=1
```

**成功响应：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "roomNumber": "101",
      "rent": 1500.00,
      "defaultDeposit": 3000.00,
      "electricityUnitPrice": 1.2,
      "waterUnitPrice": 3.5,
      "hotWaterUnitPrice": 6.0,
      "buildingId": 3,
      "buildingName": "阳光小区1号楼",
      "landlordName": "张三",
      "effectiveElectricityUnitPrice": 1.2,
      "effectiveWaterUnitPrice": 3.5,
      "effectiveHotWaterUnitPrice": 6.0,
      "createdBy": 1,
      "createdByUsername": "superadmin",
      "createdAt": "2025-08-04T13:45:00",
      "updatedAt": null
    }
  ],
  "token": null,
  "tokenExpiresAt": null,
  "timestamp": 1754286078214
}
```

### 3. 获取房间详情

```http
GET /api/rooms/{id}
```

**成功响应：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "roomNumber": "101",
    "rent": 1500.00,
    "defaultDeposit": 3000.00,
    "electricityUnitPrice": 1.2,
    "waterUnitPrice": 3.5,
    "hotWaterUnitPrice": 6.0,
    "buildingId": 3,
    "buildingName": "阳光小区1号楼",
    "landlordName": "张三",
    "effectiveElectricityUnitPrice": 1.2,
    "effectiveWaterUnitPrice": 3.5,
    "effectiveHotWaterUnitPrice": 6.0,
    "createdBy": 1,
    "createdByUsername": "superadmin",
    "createdAt": "2025-08-04T13:45:00",
    "updatedAt": null
  },
  "token": null,
  "tokenExpiresAt": null,
  "timestamp": 1754286078214
}
```

### 4. 更新房间

```http
PUT /api/rooms/{id}
Authorization: Bearer <your-jwt-token>
Content-Type: application/json
```

**请求体：**
```json
{
  "roomNumber": "102",
  "rent": 1600.00,
  "defaultDeposit": 3200.00,
  "electricityUnitPrice": null,
  "waterUnitPrice": null,
  "hotWaterUnitPrice": null,
  "buildingId": 3
}
```

### 5. 删除房间

```http
DELETE /api/rooms/{id}
Authorization: Bearer <your-jwt-token>
```

**成功响应：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": "房间删除成功",
  "token": "new-jwt-token",
  "tokenExpiresAt": 1754372478208,
  "timestamp": 1754286078214
}
```

### 6. 搜索房间

```http
GET /api/rooms/search?keyword=101
```

**成功响应：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "roomNumber": "101",
      "rent": 1500.00,
      "defaultDeposit": 3000.00,
      "buildingId": 3,
      "buildingName": "阳光小区1号楼"
    }
  ],
  "token": null,
  "tokenExpiresAt": null,
  "timestamp": 1754286078214
}
```

### 7. 统计房间数量

```http
GET /api/rooms/count?buildingId=3
```

**成功响应：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": 5,
  "token": null,
  "tokenExpiresAt": null,
  "timestamp": 1754286078214
}
```

## 业务规则

### 1. 字段验证
- **房号（roomNumber）**：必填，不能为空
- **租金（rent）**：必填，不能为负数
- **默认押金（defaultDeposit）**：必填，不能为负数
- **楼宇ID（buildingId）**：必填，必须是有效的楼宇ID

### 2. 费用单价规则
- 电费、水费、热水费单价为可选字段
- 如果房间设置了单价，则使用房间的设置
- 如果房间没有设置单价（为null），则使用楼宇的设置
- `effectiveXxxUnitPrice` 字段显示最终有效的单价

### 3. 权限控制
- 创建房间：需要对指定楼宇有管理权限
- 查看房间：可以查看有权限的楼宇中的房间
- 更新房间：需要对房间所属楼宇有管理权限
- 删除房间：需要对房间所属楼宇有管理权限

### 4. 唯一性约束
- 同一楼宇中的房号不能重复
- 数据库层面有唯一约束：`UNIQUE KEY uk_room_building (room_number, building_id)`

## 错误响应

### 400 Bad Request
```json
{
  "code": 400,
  "message": "房号不能为空",
  "data": null,
  "token": null,
  "tokenExpiresAt": null,
  "timestamp": 1754286078214
}
```

### 401 Unauthorized
```json
{
  "code": 401,
  "message": "您没有权限在该楼宇中创建房间",
  "data": null,
  "token": null,
  "tokenExpiresAt": null,
  "timestamp": 1754286078214
}
```

### 404 Not Found
```json
{
  "code": 404,
  "message": "房间不存在",
  "data": null,
  "token": null,
  "tokenExpiresAt": null,
  "timestamp": 1754286078214
}
```

## 数据库表结构

```sql
CREATE TABLE rooms (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '房间ID',
    room_number VARCHAR(50) NOT NULL COMMENT '房号',
    rent DECIMAL(10,2) NOT NULL COMMENT '租金(元/月)',
    default_deposit DECIMAL(10,2) NOT NULL COMMENT '默认押金(元)',
    electricity_unit_price DECIMAL(10,2) COMMENT '电费单价(元/度)，为空则使用楼宇设置',
    water_unit_price DECIMAL(10,2) COMMENT '水费单价(元/吨)，为空则使用楼宇设置',
    hot_water_unit_price DECIMAL(10,2) COMMENT '热水单价(元/吨)，为空则使用楼宇设置',
    building_id BIGINT NOT NULL COMMENT '楼宇ID',
    created_by BIGINT NOT NULL COMMENT '创建者用户ID',
    created_at DATETIME NOT NULL COMMENT '创建时间',
    updated_at DATETIME COMMENT '更新时间',
    
    INDEX idx_building_id (building_id),
    INDEX idx_room_number (room_number),
    INDEX idx_created_by (created_by),
    INDEX idx_created_at (created_at),
    
    UNIQUE KEY uk_room_building (room_number, building_id),
    
    FOREIGN KEY (building_id) REFERENCES buildings(id) ON DELETE CASCADE,
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE RESTRICT
) COMMENT '房间信息表';
```
