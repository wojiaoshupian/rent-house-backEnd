package com.example.demo.repository;

import com.example.demo.entity.UserBuilding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserBuildingRepository extends JpaRepository<UserBuilding, Long> {

    List<UserBuilding> findByUserId(Long userId);

    List<UserBuilding> findByBuildingId(Long buildingId);

    boolean existsByUserIdAndBuildingId(Long userId, Long buildingId);

    Optional<UserBuilding> findByUserIdAndBuildingId(Long userId, Long buildingId);

    @Modifying
    @Transactional
    @Query("DELETE FROM UserBuilding ub WHERE ub.buildingId = :buildingId")
    void deleteByBuildingId(@Param("buildingId") Long buildingId);
}