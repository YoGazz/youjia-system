package com.yoga.youjia.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 测试用例状态枚举
 * 
 * 定义测试用例在生命周期中的不同状态
 */
@Getter
@AllArgsConstructor
public enum TestCaseStatus {
    
    /**
     * 草稿状态 - 用例正在编写中
     */
    DRAFT("DRAFT", "草稿", "用例正在编写中，未完成"),
    
    /**
     * 待审核状态 - 用例编写完成，等待审核
     */
    PENDING_REVIEW("PENDING_REVIEW", "待审核", "用例编写完成，等待审核"),
    
    /**
     * 审核中状态 - 用例正在被审核
     */
    UNDER_REVIEW("UNDER_REVIEW", "审核中", "用例正在被审核人员审核"),
    
    /**
     * 审核通过状态 - 用例审核通过，可以执行
     */
    APPROVED("APPROVED", "已通过", "用例审核通过，可以执行"),
    
    /**
     * 审核拒绝状态 - 用例审核不通过，需要修改
     */
    REJECTED("REJECTED", "已拒绝", "用例审核不通过，需要修改"),
    
    /**
     * 激活状态 - 用例处于活跃状态，可以被执行
     */
    ACTIVE("ACTIVE", "激活", "用例处于活跃状态，可以被执行"),
    
    /**
     * 废弃状态 - 用例已废弃，不再使用
     */
    DEPRECATED("DEPRECATED", "已废弃", "用例已废弃，不再使用"),
    
    /**
     * 归档状态 - 用例已归档保存
     */
    ARCHIVED("ARCHIVED", "已归档", "用例已归档保存");
    
    /**
     * 状态代码
     */
    private final String code;
    
    /**
     * 状态名称
     */
    private final String name;
    
    /**
     * 状态描述
     */
    private final String description;
    
    /**
     * 根据代码获取状态
     */
    public static TestCaseStatus fromCode(String code) {
        for (TestCaseStatus status : TestCaseStatus.values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("未知的测试用例状态代码: " + code);
    }
    
    /**
     * 检查是否可以执行测试
     */
    public boolean canExecute() {
        return this == APPROVED || this == ACTIVE;
    }
    
    /**
     * 检查是否可以编辑
     */
    public boolean canEdit() {
        return this == DRAFT || this == REJECTED;
    }
    
    /**
     * 检查是否可以审核
     */
    public boolean canReview() {
        return this == PENDING_REVIEW;
    }
}