# 角色管理系统使用说明

## 概述

本项目实现了一个完整的角色管理系统，将用户角色抽取到配置文件中进行统一管理，便于添加新角色和分配权限。

## 角色定义

### 1. 角色常量 (`RoleConfig.java`)

```java
// 超级管理员 - 拥有所有权限
public static final String ROLE_SUPER_ADMIN = "SUPER_ADMIN";

// 管理员 - 拥有大部分管理权限
public static final String ROLE_ADMIN = "ADMIN";

// 普通用户 - 基础用户权限
public static final String ROLE_USER = "USER";

// 访客 - 只读权限
public static final String ROLE_GUEST = "GUEST";

// 内容管理员 - 管理内容相关权限
public static final String ROLE_CONTENT_MANAGER = "CONTENT_MANAGER";

// 财务 - 财务相关权限
public static final String ROLE_FINANCE = "FINANCE";

// 客服 - 客服相关权限
public static final String ROLE_CUSTOMER_SERVICE = "CUSTOMER_SERVICE";
```

### 2. 角色组合定义

```java
// 超级管理员角色集合
public static final Set<String> SUPER_ADMIN_ROLES = new HashSet<>(Arrays.asList(
    ROLE_SUPER_ADMIN, ROLE_ADMIN, ROLE_USER
));

// 管理员角色集合
public static final Set<String> ADMIN_ROLES = new HashSet<>(Arrays.asList(
    ROLE_ADMIN, ROLE_USER
));

// 其他角色组合...
```

## 测试用户

系统初始化时会创建以下测试用户：

| 用户名 | 密码 | 角色 | 描述 |
|--------|------|------|------|
| superadmin | 123456 | SUPER_ADMIN, ADMIN, USER | 超级管理员 |
| admin | 123456 | ADMIN, USER | 系统管理员 |
| content | 123456 | CONTENT_MANAGER, USER | 内容管理员 |
| finance | 123456 | FINANCE, USER | 财务人员 |
| service | 123456 | CUSTOMER_SERVICE, USER | 客服人员 |
| user1 | 123456 | USER | 普通用户1 |
| user2 | 123456 | USER | 普通用户2 |
| guest | 123456 | GUEST | 访客用户 |
| inactive | 123456 | USER | 非活跃用户 |

## API接口

### 角色管理API

#### 1. 获取所有角色
```http
GET /api/roles
Authorization: Bearer <token>
```

#### 2. 获取角色详细信息
```http
GET /api/roles/{role}
Authorization: Bearer <token>
```

#### 3. 验证角色是否有效
```http
GET /api/roles/validate/{role}
```

#### 4. 获取角色层级关系
```http
GET /api/roles/hierarchy
Authorization: Bearer <token>
```

#### 5. 获取角色描述列表
```http
GET /api/roles/descriptions
Authorization: Bearer <token>
```

#### 6. 检查用户角色权限
```http
POST /api/roles/check
Authorization: Bearer <token>
Content-Type: application/json

{
  "userRoles": ["ADMIN", "USER"],
  "targetRole": "ADMIN"
}
```

## 使用方法

### 1. 添加新角色

在 `RoleConfig.java` 中添加新角色：

```java
// 1. 添加角色常量
public static final String ROLE_NEW_ROLE = "NEW_ROLE";

// 2. 添加角色组合
public static final Set<String> NEW_ROLE_ROLES = new HashSet<>(Arrays.asList(
    ROLE_NEW_ROLE, ROLE_USER
));

// 3. 在 getRolePermissions 方法中添加映射
case ROLE_NEW_ROLE:
    return NEW_ROLE_ROLES;

// 4. 在 getAllRoles 方法中添加角色
```

### 2. 在代码中使用角色

```java
// 检查用户是否有指定角色
if (RoleConfig.hasRole(userRoles, RoleConfig.ROLE_ADMIN)) {
    // 执行管理员操作
}

// 检查是否是管理员
if (RoleConfig.isAdmin(userRoles)) {
    // 执行管理员操作
}

// 获取角色的所有权限
Set<String> permissions = RoleConfig.getRolePermissions(RoleConfig.ROLE_ADMIN);
```

### 3. 在控制器中使用权限注解

```java
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<?> adminOnlyMethod() {
    // 只有管理员可以访问
}

@PreAuthorize("hasRole('SUPER_ADMIN')")
public ResponseEntity<?> superAdminOnlyMethod() {
    // 只有超级管理员可以访问
}

@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public ResponseEntity<?> adminOrSuperAdminMethod() {
    // 管理员或超级管理员可以访问
}
```

## 角色层级关系

```
SUPER_ADMIN > ADMIN > USER
SUPER_ADMIN > ADMIN > USER
CONTENT_MANAGER > USER
FINANCE > USER
CUSTOMER_SERVICE > USER
USER
GUEST
```

## 权限说明

### SUPER_ADMIN (超级管理员)
- 拥有所有权限
- 可以管理所有用户和角色
- 可以访问所有API接口

### ADMIN (管理员)
- 拥有大部分管理权限
- 可以管理普通用户
- 可以访问大部分管理接口

### USER (普通用户)
- 基础用户权限
- 可以访问基本的用户功能

### GUEST (访客)
- 只读权限
- 只能查看公开信息

### CONTENT_MANAGER (内容管理员)
- 管理内容相关权限
- 可以管理文章、评论等

### FINANCE (财务)
- 财务相关权限
- 可以查看财务数据

### CUSTOMER_SERVICE (客服)
- 客服相关权限
- 可以处理用户咨询

## 扩展建议

1. **添加权限表**: 可以创建权限表，实现更细粒度的权限控制
2. **角色继承**: 可以实现角色继承机制
3. **动态角色**: 可以实现动态添加角色的功能
4. **权限缓存**: 可以添加权限缓存机制提高性能
5. **审计日志**: 可以添加角色变更的审计日志

## 注意事项

1. 修改角色配置后需要重启应用
2. 角色名称建议使用大写字母和下划线
3. 在添加新角色时要考虑权限继承关系
4. 建议在测试环境中先验证角色配置 