package com.example.demo.service;

import com.example.demo.dto.ActualBillDto;
import com.example.demo.entity.ActualBill;
import com.example.demo.entity.EstimatedBill;
import com.example.demo.entity.Room;
import com.example.demo.entity.Building;
import com.example.demo.repository.ActualBillRepository;
import com.example.demo.repository.EstimatedBillRepository;
import com.example.demo.repository.RoomRepository;
import com.example.demo.repository.BuildingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

/**
 * 实际收费账单服务
 */
@Service
@Transactional
public class ActualBillService {

    private static final Logger log = LoggerFactory.getLogger(ActualBillService.class);

    @Autowired
    private ActualBillRepository actualBillRepository;

    @Autowired
    private EstimatedBillRepository estimatedBillRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private BuildingRepository buildingRepository;

    /**
     * 基于预估账单创建实际账单
     */
    public ActualBillDto createActualBillFromEstimated(Long estimatedBillId, Long userId) {
        log.info("基于预估账单 {} 创建实际账单", estimatedBillId);

        // 获取预估账单
        EstimatedBill estimatedBill = estimatedBillRepository.findById(estimatedBillId)
                .orElseThrow(() -> new RuntimeException("预估账单不存在"));

        // 检查是否已存在对应的实际账单
        if (actualBillRepository.findByEstimatedBillId(estimatedBillId).isPresent()) {
            throw new RuntimeException("该预估账单对应的实际账单已存在");
        }

        // 创建实际账单，复制预估账单的所有字段
        ActualBill actualBill = new ActualBill();
        actualBill.setEstimatedBillId(estimatedBillId);
        actualBill.setRoomId(estimatedBill.getRoomId());
        actualBill.setBillMonth(estimatedBill.getBillMonth());
        actualBill.setBillDate(LocalDate.now()); // 使用当前日期作为实际账单日期
        actualBill.setRent(estimatedBill.getRent());
        actualBill.setDeposit(estimatedBill.getDeposit());
        actualBill.setElectricityUnitPrice(estimatedBill.getElectricityUnitPrice());
        actualBill.setElectricityUsage(estimatedBill.getElectricityUsage());
        actualBill.setElectricityAmount(estimatedBill.getElectricityAmount());
        actualBill.setWaterUnitPrice(estimatedBill.getWaterUnitPrice());
        actualBill.setWaterUsage(estimatedBill.getWaterUsage());
        actualBill.setWaterAmount(estimatedBill.getWaterAmount());
        actualBill.setHotWaterUnitPrice(estimatedBill.getHotWaterUnitPrice());
        actualBill.setHotWaterUsage(estimatedBill.getHotWaterUsage());
        actualBill.setHotWaterAmount(estimatedBill.getHotWaterAmount());
        actualBill.setOtherFees(estimatedBill.getOtherFees());
        actualBill.setOtherFeesDescription(estimatedBill.getOtherFeesDescription());
        actualBill.setTotalAmount(estimatedBill.getTotalAmount());
        actualBill.setNotes(estimatedBill.getNotes());
        actualBill.setCreatedBy(userId);

        // 保存实际账单
        ActualBill savedBill = actualBillRepository.save(actualBill);
        
        log.info("实际账单创建成功，账单ID: {}", savedBill.getId());
        return convertToDto(savedBill);
    }

    /**
     * 手动创建实际账单
     */
    public ActualBillDto createActualBill(ActualBillDto billDto, Long userId) {
        log.info("手动创建实际账单，房间ID: {}, 账单月份: {}", billDto.getRoomId(), billDto.getBillMonth());

        // 检查是否已存在该房间该月份的实际账单
        if (actualBillRepository.existsByRoomIdAndBillMonth(billDto.getRoomId(), billDto.getBillMonth())) {
            throw new RuntimeException("该房间在指定月份的实际账单已存在");
        }

        ActualBill actualBill = new ActualBill();
        copyDtoToEntity(billDto, actualBill);
        actualBill.setCreatedBy(userId);

        ActualBill savedBill = actualBillRepository.save(actualBill);
        
        log.info("实际账单创建成功，账单ID: {}", savedBill.getId());
        return convertToDto(savedBill);
    }

    /**
     * 更新实际账单
     */
    public ActualBillDto updateActualBill(Long id, ActualBillDto billDto) {
        log.info("更新实际账单，账单ID: {}", id);

        ActualBill existingBill = actualBillRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("实际账单不存在"));

        copyDtoToEntity(billDto, existingBill);
        ActualBill savedBill = actualBillRepository.save(existingBill);
        
