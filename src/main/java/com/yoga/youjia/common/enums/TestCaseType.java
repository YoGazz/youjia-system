package com.yoga.youjia.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 测试用例类型枚举
 * 
 * 定义测试用例的不同类型分类
 */
@Getter
@AllArgsConstructor
public enum TestCaseType {
    
    /**
     * 功能测试
     */
    FUNCTIONAL("FUNCTIONAL", "功能测试", "验证系统功能是否按照需求正常工作"),
    
    /**
     * 接口测试
     */
    API("API", "接口测试", "验证系统接口的功能、性能、稳定性"),
    
    /**
     * 性能测试
     */
    PERFORMANCE("PERFORMANCE", "性能测试", "验证系统在各种负载条件下的性能表现"),
    
    /**
     * 安全测试
     */
    SECURITY("SECURITY", "安全测试", "验证系统的安全性和防护能力"),
    
    /**
     * 兼容性测试
     */
    COMPATIBILITY("COMPATIBILITY", "兼容性测试", "验证系统在不同环境下的兼容性"),
    
    /**
     * 易用性测试
     */
    USABILITY("USABILITY", "易用性测试", "验证系统的用户体验和易用性"),
    
    /**
     * 回归测试
     */
    REGRESSION("REGRESSION", "回归测试", "验证修复或新增功能后系统的稳定性"),
    
    /**
     * 冒烟测试
     */
    SMOKE("SMOKE", "冒烟测试", "验证系统基本功能是否正常");
    
    /**
     * 类型代码
     */
    private final String code;
    
    /**
     * 类型名称
     */
    private final String name;
    
    /**
     * 类型描述
     */
    private final String description;
    
    /**
     * 根据代码获取类型
     */
    public static TestCaseType fromCode(String code) {
        for (TestCaseType type : TestCaseType.values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("未知的测试用例类型代码: " + code);
    }
}