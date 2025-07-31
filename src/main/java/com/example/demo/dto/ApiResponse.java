package com.example.demo.dto;



/**
 * 统一API响应格式
 */


public class ApiResponse<T> {
    
    private Integer code;        // 状态码
    private String message;      // 消息
    private T data;             // 数据
    private String token;        // JWT token（可选）
    private Long timestamp;      // 时间戳
    
    // 手动添加getter/setter方法（由于Lombok问题）
    public Integer getCode() { return code; }
    public void setCode(Integer code) { this.code = code; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
    
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    
    public Long getTimestamp() { return timestamp; }
    public void setTimestamp(Long timestamp) { this.timestamp = timestamp; }
    
    // 手动添加构造函数（由于Lombok问题）
    public ApiResponse() {}
    
    public ApiResponse(Integer code, String message, T data, String token, Long timestamp) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.token = token;
        this.timestamp = timestamp;
    }
    
    // 成功响应（无数据）
    public static <T> ApiResponse<T> success() {
        return new ApiResponse<T>(200, "操作成功", null, null, System.currentTimeMillis());
    }
    
    // 成功响应（有数据）
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<T>(200, "操作成功", data, null, System.currentTimeMillis());
    }
    
    // 成功响应（有数据和token）
    public static <T> ApiResponse<T> success(T data, String token) {
        return new ApiResponse<T>(200, "操作成功", data, token, System.currentTimeMillis());
    }
    
    // 失败响应
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<T>(400, message, null, null, System.currentTimeMillis());
    }
    
    // 失败响应（自定义状态码）
    public static <T> ApiResponse<T> error(Integer code, String message) {
        return new ApiResponse<T>(code, message, null, null, System.currentTimeMillis());
    }
    
    // 未授权响应
    public static <T> ApiResponse<T> unauthorized(String message) {
        return new ApiResponse<T>(401, message, null, null, System.currentTimeMillis());
    }
    
    // 禁止访问响应
    public static <T> ApiResponse<T> forbidden(String message) {
        return new ApiResponse<T>(403, message, null, null, System.currentTimeMillis());
    }
    
    // 服务器错误响应
    public static <T> ApiResponse<T> serverError(String message) {
        return new ApiResponse<T>(500, message, null, null, System.currentTimeMillis());
    }
} 