package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "rooms")
public class Room {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "房号不能为空")
    @Column(name = "room_number", nullable = false, length = 50)
    private String roomNumber;
    
    @NotNull(message = "租金不能为空")
    @DecimalMin(value = "0.0", message = "租金不能为负数")
    @Column(name = "rent", nullable = false, precision = 10, scale = 2)
    private BigDecimal rent;
    
    @NotNull(message = "默认押金不能为空")
    @DecimalMin(value = "0.0", message = "默认押金不能为负数")
    @Column(name = "default_deposit", nullable = false, precision = 10, scale = 2)
    private BigDecimal defaultDeposit;
    
    // 电费单价，如果为null则使用楼宇设置
    @DecimalMin(value = "0.0", message = "电费单价不能为负数")
    @Column(name = "electricity_unit_price", precision = 10, scale = 2)
    private BigDecimal electricityUnitPrice;
    
    // 水费单价，如果为null则使用楼宇设置
    @DecimalMin(value = "0.0", message = "水费单价不能为负数")
    @Column(name = "water_unit_price", precision = 10, scale = 2)
    private BigDecimal waterUnitPrice;
    
    // 热水费单价，如果为null则使用楼宇设置
    @DecimalMin(value = "0.0", message = "热水费单价不能为负数")
    @Column(name = "hot_water_unit_price", precision = 10, scale = 2)
    private BigDecimal hotWaterUnitPrice;
    
    // 楼宇ID - 多对一关系
    @NotNull(message = "楼宇ID不能为空")
    @Column(name = "building_id", nullable = false)
    private Long buildingId;

    /**
     * 出租状态
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "rental_status", nullable = false)
    private RentalStatus rentalStatus = RentalStatus.VACANT;

    /**
     * 出租状态枚举
     */
    public enum RentalStatus {
        VACANT("空置"),
        RENTED("已出租"),
        MAINTENANCE("维修中"),
        RESERVED("已预订");

        private final String description;

        RentalStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
    
    // 楼宇实体关联 - 多对一关系（暂时注释掉外键约束）
    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "building_id", insertable = false, updatable = false)
    // private Building building;
    
    @Column(name = "created_by", nullable = false)
    private Long createdBy; // 创建者用户ID
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // 构造函数
    public Room() {}
    
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
    
    // public Building getBuilding() { return building; }
    // public void setBuilding(Building building) { this.building = building; }
    
    public Long getCreatedBy() { return createdBy; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public RentalStatus getRentalStatus() { return rentalStatus; }
    public void setRentalStatus(RentalStatus rentalStatus) { this.rentalStatus = rentalStatus; }
    
    /**
     * 获取有效的电费单价（如果房间设置了则使用房间的，否则返回null，由服务层处理）
     */
    public BigDecimal getEffectiveElectricityUnitPrice() {
        return electricityUnitPrice;
    }

    /**
     * 获取有效的水费单价（如果房间设置了则使用房间的，否则返回null，由服务层处理）
     */
    public BigDecimal getEffectiveWaterUnitPrice() {
        return waterUnitPrice;
    }

    /**
     * 获取有效的热水费单价（如果房间设置了则使用房间的，否则返回null，由服务层处理）
     */
    public BigDecimal getEffectiveHotWaterUnitPrice() {
        return hotWaterUnitPrice;
    }
}
