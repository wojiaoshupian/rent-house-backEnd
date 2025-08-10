package com.example.demo.mapper;

import com.example.demo.dto.UtilityReadingDto;
import com.example.demo.entity.UtilityReading;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.ArrayList;
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
    @Mapping(target = "photos", source = "photos", qualifiedByName = "stringToList")
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
    @Mapping(target = "photos", source = "photos", qualifiedByName = "listToString")
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

    /**
     * String转List<String>
     */
    @Named("stringToList")
    default List<String> stringToList(String photos) {
        if (photos == null || photos.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(photos, new TypeReference<List<String>>() {});
        } catch (JsonProcessingException e) {
            // 如果JSON解析失败，尝试按逗号分割
            String[] parts = photos.split(",");
            List<String> result = new ArrayList<>();
            for (String part : parts) {
                String trimmed = part.trim();
                if (!trimmed.isEmpty()) {
                    result.add(trimmed);
                }
            }
            return result;
        }
    }

    /**
     * List<String>转String
     */
    @Named("listToString")
    default String listToString(List<String> photos) {
        if (photos == null || photos.isEmpty()) {
            return null;
        }
        
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(photos);
        } catch (JsonProcessingException e) {
            // 如果JSON序列化失败，使用逗号分隔
            return String.join(",", photos);
        }
    }
}
