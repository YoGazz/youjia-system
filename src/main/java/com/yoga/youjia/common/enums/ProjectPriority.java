package com.yoga.youjia.common.enums;

/**
 * 项目优先级枚举
 * 
 * 定义项目的重要性和紧急程度
 */
public enum ProjectPriority {
    
    LOW("LOW", "低", "优先级较低，可以延后处理", 1, "#52c41a"),
    MEDIUM("MEDIUM", "中", "标准优先级，按计划执行", 2, "#faad14"),
    HIGH("HIGH", "高", "高优先级，需要重点关注", 3, "#fa541c"),
    URGENT("URGENT", "紧急", "紧急优先级，需要立即处理", 4, "#f5222d");
    
    private final String code;
    private final String displayName;
    private final String description;
    private final Integer level;
    private final String color; // 用于前端显示的颜色
    
    ProjectPriority(String code, String displayName, String description, Integer level, String color) {
        this.code = code;
        this.displayName = displayName;
        this.description = description;
        this.level = level;
        this.color = color;
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
    
    public String getColor() {
        return color;
    }
    
    /**
     * 检查是否为高优先级（高和紧急）
     */
    public boolean isHighPriority() {
        return this == HIGH || this == URGENT;
    }
    
    /**
     * 比较优先级高低
     */
    public boolean isHigherThan(ProjectPriority other) {
        return this.level > other.level;
    }
    
    /**
     * 根据code获取枚举
     */
    public static ProjectPriority fromCode(String code) {
        if (code == null) {
            return null;
        }
        
        for (ProjectPriority priority : values()) {
            if (priority.code.equals(code)) {
                return priority;
            }
        }
        
        throw new IllegalArgumentException("未知的项目优先级代码: " + code);
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}