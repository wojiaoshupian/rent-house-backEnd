package com.example.demo.repository;

import com.example.demo.entity.Building;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BuildingRepository extends JpaRepository<Building, Long> {

    List<Building> findByCreatedBy(Long createdBy);

    boolean existsByBuildingName(String buildingName);

    @Query("SELECT b FROM Building b WHERE b.buildingName LIKE %:keyword% OR b.landlordName LIKE %:keyword%")
    List<Building> searchBuildings(@Param("keyword") String keyword);
}