package com.example.demo.controller;

import com.example.demo.util.ApiResponse;
import com.example.demo.dto.BuildingDto;
import com.example.demo.dto.UserDto;
import com.example.demo.entity.UserBuilding;
import com.example.demo.repository.UserBuildingRepository;
import com.example.demo.service.BuildingService;
import com.example.demo.service.UserService;
import com.example.demo.security.JwtTokenUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/buildings")
@Tag(name = "楼宇管理", description = "楼宇相关操作")
public class BuildingController {
    
    private static final Logger log = LoggerFactory.getLogger(BuildingController.class);
    
    @Autowired
    private BuildingService buildingService;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserBuildingRepository userBuildingRepository;

    public BuildingController() {}
    
    @PostMapping
    @Operation(summary = "创建楼宇", description = "创建新的楼宇")
    public ResponseEntity<ApiResponse<BuildingDto>> createBuilding(
            @Valid @RequestBody BuildingDto buildingDto,
            @RequestParam(required = false) Long userId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            // 从JWT token获取用户ID
            Long currentUserId = userId;
            String newToken = null;
            Long tokenExpiresAt = null;

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                if (jwtTokenUtil.validateToken(token)) {
                    String username = jwtTokenUtil.extractUsername(token);
                    // 根据username获取userId
                    if (currentUserId == null) {
                        UserDto user = userService.findByUsername(username).orElse(null);
                        if (user != null) {
                            currentUserId = user.getId();
                        } else {
                            currentUserId = 1L; // 默认值
                        }
                    }
                    // 刷新token
                    newToken = jwtTokenUtil.refreshToken(token);
                    if (newToken != null) {
                        tokenExpiresAt = jwtTokenUtil.calculateExpirationTime();
                    }
                }
            }

            // 如果没有提供userId，使用默认值1
            if (currentUserId == null) {
                currentUserId = 1L;
            }

            BuildingDto createdBuilding = buildingService.createBuilding(buildingDto, currentUserId);
            log.info("楼宇创建成功: {}", createdBuilding.getBuildingName());

            // 创建响应，包含新token和过期时间，返回单个创建的楼宇
            ApiResponse<BuildingDto> response = ApiResponse.success(createdBuilding, newToken, tokenExpiresAt);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("楼宇创建失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping
    @Operation(summary = "获取所有楼宇", description = "获取系统中所有楼宇")
    public ResponseEntity<ApiResponse<List<BuildingDto>>> getAllBuildings() {
        List<BuildingDto> buildings = buildingService.getAllBuildings();
        return ResponseEntity.ok(ApiResponse.success(buildings));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取楼宇", description = "根据楼宇ID获取楼宇信息")
    public ResponseEntity<ApiResponse<BuildingDto>> getBuildingById(@PathVariable Long id) {
        try {
            BuildingDto building = buildingService.getBuildingById(id);
            return ResponseEntity.ok(ApiResponse.success(building));
        } catch (Exception e) {
            log.error("获取楼宇失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/user/{userId}")
    @Operation(summary = "获取用户创建的楼宇", description = "获取指定用户创建的所有楼宇")
    public ResponseEntity<ApiResponse<List<BuildingDto>>> getBuildingsByUser(@PathVariable Long userId) {
        List<BuildingDto> buildings = buildingService.getBuildingsByUser(userId);
        return ResponseEntity.ok(ApiResponse.success(buildings));
    }
    
    @GetMapping("/owned/{userId}")
    @Operation(summary = "获取用户拥有的楼宇", description = "获取指定用户拥有的所有楼宇")
    public ResponseEntity<ApiResponse<List<BuildingDto>>> getUserOwnedBuildings(@PathVariable Long userId) {
        try {
            List<BuildingDto> buildings = buildingService.getUserOwnedBuildings(userId);
            return ResponseEntity.ok(ApiResponse.success(buildings));
        } catch (Exception e) {
            log.error("获取用户拥有的楼宇失败", e);
            return ResponseEntity.ok(ApiResponse.error("获取楼宇列表失败: " + e.getMessage()));
        }
    }

    @GetMapping("/owned/{userId}/simple")
    @Operation(summary = "获取用户拥有的楼宇（简化版）", description = "获取指定用户拥有的楼宇ID列表")
    public ResponseEntity<ApiResponse<String>> getUserOwnedBuildingsSimple(@PathVariable Long userId) {
        try {
            List<UserBuilding> userBuildings = userBuildingRepository.findByUserId(userId);
            List<Long> buildingIds = userBuildings.stream()
                    .map(UserBuilding::getBuildingId)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(ApiResponse.success("用户拥有的楼宇ID: " + buildingIds.toString()));
        } catch (Exception e) {
            log.error("获取用户拥有的楼宇失败", e);
            return ResponseEntity.ok(ApiResponse.error("获取楼宇列表失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/search")
    @Operation(summary = "搜索楼宇", description = "根据关键词搜索楼宇")
    public ResponseEntity<ApiResponse<List<BuildingDto>>> searchBuildings(@RequestParam String keyword) {
        List<BuildingDto> buildings = buildingService.searchBuildings(keyword);
        return ResponseEntity.ok(ApiResponse.success(buildings));
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "更新楼宇", description = "更新楼宇信息")
    public ResponseEntity<ApiResponse<BuildingDto>> updateBuilding(
            @PathVariable Long id,
            @Valid @RequestBody BuildingDto buildingDto) {
        try {
            BuildingDto updatedBuilding = buildingService.updateBuilding(id, buildingDto);
            return ResponseEntity.ok(ApiResponse.success(updatedBuilding));
        } catch (Exception e) {
            log.error("更新楼宇失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "删除楼宇", description = "删除指定楼宇")
    public ResponseEntity<ApiResponse<String>> deleteBuilding(@PathVariable Long id) {
        try {
            buildingService.deleteBuilding(id);
            return ResponseEntity.ok(ApiResponse.success("楼宇删除成功"));
        } catch (Exception e) {
            log.error("删除楼宇失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PostMapping("/{buildingId}/assign/{userId}")
    @Operation(summary = "分配楼宇给用户", description = "将楼宇分配给指定用户")
    public ResponseEntity<ApiResponse<String>> assignBuildingToUser(
            @PathVariable Long buildingId,
            @PathVariable Long userId) {
        try {
            buildingService.assignBuildingToUser(buildingId, userId);
            return ResponseEntity.ok(ApiResponse.success("楼宇分配成功"));
        } catch (Exception e) {
            log.error("楼宇分配失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @DeleteMapping("/{buildingId}/remove/{userId}")
    @Operation(summary = "移除用户的楼宇", description = "从用户移除指定楼宇")
    public ResponseEntity<ApiResponse<String>> removeBuildingFromUser(
            @PathVariable Long buildingId,
            @PathVariable Long userId) {
        try {
            buildingService.removeBuildingFromUser(buildingId, userId);
            return ResponseEntity.ok(ApiResponse.success("楼宇移除成功"));
        } catch (Exception e) {
            log.error("楼宇移除失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
} 