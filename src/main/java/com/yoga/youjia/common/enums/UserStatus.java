package com.yoga.youjia.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户状态枚举
 * 
 * 定义用户账户的不同状态
 */
@Getter
@AllArgsConstructor
public enum UserStatus {
    
    /**
     * 激活状态 - 正常使用
     */
    ACTIVE("ACTIVE", "激活", "用户账户正常，可以登录和使用系统"),
    
    /**
     * 停用状态 - 禁止登录
     */
    INACTIVE("INACTIVE", "停用", "用户账户被停用，不能登录系统"),
    
    /**
     * 锁定状态 - 密码错误次数过多或安全原因锁定
     */
    LOCKED("LOCKED", "锁定", "用户账户被锁定，需要管理员解锁"),
    
    /**
     * 待激活状态 - 新注册用户待验证
     */
    PENDING("PENDING", "待激活", "新注册用户，待邮箱验证或管理员审核"),
    
    /**
     * 已删除状态 - 软删除
     */
    DELETED("DELETED", "已删除", "用户账户已删除，保留数据但不能使用");
    
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
    public static UserStatus fromCode(String code) {
        for (UserStatus status : UserStatus.values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("未知的用户状态代码: " + code);
    }
    
    /**
     * 检查是否可以登录
     */
    public boolean canLogin() {
        return this == ACTIVE;
    }
    
    /**
     * 检查是否可以激活
     */
    public boolean canActivate() {
        return this == PENDING || this == INACTIVE;
    }
    
    /**
     * 检查是否可以停用
     */
    public boolean canDeactivate() {
        return this == ACTIVE;
    }
    
    /**
     * 检查是否可以锁定
     */
    public boolean canLock() {
        return this == ACTIVE || this == INACTIVE;
    }
    
    /**
     * 检查是否可以解锁
     */
    public boolean canUnlock() {
        return this == LOCKED;
    }
}