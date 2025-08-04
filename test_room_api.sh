#!/bin/bash

# 测试房间管理API的脚本
# 使用方法: ./test_room_api.sh

BASE_URL="http://localhost:8080"

echo "=== 测试房间管理API ==="
echo

# 1. 登录获取token
echo "1. 登录获取token..."
LOGIN_RESPONSE=$(curl -s -X POST "$BASE_URL/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"username": "superadmin", "password": "123456"}')

echo "登录响应: $LOGIN_RESPONSE"
echo

# 提取token
TOKEN=$(echo $LOGIN_RESPONSE | grep -o '"token":"[^"]*"' | cut -d'"' -f4)
echo "提取的Token: $TOKEN"
echo

# 2. 创建房间
echo "2. 创建房间..."
CREATE_RESPONSE=$(curl -s -X POST "$BASE_URL/api/rooms" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "roomNumber": "101",
    "rent": 1500.00,
    "defaultDeposit": 3000.00,
    "electricityUnitPrice": 1.2,
    "waterUnitPrice": 3.5,
    "hotWaterUnitPrice": 6.0,
    "buildingId": 3
  }')

echo "创建房间响应: $CREATE_RESPONSE"
echo

# 3. 获取所有房间
echo "3. 获取所有房间..."
GET_ALL_RESPONSE=$(curl -s -X GET "$BASE_URL/api/rooms" \
  -H "Authorization: Bearer $TOKEN")

echo "获取所有房间响应: $GET_ALL_RESPONSE"
echo

# 4. 根据楼宇ID获取房间
echo "4. 根据楼宇ID获取房间..."
GET_BY_BUILDING_RESPONSE=$(curl -s -X GET "$BASE_URL/api/rooms?buildingId=3" \
  -H "Authorization: Bearer $TOKEN")

echo "根据楼宇ID获取房间响应: $GET_BY_BUILDING_RESPONSE"
echo

# 5. 搜索房间
echo "5. 搜索房间..."
SEARCH_RESPONSE=$(curl -s -X GET "$BASE_URL/api/rooms/search?keyword=101" \
  -H "Authorization: Bearer $TOKEN")

echo "搜索房间响应: $SEARCH_RESPONSE"
echo

# 6. 统计房间数量
echo "6. 统计房间数量..."
COUNT_RESPONSE=$(curl -s -X GET "$BASE_URL/api/rooms/count?buildingId=3" \
  -H "Authorization: Bearer $TOKEN")

echo "统计房间数量响应: $COUNT_RESPONSE"
echo

echo "=== 测试完成 ==="
