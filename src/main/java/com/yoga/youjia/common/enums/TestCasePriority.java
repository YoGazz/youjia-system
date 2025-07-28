package com.yoga.youjia.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 测试用例优先级枚举
 * 
 * 定义测试用例的优先级等级
 */
@Getter
@AllArgsConstructor
public enum TestCasePriority {
    
    /**
     * 非常高优先级 - 阻塞性用例
     */
    BLOCKER("BLOCKER", "阻塞", "阻塞性用例，必须优先执行", 1),
    
    /**
     * 高优先级 - 核心功能
     */
    HIGH("HIGH", "高", "核心功能用例，优先级很高", 2),
    
    /**
     * 中优先级 - 重要功能
     */
    MEDIUM("MEDIUM", "中", "重要功能用例，正常优先级", 3),
    
    /**
     * 低优先级 - 次要功能
     */
    LOW("LOW", "低", "次要功能用例，可延后执行", 4),
    
    /**
     * 非常低优先级 - 边缘功能
     */
    TRIVIAL("TRIVIAL", "微小", "边缘功能用例，最低优先级", 5);
    
    /**
     * 优先级代码
     */
    private final String code;
    
    /**
     * 优先级名称
     */
    private final String name;
    
    /**
     * 优先级描述
     */
    private final String description;
    
    /**
     * 优先级数值（数值越小优先级越高）
     */
    private final Integer level;
    
    /**
     * 根据代码获取优先级
     */
    public static TestCasePriority fromCode(String code) {
        for (TestCasePriority priority : TestCasePriority.values()) {
            if (priority.getCode().equals(code)) {
                return priority;
            }
        }
        throw new IllegalArgumentException("未知的测试用例优先级代码: " + code);
    }
    
    /**
     * 检查当前优先级是否高于目标优先级
     */
    public boolean isHigherThan(TestCasePriority target) {
        return this.level < target.level;
    }
}