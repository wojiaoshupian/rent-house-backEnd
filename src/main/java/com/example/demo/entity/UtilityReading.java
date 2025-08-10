package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 水电表记录实体
 */
@Entity
@Table(name = "utility_readings",
       uniqueConstraints = @UniqueConstraint(name = "uk_room_date", columnNames = {"room_id", "reading_date"}),
       indexes = {
           @Index(name = "idx_room_id", columnList = "room_id"),
           @Index(name = "idx_reading_date", columnList = "reading_date"),
           @Index(name = "idx_reading_time", columnList = "reading_time"),
           @Index(name = "idx_room_date", columnList = "room_id, reading_date")
       })
public class UtilityReading {

    /**
     * 主键ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 创建人ID
     */
    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    /**
     * 创建时间
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    /**
     * 更新时间
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * 房间ID
     */
    @Column(name = "room_id", nullable = false)
    private Long roomId;

    /**
     * 抄表日期
     */
    @Column(name = "reading_date", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate readingDate;

    /**
     * 抄表时间
     */
    @Column(name = "reading_time", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime readingTime;

    // ==================== 电表相关 ====================
    
    /**
     * 电表读数(度)
     */
    @Column(name = "electricity_reading", nullable = false, precision = 10, scale = 2)
    private BigDecimal electricityReading;

    /**
     * 上次电表读数(度)
     */
    @Column(name = "electricity_previous_reading", precision = 10, scale = 2)
    private BigDecimal electricityPreviousReading = BigDecimal.ZERO;

    /**
     * 本期用电量(度) - 数据库计算字段
     */
    @Column(name = "electricity_usage", precision = 10, scale = 2, insertable = false, updatable = false)
    private BigDecimal electricityUsage;

    // ==================== 水表相关 ====================
    
    /**
     * 水表读数(吨)
     */
    @Column(name = "water_reading", nullable = false, precision = 10, scale = 2)
    private BigDecimal waterReading;

    /**
     * 上次水表读数(吨)
     */
    @Column(name = "water_previous_reading", precision = 10, scale = 2)
    private BigDecimal waterPreviousReading = BigDecimal.ZERO;

    /**
     * 本期用水量(吨) - 数据库计算字段
     */
    @Column(name = "water_usage", precision = 10, scale = 2, insertable = false, updatable = false)
    private BigDecimal waterUsage;

    // ==================== 热水表相关 ====================
    
    /**
     * 热水表读数(吨)
     */
    @Column(name = "hot_water_reading", precision = 10, scale = 2)
    private BigDecimal hotWaterReading;

    /**
     * 上次热水表读数(吨)
     */
    @Column(name = "hot_water_previous_reading", precision = 10, scale = 2)
    private BigDecimal hotWaterPreviousReading = BigDecimal.ZERO;

    /**
     * 本期热水用量(吨) - 数据库计算字段
     */
    @Column(name = "hot_water_usage", precision = 10, scale = 2, insertable = false, updatable = false)
    private BigDecimal hotWaterUsage;

    // ==================== 抄表信息 ====================
    
    /**
     * 抄表人
     */
    @Column(name = "meter_reader", nullable = false, length = 100)
    private String meterReader;

    /**
     * 抄表类型
     */
    @Column(name = "reading_type")
    private String readingType = ReadingType.MANUAL.name();

    /**
     * 读数状态
     */
    @Column(name = "reading_status")
    private String readingStatus = ReadingStatus.PENDING.name();

    /**
     * 备注信息
     */
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    /**
     * 抄表照片URL列表
     */
    @Column(name = "photos", columnDefinition = "TEXT")
    private String photos;

    // ==================== 关联关系 ====================
    
    /**
     * 关联房间
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", insertable = false, updatable = false)
    private Room room;

    // ==================== Getter和Setter方法 ====================

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getRoomId() { return roomId; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }

    public LocalDate getReadingDate() { return readingDate; }
    public void setReadingDate(LocalDate readingDate) { this.readingDate = readingDate; }

    public LocalDateTime getReadingTime() { return readingTime; }
    public void setReadingTime(LocalDateTime readingTime) { this.readingTime = readingTime; }

    public BigDecimal getElectricityReading() { return electricityReading; }
    public void setElectricityReading(BigDecimal electricityReading) { this.electricityReading = electricityReading; }

    public BigDecimal getElectricityPreviousReading() { return electricityPreviousReading; }
    public void setElectricityPreviousReading(BigDecimal electricityPreviousReading) { this.electricityPreviousReading = electricityPreviousReading; }

    public BigDecimal getElectricityUsage() { return electricityUsage; }
    public void setElectricityUsage(BigDecimal electricityUsage) { this.electricityUsage = electricityUsage; }

    public BigDecimal getWaterReading() { return waterReading; }
    public void setWaterReading(BigDecimal waterReading) { this.waterReading = waterReading; }

    public BigDecimal getWaterPreviousReading() { return waterPreviousReading; }
    public void setWaterPreviousReading(BigDecimal waterPreviousReading) { this.waterPreviousReading = waterPreviousReading; }

    public BigDecimal getWaterUsage() { return waterUsage; }
    public void setWaterUsage(BigDecimal waterUsage) { this.waterUsage = waterUsage; }

    public BigDecimal getHotWaterReading() { return hotWaterReading; }
    public void setHotWaterReading(BigDecimal hotWaterReading) { this.hotWaterReading = hotWaterReading; }

    public BigDecimal getHotWaterPreviousReading() { return hotWaterPreviousReading; }
    public void setHotWaterPreviousReading(BigDecimal hotWaterPreviousReading) { this.hotWaterPreviousReading = hotWaterPreviousReading; }

    public BigDecimal getHotWaterUsage() { return hotWaterUsage; }
    public void setHotWaterUsage(BigDecimal hotWaterUsage) { this.hotWaterUsage = hotWaterUsage; }

    public String getMeterReader() { return meterReader; }
    public void setMeterReader(String meterReader) { this.meterReader = meterReader; }

    public ReadingType getReadingType() { 
        return ReadingType.valueOf(readingType); 
    }
    public void setReadingType(ReadingType readingType) { 
        this.readingType = readingType.name(); 
    }

    public ReadingStatus getReadingStatus() { 
        return ReadingStatus.valueOf(readingStatus); 
    }
    public void setReadingStatus(ReadingStatus readingStatus) { 
        this.readingStatus = readingStatus.name(); 
    }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getPhotos() { return photos; }
    public void setPhotos(String photos) { this.photos = photos; }

    public Room getRoom() { return room; }
    public void setRoom(Room room) { this.room = room; }

    public Long getCreatedBy() { return createdBy; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // ==================== 枚举定义 ====================

    /**
     * 抄表类型
     */
    public enum ReadingType {
        MANUAL("手动抄表"),
        AUTO("自动抄表"),
        ESTIMATED("估算读数");

        private final String description;

        ReadingType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 读数状态
     */
    public enum ReadingStatus {
        PENDING("待确认"),
        CONFIRMED("已确认"),
        DISPUTED("有争议");

        private final String description;

        ReadingStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}
