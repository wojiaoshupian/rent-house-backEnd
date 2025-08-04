package com.example.demo.mapper;

import com.example.demo.dto.BuildingDto;
import com.example.demo.entity.Building;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class BuildingMapper {

    public BuildingDto toDto(Building building) {
        if (building == null) {
            return null;
        }

        BuildingDto dto = new BuildingDto();
        dto.setId(building.getId());
        dto.setBuildingName(building.getBuildingName());
        dto.setLandlordName(building.getLandlordName());
        dto.setElectricityUnitPrice(building.getElectricityUnitPrice());
        dto.setWaterUnitPrice(building.getWaterUnitPrice());
        dto.setHotWaterUnitPrice(building.getHotWaterUnitPrice());
        dto.setElectricityCost(building.getElectricityCost());
        dto.setWaterCost(building.getWaterCost());
        dto.setHotWaterCost(building.getHotWaterCost());
        dto.setRentCollectionMethod(building.getRentCollectionMethod());
        dto.setCreatedBy(building.getCreatedBy());
        dto.setCreatedAt(building.getCreatedAt());
        dto.setUpdatedAt(building.getUpdatedAt());

        return dto;
    }

    public Building toEntity(BuildingDto buildingDto) {
        if (buildingDto == null) {
            return null;
        }

        Building building = new Building();
        building.setBuildingName(buildingDto.getBuildingName());
        building.setLandlordName(buildingDto.getLandlordName());
        building.setElectricityUnitPrice(buildingDto.getElectricityUnitPrice());
        building.setWaterUnitPrice(buildingDto.getWaterUnitPrice());
        building.setHotWaterUnitPrice(buildingDto.getHotWaterUnitPrice());
        building.setElectricityCost(buildingDto.getElectricityCost());
        building.setWaterCost(buildingDto.getWaterCost());
        building.setHotWaterCost(buildingDto.getHotWaterCost());
        building.setRentCollectionMethod(buildingDto.getRentCollectionMethod());

        return building;
    }

    public void updateEntityFromDto(BuildingDto buildingDto, Building building) {
        if (buildingDto == null || building == null) {
            return;
        }

        if (buildingDto.getBuildingName() != null) {
            building.setBuildingName(buildingDto.getBuildingName());
        }
        if (buildingDto.getLandlordName() != null) {
            building.setLandlordName(buildingDto.getLandlordName());
        }
        if (buildingDto.getElectricityUnitPrice() != null) {
            building.setElectricityUnitPrice(buildingDto.getElectricityUnitPrice());
        }
        if (buildingDto.getWaterUnitPrice() != null) {
            building.setWaterUnitPrice(buildingDto.getWaterUnitPrice());
        }
        if (buildingDto.getHotWaterUnitPrice() != null) {
            building.setHotWaterUnitPrice(buildingDto.getHotWaterUnitPrice());
        }
        if (buildingDto.getElectricityCost() != null) {
            building.setElectricityCost(buildingDto.getElectricityCost());
        }
        if (buildingDto.getWaterCost() != null) {
            building.setWaterCost(buildingDto.getWaterCost());
        }
        if (buildingDto.getHotWaterCost() != null) {
            building.setHotWaterCost(buildingDto.getHotWaterCost());
        }
        if (buildingDto.getRentCollectionMethod() != null) {
            building.setRentCollectionMethod(buildingDto.getRentCollectionMethod());
        }
    }

    public List<BuildingDto> toDtoList(List<Building> buildings) {
        if (buildings == null || buildings.isEmpty()) {
            return new ArrayList<>();
        }

        List<BuildingDto> result = new ArrayList<>();
        for (Building building : buildings) {
            try {
                BuildingDto dto = toDto(building);
                if (dto != null) {
                    result.add(dto);
                }
            } catch (Exception e) {
                // 跳过有问题的数据，继续处理其他数据
                System.err.println("Error converting building to DTO: " + e.getMessage());
            }
        }

        return result;
    }

    public List<Building> toEntityList(List<BuildingDto> buildingDtos) {
        if (buildingDtos == null) {
            return null;
        }
        return buildingDtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
}
