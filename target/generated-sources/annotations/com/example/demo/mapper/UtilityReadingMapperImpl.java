package com.example.demo.mapper;

import com.example.demo.dto.UtilityReadingDto;
import com.example.demo.entity.Room;
import com.example.demo.entity.UtilityReading;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-08-04T15:49:37+0800",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.42.50.v20250729-0351, environment: Java 21.0.8 (Eclipse Adoptium)"
)
@Component
public class UtilityReadingMapperImpl implements UtilityReadingMapper {

    @Override
    public UtilityReadingDto toDto(UtilityReading entity) {
        if ( entity == null ) {
            return null;
        }

        UtilityReadingDto utilityReadingDto = new UtilityReadingDto();

        utilityReadingDto.setRoomNumber( entityRoomRoomNumber( entity ) );
        utilityReadingDto.setReadingTypeDescription( readingTypeToDescription( entity.getReadingType() ) );
        utilityReadingDto.setReadingStatusDescription( readingStatusToDescription( entity.getReadingStatus() ) );
        utilityReadingDto.setCreatedAt( entity.getCreatedAt() );
        utilityReadingDto.setCreatedBy( entity.getCreatedBy() );
        utilityReadingDto.setElectricityPreviousReading( entity.getElectricityPreviousReading() );
        utilityReadingDto.setElectricityReading( entity.getElectricityReading() );
        utilityReadingDto.setElectricityUsage( entity.getElectricityUsage() );
        utilityReadingDto.setHotWaterPreviousReading( entity.getHotWaterPreviousReading() );
        utilityReadingDto.setHotWaterReading( entity.getHotWaterReading() );
        utilityReadingDto.setHotWaterUsage( entity.getHotWaterUsage() );
        utilityReadingDto.setId( entity.getId() );
        utilityReadingDto.setMeterReader( entity.getMeterReader() );
        utilityReadingDto.setNotes( entity.getNotes() );
        List<String> list = entity.getPhotos();
        if ( list != null ) {
            utilityReadingDto.setPhotos( new ArrayList<String>( list ) );
        }
        utilityReadingDto.setReadingDate( entity.getReadingDate() );
        utilityReadingDto.setReadingStatus( entity.getReadingStatus() );
        utilityReadingDto.setReadingTime( entity.getReadingTime() );
        utilityReadingDto.setReadingType( entity.getReadingType() );
        utilityReadingDto.setRoomId( entity.getRoomId() );
        utilityReadingDto.setUpdatedAt( entity.getUpdatedAt() );
        utilityReadingDto.setWaterPreviousReading( entity.getWaterPreviousReading() );
        utilityReadingDto.setWaterReading( entity.getWaterReading() );
        utilityReadingDto.setWaterUsage( entity.getWaterUsage() );

        return utilityReadingDto;
    }

    @Override
    public UtilityReading toEntity(UtilityReadingDto dto) {
        if ( dto == null ) {
            return null;
        }

        UtilityReading utilityReading = new UtilityReading();

        utilityReading.setCreatedAt( dto.getCreatedAt() );
        utilityReading.setCreatedBy( dto.getCreatedBy() );
        utilityReading.setElectricityReading( dto.getElectricityReading() );
        utilityReading.setHotWaterReading( dto.getHotWaterReading() );
        utilityReading.setId( dto.getId() );
        utilityReading.setMeterReader( dto.getMeterReader() );
        utilityReading.setNotes( dto.getNotes() );
        List<String> list = dto.getPhotos();
        if ( list != null ) {
            utilityReading.setPhotos( new ArrayList<String>( list ) );
        }
        utilityReading.setReadingDate( dto.getReadingDate() );
        utilityReading.setReadingStatus( dto.getReadingStatus() );
        utilityReading.setReadingTime( dto.getReadingTime() );
        utilityReading.setReadingType( dto.getReadingType() );
        utilityReading.setRoomId( dto.getRoomId() );
        utilityReading.setUpdatedAt( dto.getUpdatedAt() );
        utilityReading.setWaterReading( dto.getWaterReading() );

        return utilityReading;
    }

    @Override
    public List<UtilityReadingDto> toDtoList(List<UtilityReading> entities) {
        if ( entities == null ) {
            return null;
        }

        List<UtilityReadingDto> list = new ArrayList<UtilityReadingDto>( entities.size() );
        for ( UtilityReading utilityReading : entities ) {
            list.add( toDto( utilityReading ) );
        }

        return list;
    }

    @Override
    public List<UtilityReading> toEntityList(List<UtilityReadingDto> dtos) {
        if ( dtos == null ) {
            return null;
        }

        List<UtilityReading> list = new ArrayList<UtilityReading>( dtos.size() );
        for ( UtilityReadingDto utilityReadingDto : dtos ) {
            list.add( toEntity( utilityReadingDto ) );
        }

        return list;
    }

    private String entityRoomRoomNumber(UtilityReading utilityReading) {
        if ( utilityReading == null ) {
            return null;
        }
        Room room = utilityReading.getRoom();
        if ( room == null ) {
            return null;
        }
        String roomNumber = room.getRoomNumber();
        if ( roomNumber == null ) {
            return null;
        }
        return roomNumber;
    }
}
