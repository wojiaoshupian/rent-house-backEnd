package com.example.demo.util;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

/**
 * API响应包装类
 */
public class ApiResponse<T> {

    /**
     * 响应码
     */
    private int code;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 令牌
     */
    private String token;

    /**
     * 令牌过期时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime tokenExpiresAt;

    /**
     * 响应时间戳
     */
    private long timestamp;

    /**
     * 分页信息
     */
    private Pagination pagination;

    public ApiResponse() {
        this.timestamp = System.currentTimeMillis();
    }

    public ApiResponse(int code, String message, T data) {
        this();
        this.code = code;
        this.message = message;
        this.data = data;
        this.pagination = null;
    }

    public ApiResponse(int code, String message, T data, Pagination pagination) {
        this();
        this.code = code;
        this.message = message;
        this.data = data;
        this.pagination = pagination;
    }

    /**
     * 成功响应
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "操作成功", data);
    }

    /**
     * 成功响应（无数据）
     */
    public static <T> ApiResponse<T> success() {
        return success(null);
    }

    /**
     * 成功响应（带消息）
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(200, message, data);
    }

    /**
     * 成功响应（带分页信息）
     */
    public static <T> ApiResponse<T> success(T data, Pagination pagination) {
        return new ApiResponse<>(200, "操作成功", data, pagination);
    }

    /**
     * 成功响应（带token信息）
     */
    public static <T> ApiResponse<T> success(T data, String token, Long tokenExpiresAt) {
        ApiResponse<T> response = new ApiResponse<>(200, "操作成功", data);
        response.setToken(token);
        response.setTokenExpiresAt(tokenExpiresAt != null ?
            java.time.LocalDateTime.ofInstant(
                java.time.Instant.ofEpochMilli(tokenExpiresAt),
                java.time.ZoneId.systemDefault()
            ) : null);
        return response;
    }

    /**
     * 错误响应
     */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(400, message, null);
    }

    /**
     * 错误响应（带错误码）
     */
    public static <T> ApiResponse<T> error(int code, String message) {
        return new ApiResponse<>(code, message, null);
    }

    /**
     * 未授权响应
     */
    public static <T> ApiResponse<T> unauthorized(String message) {
        return new ApiResponse<>(401, message, null);
    }

    /**
     * 禁止访问响应
     */
    public static <T> ApiResponse<T> forbidden(String message) {
        return new ApiResponse<>(403, message, null);
    }

    /**
     * 资源未找到响应
     */
    public static <T> ApiResponse<T> notFound(String message) {
        return new ApiResponse<>(404, message, null);
    }

    /**
     * 服务器内部错误响应
     */
    public static <T> ApiResponse<T> serverError(String message) {
        return new ApiResponse<>(500, message, null);
    }

    // ==================== Getter和Setter方法 ====================

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDateTime getTokenExpiresAt() {
        return tokenExpiresAt;
    }

    public void setTokenExpiresAt(LocalDateTime tokenExpiresAt) {
        this.tokenExpiresAt = tokenExpiresAt;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }

    /**
     * 分页信息内部类
     */
    public static class Pagination {
        private int page;           // 当前页码（从0开始）
        private int size;           // 每页大小
        private long totalElements; // 总元素数
        private int totalPages;     // 总页数
        private boolean first;      // 是否第一页
        private boolean last;       // 是否最后一页

        public Pagination() {}

        public Pagination(int page, int size, long totalElements, int totalPages, boolean first, boolean last) {
            this.page = page;
            this.size = size;
            this.totalElements = totalElements;
            this.totalPages = totalPages;
            this.first = first;
            this.last = last;
        }

        // Getter和Setter方法
        public int getPage() { return page; }
        public void setPage(int page) { this.page = page; }

        public int getSize() { return size; }
        public void setSize(int size) { this.size = size; }

        public long getTotalElements() { return totalElements; }
        public void setTotalElements(long totalElements) { this.totalElements = totalElements; }

        public int getTotalPages() { return totalPages; }
        public void setTotalPages(int totalPages) { this.totalPages = totalPages; }

        public boolean isFirst() { return first; }
        public void setFirst(boolean first) { this.first = first; }

        public boolean isLast() { return last; }
        public void setLast(boolean last) { this.last = last; }
    }
}
