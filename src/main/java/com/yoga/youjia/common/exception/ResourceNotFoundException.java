package com.yoga.youjia.common.exception;

/**
 * 资源未找到异常
 * 
 * 当请求的资源不存在时抛出此异常
 */
public class ResourceNotFoundException extends BusinessException {

    public ResourceNotFoundException(String resourceName, String resourceId) {
        super("404", String.format("%s [ID: %s] 不存在", resourceName, resourceId));
    }

    public ResourceNotFoundException(String resourceName) {
        super("404", String.format("%s 不存在", resourceName));
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}