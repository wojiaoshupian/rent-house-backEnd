package com.example.demo.service;

import com.example.demo.dto.CreateUtilityReadingRequest;
import com.example.demo.dto.UtilityReadingDto;
import com.example.demo.dto.UtilityReadingQueryDto;
import com.example.demo.entity.UtilityReading;
import com.example.demo.entity.Room;
import com.example.demo.entity.Building;
// import com.example.demo.mapper.UtilityReadingMapper;
import com.example.demo.repository.UtilityReadingRepository;
import com.example.demo.repository.RoomRepository;
import com.example.demo.repository.BuildingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 水电表记录服务
 */
@Service
public class UtilityReadingService {

    private static final Logger log = LoggerFactory.getLogger(UtilityReadingService.class);

    @Autowired
    private UtilityReadingRepository utilityReadingRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private BuildingRepository buildingRepository;

    // @Autowired
    // private UtilityReadingMapper utilityReadingMapper;

    /**
     * 创建水电表记录
     */
    @CacheEvict(value = {"utilityReadings", "roomReadings"}, allEntries = true)
    @Transactional
    public UtilityReadingDto createReading(CreateUtilityReadingRequest request, Long userId) {
        log.info("创建水电表记录，房间ID: {}, 抄表日期: {}", request.getRoomId(), request.getReadingDate());

        // 验证房间是否存在 (暂时跳过，后续可以添加)
        // Room room = roomRepository.findById(request.getRoomId())
        //         .orElseThrow(() -> new RuntimeException("房间不存在"));

        // 检查是否已有当天的记录
        if (utilityReadingRepository.existsByRoomIdAndReadingDate(request.getRoomId(), request.getReadingDate())) {
            throw new RuntimeException("该房间在指定日期已有抄表记录");
        }

        // 创建实体
        UtilityReading reading = new UtilityReading();
        reading.setRoomId(request.getRoomId());
        reading.setReadingDate(request.getReadingDate());
        reading.setReadingTime(request.getReadingTime() != null ? request.getReadingTime() : LocalDateTime.now());
        reading.setElectricityReading(request.getElectricityReading());
        reading.setWaterReading(request.getWaterReading());
        reading.setHotWaterReading(request.getHotWaterReading());
        reading.setMeterReader(request.getMeterReader());
        reading.setReadingType(request.getReadingType() != null ? request.getReadingType() : UtilityReading.ReadingType.MANUAL);
        reading.setReadingStatus(UtilityReading.ReadingStatus.CONFIRMED); // 默认设置为已确认状态
        reading.setNotes(request.getNotes());
        reading.setPhotos(request.getPhotos());
        reading.setCreatedBy(userId);

        // 保存记录（触发器会自动设置上次读数）
        UtilityReading savedReading = utilityReadingRepository.save(reading);
        log.info("水电表记录创建成功，ID: {}", savedReading.getId());

        return convertToDto(savedReading);
    }

    /**
     * 根据ID获取水电表记录
     */
    @Cacheable(value = "utilityReadings", key = "#id")
    public UtilityReadingDto getReadingById(Long id) {
        log.info("获取水电表记录，ID: {}", id);
        
        UtilityReading reading = utilityReadingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("水电表记录不存在"));
        