        log.info("实际账单更新成功，账单ID: {}", id);
        return convertToDto(savedBill);
    }

    /**
     * 分页查询实际账单
     */
    public Page<ActualBillDto> getActualBills(Long roomId, String billMonth, 
                                             ActualBill.BillStatus billStatus,
                                             ActualBill.PaymentStatus paymentStatus,
                                             int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("billMonth").descending().and(Sort.by("roomId")));
        Page<ActualBill> billPage = actualBillRepository.findBillsWithFilters(roomId, billMonth, billStatus, paymentStatus, pageable);
        
        return billPage.map(this::convertToDto);
    }

    /**
     * 根据ID获取实际账单
     */
    public ActualBillDto getActualBillById(Long id) {
        ActualBill bill = actualBillRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("实际账单不存在"));
        return convertToDto(bill);
    }

    /**
     * 更新账单状态
     */
    public ActualBillDto updateBillStatus(Long id, ActualBill.BillStatus status) {
        ActualBill bill = actualBillRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("实际账单不存在"));
        
        bill.setBillStatus(status);
        ActualBill savedBill = actualBillRepository.save(bill);
        
        log.info("实际账单状态更新成功，账单ID: {}, 新状态: {}", id, status);
        return convertToDto(savedBill);
    }

    /**
     * 更新支付状态
     */
    public ActualBillDto updatePaymentStatus(Long id, ActualBill.PaymentStatus paymentStatus, 
                                           LocalDate paymentDate, String paymentMethod) {
        ActualBill bill = actualBillRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("实际账单不存在"));
        
        bill.setPaymentStatus(paymentStatus);
        bill.setPaymentDate(paymentDate);
        bill.setPaymentMethod(paymentMethod);
        
        // 如果标记为已支付，同时更新账单状态
        if (paymentStatus == ActualBill.PaymentStatus.PAID) {
            bill.setBillStatus(ActualBill.BillStatus.PAID);
        }
        
        ActualBill savedBill = actualBillRepository.save(bill);
        
        log.info("实际账单支付状态更新成功，账单ID: {}, 支付状态: {}", id, paymentStatus);
        return convertToDto(savedBill);
    }

    /**
     * 删除实际账单
     */
    public void deleteActualBill(Long id) {
        if (!actualBillRepository.existsById(id)) {
            throw new RuntimeException("实际账单不存在");
        }
        
        actualBillRepository.deleteById(id);
        log.info("实际账单删除成功，账单ID: {}", id);
    }

    /**
     * 复制DTO到实体
     */
    private void copyDtoToEntity(ActualBillDto dto, ActualBill entity) {
        entity.setRoomId(dto.getRoomId());
        entity.setBillMonth(dto.getBillMonth());
        entity.setBillDate(dto.getBillDate());
        entity.setRent(dto.getRent());
        entity.setDeposit(dto.getDeposit());
        entity.setElectricityUnitPrice(dto.getElectricityUnitPrice());
        entity.setElectricityUsage(dto.getElectricityUsage());
        entity.setElectricityAmount(dto.getElectricityAmount());
        entity.setWaterUnitPrice(dto.getWaterUnitPrice());
        entity.setWaterUsage(dto.getWaterUsage());
        entity.setWaterAmount(dto.getWaterAmount());
        entity.setHotWaterUnitPrice(dto.getHotWaterUnitPrice());
        entity.setHotWaterUsage(dto.getHotWaterUsage());
        entity.setHotWaterAmount(dto.getHotWaterAmount());
        entity.setOtherFees(dto.getOtherFees());
        entity.setOtherFeesDescription(dto.getOtherFeesDescription());
        entity.setTotalAmount(dto.getTotalAmount());
        entity.setBillStatus(dto.getBillStatus());
        entity.setPaymentStatus(dto.getPaymentStatus());
        entity.setPaymentDate(dto.getPaymentDate());
        entity.setPaymentMethod(dto.getPaymentMethod());
        entity.setNotes(dto.getNotes());
    }

    /**
     * 转换为DTO
     */
    private ActualBillDto convertToDto(ActualBill bill) {
        ActualBillDto dto = new ActualBillDto();
        
        dto.setId(bill.getId());
        dto.setEstimatedBillId(bill.getEstimatedBillId());
        dto.setRoomId(bill.getRoomId());
        dto.setBillMonth(bill.getBillMonth());
        dto.setBillDate(bill.getBillDate());
        dto.setRent(bill.getRent());
        dto.setDeposit(bill.getDeposit());
        dto.setElectricityUnitPrice(bill.getElectricityUnitPrice());
        dto.setElectricityUsage(bill.getElectricityUsage());
        dto.setElectricityAmount(bill.getElectricityAmount());
        dto.setWaterUnitPrice(bill.getWaterUnitPrice());
        dto.setWaterUsage(bill.getWaterUsage());
        dto.setWaterAmount(bill.getWaterAmount());
        dto.setHotWaterUnitPrice(bill.getHotWaterUnitPrice());
        dto.setHotWaterUsage(bill.getHotWaterUsage());
        dto.setHotWaterAmount(bill.getHotWaterAmount());
        dto.setOtherFees(bill.getOtherFees());
        dto.setOtherFeesDescription(bill.getOtherFeesDescription());
        dto.setTotalAmount(bill.getTotalAmount());
        dto.setBillStatus(bill.getBillStatus());
        dto.setBillStatusDescription(bill.getBillStatus() != null ? bill.getBillStatus().getDescription() : null);
        dto.setPaymentStatus(bill.getPaymentStatus());
        dto.setPaymentStatusDescription(bill.getPaymentStatus() != null ? bill.getPaymentStatus().getDescription() : null);
        dto.setPaymentDate(bill.getPaymentDate());
        dto.setPaymentMethod(bill.getPaymentMethod());
        dto.setNotes(bill.getNotes());
        dto.setCreatedBy(bill.getCreatedBy());
        dto.setCreatedAt(bill.getCreatedAt());
        dto.setUpdatedAt(bill.getUpdatedAt());

        // 设置房间和楼宇信息
        if (bill.getRoomId() != null) {
            Optional<Room> roomOpt = roomRepository.findById(bill.getRoomId());
            if (roomOpt.isPresent()) {
                Room room = roomOpt.get();
                dto.setRoomNumber(room.getRoomNumber());
                
                if (room.getBuildingId() != null) {
                    Optional<Building> buildingOpt = buildingRepository.findById(room.getBuildingId());
                    if (buildingOpt.isPresent()) {
                        dto.setBuildingName(buildingOpt.get().getBuildingName());
                    }
                }
            }
        }

        return dto;
    }
}
