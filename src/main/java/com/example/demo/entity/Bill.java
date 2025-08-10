package com.example.demo.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 账单实体
 */
@Entity
@Table(name = "estimated_bills")
public class Bill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 房间ID
     */
    @Column(name = "room_id", nullable = false)
    private Long roomId;

    /**
     * 账单年月 (格式: YYYY-MM)
     */
    @Column(name = "bill_month", nullable = false, length = 7)
    private String billMonth;

    /**
     * 账单生成日期
     */
    @Column(name = "bill_date", nullable = false)
    private LocalDate billDate;

    /**
     * 房租
     */
    @Column(name = "rent", precision = 10, scale = 2)
    private BigDecimal rent;

    /**
     * 押金
     */
    @Column(name = "deposit", precision = 10, scale = 2)
    private BigDecimal deposit;

    /**
     * 电费单价
     */
    @Column(name = "electricity_unit_price", precision = 8, scale = 4)
    private BigDecimal electricityUnitPrice;

    /**
     * 电费用量
     */
    @Column(name = "electricity_usage", precision = 10, scale = 2)
    private BigDecimal electricityUsage;

    /**
     * 电费金额
     */
    @Column(name = "electricity_amount", precision = 10, scale = 2)
    private BigDecimal electricityAmount;

    /**
     * 水费单价
     */
    @Column(name = "water_unit_price", precision = 8, scale = 4)
    private BigDecimal waterUnitPrice;

    /**
     * 水费用量
     */
    @Column(name = "water_usage", precision = 10, scale = 2)
    private BigDecimal waterUsage;

    /**
     * 水费金额
     */
    @Column(name = "water_amount", precision = 10, scale = 2)
    private BigDecimal waterAmount;

    /**
     * 热水费单价
     */
    @Column(name = "hot_water_unit_price", precision = 8, scale = 4)
    private BigDecimal hotWaterUnitPrice;

    /**
     * 热水费用量
     */
    @Column(name = "hot_water_usage", precision = 10, scale = 2)
    private BigDecimal hotWaterUsage;

    /**
     * 热水费金额
     */
    @Column(name = "hot_water_amount", precision = 10, scale = 2)
    private BigDecimal hotWaterAmount;

    /**
     * 其他费用
     */
    @Column(name = "other_fees", precision = 10, scale = 2)
    private BigDecimal otherFees;

    /**
     * 其他费用说明
     */
    @Column(name = "other_fees_description", columnDefinition = "TEXT")
    private String otherFeesDescription;

    /**
     * 总金额
     */
    @Column(name = "total_amount", precision = 10, scale = 2)
    private BigDecimal totalAmount;

    /**
     * 账单状态
     */
    @Column(name = "bill_status")
    private String billStatus = BillStatus.GENERATED.name();

    /**
     * 备注
     */
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    /**
     * 创建人
     */
    @Column(name = "created_by")
    private Long createdBy;

    /**
     * 创建时间
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * 账单状态枚举
     */
    public enum BillStatus {
        GENERATED("已生成"),
        CONFIRMED("已确认"),
        SENT("已发送"),
        PAID("已支付"),
        CANCELLED("已取消");

        private final String description;

        BillStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    // 构造函数
    public Bill() {
        this.createdAt = LocalDateTime.now();
    }

    // Getter和Setter方法
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getRoomId() { return roomId; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }

    public String getBillMonth() { return billMonth; }
    public void setBillMonth(String billMonth) { this.billMonth = billMonth; }

    public LocalDate getBillDate() { return billDate; }
    public void setBillDate(LocalDate billDate) { this.billDate = billDate; }

    public BigDecimal getRent() { return rent; }
    public void setRent(BigDecimal rent) { this.rent = rent; }

    public BigDecimal getDeposit() { return deposit; }
    public void setDeposit(BigDecimal deposit) { this.deposit = deposit; }

    public BigDecimal getElectricityUnitPrice() { return electricityUnitPrice; }
    public void setElectricityUnitPrice(BigDecimal electricityUnitPrice) { this.electricityUnitPrice = electricityUnitPrice; }

    public BigDecimal getElectricityUsage() { return electricityUsage; }
    public void setElectricityUsage(BigDecimal electricityUsage) { this.electricityUsage = electricityUsage; }

    public BigDecimal getElectricityAmount() { return electricityAmount; }
    public void setElectricityAmount(BigDecimal electricityAmount) { this.electricityAmount = electricityAmount; }

    public BigDecimal getWaterUnitPrice() { return waterUnitPrice; }
    public void setWaterUnitPrice(BigDecimal waterUnitPrice) { this.waterUnitPrice = waterUnitPrice; }

    public BigDecimal getWaterUsage() { return waterUsage; }
    public void setWaterUsage(BigDecimal waterUsage) { this.waterUsage = waterUsage; }

    public BigDecimal getWaterAmount() { return waterAmount; }
    public void setWaterAmount(BigDecimal waterAmount) { this.waterAmount = waterAmount; }

    public BigDecimal getHotWaterUnitPrice() { return hotWaterUnitPrice; }
    public void setHotWaterUnitPrice(BigDecimal hotWaterUnitPrice) { this.hotWaterUnitPrice = hotWaterUnitPrice; }

    public BigDecimal getHotWaterUsage() { return hotWaterUsage; }
    public void setHotWaterUsage(BigDecimal hotWaterUsage) { this.hotWaterUsage = hotWaterUsage; }

    public BigDecimal getHotWaterAmount() { return hotWaterAmount; }
    public void setHotWaterAmount(BigDecimal hotWaterAmount) { this.hotWaterAmount = hotWaterAmount; }

    public BigDecimal getOtherFees() { return otherFees; }
    public void setOtherFees(BigDecimal otherFees) { this.otherFees = otherFees; }

    public String getOtherFeesDescription() { return otherFeesDescription; }
    public void setOtherFeesDescription(String otherFeesDescription) { this.otherFeesDescription = otherFeesDescription; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public BillStatus getBillStatus() { 
        return BillStatus.valueOf(billStatus); 
    }
    public void setBillStatus(BillStatus billStatus) { 
        this.billStatus = billStatus.name(); 
    }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Long getCreatedBy() { return createdBy; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
