package com.example.demo.config;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;

@Component
public class DataInitializer implements CommandLineRunner {
    
    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            createTestUsers();
            log.info("测试数据初始化完成");
        } else {
            log.info("数据库中已有数据，跳过初始化");
        }
    }
    
    private void createTestUsers() {
        // 超级管理员
        User superAdmin = new User();
        superAdmin.setUsername("superadmin");
        superAdmin.setPassword(passwordEncoder.encode("123456"));
        superAdmin.setPhone("13800000001");
        superAdmin.setEmail("superadmin@example.com");
        superAdmin.setFullName("超级管理员");
        superAdmin.setStatus(User.UserStatus.ACTIVE);
        superAdmin.setRoles(RoleConfig.SUPER_ADMIN_ROLES);
        userRepository.save(superAdmin);
        
        // 管理员
        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("123456"));
        admin.setPhone("13800000002");
        admin.setEmail("admin@example.com");
        admin.setFullName("系统管理员");
        admin.setStatus(User.UserStatus.ACTIVE);
        admin.setRoles(RoleConfig.ADMIN_ROLES);
        userRepository.save(admin);
        
        // 内容管理员
        User contentManager = new User();
        contentManager.setUsername("content");
        contentManager.setPassword(passwordEncoder.encode("123456"));
        contentManager.setPhone("13800000003");
        contentManager.setEmail("content@example.com");
        contentManager.setFullName("内容管理员");
        contentManager.setStatus(User.UserStatus.ACTIVE);
        contentManager.setRoles(RoleConfig.CONTENT_MANAGER_ROLES);
        userRepository.save(contentManager);
        
        // 财务人员
        User finance = new User();
        finance.setUsername("finance");
        finance.setPassword(passwordEncoder.encode("123456"));
        finance.setPhone("13800000004");
        finance.setEmail("finance@example.com");
        finance.setFullName("财务人员");
        finance.setStatus(User.UserStatus.ACTIVE);
        finance.setRoles(RoleConfig.FINANCE_ROLES);
        userRepository.save(finance);
        
        // 客服人员
        User customerService = new User();
        customerService.setUsername("service");
        customerService.setPassword(passwordEncoder.encode("123456"));
        customerService.setPhone("13800000005");
        customerService.setEmail("service@example.com");
        customerService.setFullName("客服人员");
        customerService.setStatus(User.UserStatus.ACTIVE);
        customerService.setRoles(RoleConfig.CUSTOMER_SERVICE_ROLES);
        userRepository.save(customerService);
        
        // 普通用户1
        User user1 = new User();
        user1.setUsername("user1");
        user1.setPassword(passwordEncoder.encode("123456"));
        user1.setPhone("13800000006");
        user1.setEmail("user1@example.com");
        user1.setFullName("张三");
        user1.setStatus(User.UserStatus.ACTIVE);
        user1.setRoles(RoleConfig.USER_ROLES);
        userRepository.save(user1);
        
        // 普通用户2
        User user2 = new User();
        user2.setUsername("user2");
        user2.setPassword(passwordEncoder.encode("123456"));
        user2.setPhone("13800000007");
        user2.setEmail("user2@example.com");
        user2.setFullName("李四");
        user2.setStatus(User.UserStatus.ACTIVE);
        user2.setRoles(RoleConfig.USER_ROLES);
        userRepository.save(user2);
        
        // 访客用户
        User guest = new User();
        guest.setUsername("guest");
        guest.setPassword(passwordEncoder.encode("123456"));
        guest.setPhone("13800000008");
        guest.setEmail("guest@example.com");
        guest.setFullName("访客用户");
        guest.setStatus(User.UserStatus.ACTIVE);
        guest.setRoles(RoleConfig.GUEST_ROLES);
        userRepository.save(guest);
        
        // 非活跃用户
        User inactiveUser = new User();
        inactiveUser.setUsername("inactive");
        inactiveUser.setPassword(passwordEncoder.encode("123456"));
        inactiveUser.setPhone("13800000009");
        inactiveUser.setEmail("inactive@example.com");
        inactiveUser.setFullName("非活跃用户");
        inactiveUser.setStatus(User.UserStatus.INACTIVE);
        inactiveUser.setRoles(RoleConfig.USER_ROLES);
        userRepository.save(inactiveUser);
        
        log.info("创建了 {} 个测试用户", userRepository.count());
    }
} 