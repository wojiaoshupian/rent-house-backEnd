package com.example.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;


/**
 * 用户注册DTO
 * 只包含前端需要传递的字段，不包含id等后端生成的字段
 */

public class UserRegistrationDto {
    
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度必须在3-50个字符之间")
    private String username;
    
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, message = "密码长度至少6个字符")
    private String password;
    
    @NotBlank(message = "手机号码不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号码格式不正确")
    private String phone;
    
    @Email(message = "邮箱格式不正确")
    private String email;
    
    @Size(max = 100, message = "姓名长度不能超过100个字符")
    private String fullName;

    // 构造函数
    public UserRegistrationDto() {}

    public UserRegistrationDto(String username, String password, String phone, String email, String fullName) {
        this.username = username;
        this.password = password;
        this.phone = phone;
        this.email = email;
        this.fullName = fullName;
    }

    // 手动添加getter/setter方法
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
} 