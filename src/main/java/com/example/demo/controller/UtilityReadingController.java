package com.example.demo.controller;

import com.example.demo.dto.CreateUtilityReadingRequest;
import com.example.demo.dto.UtilityReadingDto;
import com.example.demo.dto.UtilityReadingQueryDto;
import com.example.demo.service.UtilityReadingService;
import com.example.demo.util.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 水电表记录控制器
 */
@RestController
@RequestMapping("/api/utility-readings")
public class UtilityReadingController {

    private static final Logger log = LoggerFactory.getLogger(UtilityReadingController.class);

    @Autowired
    private UtilityReadingService utilityReadingService;

    /**
     * 创建水电表记录
     */
    @PostMapping
    public ResponseEntity<ApiResponse<UtilityReadingDto>> createReading(
            @RequestBody CreateUtilityReadingRequest request) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Long userId = getUserIdFromAuth(auth);
            
            UtilityReadingDto reading = utilityReadingService.createReading(request, userId);
            log.info("水电表记录创建成功，房间ID: {}, 记录ID: {}", request.getRoomId(), reading.getId());
            
            return ResponseEntity.ok(ApiResponse.success(reading));
        } catch (Exception e) {
            log.error("创建水电表记录失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 根据ID获取水电表记录
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UtilityReadingDto>> getReading(@PathVariable Long id) {
        try {
            UtilityReadingDto reading = utilityReadingService.getReadingById(id);
            log.info("获取水电表记录成功，ID: {}", id);
            
            return ResponseEntity.ok(ApiResponse.success(reading));
        } catch (Exception e) {
            log.error("获取水电表记录失败，ID: {}", id, e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 获取房间的水电表记录列表
     */
    @GetMapping("/room/{roomId}")
    public ResponseEntity<ApiResponse<List<UtilityReadingDto>>> getRoomReadings(@PathVariable Long roomId) {
        try {
            List<UtilityReadingDto> readings = utilityReadingService.getRoomReadings(roomId);
            log.info("获取房间水电表记录成功，房间ID: {}, 记录数: {}", roomId, readings.size());
            
            return ResponseEntity.ok(ApiResponse.success(readings));
        } catch (Exception e) {
            log.error("获取房间水电表记录失败，房间ID: {}", roomId, e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 获取房间指定日期范围的水电表记录
     */
    @GetMapping("/room/{roomId}/range")
    public ResponseEntity<ApiResponse<List<UtilityReadingDto>>> getRoomReadingsByDateRange(
            @PathVariable Long roomId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            List<UtilityReadingDto> readings = utilityReadingService
                    .getRoomReadingsByDateRange(roomId, startDate, endDate);
            log.info("获取房间指定日期范围水电表记录成功，房间ID: {}, 记录数: {}", roomId, readings.size());
            
            return ResponseEntity.ok(ApiResponse.success(readings));
        } catch (Exception e) {
            log.error("获取房间指定日期范围水电表记录失败，房间ID: {}", roomId, e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 获取房间最新的水电表记录
     */
    @GetMapping("/room/{roomId}/latest")
    public ResponseEntity<ApiResponse<UtilityReadingDto>> getLatestRoomReading(@PathVariable Long roomId) {
        try {
            Optional<UtilityReadingDto> reading = utilityReadingService.getLatestRoomReading(roomId);
            log.info("获取房间最新水电表记录成功，房间ID: {}", roomId);
            
            return ResponseEntity.ok(ApiResponse.success(reading.orElse(null)));
        } catch (Exception e) {
            log.error("获取房间最新水电表记录失败，房间ID: {}", roomId, e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 分页查询水电表记录
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<UtilityReadingDto>>> getReadings(UtilityReadingQueryDto queryDto) {
        try {
            // 统一使用分页查询，返回数组格式和分页信息
            Page<UtilityReadingDto> page = utilityReadingService.getReadings(queryDto);
            List<UtilityReadingDto> readings = page.getContent();

            // 创建分页信息
            ApiResponse.Pagination pagination = new ApiResponse.Pagination(
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
            );

            log.info("分页查询水电表记录成功，页码: {}, 每页: {}, 总数: {}",
                page.getNumber(), page.getSize(), page.getTotalElements());
            return ResponseEntity.ok(ApiResponse.success(readings, pagination));
        } catch (Exception e) {
            log.error("查询水电表记录失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 更新水电表记录
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UtilityReadingDto>> updateReading(
            @PathVariable Long id,
            @RequestBody CreateUtilityReadingRequest request) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Long userId = getUserIdFromAuth(auth);
            
            UtilityReadingDto reading = utilityReadingService.updateReading(id, request, userId);
            log.info("水电表记录更新成功，ID: {}", id);
            
            return ResponseEntity.ok(ApiResponse.success(reading));
        } catch (Exception e) {
            log.error("更新水电表记录失败，ID: {}", id, e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 确认水电表记录
     */
    @PostMapping("/{id}/confirm")
    public ResponseEntity<ApiResponse<UtilityReadingDto>> confirmReading(@PathVariable Long id) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Long userId = getUserIdFromAuth(auth);
            
            UtilityReadingDto reading = utilityReadingService.confirmReading(id, userId);
            log.info("水电表记录确认成功，ID: {}", id);
            
            return ResponseEntity.ok(ApiResponse.success(reading));
        } catch (Exception e) {
            log.error("确认水电表记录失败，ID: {}", id, e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 删除水电表记录
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteReading(@PathVariable Long id) {
        try {
            utilityReadingService.deleteReading(id);
            log.info("水电表记录删除成功，ID: {}", id);
            
            return ResponseEntity.ok(ApiResponse.success(null));
        } catch (Exception e) {
            log.error("删除水电表记录失败，ID: {}", id, e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 获取房间用量统计
     */
    @GetMapping("/room/{roomId}/statistics")
    public ResponseEntity<ApiResponse<Object[]>> getRoomUsageStatistics(
            @PathVariable Long roomId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            Object[] statistics = utilityReadingService.getRoomUsageStatistics(roomId, startDate, endDate);
            log.info("获取房间用量统计成功，房间ID: {}", roomId);
            
            return ResponseEntity.ok(ApiResponse.success(statistics));
        } catch (Exception e) {
            log.error("获取房间用量统计失败，房间ID: {}", roomId, e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 获取楼宇用量统计
     */
    @GetMapping("/building/{buildingId}/statistics")
    public ResponseEntity<ApiResponse<Object[]>> getBuildingUsageStatistics(
            @PathVariable Long buildingId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            Object[] statistics = utilityReadingService.getBuildingUsageStatistics(buildingId, startDate, endDate);
            log.info("获取楼宇用量统计成功，楼宇ID: {}", buildingId);
            
            return ResponseEntity.ok(ApiResponse.success(statistics));
        } catch (Exception e) {
            log.error("获取楼宇用量统计失败，楼宇ID: {}", buildingId, e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 获取待确认记录数量
     */
    @GetMapping("/pending/count")
    public ResponseEntity<ApiResponse<Long>> getPendingReadingsCount() {
        try {
            Long count = utilityReadingService.getPendingReadingsCount();
            log.info("获取待确认记录数量成功: {}", count);
            
            return ResponseEntity.ok(ApiResponse.success(count));
        } catch (Exception e) {
            log.error("获取待确认记录数量失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 获取异常读数记录
     */
    @GetMapping("/room/{roomId}/abnormal")
    public ResponseEntity<ApiResponse<List<UtilityReadingDto>>> getAbnormalReadings(
            @PathVariable Long roomId,
            @RequestParam(defaultValue = "1000.0") Double electricityThreshold,
            @RequestParam(defaultValue = "50.0") Double waterThreshold) {
        try {
            List<UtilityReadingDto> readings = utilityReadingService
                    .getAbnormalReadings(roomId, electricityThreshold, waterThreshold);
            log.info("获取异常读数记录成功，房间ID: {}, 记录数: {}", roomId, readings.size());
            
            return ResponseEntity.ok(ApiResponse.success(readings));
        } catch (Exception e) {
            log.error("获取异常读数记录失败，房间ID: {}", roomId, e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 从认证信息中获取用户ID
     */
    private Long getUserIdFromAuth(Authentication auth) {
        // 这里需要根据实际的认证实现来获取用户ID
        // 假设用户名就是用户ID，实际项目中需要查询用户表
        return 1L; // 临时返回固定值
    }
}
