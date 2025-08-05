package com.example.demo.repository;

import com.example.demo.entity.EstimatedBill;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 预估收费账单Repository
 */
@Repository
public interface EstimatedBillRepository extends JpaRepository<EstimatedBill, Long> {

    /**
     * 根据房间ID查询账单
     */
    List<EstimatedBill> findByRoomIdOrderByBillMonthDesc(Long roomId);

    /**
     * 根据房间ID和账单月份查询账单
     */
    Optional<EstimatedBill> findByRoomIdAndBillMonth(Long roomId, String billMonth);

    /**
     * 根据账单月份查询所有账单
     */
    List<EstimatedBill> findByBillMonth(String billMonth);

    /**
     * 根据账单状态查询账单
     */
    List<EstimatedBill> findByBillStatus(EstimatedBill.BillStatus billStatus);

    /**
     * 分页查询账单
     */
    @Query("SELECT e FROM EstimatedBill e WHERE " +
           "(:roomId IS NULL OR e.roomId = :roomId) AND " +
           "(:billMonth IS NULL OR e.billMonth = :billMonth) AND " +
           "(:billStatus IS NULL OR e.billStatus = :billStatus) " +
           "ORDER BY e.billMonth DESC, e.roomId ASC")
    Page<EstimatedBill> findBillsWithFilters(
            @Param("roomId") Long roomId,
            @Param("billMonth") String billMonth,
            @Param("billStatus") EstimatedBill.BillStatus billStatus,
            Pageable pageable);

    /**
     * 检查指定房间和月份是否已存在账单
     */
    boolean existsByRoomIdAndBillMonth(Long roomId, String billMonth);

    /**
     * 获取指定房间的最新账单
     */
    @Query("SELECT e FROM EstimatedBill e WHERE e.roomId = :roomId ORDER BY e.billMonth DESC LIMIT 1")
    Optional<EstimatedBill> findLatestBillByRoomId(@Param("roomId") Long roomId);

    /**
     * 统计指定月份的账单数量
     */
    @Query("SELECT COUNT(e) FROM EstimatedBill e WHERE e.billMonth = :billMonth")
    long countByBillMonth(@Param("billMonth") String billMonth);

    /**
     * 获取指定月份的账单总金额
     */
    @Query("SELECT COALESCE(SUM(e.totalAmount), 0) FROM EstimatedBill e WHERE e.billMonth = :billMonth")
    java.math.BigDecimal getTotalAmountByBillMonth(@Param("billMonth") String billMonth);

    /**
     * 统计指定房间的账单数量
     */
    long countByRoomId(Long roomId);

    /**
     * 删除指定房间的所有账单
     */
    void deleteByRoomId(Long roomId);
}
