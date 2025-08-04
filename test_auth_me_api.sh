#!/bin/bash

# 测试获取当前用户信息接口的脚本
# 使用方法: ./test_auth_me_api.sh

BASE_URL="http://localhost:8080"

echo "=== 测试获取当前用户信息接口 ==="
echo

# 1. 登录获取token
echo "1. 登录获取token..."
LOGIN_RESPONSE=$(curl -s -X POST "$BASE_URL/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"username": "testuser123", "password": "123456"}')

echo "登录响应: $LOGIN_RESPONSE"
echo

# 提取token
TOKEN=$(echo $LOGIN_RESPONSE | grep -o '"token":"[^"]*"' | cut -d'"' -f4)
echo "提取的Token: $TOKEN"
echo

# 2. 使用有效token获取当前用户信息
echo "2. 使用有效token获取当前用户信息..."
ME_RESPONSE=$(curl -s -X GET "$BASE_URL/api/auth/me" \
  -H "Authorization: Bearer $TOKEN")

echo "当前用户信息响应: $ME_RESPONSE"
echo

# 3. 测试无效token
echo "3. 测试无效token..."
INVALID_RESPONSE=$(curl -s -X GET "$BASE_URL/api/auth/me" \
  -H "Authorization: Bearer invalid-token")

echo "无效token响应: $INVALID_RESPONSE"
echo

# 4. 测试缺少Authorization头
echo "4. 测试缺少Authorization头..."
NO_AUTH_RESPONSE=$(curl -s -X GET "$BASE_URL/api/auth/me")

echo "缺少Authorization头响应: $NO_AUTH_RESPONSE"
echo

# 5. 测试错误的Authorization格式
echo "5. 测试错误的Authorization格式..."
WRONG_FORMAT_RESPONSE=$(curl -s -X GET "$BASE_URL/api/auth/me" \
  -H "Authorization: InvalidFormat token")

echo "错误格式响应: $WRONG_FORMAT_RESPONSE"
echo

echo "=== 测试完成 ==="
