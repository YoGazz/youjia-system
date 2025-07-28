package com.yoga.youjia.common.exception;

import com.yoga.youjia.common.enums.ErrorCode;

/**
 * 数据冲突异常
 * 
 * 当数据存在冲突时抛出此异常（如用户名重复）
 * 支持新的ErrorCode系统
 */
public class DataConflictException extends BusinessException {

    private String conflictField;
    private Object conflictValue;

    // ========== 基础构造函数 ==========
    
    /**
     * 使用默认消息构造异常
     */
    public DataConflictException(String message) {
        super(ErrorCode.DATA_EXISTS, message);
    }

    /**
     * 使用字段名和值构造异常
     */
    public DataConflictException(String fieldName, String value) {
        super(ErrorCode.DATA_EXISTS, String.format("%s [%s] 已存在", fieldName, value));
        this.conflictField = fieldName;
        this.conflictValue = value;
    }
    
    /**
     * 使用ErrorCode枚举和冲突信息构造异常
     */
    public DataConflictException(ErrorCode errorCode, String fieldName, Object value) {
        super(errorCode, String.format("%s [%s] 已存在", fieldName, value));
        this.conflictField = fieldName;
        this.conflictValue = value;
    }
    
    /**
     * 使用ErrorCode枚举和自定义消息构造异常
     */
    public DataConflictException(ErrorCode errorCode, String customMessage) {
        super(errorCode, customMessage);
    }

    /**
     * 使用默认消息和原因构造异常
     */
    public DataConflictException(String message, Throwable cause) {
        super(message, cause);
    }
    
    // ========== 静态工厂方法 ==========
    
    /**
     * 创建用户名已存在异常
     */
    public static DataConflictException usernameExists(String username) {
        return new DataConflictException(ErrorCode.USERNAME_ALREADY_EXISTS, "用户名", username);
    }
    
    /**
     * 创建邮箱已存在异常
     */
    public static DataConflictException emailExists(String email) {
        return new DataConflictException(ErrorCode.EMAIL_ALREADY_EXISTS, "邮箱", email);
    }
    
    /**
     * 创建项目编码已存在异常
     */
    public static DataConflictException projectCodeExists(String projectCode) {
        return new DataConflictException(ErrorCode.PROJECT_CODE_ALREADY_EXISTS, "项目编码", projectCode);
    }
    
    /**
     * 创建项目名称已存在异常
     */
    public static DataConflictException projectNameExists(String projectName) {
        return new DataConflictException(ErrorCode.PROJECT_ALREADY_EXISTS, "项目名称", projectName);
    }
    
    /**
     * 创建项目成员已存在异常
     */
    public static DataConflictException projectMemberExists(Long projectId, Long userId) {
        return new DataConflictException(ErrorCode.PROJECT_MEMBER_ALREADY_EXISTS, 
                String.format("用户已是项目成员: 项目ID=%d, 用户ID=%d", projectId, userId));
    }
    
    /**
     * 创建手机号已存在异常
     */
    public static DataConflictException phoneExists(String phone) {
        return new DataConflictException(ErrorCode.DATA_EXISTS, "手机号", phone);
    }
    
    /**
     * 创建通用数据冲突异常
     */
    public static DataConflictException generic(String fieldName, Object value) {
        return new DataConflictException(ErrorCode.DATA_EXISTS, fieldName, value);
    }
    
    /**
     * 创建业务规则冲突异常
     */
    public static DataConflictException businessRule(String message) {
        return new DataConflictException(ErrorCode.DATA_EXISTS, message);
    }

    // ========== Getter方法 ==========
    
    public String getConflictField() {
        return conflictField;
    }
    
    public Object getConflictValue() {
        return conflictValue;
    }
    
    @Override
    public String toString() {
        return String.format("DataConflictException{errorCode='%s', errorMessage='%s', conflictField='%s', conflictValue=%s}", 
                           getErrorCode(), getErrorMessage(), conflictField, conflictValue);
    }
}