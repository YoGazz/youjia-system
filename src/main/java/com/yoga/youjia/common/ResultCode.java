package com.yoga.youjia.common;

import lombok.Getter;

/**
 * 结果代码枚举类
 */
@Getter
public enum ResultCode {

    // 操作成功
    SUCCESS(200, "操作成功"),

    // 客户端错误
    BAD_REQUEST(400, "请求错误"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "资源未找到"),
    CONFLICT(409, "资源冲突"),
    LOGIN_FAILED(401, "登录失败"),

    // 服务器错误
    INTERNAL_SERVER_ERROR(500, "服务器内部错误"),
    SYSTEM_ERROR(500, "系统错误");

    private final int code;
    private final String message;

    // 构造函数
    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
