package com.example.demo.controller;

import com.example.demo.util.ApiResponse;
import com.example.demo.dto.RoomDto;
import com.example.demo.dto.UpdateRentalStatusRequest;
import com.example.demo.entity.Room;
import com.example.demo.security.JwtTokenUtil;
import com.example.demo.service.RoomService;
import com.example.demo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/rooms")
@Tag(name = "房间管理", description = "房间管理相关接口")
public class RoomController {

    private static final Logger log = LoggerFactory.getLogger(RoomController.class);

    @Autowired
    private RoomService roomService;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @PostMapping
    @Operation(summary = "创建房间", description = "在指定楼宇中创建新房间")
    public ResponseEntity<ApiResponse<RoomDto>> createRoom(
            @Valid @RequestBody RoomDto roomDto,
            @RequestHeader("Authorization") String authHeader,
            @Parameter(description = "用户ID（可选，如果不提供则从token中获取）") @RequestParam(required = false) Long userId) {
        
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
                        currentUserId = userService.findByUsername(username)
                                .map(user -> user.getId())
                                .orElse(1L); // 默认值
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

            RoomDto createdRoom = roomService.createRoom(roomDto, currentUserId);
            log.info("房间创建成功: {}", createdRoom.getRoomNumber());

            // 创建响应，包含新token和过期时间
            ApiResponse<RoomDto> response = ApiResponse.success(createdRoom, newToken, tokenExpiresAt);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("房间创建失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping
    @Operation(summary = "获取房间列表", description = "获取所有房间或指定楼宇的房间列表")
    public ResponseEntity<ApiResponse<List<RoomDto>>> getRooms(
            @Parameter(description = "楼宇ID（可选）") @RequestParam(required = false) Long buildingId,
            @Parameter(description = "用户ID（可选）") @RequestParam(required = false) Long userId) {
        
        try {
            List<RoomDto> rooms;
            
            if (buildingId != null) {
                rooms = roomService.getRoomsByBuildingId(buildingId);
                log.info("获取楼宇{}的房间列表成功，共{}个房间", buildingId, rooms.size());
            } else if (userId != null) {
                rooms = roomService.getRoomsByUserId(userId);
                log.info("获取用户{}的房间列表成功，共{}个房间", userId, rooms.size());
            } else {
                rooms = roomService.getAllRooms();
                log.info("获取所有房间列表成功，共{}个房间", rooms.size());
            }
            
            return ResponseEntity.ok(ApiResponse.success(rooms));
            
        } catch (Exception e) {
            log.error("获取房间列表失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取房间详情", description = "根据房间ID获取房间详细信息")
    public ResponseEntity<ApiResponse<RoomDto>> getRoomById(@PathVariable Long id) {
        try {
            Optional<RoomDto> room = roomService.getRoomById(id);
            
            if (room.isPresent()) {
                log.info("获取房间详情成功: {}", id);
                return ResponseEntity.ok(ApiResponse.success(room.get()));
            } else {
                log.error("房间不存在: {}", id);
                return ResponseEntity.badRequest().body(ApiResponse.error("房间不存在"));
            }
            
        } catch (Exception e) {
            log.error("获取房间详情失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新房间", description = "更新指定房间的信息")
    public ResponseEntity<ApiResponse<RoomDto>> updateRoom(
            @PathVariable Long id,
            @Valid @RequestBody RoomDto roomDto,
            @RequestHeader("Authorization") String authHeader,
            @Parameter(description = "用户ID（可选，如果不提供则从token中获取）") @RequestParam(required = false) Long userId) {
        
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
                        currentUserId = userService.findByUsername(username)
                                .map(user -> user.getId())
                                .orElse(1L); // 默认值
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

            RoomDto updatedRoom = roomService.updateRoom(id, roomDto, currentUserId);
            log.info("房间更新成功: {}", updatedRoom.getRoomNumber());

            // 创建响应，包含新token和过期时间
            ApiResponse<RoomDto> response = ApiResponse.success(updatedRoom, newToken, tokenExpiresAt);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("房间更新失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 更新房间出租状态
     */
    @PutMapping("/{id}/rental-status")
    @Operation(summary = "更新房间出租状态", description = "更新指定房间的出租状态")
    public ResponseEntity<ApiResponse<RoomDto>> updateRoomRentalStatus(
            @PathVariable Long id,
            @RequestBody UpdateRentalStatusRequest request,
            @RequestHeader("Authorization") String authHeader,
            @Parameter(description = "用户ID（可选，如果不提供则从token中获取）") @RequestParam(required = false) Long userId) {

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
                        currentUserId = userService.findByUsername(username)
                                .map(user -> user.getId())
                                .orElse(1L); // 默认值
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

            RoomDto updatedRoom = roomService.updateRoomRentalStatus(id, request.getRentalStatus(), currentUserId);
            log.info("房间出租状态更新成功: {}, 新状态: {}", id, request.getRentalStatus());

            // 创建响应，包含新token和过期时间
            ApiResponse<RoomDto> response = ApiResponse.success(updatedRoom, newToken, tokenExpiresAt);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("房间出租状态更新失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除房间", description = "删除指定的房间")
    public ResponseEntity<ApiResponse<String>> deleteRoom(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader,
            @Parameter(description = "用户ID（可选，如果不提供则从token中获取）") @RequestParam(required = false) Long userId) {
        
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
                        currentUserId = userService.findByUsername(username)
                                .map(user -> user.getId())
                                .orElse(1L); // 默认值
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

            roomService.deleteRoom(id, currentUserId);
            log.info("房间删除成功: {}", id);

            // 创建响应，包含新token和过期时间
            ApiResponse<String> response = ApiResponse.success("房间删除成功", newToken, tokenExpiresAt);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("房间删除失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/search")
    @Operation(summary = "搜索房间", description = "根据关键词搜索房间")
    public ResponseEntity<ApiResponse<List<RoomDto>>> searchRooms(
            @Parameter(description = "搜索关键词") @RequestParam String keyword) {
        
        try {
            List<RoomDto> rooms = roomService.searchRooms(keyword);
            log.info("搜索房间成功，关键词: {}，结果数量: {}", keyword, rooms.size());
            return ResponseEntity.ok(ApiResponse.success(rooms));
            
        } catch (Exception e) {
            log.error("搜索房间失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/count")
    @Operation(summary = "统计房间数量", description = "统计指定楼宇的房间数量")
    public ResponseEntity<ApiResponse<Long>> countRooms(
            @Parameter(description = "楼宇ID") @RequestParam Long buildingId) {
        
        try {
            long count = roomService.countRoomsByBuildingId(buildingId);
            log.info("统计楼宇{}的房间数量: {}", buildingId, count);
            return ResponseEntity.ok(ApiResponse.success(count));
            
        } catch (Exception e) {
            log.error("统计房间数量失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
