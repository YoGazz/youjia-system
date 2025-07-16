package com.yoga.youjia.controller;

import com.yoga.youjia.entity.User;
import com.yoga.youjia.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器类
 * 
 * 这个类专门处理用户认证相关的HTTP请求，包括：
 * - 用户注册
 * - 用户登录
 * - 用户名/邮箱可用性检查
 * 
 * 为什么要单独创建AuthController？
 * 1. 职责分离：认证功能和用户管理功能分开
 * 2. URL组织：认证相关的接口统一放在/api/auth下
 * 3. 便于维护：认证相关的接口集中管理
 * 4. 符合RESTful设计：不同的资源使用不同的控制器
 */
@RestController                 // 告诉Spring这是一个REST控制器
@RequestMapping("/api/auth")    // 认证相关接口的基础路径
public class AuthController {

    /**
     * 认证服务对象
     * 
     * 用于处理认证相关的业务逻辑
     */
    @Autowired  // 让Spring自动注入AuthService
    private AuthService authService;

    /**
     * 用户注册接口
     * 
     * 处理用户注册请求
     * 
     * 请求地址：POST /api/auth/register
     * 请求体：
     * {
     *   "username": "zhangsan",
     *   "password": "123456",
     *   "email": "zhangsan@example.com"
     * }
     * 
     * 成功响应：
     * {
     *   "success": true,
     *   "message": "注册成功",
     *   "data": { 用户信息 }
     * }
     */
    @PostMapping("/register")   // 处理POST请求，完整地址是/api/auth/register
    public Map<String, Object> register(@RequestBody User user) {
        // 创建返回结果的Map
        Map<String, Object> result = new HashMap<>();

        try {
            // 调用AuthService处理注册逻辑
            User registeredUser = authService.register(user);

            // 注册成功
            result.put("success", true);
            result.put("message", "注册成功");
            result.put("data", registeredUser);

        } catch (IllegalArgumentException e) {
            // 注册失败（用户名或邮箱重复）
            result.put("success", false);
            result.put("message", e.getMessage());
            result.put("data", null);

        } catch (Exception e) {
            // 系统错误
            result.put("success", false);
            result.put("message", "系统错误，请稍后重试");
            result.put("data", null);
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 用户登录接口
     * 
     * 处理用户登录请求
     * 
     * 请求地址：POST /api/auth/login
     * 请求体：
     * {
     *   "username": "zhangsan",
     *   "password": "123456"
     * }
     * 
     * 成功响应：
     * {
     *   "success": true,
     *   "message": "登录成功",
     *   "data": { 用户信息（不包含密码） }
     * }
     * 
     * 失败响应：
     * {
     *   "success": false,
     *   "message": "用户名或密码错误",
     *   "data": null
     * }
     */
    @PostMapping("/login")      // 处理POST请求，完整地址是/api/auth/login
    public Map<String, Object> login(@RequestBody Map<String, String> loginData) {
        // 创建返回结果的Map
        Map<String, Object> result = new HashMap<>();

        try {
            // 从请求数据中获取用户名和密码
            String username = loginData.get("username");
            String password = loginData.get("password");

            // 检查参数是否为空
            if (username == null || username.trim().isEmpty()) {
                result.put("success", false);
                result.put("message", "用户名不能为空");
                result.put("data", null);
                return result;
            }

            if (password == null || password.trim().isEmpty()) {
                result.put("success", false);
                result.put("message", "密码不能为空");
                result.put("data", null);
                return result;
            }

            // 调用AuthService处理登录逻辑
            User loginUser = authService.login(username, password);

            // 登录成功
            result.put("success", true);
            result.put("message", "登录成功");
            result.put("data", loginUser);

        } catch (IllegalArgumentException e) {
            // 登录失败（用户名密码错误、账户被禁用等）
            result.put("success", false);
            result.put("message", e.getMessage());
            result.put("data", null);

        } catch (Exception e) {
            // 系统错误
            result.put("success", false);
            result.put("message", "系统错误，请稍后重试");
            result.put("data", null);
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 邮箱登录接口
     * 
     * 支持用户使用邮箱进行登录
     * 
     * 请求地址：POST /api/auth/login-email
     * 请求体：
     * {
     *   "email": "zhangsan@example.com",
     *   "password": "123456"
     * }
     */
    @PostMapping("/login-email") // 邮箱登录接口
    public Map<String, Object> loginByEmail(@RequestBody Map<String, String> loginData) {
        Map<String, Object> result = new HashMap<>();

        try {
            String email = loginData.get("email");
            String password = loginData.get("password");

            // 参数验证
            if (email == null || email.trim().isEmpty()) {
                result.put("success", false);
                result.put("message", "邮箱不能为空");
                result.put("data", null);
                return result;
            }

            if (password == null || password.trim().isEmpty()) {
                result.put("success", false);
                result.put("message", "密码不能为空");
                result.put("data", null);
                return result;
            }

            // 调用AuthService处理邮箱登录
            User loginUser = authService.loginByEmail(email, password);

            // 登录成功
            result.put("success", true);
            result.put("message", "登录成功");
            result.put("data", loginUser);

        } catch (IllegalArgumentException e) {
            result.put("success", false);
            result.put("message", e.getMessage());
            result.put("data", null);

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "系统错误，请稍后重试");
            result.put("data", null);
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 检查用户名是否可用
     * 
     * 用于注册页面的实时验证
     * 
     * 请求地址：GET /api/auth/check-username?username=zhangsan
     * 
     * 响应：
     * {
     *   "success": true,
     *   "message": "用户名可用",
     *   "data": { "available": true }
     * }
     */
    @GetMapping("/check-username")
    public Map<String, Object> checkUsername(@RequestParam String username) {
        Map<String, Object> result = new HashMap<>();

        try {
            boolean available = authService.isUsernameAvailable(username);
            
            result.put("success", true);
            result.put("message", available ? "用户名可用" : "用户名已存在");
            
            Map<String, Object> data = new HashMap<>();
            data.put("available", available);
            result.put("data", data);

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "检查失败");
            result.put("data", null);
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 检查邮箱是否可用
     * 
     * 用于注册页面的实时验证
     * 
     * 请求地址：GET /api/auth/check-email?email=zhangsan@example.com
     */
    @GetMapping("/check-email")
    public Map<String, Object> checkEmail(@RequestParam String email) {
        Map<String, Object> result = new HashMap<>();

        try {
            boolean available = authService.isEmailAvailable(email);
            
            result.put("success", true);
            result.put("message", available ? "邮箱可用" : "邮箱已存在");
            
            Map<String, Object> data = new HashMap<>();
            data.put("available", available);
            result.put("data", data);

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "检查失败");
            result.put("data", null);
            e.printStackTrace();
        }

        return result;
    }
}
