package com.example.demo.dto;

import com.example.demo.entity.UtilityReading;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

/**
 * 水电表记录查询条件DTO
 */
public class UtilityReadingQueryDto {

    /**
     * 房间ID
     */
    private Long roomId;

    /**
     * 楼宇ID
     */
    private Long buildingId;

    /**
     * 开始日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    /**
     * 结束日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    /**
     * 抄表人
     */
    private String meterReader;

    /**
     * 抄表类型
     */
    private UtilityReading.ReadingType readingType;

    /**
     * 读数状态
     */
    private UtilityReading.ReadingStatus readingStatus;

    /**
     * 页码
     */
    private Integer page = 0;

    /**
     * 每页大小
     */
    private Integer size = 10;

    /**
     * 排序字段
     */
    private String sortBy = "readingDate";

    /**
     * 排序方向
     */
    private String sortDirection = "DESC";

    // ==================== Getter和Setter方法 ====================

    public Long getRoomId() { return roomId; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }

    public Long getBuildingId() { return buildingId; }
    public void setBuildingId(Long buildingId) { this.buildingId = buildingId; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public String getMeterReader() { return meterReader; }
    public void setMeterReader(String meterReader) { this.meterReader = meterReader; }

    public UtilityReading.ReadingType getReadingType() { return readingType; }
    public void setReadingType(UtilityReading.ReadingType readingType) { this.readingType = readingType; }

    public UtilityReading.ReadingStatus getReadingStatus() { return readingStatus; }
    public void setReadingStatus(UtilityReading.ReadingStatus readingStatus) { this.readingStatus = readingStatus; }

    public Integer getPage() { return page; }
    public void setPage(Integer page) { this.page = page; }

    public Integer getSize() { return size; }
    public void setSize(Integer size) { this.size = size; }

    public String getSortBy() { return sortBy; }
    public void setSortBy(String sortBy) { this.sortBy = sortBy; }

    public String getSortDirection() { return sortDirection; }
    public void setSortDirection(String sortDirection) { this.sortDirection = sortDirection; }
}
