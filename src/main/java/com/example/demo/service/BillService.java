package com.example.demo.service;

import com.example.demo.entity.Bill;
import com.example.demo.entity.Building;
import com.example.demo.entity.Room;
import com.example.demo.entity.UtilityReading;
import com.example.demo.dto.BillDto;
import com.example.demo.repository.BillRepository;
import com.example.demo.repository.BuildingRepository;
import com.example.demo.repository.RoomRepository;
import com.example.demo.repository.UtilityReadingRepository;
import com.example.demo.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

/**
 * 账单Service
 */
@Service
@Transactional
public class BillService {

    private static final Logger log = LoggerFactory.getLogger(BillService.class);

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private BuildingRepository buildingRepository;

    @Autowired
    private UtilityReadingRepository utilityReadingRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * 为指定房间生成账单
     */
    @CacheEvict(value = {"bills", "rooms"}, allEntries = true)
    public BillDto generateBillForRoom(Long roomId, String billMonth, Long userId) {
        log.info("为房间 {} 生成 {} 月份的账单", roomId, billMonth);

        // 检查是否已存在账单
        if (billRepository.existsByRoomIdAndBillMonth(roomId, billMonth)) {
            throw new RuntimeException("该房间在指定月份的账单已存在");
        }

        // 获取房间信息
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("房间不存在"));

        // 检查房间是否已出租
        if (room.getRentalStatus() != Room.RentalStatus.RENTED) {
            throw new RuntimeException("只能为已出租的房间生成账单，当前房间状态：" + room.getRentalStatus().getDescription());
        }

        // 获取楼宇信息
        Building building = buildingRepository.findById(room.getBuildingId())
                .orElseThrow(() -> new RuntimeException("楼宇信息不存在"));

        Bill bill = new Bill();
        bill.setRoomId(roomId);
        bill.setBillMonth(billMonth);
        bill.setBillDate(LocalDate.now());
        bill.setCreatedBy(userId);

        // 从房间信息获取房租和押金
        bill.setRent(room.getRent()); // 房租

        // 押金处理逻辑：检查是否为该房间的首月账单
        BigDecimal depositAmount = calculateDepositAmount(roomId, billMonth, room);
        bill.setDeposit(depositAmount);

        // 从房间或楼宇信息获取水电费单价（优先使用房间设置，如果没有则使用楼宇设置）
        bill.setElectricityUnitPrice(room.getElectricityUnitPrice() != null ? 
            room.getElectricityUnitPrice() : building.getElectricityUnitPrice());
        bill.setWaterUnitPrice(room.getWaterUnitPrice() != null ? 
            room.getWaterUnitPrice() : building.getWaterUnitPrice());
        bill.setHotWaterUnitPrice(room.getHotWaterUnitPrice() != null ? 
            room.getHotWaterUnitPrice() : building.getHotWaterUnitPrice());

        // 计算水电费用量和金额
        calculateUtilityUsageAndAmount(bill, roomId, billMonth);

        // 设置其他费用
        bill.setOtherFees(BigDecimal.ZERO);
        bill.setOtherFeesDescription("");

        // 计算总金额
        BigDecimal totalAmount = bill.getRent()
                .add(bill.getDeposit())
                .add(bill.getElectricityAmount())
                .add(bill.getWaterAmount())
                .add(bill.getHotWaterAmount())
                .add(bill.getOtherFees());
        bill.setTotalAmount(totalAmount);

        // 保存账单
        Bill savedBill = billRepository.save(bill);
        log.info("账单生成成功，账单ID: {}", savedBill.getId());