        return convertToDto(reading);
    }

    /**
     * 获取房间的水电表记录列表
     */
    @Cacheable(value = "roomReadings", key = "#roomId")
    public List<UtilityReadingDto> getRoomReadings(Long roomId) {
        log.info("获取房间水电表记录，房间ID: {}", roomId);
        
        List<UtilityReading> readings = utilityReadingRepository
                .findByRoomIdOrderByReadingDateDescReadingTimeDesc(roomId);
        
        return convertToDtoList(readings);
    }

    /**
     * 获取房间指定日期范围的水电表记录
     */
    public List<UtilityReadingDto> getRoomReadingsByDateRange(Long roomId, LocalDate startDate, LocalDate endDate) {
        log.info("获取房间水电表记录，房间ID: {}, 日期范围: {} - {}", roomId, startDate, endDate);
        
        List<UtilityReading> readings = utilityReadingRepository
                .findByRoomIdAndReadingDateBetweenOrderByReadingDateDescReadingTimeDesc(roomId, startDate, endDate);
        
        return convertToDtoList(readings);
    }

    /**
     * 获取房间最新的水电表记录
     */
    public Optional<UtilityReadingDto> getLatestRoomReading(Long roomId) {
        log.info("获取房间最新水电表记录，房间ID: {}", roomId);
        
        Optional<UtilityReading> reading = utilityReadingRepository
                .findFirstByRoomIdOrderByReadingDateDescReadingTimeDesc(roomId);
        
        return reading.map(this::convertToDto);
    }

    /**
     * 分页查询水电表记录
     */
    public Page<UtilityReadingDto> getReadings(UtilityReadingQueryDto queryDto) {
        log.info("分页查询水电表记录，查询条件: {}", queryDto);

        // 构建排序
        Sort sort = Sort.by(
                "DESC".equalsIgnoreCase(queryDto.getSortDirection()) ? Sort.Direction.DESC : Sort.Direction.ASC,
                queryDto.getSortBy()
        );
        
        Pageable pageable = PageRequest.of(queryDto.getPage(), queryDto.getSize(), sort);

        // 执行查询
        Page<UtilityReading> readings = utilityReadingRepository.findByConditions(
                queryDto.getRoomId(),
                queryDto.getBuildingId(),
                queryDto.getStartDate(),
                queryDto.getEndDate(),
                queryDto.getMeterReader(),
                queryDto.getReadingType(),
                queryDto.getReadingStatus(),
                pageable
        );

        return readings.map(this::convertToDto);
    }

    /**
     * 更新水电表记录
     */
    @CacheEvict(value = {"utilityReadings", "roomReadings"}, allEntries = true)
    @Transactional
    public UtilityReadingDto updateReading(Long id, CreateUtilityReadingRequest request, Long userId) {
        log.info("更新水电表记录，ID: {}", id);

        UtilityReading reading = utilityReadingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("水电表记录不存在"));

        // 更新字段（只更新非null的字段）
        if (request.getElectricityReading() != null) {
            reading.setElectricityReading(request.getElectricityReading());
        }
        if (request.getWaterReading() != null) {
            reading.setWaterReading(request.getWaterReading());
        }
        if (request.getHotWaterReading() != null) {
            reading.setHotWaterReading(request.getHotWaterReading());
        }
        if (request.getMeterReader() != null && !request.getMeterReader().trim().isEmpty()) {
            reading.setMeterReader(request.getMeterReader());
        }
        if (request.getReadingType() != null) {
            reading.setReadingType(request.getReadingType());
        }
        if (request.getNotes() != null) {
            reading.setNotes(request.getNotes());
        }
        if (request.getPhotos() != null) {
            reading.setPhotos(request.getPhotos());
        }

        // 设置更新时间
        reading.setUpdatedAt(LocalDateTime.now());

        UtilityReading savedReading = utilityReadingRepository.save(reading);
        log.info("水电表记录更新成功，ID: {}", savedReading.getId());

        return convertToDto(savedReading);
    }

    /**
     * 确认水电表记录
     */
    @CacheEvict(value = {"utilityReadings", "roomReadings"}, allEntries = true)
    @Transactional
    public UtilityReadingDto confirmReading(Long id, Long userId) {
        log.info("确认水电表记录，ID: {}", id);

        UtilityReading reading = utilityReadingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("水电表记录不存在"));

        reading.setReadingStatus(UtilityReading.ReadingStatus.CONFIRMED);
        UtilityReading savedReading = utilityReadingRepository.save(reading);

        log.info("水电表记录确认成功，ID: {}", savedReading.getId());
        return convertToDto(savedReading);
    }

    /**
     * 删除水电表记录
     */
    @CacheEvict(value = {"utilityReadings", "roomReadings"}, allEntries = true)
    @Transactional
    public void deleteReading(Long id) {
        log.info("删除水电表记录，ID: {}", id);

        if (!utilityReadingRepository.existsById(id)) {
            throw new RuntimeException("水电表记录不存在");
        }

        utilityReadingRepository.deleteById(id);
        log.info("水电表记录删除成功，ID: {}", id);
    }

    /**
     * 获取房间用量统计
     */
    public Object[] getRoomUsageStatistics(Long roomId, LocalDate startDate, LocalDate endDate) {
        log.info("获取房间用量统计，房间ID: {}, 日期范围: {} - {}", roomId, startDate, endDate);
        return utilityReadingRepository.getUsageStatistics(roomId, startDate, endDate);
    }

    /**
     * 获取楼宇用量统计
     */
    public Object[] getBuildingUsageStatistics(Long buildingId, LocalDate startDate, LocalDate endDate) {
        log.info("获取楼宇用量统计，楼宇ID: {}, 日期范围: {} - {}", buildingId, startDate, endDate);
        return utilityReadingRepository.getBuildingUsageStatistics(buildingId, startDate, endDate);
    }

    /**
     * 获取待确认记录数量
     */
    public Long getPendingReadingsCount() {
        return utilityReadingRepository.countPendingReadings();
    }

    /**
     * 获取异常读数记录
     */
    public List<UtilityReadingDto> getAbnormalReadings(Long roomId, Double electricityThreshold, Double waterThreshold) {
        log.info("获取异常读数记录，房间ID: {}", roomId);
        
        List<UtilityReading> readings = utilityReadingRepository
                .findAbnormalReadings(roomId, electricityThreshold, waterThreshold);

        return convertToDtoList(readings);
    }

    // ==================== 手动转换方法 ====================

    /**
     * 实体转DTO
     */
    private UtilityReadingDto convertToDto(UtilityReading entity) {
        if (entity == null) {
            return null;
        }

        UtilityReadingDto dto = new UtilityReadingDto();
        dto.setId(entity.getId());
        dto.setRoomId(entity.getRoomId());
        dto.setReadingDate(entity.getReadingDate());
        dto.setReadingTime(entity.getReadingTime());
        dto.setElectricityReading(entity.getElectricityReading());
        dto.setElectricityPreviousReading(entity.getElectricityPreviousReading());
        dto.setElectricityUsage(entity.getElectricityUsage());
        dto.setWaterReading(entity.getWaterReading());
        dto.setWaterPreviousReading(entity.getWaterPreviousReading());
        dto.setWaterUsage(entity.getWaterUsage());
        dto.setHotWaterReading(entity.getHotWaterReading());
        dto.setHotWaterPreviousReading(entity.getHotWaterPreviousReading());
        dto.setHotWaterUsage(entity.getHotWaterUsage());
        dto.setMeterReader(entity.getMeterReader());
        dto.setReadingType(entity.getReadingType());
        dto.setReadingTypeDescription(entity.getReadingType() != null ? entity.getReadingType().getDescription() : null);
        dto.setReadingStatus(entity.getReadingStatus());
        dto.setReadingStatusDescription(entity.getReadingStatus() != null ? entity.getReadingStatus().getDescription() : null);
        dto.setNotes(entity.getNotes());
        dto.setPhotos(entity.getPhotos());
        dto.setCreatedBy(entity.getCreatedBy());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        // 设置房间和楼宇信息
        if (entity.getRoomId() != null) {
            Optional<Room> roomOpt = roomRepository.findById(entity.getRoomId());
            if (roomOpt.isPresent()) {
                Room room = roomOpt.get();
                dto.setRoomNumber(room.getRoomNumber());

                // 设置楼宇名称
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

    /**
     * 实体列表转DTO列表
     */
    private List<UtilityReadingDto> convertToDtoList(List<UtilityReading> entities) {
        if (entities == null) {
            return new ArrayList<>();
        }

        return entities.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
}
