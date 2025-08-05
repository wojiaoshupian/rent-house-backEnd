package com.example.demo.service;

import com.example.demo.dto.RoomDto;
import com.example.demo.entity.Room;
import com.example.demo.entity.Building;
import com.example.demo.entity.UserBuilding;
import com.example.demo.mapper.RoomMapper;
import com.example.demo.repository.RoomRepository;
import com.example.demo.repository.BuildingRepository;
import com.example.demo.repository.UserBuildingRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.BillRepository;
import com.example.demo.repository.UtilityReadingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RoomService {

    private static final Logger log = LoggerFactory.getLogger(RoomService.class);

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private BuildingRepository buildingRepository;

    @Autowired
    private UserBuildingRepository userBuildingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private UtilityReadingRepository utilityReadingRepository;

    @Autowired
    private RoomMapper roomMapper;

    /**
     * 创建房间
     */
    @CacheEvict(value = {"rooms", "buildings"}, allEntries = true)
    @Transactional
    public RoomDto createRoom(RoomDto roomDto, Long userId) {
        log.info("创建房间: {}, 用户ID: {}", roomDto.getRoomNumber(), userId);

        // 验证楼宇是否存在
        Building building = buildingRepository.findById(roomDto.getBuildingId())
                .orElseThrow(() -> new RuntimeException("楼宇不存在"));

        // 验证用户是否有权限操作该楼宇
        if (!userBuildingRepository.existsByUserIdAndBuildingId(userId, roomDto.getBuildingId())) {
            throw new RuntimeException("您没有权限在该楼宇中创建房间");
        }

        // 验证房号在该楼宇中是否已存在
        if (roomRepository.existsByRoomNumberAndBuildingId(roomDto.getRoomNumber(), roomDto.getBuildingId())) {
            throw new RuntimeException("该楼宇中已存在相同房号的房间");
        }

        Room room = roomMapper.toEntity(roomDto);
        room.setCreatedBy(userId);
        room.setCreatedAt(LocalDateTime.now());

        Room savedRoom = roomRepository.save(room);
        log.info("房间创建成功: {}", savedRoom.getRoomNumber());

        return roomMapper.toDtoWithBuilding(savedRoom, building);
    }

    /**
     * 获取所有房间
     */
    @Cacheable(value = "rooms")
    public List<RoomDto> getAllRooms() {
        log.info("获取所有房间");
        List<Room> rooms = roomRepository.findAll();
        return rooms.stream()
                .map(room -> {
                    Building building = buildingRepository.findById(room.getBuildingId()).orElse(null);
                    RoomDto dto = roomMapper.toDtoWithBuilding(room, building);
                    // 设置创建者用户名
                    userRepository.findById(room.getCreatedBy())
                            .ifPresent(user -> dto.setCreatedByUsername(user.getUsername()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    /**
     * 根据ID获取房间
     */
    public Optional<RoomDto> getRoomById(Long id) {
        log.info("根据ID获取房间: {}", id);
        return roomRepository.findById(id)
                .map(room -> {
                    Building building = buildingRepository.findById(room.getBuildingId()).orElse(null);
                    RoomDto dto = roomMapper.toDtoWithBuilding(room, building);
                    // 设置创建者用户名
                    userRepository.findById(room.getCreatedBy())
                            .ifPresent(user -> dto.setCreatedByUsername(user.getUsername()));
                    return dto;
                });
    }

    /**
     * 根据楼宇ID获取房间列表
     */
    public List<RoomDto> getRoomsByBuildingId(Long buildingId) {
        log.info("根据楼宇ID获取房间列表: {}", buildingId);
        Building building = buildingRepository.findById(buildingId).orElse(null);
        List<Room> rooms = roomRepository.findByBuildingId(buildingId);
        
        return rooms.stream()
                .map(room -> {
                    RoomDto dto = roomMapper.toDtoWithBuilding(room, building);
                    // 设置创建者用户名
                    userRepository.findById(room.getCreatedBy())
                            .ifPresent(user -> dto.setCreatedByUsername(user.getUsername()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    /**
     * 根据用户ID获取房间列表（用户有权限的楼宇中的房间）
     */
    public List<RoomDto> getRoomsByUserId(Long userId) {
        log.info("根据用户ID获取房间列表: {}", userId);
        
        // 获取用户有权限的楼宇ID列表
        List<Long> buildingIds = userBuildingRepository.findByUserId(userId)
                .stream()
                .map(UserBuilding::getBuildingId)
                .collect(Collectors.toList());
        
        if (buildingIds.isEmpty()) {
            return List.of();
        }
        
        List<Room> rooms = roomRepository.findByBuildingIdIn(buildingIds);
        return rooms.stream()
                .map(room -> {
                    Building building = buildingRepository.findById(room.getBuildingId()).orElse(null);
                    RoomDto dto = roomMapper.toDtoWithBuilding(room, building);
                    // 设置创建者用户名
                    userRepository.findById(room.getCreatedBy())
                            .ifPresent(user -> dto.setCreatedByUsername(user.getUsername()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    /**
     * 更新房间
     */
    @CacheEvict(value = {"rooms", "buildings"}, allEntries = true)
    @Transactional
    public RoomDto updateRoom(Long id, RoomDto roomDto, Long userId) {
        log.info("更新房间: {}, 用户ID: {}", id, userId);

        Room existingRoom = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("房间不存在"));

        // 验证用户是否有权限操作该楼宇
        if (!userBuildingRepository.existsByUserIdAndBuildingId(userId, existingRoom.getBuildingId())) {
            throw new RuntimeException("您没有权限修改该房间");
        }

        // 如果要修改房号，检查新房号是否已存在
        if (!existingRoom.getRoomNumber().equals(roomDto.getRoomNumber())) {
            if (roomRepository.existsByRoomNumberAndBuildingId(roomDto.getRoomNumber(), existingRoom.getBuildingId())) {
                throw new RuntimeException("该楼宇中已存在相同房号的房间");
            }
        }

        roomMapper.updateEntityFromDto(roomDto, existingRoom);
        existingRoom.setUpdatedAt(LocalDateTime.now());

        Room updatedRoom = roomRepository.save(existingRoom);
        Building building = buildingRepository.findById(updatedRoom.getBuildingId()).orElse(null);
        
        log.info("房间更新成功: {}", updatedRoom.getRoomNumber());
        return roomMapper.toDtoWithBuilding(updatedRoom, building);
    }

    /**
     * 删除房间
     */
    @CacheEvict(value = {"rooms", "buildings"}, allEntries = true)
    @Transactional
    public void deleteRoom(Long id, Long userId) {
        log.info("删除房间: {}, 用户ID: {}", id, userId);

        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("房间不存在"));

        // 验证用户是否有权限操作该楼宇
        if (!userBuildingRepository.existsByUserIdAndBuildingId(userId, room.getBuildingId())) {
            throw new RuntimeException("您没有权限删除该房间");
        }

        // 检查并删除相关数据
        deleteRoomRelatedData(id);

        // 删除房间
        roomRepository.delete(room);
        log.info("房间删除成功: {}", room.getRoomNumber());
    }

    /**
     * 删除房间相关数据
     */
    private void deleteRoomRelatedData(Long roomId) {
        log.info("开始删除房间 {} 的相关数据", roomId);

        // 删除水电表记录（先删除，因为可能有外键约束问题）
        long utilityReadingCount = utilityReadingRepository.countByRoomId(roomId);
        if (utilityReadingCount > 0) {
            utilityReadingRepository.deleteByRoomId(roomId);
            log.info("删除房间 {} 的水电表记录 {} 条", roomId, utilityReadingCount);
        }

        // 删除账单
        long billCount = billRepository.countByRoomId(roomId);
        if (billCount > 0) {
            billRepository.deleteByRoomId(roomId);
            log.info("删除房间 {} 的账单 {} 条", roomId, billCount);
        }

        log.info("房间 {} 的相关数据删除完成", roomId);
    }

    /**
     * 搜索房间
     */
    public List<RoomDto> searchRooms(String keyword) {
        log.info("搜索房间: {}", keyword);
        List<Room> rooms = roomRepository.searchRooms(keyword);
        return rooms.stream()
                .map(room -> {
                    Building building = buildingRepository.findById(room.getBuildingId()).orElse(null);
                    RoomDto dto = roomMapper.toDtoWithBuilding(room, building);
                    // 设置创建者用户名
                    userRepository.findById(room.getCreatedBy())
                            .ifPresent(user -> dto.setCreatedByUsername(user.getUsername()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    /**
     * 统计指定楼宇的房间数量
     */
    public long countRoomsByBuildingId(Long buildingId) {
        return roomRepository.countByBuildingId(buildingId);
    }

    /**
     * 更新房间出租状态
     */
    @CacheEvict(value = {"rooms", "buildings"}, allEntries = true)
    @Transactional
    public RoomDto updateRoomRentalStatus(Long id, Room.RentalStatus rentalStatus, Long userId) {
        log.info("更新房间出租状态: {}, 新状态: {}, 用户ID: {}", id, rentalStatus, userId);

        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("房间不存在"));

        // 验证用户是否有权限操作该楼宇
        if (!userBuildingRepository.existsByUserIdAndBuildingId(userId, room.getBuildingId())) {
            throw new RuntimeException("您没有权限修改该房间的出租状态");
        }

        // 更新出租状态
        room.setRentalStatus(rentalStatus);
        room.setUpdatedAt(LocalDateTime.now());

        Room savedRoom = roomRepository.save(room);
        log.info("房间出租状态更新成功: {}", savedRoom.getRoomNumber());

        // 转换为DTO并返回
        Building building = buildingRepository.findById(savedRoom.getBuildingId()).orElse(null);
        RoomDto dto = roomMapper.toDtoWithBuilding(savedRoom, building);

        // 设置创建者用户名
        userRepository.findById(savedRoom.getCreatedBy())
                .ifPresent(user -> dto.setCreatedByUsername(user.getUsername()));

        return dto;
    }
}
