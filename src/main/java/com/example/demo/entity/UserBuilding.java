package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_buildings")
public class UserBuilding {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "building_id", nullable = false)
    private Long buildingId;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    // 构造函数
    public UserBuilding() {}
    
    public UserBuilding(Long userId, Long buildingId) {
        this.userId = userId;
        this.buildingId = buildingId;
        this.createdAt = LocalDateTime.now();
    }
    
    // Getter和Setter方法
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public Long getBuildingId() { return buildingId; }
    public void setBuildingId(Long buildingId) { this.buildingId = buildingId; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
} 