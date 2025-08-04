package com.example.demo.dto;

import com.example.demo.entity.UtilityReading;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 创建水电表记录请求DTO
 */
public class CreateUtilityReadingRequest {

    /**
     * 房间ID
     */
    private Long roomId;

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

    /**
     * 电表读数(度)
     */
    private BigDecimal electricityReading;

    /**
     * 水表读数(吨)
     */
    private BigDecimal waterReading;

    /**
     * 热水表读数(吨)
     */
    private BigDecimal hotWaterReading;

    /**
     * 抄表人
     */
    private String meterReader;

    /**
     * 抄表类型
     */
    private UtilityReading.ReadingType readingType;

    /**
     * 备注信息
     */
    private String notes;

    /**
     * 抄表照片URL列表
     */
    private List<String> photos;

    // ==================== Getter和Setter方法 ====================

    public Long getRoomId() { return roomId; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }

    public LocalDate getReadingDate() { return readingDate; }
    public void setReadingDate(LocalDate readingDate) { this.readingDate = readingDate; }

    public LocalDateTime getReadingTime() { return readingTime; }
    public void setReadingTime(LocalDateTime readingTime) { this.readingTime = readingTime; }

    public BigDecimal getElectricityReading() { return electricityReading; }
    public void setElectricityReading(BigDecimal electricityReading) { this.electricityReading = electricityReading; }

    public BigDecimal getWaterReading() { return waterReading; }
    public void setWaterReading(BigDecimal waterReading) { this.waterReading = waterReading; }

    public BigDecimal getHotWaterReading() { return hotWaterReading; }
    public void setHotWaterReading(BigDecimal hotWaterReading) { this.hotWaterReading = hotWaterReading; }

    public String getMeterReader() { return meterReader; }
    public void setMeterReader(String meterReader) { this.meterReader = meterReader; }

    public UtilityReading.ReadingType getReadingType() { return readingType; }
    public void setReadingType(UtilityReading.ReadingType readingType) { this.readingType = readingType; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public List<String> getPhotos() { return photos; }
    public void setPhotos(List<String> photos) { this.photos = photos; }
}
