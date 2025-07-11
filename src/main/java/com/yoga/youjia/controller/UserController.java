package com.yoga.youjia.controller;

import com.yoga.youjia.entity.User;
import com.yoga.youjia.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户控制器类（简化版）
 *
 * 这个类负责处理用户相关的网络请求，比如用户注册。
 * 当前端发送请求到服务器时，这个类会接收请求并处理。
 *
 * 主要功能：
 * - 接收前端发来的用户注册信息
 * - 调用UserService来处理注册逻辑
 * - 返回注册结果给前端
 */
@RestController                 // 这个注解告诉Spring这是一个控制器类
                                // 控制器的作用是处理网络请求
                                // @RestController会自动把返回的对象转换成JSON格式

@RequestMapping("/api/users")   // 这个注解设置了基础的网址路径
                                // 比如注册接口的完整地址就是：/api/users/register
public class UserController {

    /**
     * 用户服务对象
     *
     * 这个对象用来处理用户相关的业务逻辑，比如注册、登录等。
     * @Autowired注解让Spring自动创建UserService对象并注入到这里。
     */
    @Autowired  // 这个注解让Spring自动给我们创建UserService对象
    private UserService userService;

    /**
     * 用户注册接口
     *
     * 这个方法处理用户注册请求。
     * 当前端发送注册信息到 /api/users/register 时，这个方法会被调用。
     *
     * 请求示例：
     * POST /api/users/register
     * {
     *   "username": "zhangSan",
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
     *
     * 失败响应：
     * {
     *   "success": false,
     *   "message": "用户名已存在",
     *   "data": null
     * }
     */
    @PostMapping("/register")   // 这个注解表示处理POST请求，地址是/register
    public Map<String, Object> register(@RequestBody User user) {
        // @RequestBody注解会把前端发来的JSON数据自动转换成User对象
        // 创建一个Map来存放返回给前端的数据
        Map<String, Object> result = new HashMap<>();

        try {
            // 调用UserService来处理注册逻辑
            User registeredUser = userService.register(user);

            // 注册成功，设置返回数据
            result.put("success", true);           // 表示操作成功
            result.put("message", "注册成功");      // 给用户看的消息
            result.put("data", registeredUser);    // 返回注册后的用户信息

        } catch (IllegalArgumentException e) {
            // 如果注册失败（比如用户名重复），会抛出异常
            // 我们捕获这个异常并返回错误信息
            result.put("success", false);          // 表示操作失败
            result.put("message", e.getMessage()); // 错误消息（比如"用户名已存在"）
            result.put("data", null);              // 失败时不返回数据

        } catch (Exception e) {
            // 如果发生其他未知错误
            result.put("success", false);
            result.put("message", "系统错误，请稍后重试");
            result.put("data", null);

            // 打印错误信息，方便调试
            e.printStackTrace();
        }

        // 返回结果给前端
        return result;
    }

    // TODO: 以后可以在这里添加其他功能
    // 比如：用户登录、获取用户信息、修改用户信息等

}
