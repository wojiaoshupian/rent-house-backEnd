package com.example.demo.controller;

import com.example.demo.util.ApiResponse;
import com.example.demo.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

@RestController
@RequestMapping("/api/roles")
@Tag(name = "角色管理", description = "角色相关操作")
public class RoleController {
    
    private static final Logger log = LoggerFactory.getLogger(RoleController.class);
    
    @Autowired
    private RoleService roleService;
    
    public RoleController() {}
    
    @GetMapping
    @Operation(summary = "获取所有角色", description = "获取系统中所有可用的角色")
    public ResponseEntity<ApiResponse<List<String>>> getAllRoles() {
        List<String> roles = new ArrayList<>(roleService.getAllRoles());
        return ResponseEntity.ok(ApiResponse.success(roles));
    }
    
    @GetMapping("/validate/{role}")
    @Operation(summary = "验证角色", description = "验证指定角色是否有效")
    public ResponseEntity<ApiResponse<Map<String, Object>>> validateRole(@PathVariable String role) {
        boolean isValid = roleService.isValidRole(role);
        Map<String, Object> response = Map.of(
            "role", role,
            "valid", isValid,
            "message", isValid ? "角色有效" : "角色无效"
        );
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @GetMapping("/permissions/{role}")
    @Operation(summary = "获取角色权限", description = "获取指定角色的权限列表")
    public ResponseEntity<ApiResponse<List<String>>> getRolePermissions(@PathVariable String role) {
        try {
            List<String> permissions = new ArrayList<>(roleService.getRolePermissions(role));
            return ResponseEntity.ok(ApiResponse.success(permissions));
        } catch (Exception e) {
            log.error("获取角色权限失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/hierarchy")
    @Operation(summary = "获取角色层级", description = "获取角色层级关系")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getRoleHierarchy() {
        Map<String, Object> hierarchy = new HashMap<>();
        Set<String> allRoles = roleService.getAllRoles();
        
        for (String role : allRoles) {
            hierarchy.put(role, roleService.getRoleHierarchy(role));
        }
        
        return ResponseEntity.ok(ApiResponse.success(hierarchy));
    }
} 