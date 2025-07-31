package com.example.demo.dto;

import com.example.demo.entity.Building;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class BuildingDto {
    
    private Long id;
    
    @NotBlank(message = "楼宇名称不能为空")
    private String buildingName;
    
    @NotBlank(message = "房东名称不能为空")
    private String landlordName;
    
    @NotNull(message = "电费单价不能为空")
    @DecimalMin(value = "0.0", message = "电费单价不能为负数")
    private BigDecimal electricityUnitPrice;
    
    @NotNull(message = "水费单价不能为空")
    @DecimalMin(value = "0.0", message = "水费单价不能为负数")
    private BigDecimal waterUnitPrice;
    
    @DecimalMin(value = "0.0", message = "热水单价不能为负数")
    private BigDecimal hotWaterUnitPrice;
    
    @DecimalMin(value = "0.0", message = "电费成本不能为负数")
    private BigDecimal electricityCost;
    
    @DecimalMin(value = "0.0", message = "水费成本不能为负数")
    private BigDecimal waterCost;
    
    @DecimalMin(value = "0.0", message = "热水费成本不能为负数")
    private BigDecimal hotWaterCost;
    
    private Building.RentCollectionMethod rentCollectionMethod = Building.RentCollectionMethod.FIXED_MONTH_START;
    
    private Long createdBy;
    private String createdByUsername; // 创建者用户名
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 构造函数
    public BuildingDto() {}
    
    // Getter和Setter方法
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getBuildingName() { return buildingName; }
    public void setBuildingName(String buildingName) { this.buildingName = buildingName; }
    
    public String getLandlordName() { return landlordName; }
    public void setLandlordName(String landlordName) { this.landlordName = landlordName; }
    
    public BigDecimal getElectricityUnitPrice() { return electricityUnitPrice; }
    public void setElectricityUnitPrice(BigDecimal electricityUnitPrice) { this.electricityUnitPrice = electricityUnitPrice; }
    
    public BigDecimal getWaterUnitPrice() { return waterUnitPrice; }
    public void setWaterUnitPrice(BigDecimal waterUnitPrice) { this.waterUnitPrice = waterUnitPrice; }
    
    public BigDecimal getHotWaterUnitPrice() { return hotWaterUnitPrice; }
    public void setHotWaterUnitPrice(BigDecimal hotWaterUnitPrice) { this.hotWaterUnitPrice = hotWaterUnitPrice; }
    
    public BigDecimal getElectricityCost() { return electricityCost; }
    public void setElectricityCost(BigDecimal electricityCost) { this.electricityCost = electricityCost; }
    
    public BigDecimal getWaterCost() { return waterCost; }
    public void setWaterCost(BigDecimal waterCost) { this.waterCost = waterCost; }
    
    public BigDecimal getHotWaterCost() { return hotWaterCost; }
    public void setHotWaterCost(BigDecimal hotWaterCost) { this.hotWaterCost = hotWaterCost; }
    
    public Building.RentCollectionMethod getRentCollectionMethod() { return rentCollectionMethod; }
    public void setRentCollectionMethod(Building.RentCollectionMethod rentCollectionMethod) { this.rentCollectionMethod = rentCollectionMethod; }
    
    public Long getCreatedBy() { return createdBy; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }
    
    public String getCreatedByUsername() { return createdByUsername; }
    public void setCreatedByUsername(String createdByUsername) { this.createdByUsername = createdByUsername; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
} 