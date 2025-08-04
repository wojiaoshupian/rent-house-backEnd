package com.example.demo.dto;

import com.example.demo.entity.UtilityReading;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 水电表记录DTO
 */
public class UtilityReadingDto {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 房间ID
     */
    private Long roomId;

    /**
     * 房间号
     */
    private String roomNumber;

    /**
     * 楼宇名称
     */
    private String buildingName;

    /**
     * 抄表日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate readingDate;

    /**
     * 抄表时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime readingTime;

    // ==================== 电表相关 ====================
    
    /**
     * 电表读数(度)
     */
    private BigDecimal electricityReading;

    /**
     * 上次电表读数(度)
     */
    private BigDecimal electricityPreviousReading;

    /**
     * 本期用电量(度)
     */
    private BigDecimal electricityUsage;

    // ==================== 水表相关 ====================
    
    /**
     * 水表读数(吨)
     */
    private BigDecimal waterReading;

    /**
     * 上次水表读数(吨)
     */
    private BigDecimal waterPreviousReading;

    /**
     * 本期用水量(吨)
     */
    private BigDecimal waterUsage;

    // ==================== 热水表相关 ====================
    
    /**
     * 热水表读数(吨)
     */
    private BigDecimal hotWaterReading;

    /**
     * 上次热水表读数(吨)
     */
    private BigDecimal hotWaterPreviousReading;

    /**
     * 本期热水用量(吨)
     */
    private BigDecimal hotWaterUsage;

    // ==================== 抄表信息 ====================
    
    /**
     * 抄表人
     */
    private String meterReader;

    /**
     * 抄表类型
     */
    private UtilityReading.ReadingType readingType;

    /**
     * 抄表类型描述
     */
    private String readingTypeDescription;

    /**
     * 读数状态
     */
    private UtilityReading.ReadingStatus readingStatus;

    /**
     * 读数状态描述
     */
    private String readingStatusDescription;

    /**
     * 备注信息
     */
    private String notes;

    /**
     * 抄表照片URL列表
     */
    private List<String> photos;

    // ==================== 审计字段 ====================
    
    /**
     * 创建人ID
     */
    private Long createdBy;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    // ==================== Getter和Setter方法 ====================

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getRoomId() { return roomId; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public String getBuildingName() { return buildingName; }
    public void setBuildingName(String buildingName) { this.buildingName = buildingName; }

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

    public UtilityReading.ReadingType getReadingType() { return readingType; }
    public void setReadingType(UtilityReading.ReadingType readingType) { this.readingType = readingType; }

    public String getReadingTypeDescription() { return readingTypeDescription; }
    public void setReadingTypeDescription(String readingTypeDescription) { this.readingTypeDescription = readingTypeDescription; }

    public UtilityReading.ReadingStatus getReadingStatus() { return readingStatus; }
    public void setReadingStatus(UtilityReading.ReadingStatus readingStatus) { this.readingStatus = readingStatus; }

    public String getReadingStatusDescription() { return readingStatusDescription; }
    public void setReadingStatusDescription(String readingStatusDescription) { this.readingStatusDescription = readingStatusDescription; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public List<String> getPhotos() { return photos; }
    public void setPhotos(List<String> photos) { this.photos = photos; }

    public Long getCreatedBy() { return createdBy; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}


