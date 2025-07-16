package com.yoga.youjia.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 用户注册请求数据传输对象
 *
 * 这个类用于接收前端发送的用户注册请求数据。
 * 包含注册所需的字段，并使用注解进行验证。
 * 功能：
 * - 接收用户注册信息
 * - 验证必要字段是否填写
 * * 使用场景：
 * - 前端发送注册请求时，Spring会自动将请求体转换为这个对象
 * - 可以在控制器中直接使用这个对象作为方法参数
 */

@Data
public class RegisterRequestDTO {

    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20, message = "用户名长度必须在3到20之间")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6到20之间")
    private String password;

    @NotBlank(message = "确认密码不能为空")
    private String confirmPassword;

    @Size(min = 1, max = 20, message = "真实姓名长度必须在1到20之间")
    private String realName;

    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;
}
