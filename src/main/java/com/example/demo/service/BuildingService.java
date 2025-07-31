package com.example.demo.service;

import com.example.demo.dto.BuildingDto;
import com.example.demo.entity.Building;
import com.example.demo.entity.UserBuilding;
import com.example.demo.mapper.BuildingMapper;
import com.example.demo.repository.BuildingRepository;
import com.example.demo.repository.UserBuildingRepository;
import com.example.demo.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class BuildingService {

    private static final Logger log = LoggerFactory.getLogger(BuildingService.class);

    @Autowired
    private BuildingRepository buildingRepository;

    @Autowired
    private UserBuildingRepository userBuildingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BuildingMapper buildingMapper;

    public BuildingService() {}
    
    /**
     * 创建楼宇
     */
    @CacheEvict(value = "buildings", allEntries = true)
    public BuildingDto createBuilding(BuildingDto buildingDto, Long userId) {
        log.info("创建楼宇: {}, 创建者: {}", buildingDto.getBuildingName(), userId);

        // 检查楼宇名称是否已存在
        if (buildingRepository.existsByBuildingName(buildingDto.getBuildingName())) {
            throw new RuntimeException("楼宇名称已存在");
        }

        Building building = buildingMapper.toEntity(buildingDto);
        building.setCreatedBy(userId);
        building.setCreatedAt(LocalDateTime.now());

        Building savedBuilding = buildingRepository.save(building);

        // 创建用户楼宇关联
        UserBuilding userBuilding = new UserBuilding(userId, savedBuilding.getId());
        userBuildingRepository.save(userBuilding);

        log.info("楼宇创建成功: {}", savedBuilding.getBuildingName());

        return buildingMapper.toDto(savedBuilding);
    }
    
    /**
     * 获取所有楼宇
     */
    @Cacheable(value = "buildings")
    public List<BuildingDto> getAllBuildings() {
        return buildingMapper.toDtoList(buildingRepository.findAll());
    }

    /**
     * 根据ID获取楼宇
     */
    public BuildingDto getBuildingById(Long id) {
        Building building = buildingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("楼宇不存在"));
        return buildingMapper.toDto(building);
    }

    /**
     * 获取用户创建的楼宇
     */
    public List<BuildingDto> getBuildingsByUser(Long userId) {
        return buildingMapper.toDtoList(buildingRepository.findByCreatedBy(userId));
    }

    /**
     * 获取用户拥有的楼宇（通过关联表）
     */
    public List<BuildingDto> getUserOwnedBuildings(Long userId) {
        List<UserBuilding> userBuildings = userBuildingRepository.findByUserId(userId);
        List<Long> buildingIds = userBuildings.stream()
                .map(UserBuilding::getBuildingId)
                .collect(Collectors.toList());

        return buildingMapper.toDtoList(buildingRepository.findAllById(buildingIds));
    }

    /**
     * 搜索楼宇
     */
    public List<BuildingDto> searchBuildings(String keyword) {
        return buildingMapper.toDtoList(buildingRepository.searchBuildings(keyword));
    }
    
    /**
     * 更新楼宇
     */
    @CacheEvict(value = "buildings", allEntries = true)
    public BuildingDto updateBuilding(Long id, BuildingDto buildingDto) {
        Building building = buildingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("楼宇不存在"));

        // 如果楼宇名称发生变化，检查新名称是否已存在
        if (!building.getBuildingName().equals(buildingDto.getBuildingName()) &&
            buildingRepository.existsByBuildingName(buildingDto.getBuildingName())) {
            throw new RuntimeException("楼宇名称已存在");
        }

        buildingMapper.updateEntityFromDto(buildingDto, building);
        building.setUpdatedAt(LocalDateTime.now());

        Building savedBuilding = buildingRepository.save(building);
        log.info("楼宇更新成功: {}", savedBuilding.getBuildingName());

        return buildingMapper.toDto(savedBuilding);
    }
    
    /**
     * 删除楼宇
     */
    @Transactional
    public void deleteBuilding(Long id) {
        Building building = buildingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("楼宇不存在"));
        
        // 删除用户楼宇关联
        userBuildingRepository.deleteByBuildingId(id);
        
        // 删除楼宇
        buildingRepository.delete(building);
        log.info("楼宇删除成功: {}", building.getBuildingName());
    }
    
    /**
     * 分配楼宇给用户
     */
    @Transactional
    public void assignBuildingToUser(Long buildingId, Long userId) {
        if (!buildingRepository.existsById(buildingId)) {
            throw new RuntimeException("楼宇不存在");
        }
        
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("用户不存在");
        }
        
        if (userBuildingRepository.existsByUserIdAndBuildingId(userId, buildingId)) {
            throw new RuntimeException("用户已拥有该楼宇");
        }
        
        UserBuilding userBuilding = new UserBuilding(userId, buildingId);
        userBuildingRepository.save(userBuilding);
        log.info("楼宇分配成功: 楼宇ID={}, 用户ID={}", buildingId, userId);
    }
    
    /**
     * 移除用户的楼宇
     */
    @Transactional
    public void removeBuildingFromUser(Long buildingId, Long userId) {
        UserBuilding userBuilding = userBuildingRepository.findByUserIdAndBuildingId(userId, buildingId)
                .orElseThrow(() -> new RuntimeException("用户不拥有该楼宇"));
        
        userBuildingRepository.delete(userBuilding);
        log.info("楼宇移除成功: 楼宇ID={}, 用户ID={}", buildingId, userId);
    }

} 