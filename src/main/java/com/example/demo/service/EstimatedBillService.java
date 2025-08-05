package com.example.demo.service;

import com.example.demo.dto.EstimatedBillDto;
import com.example.demo.entity.EstimatedBill;
import com.example.demo.entity.Room;
import com.example.demo.entity.Building;
import com.example.demo.entity.UtilityReading;
import com.example.demo.repository.EstimatedBillRepository;
import com.example.demo.repository.RoomRepository;
import com.example.demo.repository.BuildingRepository;
import com.example.demo.repository.UtilityReadingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 预估收费账单服务
 */
@Service
@Transactional
public class EstimatedBillService {

    private static final Logger log = LoggerFactory.getLogger(EstimatedBillService.class);

    @Autowired
    private EstimatedBillRepository estimatedBillRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private BuildingRepository buildingRepository;

    @Autowired
    private UtilityReadingRepository utilityReadingRepository;

    /**
     * 每月1号自动生成预估账单
     * cron表达式: 秒 分 时 日 月 周
     * 0 0 2 1 * ? 表示每月1号凌晨2点执行
     */
    @Scheduled(cron = "0 0 2 1 * ?")
    public void generateMonthlyEstimatedBills() {
        log.info("开始生成月度预估账单...");
        
        LocalDate now = LocalDate.now();
        String billMonth = now.format(DateTimeFormatter.ofPattern("yyyy-MM"));
        
        try {
            // 只获取已出租的房间
            List<Room> rentedRooms = roomRepository.findRentedRooms();
            int generatedCount = 0;

            log.info("找到 {} 个已出租的房间", rentedRooms.size());

            for (Room room : rentedRooms) {
                // 检查是否已存在该月份的账单
                if (!estimatedBillRepository.existsByRoomIdAndBillMonth(room.getId(), billMonth)) {
                    generateEstimatedBillForRoom(room.getId(), billMonth, 1L);
                    generatedCount++;
                    log.debug("为房间 {} ({}) 生成了预估账单", room.getId(), room.getRoomNumber());
                } else {
                    log.debug("房间 {} ({}) 的 {} 月份账单已存在，跳过", room.getId(), room.getRoomNumber(), billMonth);
                }
            }
            
            log.info("月度预估账单生成完成，共生成 {} 张账单", generatedCount);
        } catch (Exception e) {
            log.error("生成月度预估账单失败", e);
        }
    }

    /**
     * 为指定房间生成预估账单
     */
    public EstimatedBillDto generateEstimatedBillForRoom(Long roomId, String billMonth, Long userId) {
        log.info("为房间 {} 生成 {} 月份的预估账单", roomId, billMonth);

        // 检查是否已存在
        if (estimatedBillRepository.existsByRoomIdAndBillMonth(roomId, billMonth)) {
            throw new RuntimeException("该房间在指定月份的预估账单已存在");
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

        EstimatedBill bill = new EstimatedBill();
        bill.setRoomId(roomId);
        bill.setBillMonth(billMonth);
        bill.setBillDate(LocalDate.now());
        bill.setCreatedBy(userId);

        // 从房间信息获取房租和押金
        bill.setRent(room.getRent()); // 房租
        bill.setDeposit(BigDecimal.ZERO); // 押金通常只在首月收取

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
        calculateTotalAmount(bill);

        // 保存账单
        EstimatedBill savedBill = estimatedBillRepository.save(bill);
        
        log.info("预估账单生成成功，账单ID: {}", savedBill.getId());
        return convertToDto(savedBill);
    }

    /**
     * 计算水电费用量和金额
     */
    private void calculateUtilityUsageAndAmount(EstimatedBill bill, Long roomId, String billMonth) {
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
     * 计算总金额
     */
    private void calculateTotalAmount(EstimatedBill bill) {
        BigDecimal total = BigDecimal.ZERO;
        
        if (bill.getRent() != null) total = total.add(bill.getRent());
        if (bill.getDeposit() != null) total = total.add(bill.getDeposit());
        if (bill.getElectricityAmount() != null) total = total.add(bill.getElectricityAmount());
        if (bill.getWaterAmount() != null) total = total.add(bill.getWaterAmount());
        if (bill.getHotWaterAmount() != null) total = total.add(bill.getHotWaterAmount());
        if (bill.getOtherFees() != null) total = total.add(bill.getOtherFees());
        
        bill.setTotalAmount(total.setScale(2, RoundingMode.HALF_UP));
    }

    /**
     * 分页查询预估账单
     */
    public Page<EstimatedBillDto> getEstimatedBills(Long roomId, String billMonth, 
                                                   EstimatedBill.BillStatus billStatus,
                                                   int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("billMonth").descending().and(Sort.by("roomId")));
        Page<EstimatedBill> billPage = estimatedBillRepository.findBillsWithFilters(roomId, billMonth, billStatus, pageable);
        
        return billPage.map(this::convertToDto);
    }

    /**
     * 根据ID获取预估账单
     */
    public EstimatedBillDto getEstimatedBillById(Long id) {
        EstimatedBill bill = estimatedBillRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("预估账单不存在"));
        return convertToDto(bill);
    }

    /**
     * 更新预估账单状态
     */
    public EstimatedBillDto updateBillStatus(Long id, EstimatedBill.BillStatus status) {
        EstimatedBill bill = estimatedBillRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("预估账单不存在"));
        
        bill.setBillStatus(status);
        EstimatedBill savedBill = estimatedBillRepository.save(bill);
        
        log.info("预估账单状态更新成功，账单ID: {}, 新状态: {}", id, status);
        return convertToDto(savedBill);
    }

    /**
     * 删除预估账单
     */
    public void deleteEstimatedBill(Long id) {
        if (!estimatedBillRepository.existsById(id)) {
            throw new RuntimeException("预估账单不存在");
        }
        
        estimatedBillRepository.deleteById(id);
        log.info("预估账单删除成功，账单ID: {}", id);
    }

    /**
     * 转换为DTO
     */
    private EstimatedBillDto convertToDto(EstimatedBill bill) {
        EstimatedBillDto dto = new EstimatedBillDto();
        
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
        dto.setBillStatusDescription(bill.getBillStatus() != null ? bill.getBillStatus().getDescription() : null);
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
