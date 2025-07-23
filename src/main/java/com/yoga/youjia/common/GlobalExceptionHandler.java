package com.yoga.youjia.common;

import com.yoga.youjia.common.exception.BusinessException;
import com.yoga.youjia.common.exception.DataConflictException;
import com.yoga.youjia.common.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import jakarta.servlet.http.HttpServletRequest;
import java.util.UUID;

/**
 * 全局异常处理器
 * 
 * 统一处理系统异常，返回规范化的响应格式
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

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
        
        ApiResponse<Object> response = ApiResponse.error(e.getErrorCode(), e.getErrorMessage())
                .withTraceId(traceId);
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /**
     * 处理数据冲突异常
     */
    @ExceptionHandler(DataConflictException.class)
    public ResponseEntity<ApiResponse<Object>> handleDataConflictException(
            DataConflictException e, HttpServletRequest request) {
        String traceId = generateTraceId();
        logger.warn("数据冲突 [{}]: {} - 请求路径: {}", traceId, e.getMessage(), request.getRequestURI());
        
        ApiResponse<Object> response = ApiResponse.error(e.getErrorCode(), e.getErrorMessage())
                .withTraceId(traceId);
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    /**
     * 处理认证异常
     */
    @ExceptionHandler({AuthenticationException.class, BadCredentialsException.class})
    public ResponseEntity<ApiResponse<Object>> handleAuthenticationException(
            Exception e, HttpServletRequest request) {
        String traceId = generateTraceId();
        logger.warn("认证失败 [{}]: {} - 请求路径: {}", traceId, e.getMessage(), request.getRequestURI());
        
        ApiResponse<Object> response = ApiResponse.error("401", "认证失败，请检查用户名和密码")
                .withTraceId(traceId);
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    /**
     * 处理授权异常
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Object>> handleAccessDeniedException(
            AccessDeniedException e, HttpServletRequest request) {
        String traceId = generateTraceId();
        logger.warn("访问被拒绝 [{}]: {} - 请求路径: {}", traceId, e.getMessage(), request.getRequestURI());
        
        ApiResponse<Object> response = ApiResponse.error("403", "访问被拒绝，权限不足")
                .withTraceId(traceId);
        
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    /**
     * 处理请求参数缺失异常
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Object>> handleMissingParameter(
            MissingServletRequestParameterException e, HttpServletRequest request) {
        String traceId = generateTraceId();
        logger.warn("请求参数缺失 [{}]: 缺少参数 '{}' - 请求路径: {}", 
                   traceId, e.getParameterName(), request.getRequestURI());
        
        ApiResponse<Object> response = ApiResponse.error("400", 
                String.format("缺少必需的请求参数: %s", e.getParameterName()))
                .withTraceId(traceId);
        
        return ResponseEntity.badRequest().body(response);
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
        
        ApiResponse<Object> response = ApiResponse.error("400", 
                String.format("参数 '%s' 类型错误", e.getName()))
                .withTraceId(traceId);
        
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * 处理404异常
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleNoHandlerFound(
            NoHandlerFoundException e, HttpServletRequest request) {
        String traceId = generateTraceId();
        logger.warn("接口不存在 [{}]: {} {} - 请求路径: {}", 
                   traceId, e.getHttpMethod(), e.getRequestURL(), request.getRequestURI());
        
        ApiResponse<Object> response = ApiResponse.error("404", "请求的接口不存在")
                .withTraceId(traceId);
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /**
     * 处理JSON解析错误
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Object>> handleHttpMessageNotReadable(
            HttpMessageNotReadableException e, HttpServletRequest request) {
        String traceId = generateTraceId();
        logger.warn("JSON解析错误 [{}]: {} - 请求路径: {}", traceId, e.getMessage(), request.getRequestURI());
        
        ApiResponse<Object> response = ApiResponse.error("400", "请求参数格式错误，请检查JSON格式")
                .withTraceId(traceId);
        
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * 处理参数验证错误
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationException(
            MethodArgumentNotValidException e, HttpServletRequest request) {
        String traceId = generateTraceId();
        String errorMsg = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .reduce((msg1, msg2) -> msg1 + "; " + msg2)
                .orElse("参数验证失败");
        
        logger.warn("参数验证失败 [{}]: {} - 请求路径: {}", traceId, errorMsg, request.getRequestURI());
        
        ApiResponse<Object> response = ApiResponse.error("400", "参数验证失败: " + errorMsg)
                .withTraceId(traceId);
        
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * 处理绑定异常
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiResponse<Object>> handleBindException(
            BindException e, HttpServletRequest request) {
        String traceId = generateTraceId();
        String errorMsg = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .reduce((msg1, msg2) -> msg1 + "; " + msg2)
                .orElse("参数绑定失败");
        
        logger.warn("参数绑定失败 [{}]: {} - 请求路径: {}", traceId, errorMsg, request.getRequestURI());
        
        ApiResponse<Object> response = ApiResponse.error("400", "参数绑定失败: " + errorMsg)
                .withTraceId(traceId);
        
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * 处理其他未知异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGenericException(
            Exception e, HttpServletRequest request) {
        String traceId = generateTraceId();
        logger.error("系统异常 [{}]: {} - 请求路径: {}", traceId, e.getMessage(), request.getRequestURI(), e);
        
        ApiResponse<Object> response = ApiResponse.error("500", "系统内部错误，请联系管理员")
                .withTraceId(traceId);
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    /**
     * 生成跟踪ID
     */
    private String generateTraceId() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }
}