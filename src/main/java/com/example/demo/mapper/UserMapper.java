package com.example.demo.mapper;

import com.example.demo.dto.UserDto;
import com.example.demo.entity.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserMapper {
    
    public UserDto toDto(User user) {
        if (user == null) {
            return null;
        }
        
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        // 不映射密码
        dto.setEmail(user.getEmail());
        dto.setFullName(user.getFullName());
        dto.setStatus(user.getStatus() != null ? user.getStatus().name() : null);
        dto.setRoles(user.getRoles());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        
        return dto;
    }
    
    public User toEntity(UserDto userDto) {
        if (userDto == null) {
            return null;
        }
        
        User user = new User();
        user.setId(userDto.getId());
        user.setUsername(userDto.getUsername());
        user.setPassword(userDto.getPassword());
        user.setEmail(userDto.getEmail());
        user.setFullName(userDto.getFullName());
        if (userDto.getStatus() != null) {
            user.setStatus(User.UserStatus.valueOf(userDto.getStatus()));
        }
        user.setRoles(userDto.getRoles());
        user.setCreatedAt(userDto.getCreatedAt());
        user.setUpdatedAt(userDto.getUpdatedAt());
        
        return user;
    }
    
    public List<UserDto> toDtoList(List<User> users) {
        if (users == null) {
            return null;
        }
        
        return users.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
} 