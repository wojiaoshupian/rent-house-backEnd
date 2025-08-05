package com.example.demo.controller;

import com.example.demo.dto.BillDto;
import com.example.demo.entity.Bill;
import com.example.demo.service.BillService;
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
 * 账单Controller
 */
@RestController
@RequestMapping("/api/bills")
public class BillController {

    private static final Logger log = LoggerFactory.getLogger(BillController.class);

    @Autowired
    private BillService billService;

    /**
     * 分页查询账单
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<BillDto>>> getBills(
            @RequestParam(required = false) Long roomId,
            @RequestParam(required = false) String billMonth,
            @RequestParam(required = false) Bill.BillStatus billStatus,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<BillDto> billPage = billService.getBills(roomId, billMonth, billStatus, page, size);
            List<BillDto> bills = billPage.getContent();

            ApiResponse.Pagination pagination = new ApiResponse.Pagination(
                    billPage.getNumber(),
                    billPage.getSize(),
                    billPage.getTotalElements(),
                    billPage.getTotalPages(),
                    billPage.isFirst(),
                    billPage.isLast()
            );
            ApiResponse<List<BillDto>> response = ApiResponse.success(bills, pagination);

            log.info("分页查询账单成功，页码: {}, 每页: {}, 总数: {}", page, size, billPage.getTotalElements());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("分页查询账单失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 根据ID获取账单详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BillDto>> getBillById(@PathVariable Long id) {
        try {
            BillDto bill = billService.getBillById(id);
            log.info("获取账单详情成功，账单ID: {}", id);
            return ResponseEntity.ok(ApiResponse.success(bill));
        } catch (Exception e) {
            log.error("获取账单详情失败，账单ID: {}", id, e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 手动生成账单
     */
    @PostMapping("/generate")
    public ResponseEntity<ApiResponse<BillDto>> generateBill(
            @RequestParam Long roomId,
            @RequestParam String billMonth) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Long userId = getUserIdFromAuth(auth);
            BillDto bill = billService.generateBillForRoom(roomId, billMonth, userId);
            
            log.info("生成账单成功，房间ID: {}, 账单月份: {}", roomId, billMonth);
            return ResponseEntity.ok(ApiResponse.success(bill));
        } catch (Exception e) {
            log.error("生成账单失败，房间ID: {}, 账单月份: {}", roomId, billMonth, e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 更新账单
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BillDto>> updateBill(
            @PathVariable Long id,
            @RequestBody BillDto billDto) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Long userId = getUserIdFromAuth(auth);
            BillDto bill = billService.updateBill(id, billDto, userId);
            
            log.info("更新账单成功，账单ID: {}", id);
            return ResponseEntity.ok(ApiResponse.success(bill));
        } catch (Exception e) {
            log.error("更新账单失败，账单ID: {}", id, e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 删除账单
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBill(@PathVariable Long id) {
        try {
            billService.deleteBill(id);
            log.info("删除账单成功，账单ID: {}", id);
            return ResponseEntity.ok(ApiResponse.success(null));
        } catch (Exception e) {
            log.error("删除账单失败，账单ID: {}", id, e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 从Authentication中获取用户ID
     */
    private Long getUserIdFromAuth(Authentication auth) {
        if (auth != null && auth.getPrincipal() instanceof org.springframework.security.core.userdetails.User) {
            // 这里需要根据实际情况获取用户ID，可能需要查询数据库
            // 暂时返回1，实际应该根据用户名查询用户ID
            return 1L;
        }
        return null;
    }
}
