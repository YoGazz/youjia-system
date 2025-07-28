package com.yoga.youjia.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 系统错误码枚举
 * 
 * 统一定义系统中所有的错误码和对应的HTTP状态码
 * 错误码命名规则：XXXX（四位数字）
 * - 1000-1999: 用户相关错误
 * - 2000-2999: 项目相关错误  
 * - 3000-3999: 测试用例相关错误
 * - 4000-4999: 测试执行相关错误
 * - 5000-5999: 缺陷相关错误
 * - 9000-9999: 系统通用错误
 */
@Getter
@AllArgsConstructor
public enum ErrorCode {
    
    // ========== 系统通用错误 9000-9999 ==========
    SUCCESS("0000", "成功", HttpStatus.OK),
    SYSTEM_ERROR("9000", "系统内部错误", HttpStatus.INTERNAL_SERVER_ERROR),
    PARAM_ERROR("9001", "参数错误", HttpStatus.BAD_REQUEST),
    PARAM_MISSING("9002", "缺少必要参数", HttpStatus.BAD_REQUEST),
    PARAM_INVALID("9003", "参数格式不正确", HttpStatus.BAD_REQUEST),
    REQUEST_METHOD_NOT_SUPPORTED("9004", "不支持的请求方法", HttpStatus.METHOD_NOT_ALLOWED),
    MEDIA_TYPE_NOT_SUPPORTED("9005", "不支持的媒体类型", HttpStatus.UNSUPPORTED_MEDIA_TYPE),
    DATA_NOT_FOUND("9006", "数据不存在", HttpStatus.NOT_FOUND),
    DATA_EXISTS("9007", "数据已存在", HttpStatus.CONFLICT),
    ACCESS_DENIED("9008", "访问被拒绝", HttpStatus.FORBIDDEN),
    UNAUTHORIZED("9009", "未授权访问", HttpStatus.UNAUTHORIZED),
    RATE_LIMIT_EXCEEDED("9010", "请求过于频繁", HttpStatus.TOO_MANY_REQUESTS),
    
    // ========== 用户相关错误 1000-1999 ==========
    USER_NOT_FOUND("1000", "用户不存在", HttpStatus.NOT_FOUND),
    USER_ALREADY_EXISTS("1001", "用户已存在", HttpStatus.CONFLICT),
    USERNAME_ALREADY_EXISTS("1002", "用户名已存在", HttpStatus.CONFLICT),
    EMAIL_ALREADY_EXISTS("1003", "邮箱已存在", HttpStatus.CONFLICT),
    PASSWORD_INVALID("1004", "密码错误", HttpStatus.UNAUTHORIZED),
    PASSWORD_NOT_MATCH("1005", "密码和确认密码不一致", HttpStatus.BAD_REQUEST),
    PASSWORD_TOO_WEAK("1006", "密码强度不够", HttpStatus.BAD_REQUEST),
    USER_DISABLED("1007", "用户已被停用", HttpStatus.FORBIDDEN),
    USER_LOCKED("1008", "用户已被锁定", HttpStatus.FORBIDDEN),
    USER_NOT_ACTIVATED("1009", "用户未激活", HttpStatus.FORBIDDEN),
    INVALID_TOKEN("1010", "无效的令牌", HttpStatus.UNAUTHORIZED),
    TOKEN_EXPIRED("1011", "令牌已过期", HttpStatus.UNAUTHORIZED),
    INSUFFICIENT_PERMISSIONS("1012", "权限不足", HttpStatus.FORBIDDEN),
    
    // ========== 项目相关错误 2000-2999 ==========
    PROJECT_NOT_FOUND("2000", "项目不存在", HttpStatus.NOT_FOUND),
    PROJECT_ALREADY_EXISTS("2001", "项目已存在", HttpStatus.CONFLICT),
    PROJECT_CODE_ALREADY_EXISTS("2002", "项目编码已存在", HttpStatus.CONFLICT),
    PROJECT_ACCESS_DENIED("2003", "无权访问该项目", HttpStatus.FORBIDDEN),
    PROJECT_MEMBER_NOT_FOUND("2004", "项目成员不存在", HttpStatus.NOT_FOUND),
    PROJECT_MEMBER_ALREADY_EXISTS("2005", "项目成员已存在", HttpStatus.CONFLICT),
    PROJECT_CANNOT_DELETE("2006", "项目无法删除", HttpStatus.BAD_REQUEST),
    PROJECT_MANAGER_REQUIRED("2007", "项目必须至少有一个项目经理", HttpStatus.BAD_REQUEST),
    PROJECT_PERMISSION_DENIED("2008", "没有执行此操作的权限", HttpStatus.FORBIDDEN),
    
    // ========== 需求相关错误 2500-2599 ==========
    REQUIREMENT_NOT_FOUND("2500", "需求不存在", HttpStatus.NOT_FOUND),
    REQUIREMENT_STATUS_INVALID("2501", "需求状态不正确", HttpStatus.BAD_REQUEST),
    REQUIREMENT_CANNOT_DELETE("2502", "需求无法删除", HttpStatus.BAD_REQUEST),
    
    // ========== 测试用例相关错误 3000-3099 ==========
    TEST_CASE_NOT_FOUND("3000", "测试用例不存在", HttpStatus.NOT_FOUND),
    TEST_MODULE_NOT_FOUND("3001", "测试模块不存在", HttpStatus.NOT_FOUND),
    TEST_CASE_CANNOT_DELETE("3002", "测试用例无法删除", HttpStatus.BAD_REQUEST),
    
    // ========== 测试执行相关错误 4000-4999 ==========
    TEST_PLAN_NOT_FOUND("4000", "测试计划不存在", HttpStatus.NOT_FOUND),
    TEST_EXECUTION_NOT_FOUND("4001", "测试执行不存在", HttpStatus.NOT_FOUND),
    TEST_RESULT_INVALID("4002", "测试结果不正确", HttpStatus.BAD_REQUEST),
    
    // ========== 缺陷相关错误 5000-5999 ==========
    BUG_NOT_FOUND("5000", "缺陷不存在", HttpStatus.NOT_FOUND),
    BUG_STATUS_INVALID("5001", "缺陷状态不正确", HttpStatus.BAD_REQUEST),
    BUG_CANNOT_DELETE("5002", "缺陷无法删除", HttpStatus.BAD_REQUEST);
    
    /**
     * 错误码
     */
    private final String code;
    
    /**
     * 错误消息
     */
    private final String message;
    
    /**
     * HTTP状态码
     */
    private final HttpStatus httpStatus;
    
    /**
     * 获取错误码
     */
    public String getCode() {
        return code;
    }
    
    /**
     * 获取错误消息
     */
    public String getMessage() {
        return message;
    }
    
    /**
     * 获取HTTP状态码
     */
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
    
    /**
     * 根据错误码获取枚举
     */
    public static ErrorCode fromCode(String code) {
        for (ErrorCode errorCode : ErrorCode.values()) {
            if (errorCode.getCode().equals(code)) {
                return errorCode;
            }
        }
        return SYSTEM_ERROR;
    }
    
    /**
     * 是否为成功状态
     */
    public boolean isSuccess() {
        return this == SUCCESS;
    }
    
    /**
     * 获取HTTP状态码值
     */
    public int getHttpStatusValue() {
        return httpStatus.value();
    }
}