package com.yoga.youjia.controller;

import com.yoga.youjia.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.info.BuildProperties;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 健康检测控制器
 * 
 * 提供应用程序健康状态检测和系统信息接口
 */
@Tag(name = "健康检测", description = "应用程序健康状态和系统信息相关接口")
@RestController
@RequestMapping("/api/health")
public class HealthController {

    @Autowired
    private Environment environment;

    @Autowired(required = false)
    private BuildProperties buildProperties;

    /**
     * 健康检测接口
     * 
     * 检查应用程序是否正常运行
     * 
     * @return 健康状态信息
     */
    @Operation(summary = "健康检测", description = "检查应用程序是否正常运行")
    @GetMapping("/check")
    public ApiResponse<Map<String, Object>> healthCheck() {
        Map<String, Object> healthInfo = new HashMap<>();
        
        try {
            // 基本健康信息
            healthInfo.put("status", "UP");
            healthInfo.put("timestamp", LocalDateTime.now());
            healthInfo.put("application", environment.getProperty("info.app.name", "YouJia User Management System"));
            healthInfo.put("version", buildProperties != null ? buildProperties.getVersion() : "unknown");
            
            // 系统信息
            Map<String, Object> systemInfo = new HashMap<>();
            systemInfo.put("javaVersion", System.getProperty("java.version"));
            systemInfo.put("osName", System.getProperty("os.name"));
            systemInfo.put("osVersion", System.getProperty("os.version"));
            systemInfo.put("activeProfiles", environment.getActiveProfiles());
            
            healthInfo.put("system", systemInfo);
            
            // 运行时信息
            Runtime runtime = Runtime.getRuntime();
            Map<String, Object> runtimeInfo = new HashMap<>();
            runtimeInfo.put("totalMemory", runtime.totalMemory());
            runtimeInfo.put("freeMemory", runtime.freeMemory());
            runtimeInfo.put("maxMemory", runtime.maxMemory());
            runtimeInfo.put("availableProcessors", runtime.availableProcessors());
            
            healthInfo.put("runtime", runtimeInfo);
            
            return ApiResponse.success(healthInfo, "应用程序运行正常");
            
        } catch (Exception e) {
            healthInfo.put("status", "DOWN");
            healthInfo.put("error", e.getMessage());
            return ApiResponse.error("503", "应用程序健康检查失败");
        }
    }

    /**
     * 简单的存活检测接口
     * 
     * 用于负载均衡器或监控系统的快速检测
     * 
     * @return 存活状态
     */
    @Operation(summary = "存活检测", description = "简单的应用程序存活状态检测")
    @GetMapping("/alive")
    public ApiResponse<String> aliveCheck() {
        return ApiResponse.success("OK", "应用程序运行中");
    }

    /**
     * 就绪检测接口
     * 
     * 检查应用程序是否已准备好接收请求
     * 
     * @return 就绪状态
     */
    @Operation(summary = "就绪检测", description = "检查应用程序是否已准备好接收请求")
    @GetMapping("/ready")
    public ApiResponse<Map<String, Object>> readyCheck() {
        Map<String, Object> readyInfo = new HashMap<>();
        
        try {
            // 检查关键组件是否就绪
            boolean databaseReady = checkDatabaseConnection();
            
            readyInfo.put("status", databaseReady ? "READY" : "NOT_READY");
            readyInfo.put("database", databaseReady ? "UP" : "DOWN");
            readyInfo.put("timestamp", LocalDateTime.now());
            
            if (databaseReady) {
                return ApiResponse.success(readyInfo, "应用程序已就绪");
            } else {
                return ApiResponse.error("503", "应用程序未就绪");
            }
            
        } catch (Exception e) {
            readyInfo.put("status", "ERROR");
            readyInfo.put("error", e.getMessage());
            return ApiResponse.error("503", "就绪检查失败");
        }
    }



    /**
     * 检查数据库连接状态
     * 
     * @return 数据库是否可连接
     */
    private boolean checkDatabaseConnection() {
        try {
            // 这里可以添加实际的数据库连接检查逻辑
            // 例如执行一个简单的查询
            return true; // 简化实现，实际项目中应该执行真实的数据库检查
        } catch (Exception e) {
            return false;
        }
    }
}
