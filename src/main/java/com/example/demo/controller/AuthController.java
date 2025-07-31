package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.UserDto;
import com.example.demo.dto.UserRegistrationDto;
import com.example.demo.service.UserService;
import com.example.demo.security.JwtTokenUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "认证管理", description = "用户认证相关接口")
public class AuthController {
    
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    
    public AuthController() {}
    
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户登录并返回JWT token")
    public ResponseEntity<ApiResponse<UserDto>> login(@RequestBody Map<String, String> loginRequest) {
        try {
            String username = loginRequest.get("username");
            String password = loginRequest.get("password");
            
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
            );
            
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtTokenUtil.generateToken(userDetails);
            
            UserDto userDto = userService.findByUsername(username).orElse(null);
            if (userDto == null) {
                return ResponseEntity.badRequest().body(ApiResponse.error("用户信息获取失败"));
            }
            
            log.info("用户登录成功: {}", username);
            
            return ResponseEntity.ok(ApiResponse.success(userDto, token));
            
        } catch (Exception e) {
            log.error("用户登录失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error("用户名或密码错误"));
        }
    }
    
    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "注册新用户，包含用户名和手机号码重复校验，注册成功后返回JWT token")
    public ResponseEntity<ApiResponse<UserDto>> register(@Valid @RequestBody UserRegistrationDto registrationDto) {
        try {
            UserDto createdUser = userService.registerUser(registrationDto);
            log.info("用户注册成功: {}", registrationDto.getUsername());
            
            // 生成JWT token
            String token = jwtTokenUtil.generateToken(createdUser.getUsername());
            
            return ResponseEntity.ok(ApiResponse.success(createdUser, token));
        } catch (Exception e) {
            log.error("用户注册失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/check-username/{username}")
    @Operation(summary = "检查用户名可用性", description = "检查用户名是否已被使用")
    public ResponseEntity<ApiResponse<Map<String, Object>>> checkUsername(@PathVariable String username) {
        boolean isAvailable = userService.isUsernameAvailable(username);
        Map<String, Object> response = new HashMap<>();
        response.put("username", username);
        response.put("available", isAvailable);
        response.put("message", isAvailable ? "用户名可用" : "用户名已存在");
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @GetMapping("/check-phone/{phone}")
    @Operation(summary = "检查手机号码可用性", description = "检查手机号码是否已被使用")
    public ResponseEntity<ApiResponse<Map<String, Object>>> checkPhone(@PathVariable String phone) {
        boolean isAvailable = userService.isPhoneAvailable(phone);
        Map<String, Object> response = new HashMap<>();
        response.put("phone", phone);
        response.put("available", isAvailable);
        response.put("message", isAvailable ? "手机号码可用" : "手机号码已存在");
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @GetMapping("/check-email/{email}")
    @Operation(summary = "检查邮箱可用性", description = "检查邮箱是否已被使用")
    public ResponseEntity<ApiResponse<Map<String, Object>>> checkEmail(@PathVariable String email) {
        boolean isAvailable = userService.isEmailAvailable(email);
        Map<String, Object> response = new HashMap<>();
        response.put("email", email);
        response.put("available", isAvailable);
        response.put("message", isAvailable ? "邮箱可用" : "邮箱已存在");
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @GetMapping("/validate")
    @Operation(summary = "验证令牌", description = "验证JWT令牌是否有效")
    public ResponseEntity<ApiResponse<Map<String, Object>>> validateToken(@RequestHeader("Authorization") String token) {
        if (token != null && token.startsWith("Bearer ")) {
            String jwt = token.substring(7);
            boolean isValid = jwtTokenUtil.validateToken(jwt);
            
            if (isValid) {
                String username = jwtTokenUtil.extractUsername(jwt);
                Map<String, Object> response = new HashMap<>();
                response.put("valid", true);
                response.put("username", username);
                return ResponseEntity.ok(ApiResponse.success(response));
            }
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("valid", false);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
} 