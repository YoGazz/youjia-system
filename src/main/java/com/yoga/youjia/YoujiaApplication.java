package com.yoga.youjia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 瑜伽项目主启动类
 *
 * 这是整个Spring Boot应用程序的入口点，负责启动和配置整个应用。
 * Spring Boot通过这个类来扫描和配置所有的组件、服务、控制器等。
 *
 * 项目功能：瑜伽管理系统
 * - 用户管理（注册、登录、个人信息管理）
 * - 瑜伽课程管理
 * - 预约系统
 *
 * 技术栈：
 * - Spring Boot 3.0.2 (主框架)
 * - Spring Data JPA (数据访问层)
 * - H2 Database (开发环境数据库)
 * - MySQL (生产环境数据库)
 * - Lombok (简化Java代码)
 *
 * @author 开发者
 * @version 1.0
 * @since 2025-07-10
 */
@SpringBootApplication  // @SpringBootApplication注解的作用：
                        // 这是一个复合注解，包含了以下三个重要注解：
                        // 1. @Configuration: 标识这是一个配置类，可以定义Bean
                        // 2. @EnableAutoConfiguration: 启用Spring Boot的自动配置功能
                        //    - 根据classpath中的jar包自动配置Spring应用
                        //    - 例如：发现spring-boot-starter-web就自动配置Web环境
                        //    - 发现spring-boot-starter-data-jpa就自动配置JPA
                        // 3. @ComponentScan: 启用组件扫描
                        //    - 自动扫描当前包及子包下的所有Spring组件
                        //    - 包括@Controller、@Service、@Repository、@Component等
                        //    - 扫描范围：com.yoga.youjia包及其所有子包
public class YoujiaApplication {

    /**
     * 应用程序主入口方法
     *
     * 这是Java应用程序的标准入口点，JVM启动时会首先执行这个方法。
     * Spring Boot通过这个方法来启动整个Web应用程序。
     *
     * 启动过程：
     * 1. 创建Spring应用上下文(ApplicationContext)
     * 2. 扫描并注册所有的Spring组件(Bean)
     * 3. 执行自动配置
     * 4. 启动内嵌的Web服务器(默认Tomcat)
     * 5. 应用程序准备就绪，开始接收请求
     *
     * @param args 命令行参数
     *             - 可以通过命令行传递配置参数
     *             - 例如：--server.port=8081 (修改端口)
     *             - 例如：--spring.profiles.active=prod (激活生产环境配置)
     */
    public static void main(String[] args) {
        // SpringApplication.run()方法的作用：
        // 1. 创建SpringApplication实例
        // 2. 推断应用类型(Web应用、响应式应用或普通应用)
        // 3. 加载所有的ApplicationContextInitializer
        // 4. 加载所有的ApplicationListener
        // 5. 推断主配置类
        // 6. 启动应用程序
        //
        // 参数说明：
        // - YoujiaApplication.class: 主配置类，告诉Spring Boot从哪里开始扫描
        // - args: 命令行参数，会传递给Spring Boot进行处理
        SpringApplication.run(YoujiaApplication.class, args);

        // 当这行代码执行完成后，应用程序就已经启动完毕
        // 可以通过 http://localhost:8080 访问应用程序
        // 如果启用了H2控制台，可以通过 http://localhost:8080/h2-console 访问数据库
    }

}
