package com.yoga.youjia.common;

import com.yoga.youjia.common.enums.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 统一API响应类
 *
 * 所有API端点的标准响应格式，遵循RESTful规范
 *
 * @param <T> 响应数据类型
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    
    /**
     * 请求是否成功
     */
    private boolean success;
    
    /**
     * 错误码（新的错误码系统）
     */
    private String code;
    
    /**
     * 响应消息
     */
    private String message;
    
    /**
     * 响应数据
     */
    private T data;
    
    /**
     * 错误详细信息（用于调试）
     */
    private String details;
    
    /**
     * 时间戳
     */
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
    
    /**
     * 请求跟踪ID（用于日志追踪）
     */
    private String traceId;

    // ========== 成功响应方法 ==========
    
    /**
     * 创建成功响应
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .code(ErrorCode.SUCCESS.getCode())
                .message(message)
                .data(data)
                .build();
    }

    /**
     * 创建成功响应（带数据）
     */
    public static <T> ApiResponse<T> success(T data) {
        return success(data, "操作成功");
    }

    /**
     * 创建成功响应（无数据）
     */
    public static <T> ApiResponse<T> success() {
        return success(null, "操作成功");
    }

    /**
     * 创建成功响应（仅消息）
     */
    public static <T> ApiResponse<T> success(String message) {
        return success(null, message);
    }

    // ========== 错误响应方法 ==========
    
    /**
     * 创建错误响应（使用ErrorCode）
     */
    public static <T> ApiResponse<T> error(ErrorCode errorCode) {
        return ApiResponse.<T>builder()
                .success(false)
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();
    }
    
    /**
     * 创建错误响应（使用ErrorCode和自定义消息）
     */
    public static <T> ApiResponse<T> error(ErrorCode errorCode, String customMessage) {
        return ApiResponse.<T>builder()
                .success(false)
                .code(errorCode.getCode())
                .message(customMessage)
                .build();
    }
    
    /**
     * 创建错误响应（使用ErrorCode和详细信息）
     */
    public static <T> ApiResponse<T> error(ErrorCode errorCode, String customMessage, String details) {
        return ApiResponse.<T>builder()
                .success(false)
                .code(errorCode.getCode())
                .message(customMessage)
                .details(details)
                .build();
    }

    /**
     * 创建错误响应（自定义错误码和消息）
     */
    public static <T> ApiResponse<T> error(String code, String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .code(code)
                .message(message)
                .build();
    }
    
    /**
     * 创建错误响应（对ResultCode的兼容）
     */
    public static <T> ApiResponse<T> error(ResultCode resultCode) {
        return error(String.valueOf(resultCode.getCode()), resultCode.getMessage());
    }

    /**
     * 创建错误响应（对ResultCode的兼容，带自定义消息）
     */
    public static <T> ApiResponse<T> error(ResultCode resultCode, String customMessage) {
        return error(String.valueOf(resultCode.getCode()), customMessage);
    }
    
    // ========== 工具方法 ==========

    /**
     * 设置跟踪ID
     */
    public ApiResponse<T> withTraceId(String traceId) {
        this.traceId = traceId;
        return this;
    }
    
    /**
     * 设置详细信息
     */
    public ApiResponse<T> withDetails(String details) {
        this.details = details;
        return this;
    }
    
    /**
     * 检查是否为成功响应
     */
    public boolean isSuccess() {
        return success && ErrorCode.SUCCESS.getCode().equals(code);
    }
    
    /**
     * 检查是否为错误响应
     */
    public boolean isError() {
        return !isSuccess();
    }
}

