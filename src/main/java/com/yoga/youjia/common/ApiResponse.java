package com.yoga.youjia.common;

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
     * 响应数据
     */
    private T data;
    
    /**
     * 响应消息
     */
    private String message;
    
    /**
     * 错误代码（仅在失败时存在）
     */
    private String errorCode;
    
    /**
     * 错误详细信息（仅在失败时存在）
     */
    private String errorMessage;
    
    /**
     * 时间戳
     */
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
    
    /**
     * 请求跟踪ID（用于日志追踪）
     */
    private String traceId;

    /**
     * 创建成功响应
     *
     * @param data 响应数据
     * @param message 成功消息
     * @return 成功的响应对象
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .message(message)
                .build();
    }

    /**
     * 创建成功响应（带数据）
     *
     * @param data 响应数据
     * @return 成功的响应对象
     */
    public static <T> ApiResponse<T> success(T data) {
        return success(data, "操作成功");
    }

    /**
     * 创建成功响应（无数据）
     *
     * @return 成功的响应对象
     */
    public static <T> ApiResponse<T> success() {
        return success(null, "操作成功");
    }

    /**
     * 创建成功响应（仅消息）
     *
     * @param message 成功消息
     * @return 成功的响应对象
     */
    public static <T> ApiResponse<T> success(String message) {
        return success(null, message);
    }

    /**
     * 创建错误响应
     *
     * @param errorCode 错误代码
     * @param errorMessage 错误消息
     * @return 错误的响应对象
     */
    public static <T> ApiResponse<T> error(String errorCode, String errorMessage) {
        return ApiResponse.<T>builder()
                .success(false)
                .errorCode(errorCode)
                .errorMessage(errorMessage)
                .build();
    }

    /**
     * 创建错误响应（带ResultCode）
     *
     * @param resultCode 结果代码枚举
     * @return 错误的响应对象
     */
    public static <T> ApiResponse<T> error(ResultCode resultCode) {
        return error(String.valueOf(resultCode.getCode()), resultCode.getMessage());
    }

    /**
     * 创建错误响应（带ResultCode和自定义消息）
     *
     * @param resultCode 结果代码枚举
     * @param customMessage 自定义错误消息
     * @return 错误的响应对象
     */
    public static <T> ApiResponse<T> error(ResultCode resultCode, String customMessage) {
        return error(String.valueOf(resultCode.getCode()), customMessage);
    }

    /**
     * 设置跟踪ID
     *
     * @param traceId 跟踪ID
     * @return 当前对象（支持链式调用）
     */
    public ApiResponse<T> withTraceId(String traceId) {
        this.traceId = traceId;
        return this;
    }
}

