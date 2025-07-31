package com.example.demo.config;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 用户角色配置类
 * 统一管理所有用户角色，便于权限分配和管理
 */
@Component
public class RoleConfig {
    
    // ==================== 角色常量定义 ====================
    
    /**
     * 超级管理员角色
     * 拥有所有权限，包括系统管理
     */
    public static final String ROLE_SUPER_ADMIN = "SUPER_ADMIN";
    
    /**
     * 管理员角色
     * 拥有大部分管理权限
     */
    public static final String ROLE_ADMIN = "ADMIN";
    
    /**
     * 普通用户角色
     * 基础用户权限
     */
    public static final String ROLE_USER = "USER";
    
    /**
     * 访客角色
     * 只读权限
     */
    public static final String ROLE_GUEST = "GUEST";
    
    /**
     * 内容管理员角色
     * 管理内容相关权限
     */
    public static final String ROLE_CONTENT_MANAGER = "CONTENT_MANAGER";
    
    /**
     * 财务角色
     * 财务相关权限
     */
    public static final String ROLE_FINANCE = "FINANCE";
    
    /**
     * 客服角色
     * 客服相关权限
     */
    public static final String ROLE_CUSTOMER_SERVICE = "CUSTOMER_SERVICE";
    
    // ==================== 角色组合定义 ====================
    
    /**
     * 超级管理员角色集合
     */
    public static final Set<String> SUPER_ADMIN_ROLES = new HashSet<>(Arrays.asList(
            ROLE_SUPER_ADMIN, ROLE_ADMIN, ROLE_USER
    ));
    
    /**
     * 管理员角色集合
     */
    public static final Set<String> ADMIN_ROLES = new HashSet<>(Arrays.asList(
            ROLE_ADMIN, ROLE_USER
    ));
    
    /**
     * 内容管理员角色集合
     */
    public static final Set<String> CONTENT_MANAGER_ROLES = new HashSet<>(Arrays.asList(
            ROLE_CONTENT_MANAGER, ROLE_USER
    ));
    
    /**
     * 财务角色集合
     */
    public static final Set<String> FINANCE_ROLES = new HashSet<>(Arrays.asList(
            ROLE_FINANCE, ROLE_USER
    ));
    
    /**
     * 客服角色集合
     */
    public static final Set<String> CUSTOMER_SERVICE_ROLES = new HashSet<>(Arrays.asList(
            ROLE_CUSTOMER_SERVICE, ROLE_USER
    ));
    
    /**
     * 普通用户角色集合
     */
    public static final Set<String> USER_ROLES = new HashSet<>(Arrays.asList(
            ROLE_USER
    ));
    
    /**
     * 访客角色集合
     */
    public static final Set<String> GUEST_ROLES = new HashSet<>(Arrays.asList(
            ROLE_GUEST
    ));
    
    // ==================== 角色权限映射 ====================
    
    /**
     * 获取角色的所有权限
     * @param role 角色名称
     * @return 权限集合
     */
    public static Set<String> getRolePermissions(String role) {
        switch (role) {
            case ROLE_SUPER_ADMIN:
                return SUPER_ADMIN_ROLES;
            case ROLE_ADMIN:
                return ADMIN_ROLES;
            case ROLE_CONTENT_MANAGER:
                return CONTENT_MANAGER_ROLES;
            case ROLE_FINANCE:
                return FINANCE_ROLES;
            case ROLE_CUSTOMER_SERVICE:
                return CUSTOMER_SERVICE_ROLES;
            case ROLE_USER:
                return USER_ROLES;
            case ROLE_GUEST:
                return GUEST_ROLES;
            default:
                return new HashSet<>();
        }
    }
    
    /**
     * 检查是否包含指定角色
     * @param userRoles 用户角色集合
     * @param targetRole 目标角色
     * @return 是否包含
     */
    public static boolean hasRole(Set<String> userRoles, String targetRole) {
        return userRoles.contains(targetRole);
    }
    
    /**
     * 检查是否有管理员权限
     * @param userRoles 用户角色集合
     * @return 是否有管理员权限
     */
    public static boolean isAdmin(Set<String> userRoles) {
        return hasRole(userRoles, ROLE_ADMIN) || hasRole(userRoles, ROLE_SUPER_ADMIN);
    }
    
    /**
     * 检查是否有超级管理员权限
     * @param userRoles 用户角色集合
     * @return 是否有超级管理员权限
     */
    public static boolean isSuperAdmin(Set<String> userRoles) {
        return hasRole(userRoles, ROLE_SUPER_ADMIN);
    }
    
    /**
     * 获取所有可用角色列表
     * @return 角色列表
     */
    public static Set<String> getAllRoles() {
        return new HashSet<>(Arrays.asList(
                ROLE_SUPER_ADMIN,
                ROLE_ADMIN,
                ROLE_USER,
                ROLE_GUEST,
                ROLE_CONTENT_MANAGER,
                ROLE_FINANCE,
                ROLE_CUSTOMER_SERVICE
        ));
    }
} 