package com.yoga.youjia.common.enums;

/**
 * 项目成员角色枚举
 * 
 * 定义项目中成员的角色和权限
 */
public enum ProjectMemberRole {
    
    PROJECT_MANAGER("PROJECT_MANAGER", "项目经理", "负责项目整体规划和管理，拥有所有权限", 1),
    TEST_MANAGER("TEST_MANAGER", "测试经理", "负责测试策略制定和测试团队管理", 2),
    LEAD_TESTER("LEAD_TESTER", "测试主管", "负责测试用例设计和测试执行管理", 3),
    TESTER("TESTER", "测试工程师", "执行测试用例，记录测试结果", 4),
    DEVELOPER("DEVELOPER", "开发工程师", "参与项目开发，协助测试问题解决", 5),
    BUSINESS_ANALYST("BUSINESS_ANALYST", "业务分析师", "负责需求分析和业务梳理", 6),
    VIEWER("VIEWER", "查看者", "只能查看项目信息，无编辑权限", 7);
    
    private final String code;
    private final String displayName;
    private final String description;
    private final Integer level;
    
    ProjectMemberRole(String code, String displayName, String description, Integer level) {
        this.code = code;
        this.displayName = displayName;
        this.description = description;
        this.level = level;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public Integer getLevel() {
        return level;
    }
    
    /**
     * 检查是否为管理角色
     */
    public boolean isManager() {
        return this == PROJECT_MANAGER || this == TEST_MANAGER;
    }
    
    /**
     * 检查是否有编辑权限
     */
    public boolean canEdit() {
        return this != VIEWER;
    }
    
    /**
     * 检查是否有删除权限
     */
    public boolean canDelete() {
        return this == PROJECT_MANAGER || this == TEST_MANAGER;
    }
    
    /**
     * 检查是否有管理成员权限
     */
    public boolean canManageMembers() {
        return this == PROJECT_MANAGER;
    }
    
    /**
     * 检查是否有创建测试用例权限
     */
    public boolean canCreateTestCases() {
        return this == PROJECT_MANAGER || this == TEST_MANAGER || 
               this == LEAD_TESTER || this == TESTER;
    }
    
    /**
     * 检查是否有执行测试权限
     */
    public boolean canExecuteTests() {
        return this == PROJECT_MANAGER || this == TEST_MANAGER || 
               this == LEAD_TESTER || this == TESTER;
    }
    
    /**
     * 检查是否有缺陷管理权限
     */
    public boolean canManageBugs() {
        return this == PROJECT_MANAGER || this == TEST_MANAGER || 
               this == LEAD_TESTER || this == TESTER || this == DEVELOPER;
    }
    
    /**
     * 检查角色级别是否高于另一个角色
     */
    public boolean isHigherThan(ProjectMemberRole other) {
        return this.level < other.level; // 级别数字越小，权限越高
    }
    
    /**
     * 根据code获取枚举
     */
    public static ProjectMemberRole fromCode(String code) {
        if (code == null) {
            return null;
        }
        
        for (ProjectMemberRole role : values()) {
            if (role.code.equals(code)) {
                return role;
            }
        }
        
        throw new IllegalArgumentException("未知的项目成员角色代码: " + code);
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}