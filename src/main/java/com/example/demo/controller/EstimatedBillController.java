package com.example.demo.controller;

import com.example.demo.dto.EstimatedBillDto;
import com.example.demo.entity.EstimatedBill;
import com.example.demo.service.EstimatedBillService;
import com.example.demo.util.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 预估收费账单控制器
 */
@RestController
@RequestMapping("/api/estimated-bills")
public class EstimatedBillController {

    private static final Logger log = LoggerFactory.getLogger(EstimatedBillController.class);

    @Autowired
    private EstimatedBillService estimatedBillService;



    /**
     * 分页查询预估账单
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<EstimatedBillDto>>> getEstimatedBills(
            @RequestParam(required = false) Long roomId,
            @RequestParam(required = false) String billMonth,
            @RequestParam(required = false) EstimatedBill.BillStatus billStatus,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<EstimatedBillDto> billPage = estimatedBillService.getEstimatedBills(roomId, billMonth, billStatus, page, size);
            List<EstimatedBillDto> bills = billPage.getContent();
            
            // 创建分页信息
            ApiResponse.Pagination pagination = new ApiResponse.Pagination(
                billPage.getNumber(),
                billPage.getSize(),
                billPage.getTotalElements(),
                billPage.getTotalPages(),
                billPage.isFirst(),
                billPage.isLast()
            );
            
            log.info("分页查询预估账单成功，页码: {}, 每页: {}, 总数: {}", 
                billPage.getNumber(), billPage.getSize(), billPage.getTotalElements());
            return ResponseEntity.ok(ApiResponse.success(bills, pagination));
        } catch (Exception e) {
            log.error("查询预估账单失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 根据ID获取预估账单
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EstimatedBillDto>> getEstimatedBillById(@PathVariable Long id) {
        try {
            EstimatedBillDto bill = estimatedBillService.getEstimatedBillById(id);
            log.info("获取预估账单成功，账单ID: {}", id);
            return ResponseEntity.ok(ApiResponse.success(bill));
        } catch (Exception e) {
            log.error("获取预估账单失败，账单ID: {}", id, e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 为指定房间生成预估账单
     */
    @PostMapping("/generate")
    public ResponseEntity<ApiResponse<EstimatedBillDto>> generateEstimatedBill(
            @RequestParam Long roomId,
            @RequestParam String billMonth) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Long userId = getUserIdFromAuth(auth);
            EstimatedBillDto bill = estimatedBillService.generateEstimatedBillForRoom(roomId, billMonth, userId);
            
            log.info("生成预估账单成功，房间ID: {}, 账单月份: {}", roomId, billMonth);
            return ResponseEntity.ok(ApiResponse.success(bill));
        } catch (Exception e) {
            log.error("生成预估账单失败，房间ID: {}, 账单月份: {}", roomId, billMonth, e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 更新预估账单状态
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<EstimatedBillDto>> updateBillStatus(
            @PathVariable Long id,
            @RequestParam EstimatedBill.BillStatus status) {
        try {
            EstimatedBillDto bill = estimatedBillService.updateBillStatus(id, status);
            log.info("更新预估账单状态成功，账单ID: {}, 新状态: {}", id, status);
            return ResponseEntity.ok(ApiResponse.success(bill));
        } catch (Exception e) {
            log.error("更新预估账单状态失败，账单ID: {}", id, e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 删除预估账单
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteEstimatedBill(@PathVariable Long id) {
        try {
            estimatedBillService.deleteEstimatedBill(id);
            log.info("删除预估账单成功，账单ID: {}", id);
            return ResponseEntity.ok(ApiResponse.success(null));
        } catch (Exception e) {
            log.error("删除预估账单失败，账单ID: {}", id, e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 手动触发生成当月所有房间的预估账单（测试用）
     */
    @PostMapping("/generate-all")
    public ResponseEntity<ApiResponse<String>> generateAllEstimatedBills() {
        try {
            estimatedBillService.generateMonthlyEstimatedBills();
            log.info("手动触发生成月度预估账单成功");
            return ResponseEntity.ok(ApiResponse.success("月度预估账单生成任务已执行"));
        } catch (Exception e) {
            log.error("手动触发生成月度预估账单失败", e);
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
