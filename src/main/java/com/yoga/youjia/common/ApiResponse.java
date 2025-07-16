package com.yoga.youjia.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一API响应类
 *
 * 所有API端点的标准响应格式
 *
 * @param <T> 响应数据类型
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private T msg;
    private String error_code;
    private String error_msg;
    private String trace_id;

    /**
     * 创建成功响应
     *
     * @param data 响应数据
     * @return 成功的响应对象
     */
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .msg(data)
                .build();
    }

    /**
     * 创建成功响应（无数据）
     *
     * @return 成功的响应对象
     */
    public static <T> ApiResponse<T> success() {
        return success(null);
    }

    /**
     * 创建错误响应
     *
     * @param errorCode 错误代码
     * @param errorMsg 错误消息
     * @return 错误的响应对象
     */
    public static <T> ApiResponse<T> error(String errorCode, String errorMsg) {
        return ApiResponse.<T>builder()
                .success(false)
                .error_code(errorCode)
                .error_msg(errorMsg)
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
     * @param errorMsg 自定义错误消息
     * @return 错误的响应对象
     */
    public static <T> ApiResponse<T> error(ResultCode resultCode, String errorMsg) {
        return error(String.valueOf(resultCode.getCode()), "请求错误: " + errorMsg);
    }
}

