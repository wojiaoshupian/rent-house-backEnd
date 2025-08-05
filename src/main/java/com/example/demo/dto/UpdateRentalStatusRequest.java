package com.example.demo.dto;

import com.example.demo.entity.Room;
import jakarta.validation.constraints.NotNull;

/**
 * 更新房间出租状态请求DTO
 */
public class UpdateRentalStatusRequest {
    
    @NotNull(message = "出租状态不能为空")
    private Room.RentalStatus rentalStatus;
    
    public UpdateRentalStatusRequest() {}
    
    public UpdateRentalStatusRequest(Room.RentalStatus rentalStatus) {
        this.rentalStatus = rentalStatus;
    }
    
    public Room.RentalStatus getRentalStatus() {
        return rentalStatus;
    }
    
    public void setRentalStatus(Room.RentalStatus rentalStatus) {
        this.rentalStatus = rentalStatus;
    }
}
