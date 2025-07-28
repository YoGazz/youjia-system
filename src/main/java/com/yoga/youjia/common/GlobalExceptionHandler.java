package com.yoga.youjia.common;

import com.yoga.youjia.common.enums.ErrorCode;
import com.yoga.youjia.common.exception.BusinessException;
import com.yoga.youjia.common.exception.DataConflictException;
import com.yoga.youjia.common.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.HttpMediaTypeNotSupportedException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 * 
 * 统一处理系统异常，返回规范化的响应格式
 * 支持新的ErrorCode系统和更丰富的异常处理
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // ========== 业务异常处理 ==========
    
    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Object>> handleBusinessException(
            BusinessException e, HttpServletRequest request) {
        String traceId = generateTraceId();
        logger.warn("业务异常 [{}]: {} - 请求路径: {}", traceId, e.getMessage(), request.getRequestURI());
        
        ApiResponse<Object> response = ApiResponse.error(e.getErrorCode(), e.getErrorMessage())
                .withTraceId(traceId);
        
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * 处理资源未找到异常
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceNotFoundException(
            ResourceNotFoundException e, HttpServletRequest request) {
        String traceId = generateTraceId();
        logger.warn("资源未找到 [{}]: {} - 请求路径: {}", traceId, e.getMessage(), request.getRequestURI());
        
        ErrorCode errorCode = ErrorCode.fromCode(e.getErrorCode());
        ApiResponse<Object> response = ApiResponse.error(errorCode, e.getErrorMessage())
                .withTraceId(traceId);
        
        return ResponseEntity.status(errorCode.getHttpStatus()).body(response);
    }

    /**
     * 处理数据冲突异常
     */
    @ExceptionHandler(DataConflictException.class)
    public ResponseEntity<ApiResponse<Object>> handleDataConflictException(
            DataConflictException e, HttpServletRequest request) {
        String traceId = generateTraceId();
        logger.warn("数据冲突 [{}]: {} - 请求路径: {}", traceId, e.getMessage(), request.getRequestURI());
        
        ErrorCode errorCode = ErrorCode.fromCode(e.getErrorCode());
        ApiResponse<Object> response = ApiResponse.error(errorCode, e.getErrorMessage())
                .withTraceId(traceId);
        
        return ResponseEntity.status(errorCode.getHttpStatus()).body(response);
    }

    // ========== 安全相关异常处理 ==========
    
    /**
     * 处理认证异常
     */
    @ExceptionHandler({AuthenticationException.class, BadCredentialsException.class})
    public ResponseEntity<ApiResponse<Object>> handleAuthenticationException(
            Exception e, HttpServletRequest request) {
        String traceId = generateTraceId();
        logger.warn("认证失败 [{}]: {} - 请求路径: {}", traceId, e.getMessage(), request.getRequestURI());
        
        ApiResponse<Object> response = ApiResponse.error(ErrorCode.UNAUTHORIZED)
                .withTraceId(traceId);
        
        return ResponseEntity.status(ErrorCode.UNAUTHORIZED.getHttpStatus()).body(response);
    }

    /**
     * 处理授权异常
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Object>> handleAccessDeniedException(
            AccessDeniedException e, HttpServletRequest request) {
        String traceId = generateTraceId();
        logger.warn("访问被拒绝 [{}]: {} - 请求路径: {}", traceId, e.getMessage(), request.getRequestURI());
        
        ApiResponse<Object> response = ApiResponse.error(ErrorCode.ACCESS_DENIED)
                .withTraceId(traceId);
        
        return ResponseEntity.status(ErrorCode.ACCESS_DENIED.getHttpStatus()).body(response);
    }

    // ========== 参数验证异常处理 ==========
    
    /**
     * 处理请求参数缺失异常
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Object>> handleMissingParameter(
            MissingServletRequestParameterException e, HttpServletRequest request) {
        String traceId = generateTraceId();
        logger.warn("请求参数缺失 [{}]: 缺少参数 '{}' - 请求路径: {}", 
                   traceId, e.getParameterName(), request.getRequestURI());
        
        String message = String.format("缺少必需的请求参数: %s", e.getParameterName());
        ApiResponse<Object> response = ApiResponse.error(ErrorCode.PARAM_MISSING, message)
                .withTraceId(traceId);
        
        return ResponseEntity.status(ErrorCode.PARAM_MISSING.getHttpStatus()).body(response);
    }

    /**
     * 处理参数类型不匹配异常
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Object>> handleTypeMismatch(
            MethodArgumentTypeMismatchException e, HttpServletRequest request) {
        String traceId = generateTraceId();
        logger.warn("参数类型不匹配 [{}]: 参数 '{}' 类型错误 - 请求路径: {}", 
                   traceId, e.getName(), request.getRequestURI());
        
        String message = String.format("参数 '%s' 类型错误，期望类型: %s", 
                e.getName(), e.getRequiredType() != null ? e.getRequiredType().getSimpleName() : "unknown");
        ApiResponse<Object> response = ApiResponse.error(ErrorCode.PARAM_INVALID, message)
                .withTraceId(traceId);
        
        return ResponseEntity.status(ErrorCode.PARAM_INVALID.getHttpStatus()).body(response);
    }

    /**
     * 处理参数验证异常（@Valid）
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationException(
            MethodArgumentNotValidException e, HttpServletRequest request) {
        String traceId = generateTraceId();
        
        String errorDetails = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        
        logger.warn("参数验证失败 [{}]: {} - 请求路径: {}", traceId, errorDetails, request.getRequestURI());
        
        String message = "参数验证失败";
        ApiResponse<Object> response = ApiResponse.error(ErrorCode.PARAM_INVALID, message, errorDetails)
                .withTraceId(traceId);
        
        return ResponseEntity.status(ErrorCode.PARAM_INVALID.getHttpStatus()).body(response);
    }

    /**
     * 处理约束验证异常（@Validated）
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleConstraintViolationException(
            ConstraintViolationException e, HttpServletRequest request) {
        String traceId = generateTraceId();
        
        String errorDetails = e.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .collect(Collectors.joining("; "));
        
        logger.warn("约束验证失败 [{}]: {} - 请求路径: {}", traceId, errorDetails, request.getRequestURI());
        
        String message = "参数验证失败";
        ApiResponse<Object> response = ApiResponse.error(ErrorCode.PARAM_INVALID, message, errorDetails)
                .withTraceId(traceId);
        
        return ResponseEntity.status(ErrorCode.PARAM_INVALID.getHttpStatus()).body(response);
    }

    /**
     * 处理绑定异常
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiResponse<Object>> handleBindException(
            BindException e, HttpServletRequest request) {
        String traceId = generateTraceId();
        
        String errorDetails = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        
        logger.warn("参数绑定失败 [{}]: {} - 请求路径: {}", traceId, errorDetails, request.getRequestURI());
        
        String message = "参数绑定失败";
        ApiResponse<Object> response = ApiResponse.error(ErrorCode.PARAM_ERROR, message, errorDetails)
                .withTraceId(traceId);
        
        return ResponseEntity.status(ErrorCode.PARAM_ERROR.getHttpStatus()).body(response);
    }

    // ========== HTTP相关异常处理 ==========
    
    /**
     * 处理404异常
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleNoHandlerFound(
            NoHandlerFoundException e, HttpServletRequest request) {
        String traceId = generateTraceId();
        logger.warn("接口不存在 [{}]: {} {} - 请求路径: {}", 
                   traceId, e.getHttpMethod(), e.getRequestURL(), request.getRequestURI());
        
        ApiResponse<Object> response = ApiResponse.error(ErrorCode.DATA_NOT_FOUND, "请求的接口不存在")
                .withTraceId(traceId);
        
        return ResponseEntity.status(ErrorCode.DATA_NOT_FOUND.getHttpStatus()).body(response);
    }

    /**
     * 处理不支持的请求方法异常
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Object>> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
        String traceId = generateTraceId();
        logger.warn("不支持的请求方法 [{}]: {} - 请求路径: {}", 
                   traceId, e.getMethod(), request.getRequestURI());
        
        String message = String.format("不支持的请求方法: %s，支持的方法: %s", 
                e.getMethod(), String.join(", ", e.getSupportedMethods()));
        ApiResponse<Object> response = ApiResponse.error(ErrorCode.REQUEST_METHOD_NOT_SUPPORTED, message)
                .withTraceId(traceId);
        
        return ResponseEntity.status(ErrorCode.REQUEST_METHOD_NOT_SUPPORTED.getHttpStatus()).body(response);
    }

    /**
     * 处理不支持的媒体类型异常
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiResponse<Object>> handleMediaTypeNotSupported(
            HttpMediaTypeNotSupportedException e, HttpServletRequest request) {
        String traceId = generateTraceId();
        logger.warn("不支持的媒体类型 [{}]: {} - 请求路径: {}", 
                   traceId, e.getContentType(), request.getRequestURI());
        
        String message = String.format("不支持的媒体类型: %s", e.getContentType());
        ApiResponse<Object> response = ApiResponse.error(ErrorCode.MEDIA_TYPE_NOT_SUPPORTED, message)
                .withTraceId(traceId);
        
        return ResponseEntity.status(ErrorCode.MEDIA_TYPE_NOT_SUPPORTED.getHttpStatus()).body(response);
    }

    /**
     * 处理JSON解析错误
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Object>> handleHttpMessageNotReadable(
            HttpMessageNotReadableException e, HttpServletRequest request) {
        String traceId = generateTraceId();
        logger.warn("JSON解析错误 [{}]: {} - 请求路径: {}", traceId, e.getMessage(), request.getRequestURI());
        
        String message = "请求参数格式错误，请检查JSON格式";
        ApiResponse<Object> response = ApiResponse.error(ErrorCode.PARAM_INVALID, message)
                .withTraceId(traceId)
                .withDetails(e.getMessage());
        
        return ResponseEntity.status(ErrorCode.PARAM_INVALID.getHttpStatus()).body(response);
    }

    // ========== 通用异常处理 ==========
    
    /**
     * 处理其他未知异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGenericException(
            Exception e, HttpServletRequest request) {
        String traceId = generateTraceId();
        logger.error("系统异常 [{}]: {} - 请求路径: {}", traceId, e.getMessage(), request.getRequestURI(), e);
        
        ApiResponse<Object> response = ApiResponse.error(ErrorCode.SYSTEM_ERROR)
                .withTraceId(traceId)
                .withDetails("TraceId: " + traceId + ", 请联系管理员并提供该跟踪ID");
        
        return ResponseEntity.status(ErrorCode.SYSTEM_ERROR.getHttpStatus()).body(response);
    }

    // ========== 工具方法 ==========
    
    /**
     * 生成跟踪ID
     */
    private String generateTraceId() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }
}