package com.yoga.youjia.common.enums;

/**
 * 项目状态枚举
 * 
 * 定义项目生命周期中的各种状态
 */
public enum ProjectStatus {
    
    PLANNING("PLANNING", "规划中", "项目正在规划阶段，需求和方案制定中", 1),
    ACTIVE("ACTIVE", "进行中", "项目正在积极开发和测试中", 2),
    ON_HOLD("ON_HOLD", "暂停", "项目暂时暂停，等待恢复", 3),
    COMPLETED("COMPLETED", "已完成", "项目已成功完成所有目标", 4),
    CANCELLED("CANCELLED", "已取消", "项目被取消，不再继续", 5),
    ARCHIVED("ARCHIVED", "已归档", "项目已归档，仅供查阅", 6);
    
    private final String code;
    private final String displayName;
    private final String description;
    private final Integer order;
    
    ProjectStatus(String code, String displayName, String description, Integer order) {
        this.code = code;
        this.displayName = displayName;
        this.description = description;
        this.order = order;
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
    
    public Integer getOrder() {
        return order;
    }
    
    /**
     * 检查是否为活跃状态（可以进行操作的状态）
     */
    public boolean isActive() {
        return this == PLANNING || this == ACTIVE;
    }
    
    /**
     * 检查是否为已结束状态
     */
    public boolean isFinished() {
        return this == COMPLETED || this == CANCELLED || this == ARCHIVED;
    }
    
    /**
     * 检查是否可以转换到目标状态
     */
    public boolean canTransitionTo(ProjectStatus targetStatus) {
        if (targetStatus == null || targetStatus == this) {
            return false;
        }
        
        switch (this) {
            case PLANNING:
                return targetStatus == ACTIVE || targetStatus == CANCELLED;
            case ACTIVE:
                return targetStatus == ON_HOLD || targetStatus == COMPLETED || targetStatus == CANCELLED;
            case ON_HOLD:
                return targetStatus == ACTIVE || targetStatus == CANCELLED;
            case COMPLETED:
                return targetStatus == ARCHIVED;
            case CANCELLED:
                return targetStatus == ARCHIVED;
            case ARCHIVED:
                return false; // 归档后不能再转换
            default:
                return false;
        }
    }
    
    /**
     * 根据code获取枚举
     */
    public static ProjectStatus fromCode(String code) {
        if (code == null) {
            return null;
        }
        
        for (ProjectStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        
        throw new IllegalArgumentException("未知的项目状态代码: " + code);
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}