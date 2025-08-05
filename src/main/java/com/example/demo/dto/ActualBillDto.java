package com.example.demo.dto;

import com.example.demo.entity.ActualBill;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 实际收费账单DTO
 */
public class ActualBillDto {

    private Long id;
    private Long estimatedBillId;
    private Long roomId;
    private String roomNumber;
    private String buildingName;
    private String billMonth;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate billDate;

    private BigDecimal rent;
    private BigDecimal deposit;
    private BigDecimal electricityUnitPrice;
    private BigDecimal electricityUsage;
    private BigDecimal electricityAmount;
    private BigDecimal waterUnitPrice;
    private BigDecimal waterUsage;
    private BigDecimal waterAmount;
    private BigDecimal hotWaterUnitPrice;
    private BigDecimal hotWaterUsage;
    private BigDecimal hotWaterAmount;
    private BigDecimal otherFees;
    private String otherFeesDescription;
    private BigDecimal totalAmount;
    private ActualBill.BillStatus billStatus;
    private String billStatusDescription;
    private ActualBill.PaymentStatus paymentStatus;
    private String paymentStatusDescription;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate paymentDate;

    private String paymentMethod;
    private String notes;
    private Long createdBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    // 构造函数
    public ActualBillDto() {}

    // Getter和Setter方法
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getEstimatedBillId() { return estimatedBillId; }
    public void setEstimatedBillId(Long estimatedBillId) { this.estimatedBillId = estimatedBillId; }

    public Long getRoomId() { return roomId; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public String getBuildingName() { return buildingName; }
    public void setBuildingName(String buildingName) { this.buildingName = buildingName; }

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

    public ActualBill.BillStatus getBillStatus() { return billStatus; }
    public void setBillStatus(ActualBill.BillStatus billStatus) { this.billStatus = billStatus; }

    public String getBillStatusDescription() { return billStatusDescription; }
    public void setBillStatusDescription(String billStatusDescription) { this.billStatusDescription = billStatusDescription; }

    public ActualBill.PaymentStatus getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(ActualBill.PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getPaymentStatusDescription() { return paymentStatusDescription; }
    public void setPaymentStatusDescription(String paymentStatusDescription) { this.paymentStatusDescription = paymentStatusDescription; }

    public LocalDate getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDate paymentDate) { this.paymentDate = paymentDate; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Long getCreatedBy() { return createdBy; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
