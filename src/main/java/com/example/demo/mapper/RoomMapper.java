package com.example.demo.mapper;

import com.example.demo.dto.RoomDto;
import com.example.demo.entity.Room;
import com.example.demo.entity.Building;
import org.springframework.stereotype.Component;

@Component
public class RoomMapper {

    public RoomDto toDto(Room room) {
        if (room == null) {
            return null;
        }

        RoomDto dto = new RoomDto();
        dto.setId(room.getId());
        dto.setRoomNumber(room.getRoomNumber());
        dto.setRent(room.getRent());
        dto.setDefaultDeposit(room.getDefaultDeposit());
        dto.setElectricityUnitPrice(room.getElectricityUnitPrice());
        dto.setWaterUnitPrice(room.getWaterUnitPrice());
        dto.setHotWaterUnitPrice(room.getHotWaterUnitPrice());
        dto.setBuildingId(room.getBuildingId());
        dto.setRentalStatus(room.getRentalStatus());
        dto.setRentalStatusDescription(room.getRentalStatus() != null ? room.getRentalStatus().getDescription() : null);
        dto.setCreatedBy(room.getCreatedBy());
        dto.setCreatedAt(room.getCreatedAt());
        dto.setUpdatedAt(room.getUpdatedAt());

        // 设置有效的费用单价（暂时直接使用房间的设置）
        dto.setEffectiveElectricityUnitPrice(room.getEffectiveElectricityUnitPrice());
        dto.setEffectiveWaterUnitPrice(room.getEffectiveWaterUnitPrice());
        dto.setEffectiveHotWaterUnitPrice(room.getEffectiveHotWaterUnitPrice());

        return dto;
    }

    public RoomDto toDtoWithBuilding(Room room, Building building) {
        if (room == null) {
            return null;
        }

        RoomDto dto = toDto(room);
        
        // 设置楼宇信息
        if (building != null) {
            dto.setBuildingName(building.getBuildingName());
            dto.setLandlordName(building.getLandlordName());
            
            // 计算有效的费用单价
            dto.setEffectiveElectricityUnitPrice(
                room.getElectricityUnitPrice() != null ? 
                room.getElectricityUnitPrice() : building.getElectricityUnitPrice()
            );
            dto.setEffectiveWaterUnitPrice(
                room.getWaterUnitPrice() != null ? 
                room.getWaterUnitPrice() : building.getWaterUnitPrice()
            );
            dto.setEffectiveHotWaterUnitPrice(
                room.getHotWaterUnitPrice() != null ? 
                room.getHotWaterUnitPrice() : building.getHotWaterUnitPrice()
            );
        }

        return dto;
    }

    public Room toEntity(RoomDto roomDto) {
        if (roomDto == null) {
            return null;
        }

        Room room = new Room();
        room.setRoomNumber(roomDto.getRoomNumber());
        room.setRent(roomDto.getRent());
        room.setDefaultDeposit(roomDto.getDefaultDeposit());
        room.setElectricityUnitPrice(roomDto.getElectricityUnitPrice());
        room.setWaterUnitPrice(roomDto.getWaterUnitPrice());
        room.setHotWaterUnitPrice(roomDto.getHotWaterUnitPrice());
        room.setBuildingId(roomDto.getBuildingId());

        return room;
    }

    public void updateEntityFromDto(RoomDto roomDto, Room room) {
        if (roomDto == null || room == null) {
            return;
        }

        if (roomDto.getRoomNumber() != null) {
            room.setRoomNumber(roomDto.getRoomNumber());
        }
        if (roomDto.getRent() != null) {
            room.setRent(roomDto.getRent());
        }
        if (roomDto.getDefaultDeposit() != null) {
            room.setDefaultDeposit(roomDto.getDefaultDeposit());
        }
        // 注意：这些字段可以设置为null，表示使用楼宇设置
        room.setElectricityUnitPrice(roomDto.getElectricityUnitPrice());
        room.setWaterUnitPrice(roomDto.getWaterUnitPrice());
        room.setHotWaterUnitPrice(roomDto.getHotWaterUnitPrice());
        
        if (roomDto.getBuildingId() != null) {
            room.setBuildingId(roomDto.getBuildingId());
        }

        if (roomDto.getRentalStatus() != null) {
            room.setRentalStatus(roomDto.getRentalStatus());
        }
    }
}
