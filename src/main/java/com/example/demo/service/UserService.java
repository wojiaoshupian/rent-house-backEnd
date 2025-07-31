package com.example.demo.service;

import com.example.demo.dto.UserDto;
import com.example.demo.dto.UserRegistrationDto;
import com.example.demo.entity.User;
import com.example.demo.mapper.UserMapper;
import com.example.demo.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    
    public UserService(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }
    
    /**
     * 用户注册
     * @param registrationDto 注册信息
     * @return 注册成功的用户信息
     */
    @Transactional
    @CacheEvict(value = {"users", "activeUsers"}, allEntries = true)
    public UserDto registerUser(UserRegistrationDto registrationDto) {
        log.info("用户注册: {}", registrationDto.getUsername());
        
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(registrationDto.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 检查手机号码是否已存在
        if (userRepository.existsByPhone(registrationDto.getPhone())) {
            throw new RuntimeException("手机号码已存在");
        }
        
        // 创建新用户
        User user = new User();
        user.setUsername(registrationDto.getUsername());
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        user.setPhone(registrationDto.getPhone());
        user.setEmail(registrationDto.getEmail());
        user.setFullName(registrationDto.getFullName());
        user.setStatus(User.UserStatus.ACTIVE);
        user.setRoles(java.util.Set.of("USER")); // 默认角色为USER
        
        User savedUser = userRepository.save(user);
        log.info("用户注册成功: {}", savedUser.getUsername());
        
        return userMapper.toDto(savedUser);
    }
    
    /**
     * 检查用户名是否可用
     * @param username 用户名
     * @return 是否可用
     */
    public boolean isUsernameAvailable(String username) {
        return !userRepository.existsByUsername(username);
    }
    
    /**
     * 检查手机号码是否可用
     * @param phone 手机号码
     * @return 是否可用
     */
    public boolean isPhoneAvailable(String phone) {
        return !userRepository.existsByPhone(phone);
    }
    
    /**
     * 检查邮箱是否可用
     * @param email 邮箱
     * @return 是否可用
     */
    public boolean isEmailAvailable(String email) {
        return !userRepository.existsByEmail(email);
    }
    
    @Cacheable(value = "users", key = "#id")
    public Optional<UserDto> findById(Long id) {
        log.info("查找用户，ID: {}", id);
        return userRepository.findById(id)
                .map(userMapper::toDto);
    }
    
    @Cacheable(value = "users", key = "#username")
    public Optional<UserDto> findByUsername(String username) {
        log.info("根据用户名查找用户: {}", username);
        return userRepository.findByUsername(username)
                .map(userMapper::toDto);
    }
    
    /**
     * 获取所有用户
     * @return 所有用户列表
     */
    @Cacheable(value = "users")
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .toList();
    }
    
    /**
     * 根据ID获取用户
     * @param id 用户ID
     * @return 用户信息
     */
    public UserDto getUserById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toDto)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
    }
    
    public Page<UserDto> findAll(Pageable pageable) {
        log.info("分页查询所有用户");
        return userRepository.findAll(pageable)
                .map(userMapper::toDto);
    }
    
    @Cacheable(value = "activeUsers")
    public List<UserDto> findAllActiveUsers() {
        log.info("查询所有活跃用户");
        return userMapper.toDtoList(userRepository.findAllActiveUsers());
    }
    
    public List<UserDto> searchUsers(String keyword) {
        log.info("搜索用户，关键词: {}", keyword);
        return userMapper.toDtoList(userRepository.searchUsers(keyword));
    }
    
    @CacheEvict(value = {"users", "activeUsers"}, allEntries = true)
    public UserDto createUser(UserDto userDto) {
        log.info("创建新用户: {}", userDto.getUsername());
        
        if (userRepository.existsByUsername(userDto.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        
        if (userDto.getEmail() != null && userRepository.existsByEmail(userDto.getEmail())) {
            throw new RuntimeException("邮箱已存在");
        }
        
        User user = userMapper.toEntity(userDto);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        
        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }
    
    @CacheEvict(value = {"users", "activeUsers"}, allEntries = true)
    public UserDto updateUser(Long id, UserDto userDto) {
        log.info("更新用户，ID: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        // 手动更新用户属性
        if (userDto.getUsername() != null) {
            user.setUsername(userDto.getUsername());
        }
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        if (userDto.getFullName() != null) {
            user.setFullName(userDto.getFullName());
        }
        if (userDto.getStatus() != null) {
            user.setStatus(User.UserStatus.valueOf(userDto.getStatus()));
        }
        if (userDto.getRoles() != null) {
            user.setRoles(userDto.getRoles());
        }
        
        if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }
        
        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }
    
    @CacheEvict(value = {"users", "activeUsers"}, allEntries = true)
    public void deleteUser(Long id) {
        log.info("删除用户，ID: {}", id);
        userRepository.deleteById(id);
    }
    
    public long countActiveUsers() {
        return userRepository.countActiveUsers();
    }
} 