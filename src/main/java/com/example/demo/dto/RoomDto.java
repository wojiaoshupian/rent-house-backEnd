package com.example.demo.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class RoomDto {
    
    private Long id;
    
    @NotBlank(message = "房号不能为空")
    private String roomNumber;
    
    @NotNull(message = "租金不能为空")
    @DecimalMin(value = "0.0", message = "租金不能为负数")
    private BigDecimal rent;
    
    @NotNull(message = "默认押金不能为空")
    @DecimalMin(value = "0.0", message = "默认押金不能为负数")
    private BigDecimal defaultDeposit;
    
    // 电费单价，如果为null则使用楼宇设置
    @DecimalMin(value = "0.0", message = "电费单价不能为负数")
    private BigDecimal electricityUnitPrice;
    
    // 水费单价，如果为null则使用楼宇设置
    @DecimalMin(value = "0.0", message = "水费单价不能为负数")
    private BigDecimal waterUnitPrice;
    
    // 热水费单价，如果为null则使用楼宇设置
    @DecimalMin(value = "0.0", message = "热水费单价不能为负数")
    private BigDecimal hotWaterUnitPrice;
    
    @NotNull(message = "楼宇ID不能为空")
    private Long buildingId;
    
    // 楼宇信息（用于显示）
    private String buildingName;
    private String landlordName;
    
    // 有效的费用单价（计算后的值，用于前端显示）
    private BigDecimal effectiveElectricityUnitPrice;
    private BigDecimal effectiveWaterUnitPrice;
    private BigDecimal effectiveHotWaterUnitPrice;
    
    private Long createdBy;
    private String createdByUsername; // 创建者用户名
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 构造函数
    public RoomDto() {}
    
    // Getter和Setter方法
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }
    
    public BigDecimal getRent() { return rent; }
    public void setRent(BigDecimal rent) { this.rent = rent; }
    
    public BigDecimal getDefaultDeposit() { return defaultDeposit; }
    public void setDefaultDeposit(BigDecimal defaultDeposit) { this.defaultDeposit = defaultDeposit; }
    
    public BigDecimal getElectricityUnitPrice() { return electricityUnitPrice; }
    public void setElectricityUnitPrice(BigDecimal electricityUnitPrice) { this.electricityUnitPrice = electricityUnitPrice; }
    
    public BigDecimal getWaterUnitPrice() { return waterUnitPrice; }
    public void setWaterUnitPrice(BigDecimal waterUnitPrice) { this.waterUnitPrice = waterUnitPrice; }
    
    public BigDecimal getHotWaterUnitPrice() { return hotWaterUnitPrice; }
    public void setHotWaterUnitPrice(BigDecimal hotWaterUnitPrice) { this.hotWaterUnitPrice = hotWaterUnitPrice; }
    
    public Long getBuildingId() { return buildingId; }
    public void setBuildingId(Long buildingId) { this.buildingId = buildingId; }
    
    public String getBuildingName() { return buildingName; }
    public void setBuildingName(String buildingName) { this.buildingName = buildingName; }
    
    public String getLandlordName() { return landlordName; }
    public void setLandlordName(String landlordName) { this.landlordName = landlordName; }
    
    public BigDecimal getEffectiveElectricityUnitPrice() { return effectiveElectricityUnitPrice; }
    public void setEffectiveElectricityUnitPrice(BigDecimal effectiveElectricityUnitPrice) { this.effectiveElectricityUnitPrice = effectiveElectricityUnitPrice; }
    
    public BigDecimal getEffectiveWaterUnitPrice() { return effectiveWaterUnitPrice; }
    public void setEffectiveWaterUnitPrice(BigDecimal effectiveWaterUnitPrice) { this.effectiveWaterUnitPrice = effectiveWaterUnitPrice; }
    
    public BigDecimal getEffectiveHotWaterUnitPrice() { return effectiveHotWaterUnitPrice; }
    public void setEffectiveHotWaterUnitPrice(BigDecimal effectiveHotWaterUnitPrice) { this.effectiveHotWaterUnitPrice = effectiveHotWaterUnitPrice; }
    
    public Long getCreatedBy() { return createdBy; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }
    
    public String getCreatedByUsername() { return createdByUsername; }
    public void setCreatedByUsername(String createdByUsername) { this.createdByUsername = createdByUsername; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
