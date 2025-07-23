package com.yoga.youjia.common.exception;

import com.yoga.youjia.common.ResultCode;

/**
 * 业务异常类
 * 
 * 用于处理业务逻辑相关的异常情况
 */
public class BusinessException extends RuntimeException {
    
    private String errorCode;
    private String errorMessage;
    private ResultCode resultCode;

    public BusinessException(String errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public BusinessException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.resultCode = resultCode;
        this.errorCode = String.valueOf(resultCode.getCode());
        this.errorMessage = resultCode.getMessage();
    }

    public BusinessException(ResultCode resultCode, String customMessage) {
        super(customMessage);
        this.resultCode = resultCode;
        this.errorCode = String.valueOf(resultCode.getCode());
        this.errorMessage = customMessage;
    }

    public BusinessException(String message) {
        super(message);
        this.errorCode = "400";
        this.errorMessage = message;
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "500";
        this.errorMessage = message;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public ResultCode getResultCode() {
        return resultCode;
    }
}