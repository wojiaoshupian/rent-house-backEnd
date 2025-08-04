package com.example.demo.mapper;

import com.example.demo.dto.UtilityReadingDto;
import com.example.demo.entity.UtilityReading;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

/**
 * 水电表记录Mapper
 */
@Mapper(componentModel = "spring")
public interface UtilityReadingMapper {

    /**
     * 实体转DTO
     */
    @Mapping(target = "roomNumber", source = "room.roomNumber")
    @Mapping(target = "buildingName", ignore = true)
    @Mapping(target = "readingTypeDescription", source = "readingType", qualifiedByName = "readingTypeToDescription")
    @Mapping(target = "readingStatusDescription", source = "readingStatus", qualifiedByName = "readingStatusToDescription")
    UtilityReadingDto toDto(UtilityReading entity);

    /**
     * DTO转实体
     */
    @Mapping(target = "room", ignore = true)
    @Mapping(target = "electricityUsage", ignore = true)
    @Mapping(target = "waterUsage", ignore = true)
    @Mapping(target = "hotWaterUsage", ignore = true)
    @Mapping(target = "electricityPreviousReading", ignore = true)
    @Mapping(target = "waterPreviousReading", ignore = true)
    @Mapping(target = "hotWaterPreviousReading", ignore = true)
    UtilityReading toEntity(UtilityReadingDto dto);

    /**
     * 实体列表转DTO列表
     */
    List<UtilityReadingDto> toDtoList(List<UtilityReading> entities);

    /**
     * DTO列表转实体列表
     */
    List<UtilityReading> toEntityList(List<UtilityReadingDto> dtos);

    /**
     * 抄表类型转描述
     */
    @Named("readingTypeToDescription")
    default String readingTypeToDescription(UtilityReading.ReadingType readingType) {
        return readingType != null ? readingType.getDescription() : null;
    }

    /**
     * 读数状态转描述
     */
    @Named("readingStatusToDescription")
    default String readingStatusToDescription(UtilityReading.ReadingStatus readingStatus) {
        return readingStatus != null ? readingStatus.getDescription() : null;
    }
}
