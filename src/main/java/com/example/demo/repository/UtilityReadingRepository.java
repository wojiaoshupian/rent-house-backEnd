package com.example.demo.repository;

import com.example.demo.entity.UtilityReading;
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
 * 水电表记录Repository
 */
@Repository
public interface UtilityReadingRepository extends JpaRepository<UtilityReading, Long> {

    /**
     * 根据房间ID查找记录
     */
    List<UtilityReading> findByRoomIdOrderByReadingDateDescReadingTimeDesc(Long roomId);

    /**
     * 根据房间ID和日期范围查找记录
     */
    List<UtilityReading> findByRoomIdAndReadingDateBetweenOrderByReadingDateDescReadingTimeDesc(
            Long roomId, LocalDate startDate, LocalDate endDate);

    /**
     * 根据房间ID和抄表日期查找记录
     */
    Optional<UtilityReading> findByRoomIdAndReadingDate(Long roomId, LocalDate readingDate);

    /**
     * 获取房间最新的抄表记录
     */
    Optional<UtilityReading> findFirstByRoomIdOrderByReadingDateDescReadingTimeDesc(Long roomId);

    /**
     * 根据抄表日期范围查找记录
     */
    Page<UtilityReading> findByReadingDateBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);

    /**
     * 根据抄表人查找记录
     */
    Page<UtilityReading> findByMeterReader(String meterReader, Pageable pageable);

    /**
     * 根据读数状态查找记录
     */
    Page<UtilityReading> findByReadingStatus(UtilityReading.ReadingStatus readingStatus, Pageable pageable);

    /**
     * 复合查询 - 根据多个条件查找记录
     */
    @Query("SELECT ur FROM UtilityReading ur " +
           "LEFT JOIN ur.room r " +
           "WHERE (:roomId IS NULL OR ur.roomId = :roomId) " +
           "AND (:buildingId IS NULL OR r.buildingId = :buildingId) " +
           "AND (:startDate IS NULL OR ur.readingDate >= :startDate) " +
           "AND (:endDate IS NULL OR ur.readingDate <= :endDate) " +
           "AND (:meterReader IS NULL OR ur.meterReader LIKE %:meterReader%) " +
           "AND (:readingType IS NULL OR ur.readingType = :readingType) " +
           "AND (:readingStatus IS NULL OR ur.readingStatus = :readingStatus)")
    Page<UtilityReading> findByConditions(
            @Param("roomId") Long roomId,
            @Param("buildingId") Long buildingId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("meterReader") String meterReader,
            @Param("readingType") UtilityReading.ReadingType readingType,
            @Param("readingStatus") UtilityReading.ReadingStatus readingStatus,
            Pageable pageable);

    /**
     * 获取房间的用量统计
     */
    @Query("SELECT " +
           "SUM(ur.electricityUsage) as totalElectricityUsage, " +
           "SUM(ur.waterUsage) as totalWaterUsage, " +
           "SUM(ur.hotWaterUsage) as totalHotWaterUsage, " +
           "COUNT(*) as readingCount " +
           "FROM UtilityReading ur " +
           "WHERE ur.roomId = :roomId " +
           "AND ur.readingDate BETWEEN :startDate AND :endDate " +
           "AND ur.readingStatus = 'CONFIRMED'")
    Object[] getUsageStatistics(@Param("roomId") Long roomId, 
                               @Param("startDate") LocalDate startDate, 
                               @Param("endDate") LocalDate endDate);

    /**
     * 获取楼宇的用量统计
     */
    @Query("SELECT " +
           "SUM(ur.electricityUsage) as totalElectricityUsage, " +
           "SUM(ur.waterUsage) as totalWaterUsage, " +
           "SUM(ur.hotWaterUsage) as totalHotWaterUsage, " +
           "COUNT(DISTINCT ur.roomId) as roomCount, " +
           "COUNT(*) as readingCount " +
           "FROM UtilityReading ur " +
           "LEFT JOIN ur.room r " +
           "WHERE r.buildingId = :buildingId " +
           "AND ur.readingDate BETWEEN :startDate AND :endDate " +
           "AND ur.readingStatus = 'CONFIRMED'")
    Object[] getBuildingUsageStatistics(@Param("buildingId") Long buildingId, 
                                       @Param("startDate") LocalDate startDate, 
                                       @Param("endDate") LocalDate endDate);

    /**
     * 获取待确认的抄表记录数量
     */
    @Query("SELECT COUNT(*) FROM UtilityReading ur WHERE ur.readingStatus = 'PENDING'")
    Long countPendingReadings();

    /**
     * 获取指定日期的抄表记录数量
     */
    Long countByReadingDate(LocalDate readingDate);

    /**
     * 检查房间在指定日期是否已有抄表记录
     */
    boolean existsByRoomIdAndReadingDate(Long roomId, LocalDate readingDate);

    /**
     * 获取房间的历史读数趋势
     */
    @Query("SELECT ur.readingDate, ur.electricityReading, ur.waterReading, ur.hotWaterReading " +
           "FROM UtilityReading ur " +
           "WHERE ur.roomId = :roomId " +
           "AND ur.readingDate BETWEEN :startDate AND :endDate " +
           "AND ur.readingStatus = 'CONFIRMED' " +
           "ORDER BY ur.readingDate ASC")
    List<Object[]> getReadingTrend(@Param("roomId") Long roomId, 
                                  @Param("startDate") LocalDate startDate, 
                                  @Param("endDate") LocalDate endDate);

    /**
     * 获取异常读数记录（用量异常高或异常低）
     */
    @Query("SELECT ur FROM UtilityReading ur " +
           "WHERE ur.roomId = :roomId " +
           "AND (ur.electricityUsage > :electricityThreshold " +
           "     OR ur.waterUsage > :waterThreshold " +
           "     OR ur.electricityUsage < 0 " +
           "     OR ur.waterUsage < 0) " +
           "ORDER BY ur.readingDate DESC")
    List<UtilityReading> findAbnormalReadings(@Param("roomId") Long roomId,
                                             @Param("electricityThreshold") Double electricityThreshold,
                                             @Param("waterThreshold") Double waterThreshold);

    /**
     * 统计指定房间的抄表记录数量
     */
    long countByRoomId(Long roomId);

    /**
     * 删除指定房间的所有抄表记录
     */
    void deleteByRoomId(Long roomId);
}
