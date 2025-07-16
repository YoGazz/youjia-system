package com.yoga.youjia.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 用户登录请求数据传输对象
 *
 * 这个类用于接收前端发送的用户登录请求数据。
 * 包含用户名和密码字段，并使用注解进行验证。
 *
 * 功能：
 * - 接收用户登录信息
 * - 验证用户名和密码不能为空
 *
 * 使用场景：
 * - 前端发送登录请求时，Spring会自动将请求体转换为这个对象
 * - 可以在控制器中直接使用这个对象作为方法参数
 */
@Data
public class LoginRequestDTO {

    @NotBlank(message = "用户名不能为空")
    private String username; // 用户名

    @NotBlank(message = "密码不能为空")
    private String password; // 密码
}
