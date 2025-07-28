package com.yoga.youjia.common.exception;

import com.yoga.youjia.common.ResultCode;
import com.yoga.youjia.common.enums.ErrorCode;

/**
 * 业务异常类
 * 
 * 用于处理业务逻辑相关的异常情况
 * 支持新的ErrorCode系统和原有的ResultCode系统
 */
public class BusinessException extends RuntimeException {
    
    private String errorCode;
    private String errorMessage;
    private ResultCode resultCode;
    private ErrorCode newErrorCode;
    private Object[] args; // 用于支持参数化错误消息

    // ========== 基础构造函数 ==========
    
    /**
     * 使用错误码和错误消息构造异常
     */
    public BusinessException(String errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
    
    /**
     * 使用新的ErrorCode枚举构造异常
     */
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.newErrorCode = errorCode;
        this.errorCode = errorCode.getCode();
        this.errorMessage = errorCode.getMessage();
    }
    
    /**
     * 使用新的ErrorCode枚举和自定义消息构造异常
     */
    public BusinessException(ErrorCode errorCode, String customMessage) {
        super(customMessage);
        this.newErrorCode = errorCode;
        this.errorCode = errorCode.getCode();
        this.errorMessage = customMessage;
    }
    
    /**
     * 使用新的ErrorCode枚举和参数化消息构造异常
     */
    public BusinessException(ErrorCode errorCode, String messageTemplate, Object... args) {
        super(String.format(messageTemplate, args));
        this.newErrorCode = errorCode;
        this.errorCode = errorCode.getCode();
        this.errorMessage = String.format(messageTemplate, args);
        this.args = args;
    }
    
    // ========== 兼容旧系统ResultCode ==========
    
    /**
     * 使用原有的ResultCode构造异常（兼容性）
     */
    public BusinessException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.resultCode = resultCode;
        this.errorCode = String.valueOf(resultCode.getCode());
        this.errorMessage = resultCode.getMessage();
    }

    /**
     * 使用原有的ResultCode和自定义消息构造异常（兼容性）
     */
    public BusinessException(ResultCode resultCode, String customMessage) {
        super(customMessage);
        this.resultCode = resultCode;
        this.errorCode = String.valueOf(resultCode.getCode());
        this.errorMessage = customMessage;
    }
    
    // ========== 简化构造函数 ==========

    /**
     * 使用默认错误码和消息构造异常
     */
    public BusinessException(String message) {
        super(message);
        this.errorCode = ErrorCode.PARAM_ERROR.getCode();
        this.errorMessage = message;
    }

    /**
     * 使用默认错误码、消息和原因构造异常
     */
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = ErrorCode.SYSTEM_ERROR.getCode();
        this.errorMessage = message;
    }
    
    // ========== 静态工厂方法 ==========
    
    /**
     * 创建用户不存在异常
     */
    public static BusinessException userNotFound() {
        return new BusinessException(ErrorCode.USER_NOT_FOUND);
    }
    
    /**
     * 创建用户不存在异常（带用户ID）
     */
    public static BusinessException userNotFound(Long userId) {
        return new BusinessException(ErrorCode.USER_NOT_FOUND, "用户不存在: ID=%d", userId);
    }
    
    /**
     * 创建用户名已存在异常
     */
    public static BusinessException usernameExists(String username) {
        return new BusinessException(ErrorCode.USERNAME_ALREADY_EXISTS, "用户名已存在: %s", username);
    }
    
    /**
     * 创建邮箱已存在异常
     */
    public static BusinessException emailExists(String email) {
        return new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS, "邮箱已存在: %s", email);
    }
    
    /**
     * 创建密码错误异常
     */
    public static BusinessException invalidPassword() {
        return new BusinessException(ErrorCode.PASSWORD_INVALID);
    }
    
    /**
     * 创建用户被禁用异常
     */
    public static BusinessException userDisabled() {
        return new BusinessException(ErrorCode.USER_DISABLED);
    }
    
    /**
     * 创建用户被锁定异常
     */
    public static BusinessException userLocked() {
        return new BusinessException(ErrorCode.USER_LOCKED);
    }
    
    /**
     * 创建权限不足异常
     */
    public static BusinessException insufficientPermissions() {
        return new BusinessException(ErrorCode.INSUFFICIENT_PERMISSIONS);
    }
    
    /**
     * 创建项目不存在异常
     */
    public static BusinessException projectNotFound(Long projectId) {
        return new BusinessException(ErrorCode.PROJECT_NOT_FOUND, "项目不存在: ID=%d", projectId);
    }
    
    /**
     * 创建项目编码已存在异常
     */
    public static BusinessException projectCodeExists(String projectCode) {
        return new BusinessException(ErrorCode.PROJECT_CODE_ALREADY_EXISTS, "项目编码已存在: %s", projectCode);
    }
    
    /**
     * 创建参数验证失败异常
     */
    public static BusinessException paramInvalid(String paramName, String reason) {
        return new BusinessException(ErrorCode.PARAM_INVALID, "参数 '%s' 验证失败: %s", paramName, reason);
    }
    
    // ========== Getter方法 ==========

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public ResultCode getResultCode() {
        return resultCode;
    }
    
    public ErrorCode getNewErrorCode() {
        return newErrorCode;
    }
    
    public Object[] getArgs() {
        return args;
    }
    
    // ========== 工具方法 ==========
    
    /**
     * 检查是否使用了新的ErrorCode系统
     */
    public boolean isNewErrorCodeSystem() {
        return newErrorCode != null;
    }
    
    /**
     * 获取HTTP状态码
     */
    public int getHttpStatusCode() {
        if (newErrorCode != null) {
            return newErrorCode.getHttpStatusValue();
        }
        // 默认返回400
        return 400;
    }
    
    @Override
    public String toString() {
        return String.format("BusinessException{errorCode='%s', errorMessage='%s', newErrorCode=%s}", 
                           errorCode, errorMessage, newErrorCode);
    }
}