        return convertToDto(savedBill);
    }

    /**
     * 计算水电费用量和金额
     */
    private void calculateUtilityUsageAndAmount(Bill bill, Long roomId, String billMonth) {
        // 获取当月最新的水电表读数
        List<UtilityReading> currentMonthReadings = utilityReadingRepository
                .findByRoomIdAndReadingDateBetweenOrderByReadingDateDescReadingTimeDesc(roomId,
                    LocalDate.parse(billMonth + "-01"),
                    LocalDate.parse(billMonth + "-01").plusMonths(1).minusDays(1));

        // 获取上月最新的水电表读数
        LocalDate lastMonth = LocalDate.parse(billMonth + "-01").minusMonths(1);
        List<UtilityReading> lastMonthReadings = utilityReadingRepository
                .findByRoomIdAndReadingDateBetweenOrderByReadingDateDescReadingTimeDesc(roomId,
                    lastMonth.withDayOfMonth(1),
                    lastMonth.withDayOfMonth(lastMonth.lengthOfMonth()));

        // 计算用量（当月最新读数 - 上月最新读数）
        if (!currentMonthReadings.isEmpty()) {
            // 获取当月最新的抄表记录（第一个，因为已按日期倒序排列）
            UtilityReading currentReading = currentMonthReadings.get(0);
            
            if (!lastMonthReadings.isEmpty()) {
                // 获取上月最新的抄表记录（第一个，因为已按日期倒序排列）
                UtilityReading lastReading = lastMonthReadings.get(0);
                
                // 计算用量：当月读数 - 上月读数
                BigDecimal electricityUsage = currentReading.getElectricityReading().subtract(lastReading.getElectricityReading());
                BigDecimal waterUsage = currentReading.getWaterReading().subtract(lastReading.getWaterReading());
                BigDecimal hotWaterUsage = currentReading.getHotWaterReading().subtract(lastReading.getHotWaterReading());
                
                // 确保用量不为负数（防止抄表错误）
                bill.setElectricityUsage(electricityUsage.max(BigDecimal.ZERO));
                bill.setWaterUsage(waterUsage.max(BigDecimal.ZERO));
                bill.setHotWaterUsage(hotWaterUsage.max(BigDecimal.ZERO));
                
                log.info("房间 {} 在 {} 月份计算用量：电 {} 度，水 {} 吨，热水 {} 吨", 
                    roomId, billMonth, electricityUsage, waterUsage, hotWaterUsage);
            } else {
                // 只有当月抄表记录，没有上月记录，用量设为0
                log.info("房间 {} 在 {} 月份只有当月抄表记录，没有上月记录，水电费用量设为0", roomId, billMonth);
                bill.setElectricityUsage(BigDecimal.ZERO);
                bill.setWaterUsage(BigDecimal.ZERO);
                bill.setHotWaterUsage(BigDecimal.ZERO);
            }
        } else {
            // 如果没有当月抄表记录，用量为0，不产生水电费
            log.info("房间 {} 在 {} 月份没有抄表记录，水电费用量设为0", roomId, billMonth);
            bill.setElectricityUsage(BigDecimal.ZERO);
            bill.setWaterUsage(BigDecimal.ZERO);
            bill.setHotWaterUsage(BigDecimal.ZERO);
        }

        // 计算金额
        bill.setElectricityAmount(bill.getElectricityUsage().multiply(bill.getElectricityUnitPrice()).setScale(2, RoundingMode.HALF_UP));
        bill.setWaterAmount(bill.getWaterUsage().multiply(bill.getWaterUnitPrice()).setScale(2, RoundingMode.HALF_UP));
        bill.setHotWaterAmount(bill.getHotWaterUsage().multiply(bill.getHotWaterUnitPrice()).setScale(2, RoundingMode.HALF_UP));
    }

    /**
     * 分页查询账单
     */
    public Page<BillDto> getBills(Long roomId, String billMonth, 
                                 Bill.BillStatus billStatus,
                                 int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("billMonth").descending().and(Sort.by("roomId")));
        Page<Bill> billPage = billRepository.findBillsWithFilters(roomId, billMonth, billStatus, pageable);
        
        return billPage.map(this::convertToDto);
    }

    /**
     * 根据ID获取账单
     */
    public BillDto getBillById(Long id) {
        Bill bill = billRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("账单不存在"));
        return convertToDto(bill);
    }

    /**
     * 更新账单
     */
    @CacheEvict(value = {"bills", "rooms"}, allEntries = true)
    public BillDto updateBill(Long id, BillDto billDto, Long userId) {
        Bill bill = billRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("账单不存在"));

        // 更新字段
        if (billDto.getRent() != null) bill.setRent(billDto.getRent());
        if (billDto.getDeposit() != null) bill.setDeposit(billDto.getDeposit());
        if (billDto.getElectricityUsage() != null) bill.setElectricityUsage(billDto.getElectricityUsage());
        if (billDto.getWaterUsage() != null) bill.setWaterUsage(billDto.getWaterUsage());
        if (billDto.getHotWaterUsage() != null) bill.setHotWaterUsage(billDto.getHotWaterUsage());
        if (billDto.getOtherFees() != null) bill.setOtherFees(billDto.getOtherFees());
        if (billDto.getOtherFeesDescription() != null) bill.setOtherFeesDescription(billDto.getOtherFeesDescription());
        if (billDto.getBillStatus() != null) bill.setBillStatus(billDto.getBillStatus());
        if (billDto.getNotes() != null) bill.setNotes(billDto.getNotes());

        // 重新计算金额
        bill.setElectricityAmount(bill.getElectricityUsage().multiply(bill.getElectricityUnitPrice()).setScale(2, RoundingMode.HALF_UP));
        bill.setWaterAmount(bill.getWaterUsage().multiply(bill.getWaterUnitPrice()).setScale(2, RoundingMode.HALF_UP));
        bill.setHotWaterAmount(bill.getHotWaterUsage().multiply(bill.getHotWaterUnitPrice()).setScale(2, RoundingMode.HALF_UP));

        // 重新计算总金额
        BigDecimal totalAmount = bill.getRent()
                .add(bill.getDeposit())
                .add(bill.getElectricityAmount())
                .add(bill.getWaterAmount())
                .add(bill.getHotWaterAmount())
                .add(bill.getOtherFees());
        bill.setTotalAmount(totalAmount);

        Bill savedBill = billRepository.save(bill);
        log.info("账单更新成功，账单ID: {}", savedBill.getId());

        return convertToDto(savedBill);
    }

    /**
     * 删除账单
     */
    @CacheEvict(value = {"bills", "rooms"}, allEntries = true)
    public void deleteBill(Long id) {
        if (!billRepository.existsById(id)) {
            throw new RuntimeException("账单不存在");
        }

        billRepository.deleteById(id);
        log.info("账单删除成功，账单ID: {}", id);
    }

    /**
     * 计算押金金额
     * 规则：
     * 1. 如果是该房间的第一张账单，收取押金
     * 2. 如果不是第一张账单，押金为0
     * 3. 可以通过更新接口手动调整押金
     */
    private BigDecimal calculateDepositAmount(Long roomId, String billMonth, Room room) {
        // 检查该房间是否已有账单
        long existingBillCount = billRepository.countByRoomId(roomId);

        if (existingBillCount == 0) {
            // 这是该房间的第一张账单，收取押金
            BigDecimal depositAmount = room.getDefaultDeposit();
            if (depositAmount != null && depositAmount.compareTo(BigDecimal.ZERO) > 0) {
                log.info("房间 {} 首次生成账单，收取押金: {}", roomId, depositAmount);
                return depositAmount;
            }
        }

        // 不是首月账单或没有设置押金，押金为0
        log.info("房间 {} 在 {} 月份不收取押金", roomId, billMonth);
        return BigDecimal.ZERO;
    }

    /**
     * 转换为DTO
     */
    private BillDto convertToDto(Bill bill) {
        BillDto dto = new BillDto();
        dto.setId(bill.getId());
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
        dto.setBillStatusDescription(bill.getBillStatus().getDescription());
        dto.setNotes(bill.getNotes());
        dto.setCreatedBy(bill.getCreatedBy());
        dto.setCreatedAt(bill.getCreatedAt());
        dto.setUpdatedAt(bill.getUpdatedAt());

        // 获取房间和楼宇信息
        roomRepository.findById(bill.getRoomId()).ifPresent(room -> {
            dto.setRoomNumber(room.getRoomNumber());
            buildingRepository.findById(room.getBuildingId()).ifPresent(building -> {
                dto.setBuildingName(building.getBuildingName());
            });
        });

        // 获取创建人用户名
        if (bill.getCreatedBy() != null) {
            userRepository.findById(bill.getCreatedBy()).ifPresent(user -> {
                dto.setCreatedByUsername(user.getUsername());
            });
        }

        return dto;
    }
}
