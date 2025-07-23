package com.yoga.youjia.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

/**
 * 请求日志拦截器
 * 
 * 用于记录所有HTTP请求的详细信息，包括：
 * - 请求路径和方法
 * - 请求参数
 * - 响应状态码
 * - 请求耗时
 * - 客户端IP地址
 */
@Component
public class RequestLogInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(RequestLogInterceptor.class);
    private static final String REQUEST_ID_KEY = "requestId";
    private static final String START_TIME_KEY = "startTime";

    /**
     * 请求处理前调用
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 生成请求ID
        String requestId = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        
        // 将请求ID放入MDC，用于日志追踪
        MDC.put(REQUEST_ID_KEY, requestId);
        
        // 记录请求开始时间
        request.setAttribute(START_TIME_KEY, System.currentTimeMillis());
        
        // 获取客户端IP地址
        String clientIp = getClientIpAddress(request);
        
        // 记录请求信息
        logger.info("请求开始 [{}] {} {} - 客户端IP: {}, User-Agent: {}", 
                   requestId, 
                   request.getMethod(), 
                   request.getRequestURI(),
                   clientIp,
                   request.getHeader("User-Agent"));
        
        // 记录请求参数
        if (request.getQueryString() != null) {
            logger.info("请求参数 [{}]: {}", requestId, request.getQueryString());
        }
        
        return true;
    }

    /**
     * 请求处理完成后调用
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, 
                              Object handler, Exception ex) {
        try {
            String requestId = MDC.get(REQUEST_ID_KEY);
            Long startTime = (Long) request.getAttribute(START_TIME_KEY);
            
            if (startTime != null) {
                long duration = System.currentTimeMillis() - startTime;
                
                if (ex != null) {
                    logger.error("请求异常 [{}] {} {} - 状态码: {}, 耗时: {}ms, 异常: {}", 
                               requestId,
                               request.getMethod(),
                               request.getRequestURI(),
                               response.getStatus(),
                               duration,
                               ex.getMessage());
                } else {
                    logger.info("请求完成 [{}] {} {} - 状态码: {}, 耗时: {}ms", 
                               requestId,
                               request.getMethod(),
                               request.getRequestURI(),
                               response.getStatus(),
                               duration);
                }
            }
        } finally {
            // 清理MDC
            MDC.clear();
        }
    }

    /**
     * 获取客户端真实IP地址
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        
        // 如果通过多级代理，X-Forwarded-For会包含多个IP，取第一个
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        
        return ip;
    }
}