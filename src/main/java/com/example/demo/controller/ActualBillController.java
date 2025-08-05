package com.example.demo.controller;

import com.example.demo.dto.ActualBillDto;
import com.example.demo.entity.ActualBill;
import com.example.demo.service.ActualBillService;
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

/**
 * 实际收费账单控制器
 */
@RestController
@RequestMapping("/api/actual-bills")
public class ActualBillController {

    private static final Logger log = LoggerFactory.getLogger(ActualBillController.class);

    @Autowired
    private ActualBillService actualBillService;



    /**
     * 分页查询实际账单
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<ActualBillDto>>> getActualBills(
            @RequestParam(required = false) Long roomId,
            @RequestParam(required = false) String billMonth,
            @RequestParam(required = false) ActualBill.BillStatus billStatus,
            @RequestParam(required = false) ActualBill.PaymentStatus paymentStatus,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<ActualBillDto> billPage = actualBillService.getActualBills(roomId, billMonth, billStatus, paymentStatus, page, size);
            List<ActualBillDto> bills = billPage.getContent();
            
            // 创建分页信息
            ApiResponse.Pagination pagination = new ApiResponse.Pagination(
                billPage.getNumber(),
                billPage.getSize(),
                billPage.getTotalElements(),
                billPage.getTotalPages(),
                billPage.isFirst(),
                billPage.isLast()
            );
            
            log.info("分页查询实际账单成功，页码: {}, 每页: {}, 总数: {}", 
                billPage.getNumber(), billPage.getSize(), billPage.getTotalElements());
            return ResponseEntity.ok(ApiResponse.success(bills, pagination));
        } catch (Exception e) {
            log.error("查询实际账单失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 根据ID获取实际账单
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ActualBillDto>> getActualBillById(@PathVariable Long id) {
        try {
            ActualBillDto bill = actualBillService.getActualBillById(id);
            log.info("获取实际账单成功，账单ID: {}", id);
            return ResponseEntity.ok(ApiResponse.success(bill));
        } catch (Exception e) {
            log.error("获取实际账单失败，账单ID: {}", id, e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 基于预估账单创建实际账单
     */
    @PostMapping("/from-estimated/{estimatedBillId}")
    public ResponseEntity<ApiResponse<ActualBillDto>> createFromEstimatedBill(
            @PathVariable Long estimatedBillId) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Long userId = getUserIdFromAuth(auth);
            ActualBillDto bill = actualBillService.createActualBillFromEstimated(estimatedBillId, userId);
            
            log.info("基于预估账单创建实际账单成功，预估账单ID: {}, 实际账单ID: {}", estimatedBillId, bill.getId());
            return ResponseEntity.ok(ApiResponse.success(bill));
        } catch (Exception e) {
            log.error("基于预估账单创建实际账单失败，预估账单ID: {}", estimatedBillId, e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 手动创建实际账单
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ActualBillDto>> createActualBill(
            @RequestBody ActualBillDto billDto) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Long userId = getUserIdFromAuth(auth);
            ActualBillDto bill = actualBillService.createActualBill(billDto, userId);
            
            log.info("手动创建实际账单成功，房间ID: {}, 账单月份: {}", billDto.getRoomId(), billDto.getBillMonth());
            return ResponseEntity.ok(ApiResponse.success(bill));
        } catch (Exception e) {
            log.error("手动创建实际账单失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 更新实际账单
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ActualBillDto>> updateActualBill(
            @PathVariable Long id,
            @RequestBody ActualBillDto billDto) {
        try {
            ActualBillDto bill = actualBillService.updateActualBill(id, billDto);
            log.info("更新实际账单成功，账单ID: {}", id);
            return ResponseEntity.ok(ApiResponse.success(bill));
        } catch (Exception e) {
            log.error("更新实际账单失败，账单ID: {}", id, e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 更新账单状态
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<ActualBillDto>> updateBillStatus(
            @PathVariable Long id,
            @RequestParam ActualBill.BillStatus status) {
        try {
            ActualBillDto bill = actualBillService.updateBillStatus(id, status);
            log.info("更新实际账单状态成功，账单ID: {}, 新状态: {}", id, status);
            return ResponseEntity.ok(ApiResponse.success(bill));
        } catch (Exception e) {
            log.error("更新实际账单状态失败，账单ID: {}", id, e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 更新支付状态
     */
    @PutMapping("/{id}/payment")
    public ResponseEntity<ApiResponse<ActualBillDto>> updatePaymentStatus(
            @PathVariable Long id,
            @RequestParam ActualBill.PaymentStatus paymentStatus,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate paymentDate,
            @RequestParam(required = false) String paymentMethod) {
        try {
            ActualBillDto bill = actualBillService.updatePaymentStatus(id, paymentStatus, paymentDate, paymentMethod);
            log.info("更新实际账单支付状态成功，账单ID: {}, 支付状态: {}", id, paymentStatus);
            return ResponseEntity.ok(ApiResponse.success(bill));
        } catch (Exception e) {
            log.error("更新实际账单支付状态失败，账单ID: {}", id, e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 删除实际账单
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteActualBill(@PathVariable Long id) {
        try {
            actualBillService.deleteActualBill(id);
            log.info("删除实际账单成功，账单ID: {}", id);
            return ResponseEntity.ok(ApiResponse.success(null));
        } catch (Exception e) {
            log.error("删除实际账单失败，账单ID: {}", id, e);
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
