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
    date = "2025-08-10T13:38:45+0800",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
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
        utilityReadingDto.setPhotos( stringToList( entity.getPhotos() ) );
        utilityReadingDto.setId( entity.getId() );
        utilityReadingDto.setRoomId( entity.getRoomId() );
        utilityReadingDto.setReadingDate( entity.getReadingDate() );
        utilityReadingDto.setReadingTime( entity.getReadingTime() );
        utilityReadingDto.setElectricityReading( entity.getElectricityReading() );
        utilityReadingDto.setElectricityPreviousReading( entity.getElectricityPreviousReading() );
        utilityReadingDto.setElectricityUsage( entity.getElectricityUsage() );
        utilityReadingDto.setWaterReading( entity.getWaterReading() );
        utilityReadingDto.setWaterPreviousReading( entity.getWaterPreviousReading() );
        utilityReadingDto.setWaterUsage( entity.getWaterUsage() );
        utilityReadingDto.setHotWaterReading( entity.getHotWaterReading() );
        utilityReadingDto.setHotWaterPreviousReading( entity.getHotWaterPreviousReading() );
        utilityReadingDto.setHotWaterUsage( entity.getHotWaterUsage() );
        utilityReadingDto.setMeterReader( entity.getMeterReader() );
        utilityReadingDto.setReadingType( entity.getReadingType() );
        utilityReadingDto.setReadingStatus( entity.getReadingStatus() );
        utilityReadingDto.setNotes( entity.getNotes() );
        utilityReadingDto.setCreatedBy( entity.getCreatedBy() );
        utilityReadingDto.setCreatedAt( entity.getCreatedAt() );
        utilityReadingDto.setUpdatedAt( entity.getUpdatedAt() );

        return utilityReadingDto;
    }

    @Override
    public UtilityReading toEntity(UtilityReadingDto dto) {
        if ( dto == null ) {
            return null;
        }

        UtilityReading utilityReading = new UtilityReading();

        utilityReading.setPhotos( listToString( dto.getPhotos() ) );
        utilityReading.setId( dto.getId() );
        utilityReading.setRoomId( dto.getRoomId() );
        utilityReading.setReadingDate( dto.getReadingDate() );
        utilityReading.setReadingTime( dto.getReadingTime() );
        utilityReading.setElectricityReading( dto.getElectricityReading() );
        utilityReading.setWaterReading( dto.getWaterReading() );
        utilityReading.setHotWaterReading( dto.getHotWaterReading() );
        utilityReading.setMeterReader( dto.getMeterReader() );
        utilityReading.setReadingType( dto.getReadingType() );
        utilityReading.setReadingStatus( dto.getReadingStatus() );
        utilityReading.setNotes( dto.getNotes() );
        utilityReading.setCreatedBy( dto.getCreatedBy() );
        utilityReading.setCreatedAt( dto.getCreatedAt() );
        utilityReading.setUpdatedAt( dto.getUpdatedAt() );

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
