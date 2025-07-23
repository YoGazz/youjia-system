package com.yoga.youjia.common.exception;

/**
 * 数据冲突异常
 * 
 * 当数据存在冲突时抛出此异常（如用户名重复）
 */
public class DataConflictException extends BusinessException {

    public DataConflictException(String message) {
        super("409", message);
    }

    public DataConflictException(String fieldName, String value) {
        super("409", String.format("%s [%s] 已存在", fieldName, value));
    }

    public DataConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}