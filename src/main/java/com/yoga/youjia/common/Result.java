package com.yoga.youjia.common;

import lombok.Data;
import java.io.Serializable;

/**
 * 统一响应类
 * @param <T> 响应数据类型
 * @deprecated 该类已过时，请使用 ApiResponse 替代, 此类将来可能会被移除。
 */
@Data
@Deprecated(since = "1.0", forRemoval = true)
public class Result<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private int code;  // 状态码
    private String message; // 响应消息
    private T data; // 响应数据

    // 私有构造函数，防止直接实例化
    private Result() {}

    // 私有构造函数，用于创建结果对象
    private Result(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 成功响应
     * @param data 响应数据
     * @param <T> 数据类型
     * @return Result<T> 成功的响应对象
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "操作成功", data);
    }

    /**
     * 成功响应（无数据）
     * @param <T> 数据类型
     * @return Result<T> 成功的响应对象
     */
    public static <T> Result<T> success() {
        return success(null);
    }

    /**
     * 错误响应
     * @param code 错误码
     * @param message 错误消息
     * @param <T> 数据类型
     * @return Result<T> 错误的响应对象
     */
    public static <T> Result<T> error(int code, String message) {
        return new Result<>(code, message, null);
    }

    /**
     * 错误响应（使用ResultCode枚举）
     * @param resultCode 结果代码枚举
     * @param <T> 数据类型
     * @return Result<T> 错误的响应对象
     */
    public static <T> Result<T> error(ResultCode resultCode) {
        return error(resultCode.getCode(), resultCode.getMessage());
    }

    /**
     * 错误响应（使用ResultCode枚举和自定义消息）
     * @param resultCode 结果代码枚举
     * @param message 自定义错误消息
     * @param <T> 数据类型
     * @return Result<T> 错误的响应对象
     */
    public static <T> Result<T> error(ResultCode resultCode, String message) {
        return error(resultCode.getCode(), message);
    }
}
