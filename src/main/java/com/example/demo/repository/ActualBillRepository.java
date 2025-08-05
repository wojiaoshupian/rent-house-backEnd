package com.example.demo.repository;

import com.example.demo.entity.ActualBill;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 实际收费账单Repository
 */
@Repository
public interface ActualBillRepository extends JpaRepository<ActualBill, Long> {

    /**
     * 根据房间ID查询账单
     */
    List<ActualBill> findByRoomIdOrderByBillMonthDesc(Long roomId);

    /**
     * 根据房间ID和账单月份查询账单
     */
    Optional<ActualBill> findByRoomIdAndBillMonth(Long roomId, String billMonth);

    /**
     * 根据预估账单ID查询实际账单
     */
    Optional<ActualBill> findByEstimatedBillId(Long estimatedBillId);

    /**
     * 根据账单月份查询所有账单
     */
    List<ActualBill> findByBillMonth(String billMonth);

    /**
     * 根据账单状态查询账单
     */
    List<ActualBill> findByBillStatus(ActualBill.BillStatus billStatus);

    /**
     * 根据支付状态查询账单
     */
    List<ActualBill> findByPaymentStatus(ActualBill.PaymentStatus paymentStatus);

    /**
     * 分页查询账单
     */
    @Query("SELECT a FROM ActualBill a WHERE " +
           "(:roomId IS NULL OR a.roomId = :roomId) AND " +
           "(:billMonth IS NULL OR a.billMonth = :billMonth) AND " +
           "(:billStatus IS NULL OR a.billStatus = :billStatus) AND " +
           "(:paymentStatus IS NULL OR a.paymentStatus = :paymentStatus) " +
           "ORDER BY a.billMonth DESC, a.roomId ASC")
    Page<ActualBill> findBillsWithFilters(
            @Param("roomId") Long roomId,
            @Param("billMonth") String billMonth,
            @Param("billStatus") ActualBill.BillStatus billStatus,
            @Param("paymentStatus") ActualBill.PaymentStatus paymentStatus,
            Pageable pageable);

    /**
     * 检查指定房间和月份是否已存在账单
     */
    boolean existsByRoomIdAndBillMonth(Long roomId, String billMonth);

    /**
     * 获取指定房间的最新账单
     */
    @Query("SELECT a FROM ActualBill a WHERE a.roomId = :roomId ORDER BY a.billMonth DESC LIMIT 1")
    Optional<ActualBill> findLatestBillByRoomId(@Param("roomId") Long roomId);

    /**
     * 统计指定月份的账单数量
     */
    @Query("SELECT COUNT(a) FROM ActualBill a WHERE a.billMonth = :billMonth")
    long countByBillMonth(@Param("billMonth") String billMonth);

    /**
     * 获取指定月份的账单总金额
     */
    @Query("SELECT COALESCE(SUM(a.totalAmount), 0) FROM ActualBill a WHERE a.billMonth = :billMonth")
    java.math.BigDecimal getTotalAmountByBillMonth(@Param("billMonth") String billMonth);

    /**
     * 获取指定日期范围内的逾期账单
     */
    @Query("SELECT a FROM ActualBill a WHERE a.billStatus = 'OVERDUE' AND a.billDate BETWEEN :startDate AND :endDate")
    List<ActualBill> findOverdueBillsBetweenDates(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    /**
     * 获取未支付的账单
     */
    @Query("SELECT a FROM ActualBill a WHERE a.paymentStatus IN ('UNPAID', 'PARTIAL') ORDER BY a.billDate ASC")
    List<ActualBill> findUnpaidBills();

    /**
     * 统计指定月份各支付状态的账单数量
     */
    @Query("SELECT a.paymentStatus, COUNT(a) FROM ActualBill a WHERE a.billMonth = :billMonth GROUP BY a.paymentStatus")
    List<Object[]> countByPaymentStatusAndBillMonth(@Param("billMonth") String billMonth);

    /**
     * 统计指定房间的账单数量
     */
    long countByRoomId(Long roomId);

    /**
     * 删除指定房间的所有账单
     */
    void deleteByRoomId(Long roomId);
}
