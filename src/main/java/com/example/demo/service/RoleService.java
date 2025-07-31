package com.example.demo.service;

import com.example.demo.config.RoleConfig;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * 角色管理服务
 * 提供角色相关的业务逻辑
 */
@Service
public class RoleService {
    
    /**
     * 获取所有可用角色
     * @return 角色集合
     */
    public Set<String> getAllRoles() {
        return RoleConfig.getAllRoles();
    }
    
    /**
     * 检查用户是否有指定角色
     * @param userRoles 用户角色集合
     * @param targetRole 目标角色
     * @return 是否有该角色
     */
    public boolean hasRole(Set<String> userRoles, String targetRole) {
        return RoleConfig.hasRole(userRoles, targetRole);
    }
    
    /**
     * 检查用户是否有管理员权限
     * @param userRoles 用户角色集合
     * @return 是否有管理员权限
     */
    public boolean isAdmin(Set<String> userRoles) {
        return RoleConfig.isAdmin(userRoles);
    }
    
    /**
     * 检查用户是否有超级管理员权限
     * @param userRoles 用户角色集合
     * @return 是否有超级管理员权限
     */
    public boolean isSuperAdmin(Set<String> userRoles) {
        return RoleConfig.isSuperAdmin(userRoles);
    }
    
    /**
     * 获取角色的所有权限
     * @param role 角色名称
     * @return 权限集合
     */
    public Set<String> getRolePermissions(String role) {
        return RoleConfig.getRolePermissions(role);
    }
    
    /**
     * 为用户分配角色
     * @param currentRoles 当前角色集合
     * @param newRole 新角色
     * @return 更新后的角色集合
     */
    public Set<String> assignRole(Set<String> currentRoles, String newRole) {
        Set<String> updatedRoles = new HashSet<>(currentRoles);
        updatedRoles.add(newRole);
        return updatedRoles;
    }
    
    /**
     * 移除用户角色
     * @param currentRoles 当前角色集合
     * @param roleToRemove 要移除的角色
     * @return 更新后的角色集合
     */
    public Set<String> removeRole(Set<String> currentRoles, String roleToRemove) {
        Set<String> updatedRoles = new HashSet<>(currentRoles);
        updatedRoles.remove(roleToRemove);
        return updatedRoles;
    }
    
    /**
     * 验证角色是否有效
     * @param role 角色名称
     * @return 是否有效
     */
    public boolean isValidRole(String role) {
        return RoleConfig.getAllRoles().contains(role);
    }
    
    /**
     * 获取角色描述
     * @param role 角色名称
     * @return 角色描述
     */
    public String getRoleDescription(String role) {
        switch (role) {
            case RoleConfig.ROLE_SUPER_ADMIN:
                return "超级管理员 - 拥有所有权限，包括系统管理";
            case RoleConfig.ROLE_ADMIN:
                return "管理员 - 拥有大部分管理权限";
            case RoleConfig.ROLE_USER:
                return "普通用户 - 基础用户权限";
            case RoleConfig.ROLE_GUEST:
                return "访客 - 只读权限";
            case RoleConfig.ROLE_CONTENT_MANAGER:
                return "内容管理员 - 管理内容相关权限";
            case RoleConfig.ROLE_FINANCE:
                return "财务 - 财务相关权限";
            case RoleConfig.ROLE_CUSTOMER_SERVICE:
                return "客服 - 客服相关权限";
            default:
                return "未知角色";
        }
    }
    
    /**
     * 获取角色层级关系
     * @param role 角色名称
     * @return 角色层级描述
     */
    public String getRoleHierarchy(String role) {
        switch (role) {
            case RoleConfig.ROLE_SUPER_ADMIN:
                return "SUPER_ADMIN > ADMIN > USER";
            case RoleConfig.ROLE_ADMIN:
                return "ADMIN > USER";
            case RoleConfig.ROLE_CONTENT_MANAGER:
                return "CONTENT_MANAGER > USER";
            case RoleConfig.ROLE_FINANCE:
                return "FINANCE > USER";
            case RoleConfig.ROLE_CUSTOMER_SERVICE:
                return "CUSTOMER_SERVICE > USER";
            case RoleConfig.ROLE_USER:
                return "USER";
            case RoleConfig.ROLE_GUEST:
                return "GUEST";
            default:
                return "未知层级";
        }
    }
} 