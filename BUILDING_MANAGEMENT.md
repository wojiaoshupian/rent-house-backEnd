# 楼宇管理系统

## 功能概述

楼宇管理系统提供完整的楼宇信息管理功能，包括楼宇创建、查询、更新、删除，以及用户楼宇关联管理。

## 数据库设计

### 楼宇表 (buildings)
- `id`: 楼宇ID（主键）
- `building_name`: 楼宇名称（必填）
- `landlord_name`: 房东名称（必填）
- `electricity_unit_price`: 电费单价（必填）
- `water_unit_price`: 水费单价（必填）
- `hot_water_unit_price`: 热水单价（选填）
- `electricity_cost`: 电费成本（选填）
- `water_cost`: 水费成本（选填）
- `hot_water_cost`: 热水费成本（选填）
- `rent_collection_method`: 收租方式（默认固定月初收租）
- `created_by`: 创建者用户ID（必填）
- `created_at`: 创建时间
- `updated_at`: 更新时间

### 用户楼宇关联表 (user_buildings)
- `id`: 关联ID（主键）
- `user_id`: 用户ID
- `building_id`: 楼宇ID
- `created_at`: 创建时间

## API接口

### 1. 创建楼宇

```
POST /api/buildin
``` 