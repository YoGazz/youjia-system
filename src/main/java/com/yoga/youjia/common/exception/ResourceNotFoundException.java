package com.yoga.youjia.common.exception;

import com.yoga.youjia.common.enums.ErrorCode;

/**
 * 资源未找到异常
 * 
 * 当请求的资源不存在时抛出此异常
 * 支持新的ErrorCode系统
 */
public class ResourceNotFoundException extends BusinessException {

    private String resourceType;
    private Object resourceId;

    // ========== 基础构造函数 ==========
    
    /**
     * 使用资源名称和资源ID构造异常
     */
    public ResourceNotFoundException(String resourceName, String resourceId) {
        super(ErrorCode.DATA_NOT_FOUND, String.format("%s [ID: %s] 不存在", resourceName, resourceId));
        this.resourceType = resourceName;
        this.resourceId = resourceId;
    }

    /**
     * 使用资源名称构造异常
     */
    public ResourceNotFoundException(String resourceName) {
        super(ErrorCode.DATA_NOT_FOUND, String.format("%s 不存在", resourceName));
        this.resourceType = resourceName;
    }
    
    /**
     * 使用ErrorCode枚举和资源信息构造异常
     */
    public ResourceNotFoundException(ErrorCode errorCode, String resourceType, Object resourceId) {
        super(errorCode, String.format("%s [ID: %s] 不存在", resourceType, resourceId));
        this.resourceType = resourceType;
        this.resourceId = resourceId;
    }
    
    /**
     * 使用ErrorCode枚举和自定义消息构造异常
     */
    public ResourceNotFoundException(ErrorCode errorCode, String customMessage) {
        super(errorCode, customMessage);
    }

    /**
     * 使用默认消息和原因构造异常
     */
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    
    // ========== 静态工厂方法 ==========
    
    /**
     * 创建用户不存在异常
     */
    public static ResourceNotFoundException user(Long userId) {
        return new ResourceNotFoundException(ErrorCode.USER_NOT_FOUND, "用户", userId);
    }
    
    /**
     * 创建用户不存在异常（按用户名）
     */
    public static ResourceNotFoundException userByUsername(String username) {
        return new ResourceNotFoundException(ErrorCode.USER_NOT_FOUND, "用户", username);
    }
    
    /**
     * 创建项目不存在异常
     */
    public static ResourceNotFoundException project(Long projectId) {
        return new ResourceNotFoundException(ErrorCode.PROJECT_NOT_FOUND, "项目", projectId);
    }
    
    /**
     * 创建项目不存在异常（按项目编码）
     */
    public static ResourceNotFoundException projectByCode(String projectCode) {
        return new ResourceNotFoundException(ErrorCode.PROJECT_NOT_FOUND, "项目", projectCode);
    }
    
    /**
     * 创建测试用例不存在异常
     */
    public static ResourceNotFoundException testCase(Long testCaseId) {
        return new ResourceNotFoundException(ErrorCode.TEST_CASE_NOT_FOUND, "测试用例", testCaseId);
    }
    
    /**
     * 创建测试模块不存在异常
     */
    public static ResourceNotFoundException testModule(Long moduleId) {
        return new ResourceNotFoundException(ErrorCode.TEST_MODULE_NOT_FOUND, "测试模块", moduleId);
    }
    
    /**
     * 创建需求不存在异常
     */
    public static ResourceNotFoundException requirement(Long requirementId) {
        return new ResourceNotFoundException(ErrorCode.REQUIREMENT_NOT_FOUND, "需求", requirementId);
    }
    
    /**
     * 创建缺陷不存在异常
     */
    public static ResourceNotFoundException bug(Long bugId) {
        return new ResourceNotFoundException(ErrorCode.BUG_NOT_FOUND, "缺陷", bugId);
    }
    
    /**
     * 创建测试计划不存在异常
     */
    public static ResourceNotFoundException testPlan(Long testPlanId) {
        return new ResourceNotFoundException(ErrorCode.TEST_PLAN_NOT_FOUND, "测试计划", testPlanId);
    }
    
    /**
     * 创建测试执行不存在异常
     */
    public static ResourceNotFoundException testExecution(Long executionId) {
        return new ResourceNotFoundException(ErrorCode.TEST_EXECUTION_NOT_FOUND, "测试执行", executionId);
    }
    
    /**
     * 创建通用资源不存在异常
     */
    public static ResourceNotFoundException generic(String resourceType, Object resourceId) {
        return new ResourceNotFoundException(ErrorCode.DATA_NOT_FOUND, resourceType, resourceId);
    }

    // ========== Getter方法 ==========
    
    public String getResourceType() {
        return resourceType;
    }
    
    public Object getResourceId() {
        return resourceId;
    }
    
    @Override
    public String toString() {
        return String.format("ResourceNotFoundException{errorCode='%s', errorMessage='%s', resourceType='%s', resourceId=%s}", 
                           getErrorCode(), getErrorMessage(), resourceType, resourceId);
    }
}