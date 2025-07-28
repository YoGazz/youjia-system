package com.yoga.youjia.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户角色枚举
 * 
 * 定义系统中不同用户的角色类型和权限级别
 */
@Getter
@AllArgsConstructor
public enum UserRole {
    
    /**
     * 系统管理员 - 拥有所有权限
     */
    ADMIN("ADMIN", "系统管理员", "拥有系统所有权限，可以管理用户、项目、系统配置等", 1),
    
    /**
     * 项目经理 - 项目管理权限
     */
    PROJECT_MANAGER("PROJECT_MANAGER", "项目经理", "可以创建和管理项目，分配项目成员，查看项目报告", 2),
    
    /**
     * 测试经理 - 测试管理权限
     */
    TEST_MANAGER("TEST_MANAGER", "测试经理", "可以管理测试计划、测试用例、测试执行和缺陷管理", 3),
    
    /**
     * 测试工程师 - 执行测试
     */
    TESTER("TESTER", "测试工程师", "可以创建测试用例、执行测试、提交缺陷", 4),
    
    /**
     * 开发工程师 - 开发相关权限
     */
    DEVELOPER("DEVELOPER", "开发工程师", "可以查看测试结果、处理分配的缺陷", 5),
    
    /**
     * 普通用户 - 基础查看权限
     */
    USER("USER", "普通用户", "只能查看分配给自己的项目和任务", 6);
    
    /**
     * 角色代码
     */
    private final String code;
    
    /**
     * 角色名称
     */
    private final String name;
    
    /**
     * 角色描述
     */
    private final String description;
    
    /**
     * 权限级别（数字越小权限越高）
     */
    private final Integer level;
    
    /**
     * 根据代码获取角色
     */
    public static UserRole fromCode(String code) {
        for (UserRole role : UserRole.values()) {
            if (role.getCode().equals(code)) {
                return role;
            }
        }
        throw new IllegalArgumentException("未知的用户角色代码: " + code);
    }
    
    /**
     * 检查当前角色是否有权限访问目标角色的资源
     */
    public boolean hasPermission(UserRole targetRole) {
        return this.level <= targetRole.level;
    }
    
    /**
     * 是否为管理员角色
     */
    public boolean isAdmin() {
        return this == ADMIN;
    }
    
    /**
     * 是否为项目管理角色
     */
    public boolean isProjectManager() {
        return this == ADMIN || this == PROJECT_MANAGER;
    }
    
    /**
     * 是否为测试管理角色
     */
    public boolean isTestManager() {
        return this == ADMIN || this == PROJECT_MANAGER || this == TEST_MANAGER;
    }
}