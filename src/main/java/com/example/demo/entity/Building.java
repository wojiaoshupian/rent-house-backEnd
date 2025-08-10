package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "buildings")
public class Building {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "楼宇名称不能为空")
    @Column(name = "building_name", nullable = false, length = 100)
    private String buildingName;
    
    @NotBlank(message = "房东名称不能为空")
    @Column(name = "landlord_name", nullable = false, length = 50)
    private String landlordName;
    
    @NotNull(message = "电费单价不能为空")
    @DecimalMin(value = "0.0", message = "电费单价不能为负数")
    @Column(name = "electricity_unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal electricityUnitPrice;
    
    @NotNull(message = "水费单价不能为空")
    @DecimalMin(value = "0.0", message = "水费单价不能为负数")
    @Column(name = "water_unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal waterUnitPrice;
    
    @DecimalMin(value = "0.0", message = "热水单价不能为负数")
    @Column(name = "hot_water_unit_price", precision = 10, scale = 2)
    private BigDecimal hotWaterUnitPrice;
    
    @DecimalMin(value = "0.0", message = "电费成本不能为负数")
    @Column(name = "electricity_cost", precision = 10, scale = 2)
    private BigDecimal electricityCost;
    
    @DecimalMin(value = "0.0", message = "水费成本不能为负数")
    @Column(name = "water_cost", precision = 10, scale = 2)
    private BigDecimal waterCost;
    
    @DecimalMin(value = "0.0", message = "热水费成本不能为负数")
    @Column(name = "hot_water_cost", precision = 10, scale = 2)
    private BigDecimal hotWaterCost;
    
    @Column(name = "rent_collection_method", nullable = false)
    private String rentCollectionMethod = RentCollectionMethod.FIXED_MONTH_START.name();
    
    @Column(name = "created_by", nullable = false)
    private Long createdBy; // 创建者用户ID
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    public enum RentCollectionMethod {
        FIXED_MONTH_START("固定月初收租"),
        FLEXIBLE("灵活收租");
        
        private final String description;
        
        RentCollectionMethod(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    // 构造函数
    public Building() {}
    
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
    
    public RentCollectionMethod getRentCollectionMethod() { 
        return RentCollectionMethod.valueOf(rentCollectionMethod); 
    }
    public void setRentCollectionMethod(RentCollectionMethod rentCollectionMethod) { 
        this.rentCollectionMethod = rentCollectionMethod.name(); 
    }
    
    public Long getCreatedBy() { return createdBy; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
} 