package com.yoga.youjia.common;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 统一API响应封装类
 * 
 * 这个类用于统一所有API接口的响应格式，提供一致的数据结构。
 * 统一响应格式的优势：
 * - 前端可以统一处理响应数据
 * - 便于错误处理和状态判断
 * - 提高代码的可维护性
 * - 符合RESTful API设计规范
 * 
 * 响应格式：
 * {
 *   "success": true/false,     // 操作是否成功
 *   "message": "操作结果描述",   // 响应消息
 *   "data": {...},            // 响应数据（成功时）
 *   "code": 200               // 业务状态码（可选）
 * }
 * 
 * 使用示例：
 * // 成功响应
 * ApiResponse.success("操作成功", userData);
 * 
 * // 失败响应
 * ApiResponse.error("用户名已存在");
 * 
 * // 自定义状态码
 * ApiResponse.error(4001, "参数验证失败");
 * 
 * @param <T> 响应数据的类型，支持泛型以提供类型安全
 */
@JsonInclude(JsonInclude.Include.NON_NULL)  // @JsonInclude注解的作用：
                                            // 1. 控制JSON序列化时包含哪些字段
                                            // 2. NON_NULL: 只序列化非null的字段
                                            // 3. 减少响应体大小，提高网络传输效率
                                            // 4. 避免前端收到不必要的null值
public class ApiResponse<T> {

    /**
     * 操作成功标识
     * 
     * true: 操作成功
     * false: 操作失败
     * 
     * 前端可以根据这个字段判断请求是否成功，
     * 而不需要依赖HTTP状态码。
     */
    private Boolean success;

    /**
     * 响应消息
     * 
     * 用于向用户展示操作结果的描述信息。
     * 成功时：如"注册成功"、"登录成功"
     * 失败时：如"用户名已存在"、"密码错误"
     */
    private String message;

    /**
     * 响应数据
     * 
     * 成功时包含具体的业务数据，如用户信息、列表数据等。
     * 失败时通常为null。
     * 使用泛型T提供类型安全。
     */
    private T data;

    /**
     * 业务状态码（可选）
     * 
     * 用于更细粒度的状态区分，补充HTTP状态码的不足。
     * 例如：
     * - 200: 成功
     * - 4001: 参数验证失败
     * - 4002: 用户名已存在
     * - 4003: 邮箱已存在
     * - 5001: 数据库连接失败
     */
    private Integer code;

    /**
     * 私有构造函数
     * 
     * 防止外部直接实例化，强制使用静态工厂方法。
     * 这样可以确保响应对象的创建方式统一，便于维护。
     */
    private ApiResponse() {}

    /**
     * 全参数构造函数
     * 
     * @param success 成功标识
     * @param message 响应消息
     * @param data 响应数据
     * @param code 业务状态码
     */
    private ApiResponse(Boolean success, String message, T data, Integer code) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.code = code;
    }

    // ================================
    // 成功响应的静态工厂方法
    // ================================

    /**
     * 创建成功响应（仅消息）
     * 
     * 适用于只需要返回成功消息，不需要返回具体数据的场景。
     * 如：删除操作、更新操作等。
     * 
     * @param message 成功消息
     * @return ApiResponse<Void> 成功响应对象
     */
    public static ApiResponse<Void> success(String message) {
        return new ApiResponse<>(true, message, null, null);
    }

    /**
     * 创建成功响应（消息 + 数据）
     * 
     * 最常用的成功响应方法，包含成功消息和具体数据。
     * 如：用户注册成功返回用户信息、查询操作返回查询结果等。
     * 
     * @param message 成功消息
     * @param data 响应数据
     * @param <T> 数据类型
     * @return ApiResponse<T> 成功响应对象
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data, null);
    }

    /**
     * 创建成功响应（仅数据，默认消息）
     * 
     * 适用于查询操作，重点是返回数据，消息相对不重要的场景。
     * 
     * @param data 响应数据
     * @param <T> 数据类型
     * @return ApiResponse<T> 成功响应对象
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "操作成功", data, null);
    }

    /**
     * 创建成功响应（完整参数）
     * 
     * 包含所有参数的成功响应，适用于需要自定义状态码的场景。
     * 
     * @param message 成功消息
     * @param data 响应数据
     * @param code 业务状态码
     * @param <T> 数据类型
     * @return ApiResponse<T> 成功响应对象
     */
    public static <T> ApiResponse<T> success(String message, T data, Integer code) {
        return new ApiResponse<>(true, message, data, code);
    }

    // ================================
    // 失败响应的静态工厂方法
    // ================================

    /**
     * 创建失败响应（仅错误消息）
     * 
     * 最常用的失败响应方法，只包含错误消息。
     * 适用于大部分业务异常场景。
     * 
     * @param message 错误消息
     * @return ApiResponse<Void> 失败响应对象
     */
    public static ApiResponse<Void> error(String message) {
        return new ApiResponse<>(false, message, null, null);
    }

    /**
     * 创建失败响应（错误消息 + 状态码）
     * 
     * 适用于需要区分不同错误类型的场景。
     * 前端可以根据状态码进行不同的处理。
     * 
     * @param code 业务状态码
     * @param message 错误消息
     * @return ApiResponse<Void> 失败响应对象
     */
    public static ApiResponse<Void> error(Integer code, String message) {
        return new ApiResponse<>(false, message, null, code);
    }

    /**
     * 创建失败响应（完整参数）
     * 
     * 包含所有参数的失败响应，适用于需要返回错误数据的特殊场景。
     * 如：表单验证失败时返回具体的验证错误信息。
     * 
     * @param message 错误消息
     * @param data 错误相关数据
     * @param code 业务状态码
     * @param <T> 数据类型
     * @return ApiResponse<T> 失败响应对象
     */
    public static <T> ApiResponse<T> error(String message, T data, Integer code) {
        return new ApiResponse<>(false, message, data, code);
    }

    // ================================
    // Getter和Setter方法
    // ================================
    // 这些方法用于JSON序列化和反序列化

    /**
     * 获取成功标识
     * @return Boolean 成功标识
     */
    public Boolean getSuccess() {
        return success;
    }

    /**
     * 设置成功标识
     * @param success 成功标识
     */
    public void setSuccess(Boolean success) {
        this.success = success;
    }

    /**
     * 获取响应消息
     * @return String 响应消息
     */
    public String getMessage() {
        return message;
    }

    /**
     * 设置响应消息
     * @param message 响应消息
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * 获取响应数据
     * @return T 响应数据
     */
    public T getData() {
        return data;
    }

    /**
     * 设置响应数据
     * @param data 响应数据
     */
    public void setData(T data) {
        this.data = data;
    }

    /**
     * 获取业务状态码
     * @return Integer 业务状态码
     */
    public Integer getCode() {
        return code;
    }

    /**
     * 设置业务状态码
     * @param code 业务状态码
     */
    public void setCode(Integer code) {
        this.code = code;
    }

    /**
     * 重写toString方法
     * 
     * 便于调试和日志记录，提供对象的字符串表示。
     * 
     * @return String 对象的字符串表示
     */
    @Override
    public String toString() {
        return "ApiResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", data=" + data +
                ", code=" + code +
                '}';
    }
}
