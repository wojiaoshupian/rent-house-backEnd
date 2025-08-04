package com.example.demo.repository;

import com.example.demo.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    /**
     * 根据楼宇ID查找房间
     */
    List<Room> findByBuildingId(Long buildingId);

    /**
     * 根据创建者查找房间
     */
    List<Room> findByCreatedBy(Long createdBy);

    /**
     * 检查房号在指定楼宇中是否已存在
     */
    boolean existsByRoomNumberAndBuildingId(String roomNumber, Long buildingId);

    /**
     * 根据房号和楼宇ID查找房间
     */
    Room findByRoomNumberAndBuildingId(String roomNumber, Long buildingId);

    /**
     * 搜索房间（根据房号）
     */
    @Query("SELECT r FROM Room r WHERE r.roomNumber LIKE %:keyword%")
    List<Room> searchRooms(@Param("keyword") String keyword);

    /**
     * 根据楼宇ID列表查找房间
     */
    @Query("SELECT r FROM Room r WHERE r.buildingId IN :buildingIds")
    List<Room> findByBuildingIdIn(@Param("buildingIds") List<Long> buildingIds);

    /**
     * 统计指定楼宇的房间数量
     */
    long countByBuildingId(Long buildingId);

    /**
     * 根据楼宇ID删除房间
     */
    void deleteByBuildingId(Long buildingId);
}
