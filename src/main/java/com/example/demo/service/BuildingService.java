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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

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

    @PersistenceContext
    private EntityManager entityManager;

    public BuildingService() {}
    
    /**
     * 创建楼宇
     */
    @CacheEvict(value = {"buildings", "userOwnedBuildings"}, allEntries = true)
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
     * 获取用户拥有的楼宇（通过关联表）- 使用缓存优化
     */
    @Cacheable(value = "userOwnedBuildings", key = "#userId")
    @Transactional(readOnly = true)
    public List<BuildingDto> getUserOwnedBuildings(Long userId) {
        log.info("开始获取用户拥有的楼宇，用户ID: {}", userId);

        try {
            // 先获取用户楼宇关联关系
            List<UserBuilding> userBuildings = userBuildingRepository.findByUserId(userId);
            log.info("找到用户楼宇关联记录数: {}", userBuildings.size());

            if (userBuildings.isEmpty()) {
                return new ArrayList<>();
            }

            // 提取楼宇ID列表
            List<Long> buildingIds = userBuildings.stream()
                    .map(UserBuilding::getBuildingId)
                    .collect(Collectors.toList());
            log.info("楼宇ID列表: {}", buildingIds);

            // 批量查询楼宇信息
            List<Building> buildings = buildingRepository.findAllById(buildingIds);
            log.info("找到楼宇记录数: {}", buildings.size());

            // 使用优化后的Mapper转换
            List<BuildingDto> result = buildingMapper.toDtoList(buildings);
            log.info("返回楼宇DTO列表，数量: {}", result.size());

            return result;

        } catch (Exception e) {
            log.error("获取用户拥有的楼宇失败", e);
            // 如果Mapper有问题，回退到原生SQL方案
            return getUserOwnedBuildingsWithNativeSQL(userId);
        }
    }

    /**
     * 备用方案：使用原生SQL查询（当Mapper有问题时使用）
     */
    private List<BuildingDto> getUserOwnedBuildingsWithNativeSQL(Long userId) {
        log.warn("使用原生SQL备用方案获取用户拥有的楼宇，用户ID: {}", userId);

        try {
            String sql = """
                SELECT b.id, b.building_name, b.landlord_name, b.rent_collection_method,
                       b.electricity_unit_price, b.water_unit_price, b.hot_water_unit_price,
                       b.electricity_cost, b.water_cost, b.hot_water_cost,
                       b.created_by, b.created_at, b.updated_at
                FROM buildings b
                INNER JOIN user_buildings ub ON b.id = ub.building_id
                WHERE ub.user_id = ?
                """;

            @SuppressWarnings("unchecked")
            List<Object[]> results = entityManager.createNativeQuery(sql)
                    .setParameter(1, userId)
                    .getResultList();

            List<BuildingDto> buildingDtos = new ArrayList<>();
            for (Object[] row : results) {
                BuildingDto dto = new BuildingDto();
                dto.setId(((Number) row[0]).longValue());
                dto.setBuildingName((String) row[1]);
                dto.setLandlordName((String) row[2]);

                String rentMethod = (String) row[3];
                if (rentMethod != null) {
                    dto.setRentCollectionMethod(Building.RentCollectionMethod.valueOf(rentMethod));
                }

                dto.setElectricityUnitPrice(row[4] != null ? BigDecimal.valueOf(((Number) row[4]).doubleValue()) : null);
                dto.setWaterUnitPrice(row[5] != null ? BigDecimal.valueOf(((Number) row[5]).doubleValue()) : null);
                dto.setHotWaterUnitPrice(row[6] != null ? BigDecimal.valueOf(((Number) row[6]).doubleValue()) : null);
                dto.setElectricityCost(row[7] != null ? BigDecimal.valueOf(((Number) row[7]).doubleValue()) : null);
                dto.setWaterCost(row[8] != null ? BigDecimal.valueOf(((Number) row[8]).doubleValue()) : null);
                dto.setHotWaterCost(row[9] != null ? BigDecimal.valueOf(((Number) row[9]).doubleValue()) : null);
                dto.setCreatedBy(row[10] != null ? ((Number) row[10]).longValue() : null);
                dto.setCreatedAt(row[11] != null ? ((java.sql.Timestamp) row[11]).toLocalDateTime() : null);
                dto.setUpdatedAt(row[12] != null ? ((java.sql.Timestamp) row[12]).toLocalDateTime() : null);

                buildingDtos.add(dto);
            }

            return buildingDtos;

        } catch (Exception e) {
            log.error("原生SQL备用方案也失败", e);
            throw new RuntimeException("获取楼宇列表失败: " + e.getMessage(), e);
        }
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
    @CacheEvict(value = {"buildings", "userOwnedBuildings"}, allEntries = true)
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
    @CacheEvict(value = {"buildings", "userOwnedBuildings"}, allEntries = true)
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