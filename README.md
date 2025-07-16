# 瑜伽管理系统 (Youjia Yoga System)

一个基于Spring Boot的瑜伽管理系统，提供用户注册、登录、JWT认证等功能。

## 📋 项目简介

这是一个为Java初学者设计的完整项目，包含了现代Java Web开发的核心技术栈。通过这个项目，您可以学习到：

- **Spring Boot** - Java最流行的Web框架
- **Spring Security** - 安全认证框架
- **JWT** - 无状态身份认证
- **Spring Data JPA** - 数据访问层
- **H2/MySQL** - 数据库操作
- **RESTful API** - 现代API设计
- **Maven** - 项目管理工具

## 🛠️ 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Java | 17 | 编程语言 |
| Spring Boot | 3.0.2 | 主框架 |
| Spring Security | 6.0.4 | 安全框架 |
| Spring Data JPA | 3.0.2 | 数据访问 |
| H2 Database | 2.1.214 | 开发环境数据库 |
| MySQL | 8.0+ | 生产环境数据库 |
| Lombok | 1.18.30 | 代码简化工具 |
| Maven | 3.8+ | 项目管理 |

## 📁 项目结构

```
youjia/
├── src/main/java/com/yoga/youjia/
│   ├── YoujiaApplication.java          # 主启动类
│   ├── config/                         # 配置类
│   │   ├── SecurityConfig.java         # 密码加密配置
│   │   └── WebSecurityConfig.java      # 安全配置
│   ├── controller/                     # 控制器层
│   │   ├── AuthController.java         # 认证控制器
│   │   └── UserController.java         # 用户管理控制器
│   ├── service/                        # 业务逻辑层
│   │   ├── AuthService.java            # 认证服务
│   │   └── UserService.java            # 用户服务
│   ├── repository/                     # 数据访问层
│   │   └── UserRepository.java         # 用户数据访问
│   ├── entity/                         # 实体类
│   │   └── User.java                   # 用户实体
│   ├── dto/                           # 数据传输对象
│   │   ├── request/                   # 请求DTO
│   │   └── response/                  # 响应DTO
│   ├── security/                      # 安全相关
│   │   ├── filter/                    # 过滤器
│   │   └── service/                   # 安全服务
│   └── common/                        # 通用类
│       └── ApiResponse.java           # 统一响应格式
├── src/main/resources/
│   ├── application.properties         # 生产环境配置
│   └── application-dev.properties     # 开发环境配置
└── pom.xml                           # Maven配置文件
```

## 🚀 快速开始

### 环境要求

- **Java 17+** - [下载地址](https://adoptium.net/)
- **Maven 3.8+** - [下载地址](https://maven.apache.org/download.cgi)
- **IDE** - 推荐 IntelliJ IDEA 或 Eclipse
- **Git** - [下载地址](https://git-scm.com/)

### 1. 克隆项目

```bash
git clone https://github.com/your-username/youjia-yoga-system.git
cd youjia-yoga-system
```

### 2. 编译项目

```bash
mvn clean compile
```

### 3. 运行项目

#### 开发环境（使用H2内存数据库）
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

#### 生产环境（使用MySQL数据库）
```bash
# 需要先配置MySQL数据库
mvn spring-boot:run
```

### 4. 访问应用

- **应用地址**: http://localhost:8080
- **H2控制台**: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:youjia`
  - 用户名: `sa`
  - 密码: (空)

## 📚 API文档

### 认证接口

#### 用户注册
```http
POST /api/auth/register
Content-Type: application/json

{
    "username": "zhangsan",
    "password": "123456",
    "confirmPassword": "123456",
    "email": "zhangsan@example.com"
}
```

#### 用户登录
```http
POST /api/auth/login
Content-Type: application/json

{
    "username": "zhangsan",
    "password": "123456"
}
```

**响应示例：**
```json
{
    "success": true,
    "msg": {
        "user": {
            "id": 1,
            "username": "zhangsan",
            "email": "zhangsan@example.com",
            "status": "启用",
            "role": "普通用户"
        },
        "token": {
            "authorization": "Bearer",
            "access_token": "eyJhbGciOiJIUzI1NiJ9...",
            "expires_in": 86400
        }
    }
}
```

### 用户管理接口（需要JWT认证）

#### 获取用户信息
```http
GET /api/users/{id}
Authorization: Bearer <your-jwt-token>
```

#### 启用用户账户
```http
PUT /api/users/{id}/enable
Authorization: Bearer <your-jwt-token>
```

#### 禁用用户账户
```http
PUT /api/users/{id}/disable
Authorization: Bearer <your-jwt-token>
```

## 🔧 配置说明

### 开发环境配置 (application-dev.properties)

```properties
# 服务器端口
server.port=8080

# H2内存数据库配置
spring.datasource.url=jdbc:h2:mem:youjia
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# H2控制台配置
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# JPA配置
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
```

### 生产环境配置 (application.properties)

```properties
# MySQL数据库配置
spring.datasource.url=jdbc:mysql://localhost:3306/youjia
spring.datasource.username=root
spring.datasource.password=123456
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA配置
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
```

## 🧪 测试指南

### 使用curl测试

#### 1. 注册新用户
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "123456",
    "confirmPassword": "123456",
    "email": "test@example.com"
  }'
```

#### 2. 用户登录
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "123456"
  }'
```

#### 3. 访问受保护的接口
```bash
# 替换 <your-jwt-token> 为实际的JWT令牌
curl -X GET http://localhost:8080/api/users/1 \
  -H "Authorization: Bearer <your-jwt-token>"
```

### 使用Postman测试

1. 导入API集合
2. 设置环境变量：
   - `baseUrl`: `http://localhost:8080`
   - `token`: 登录后获取的JWT令牌
3. 按顺序测试：注册 → 登录 → 访问受保护接口

## 🔐 安全特性

- **密码加密**: 使用BCrypt算法加密存储密码
- **JWT认证**: 无状态的身份认证机制
- **接口保护**: 敏感接口需要JWT令牌验证
- **CORS配置**: 支持跨域请求
- **输入验证**: 使用Bean Validation验证输入数据

## 📖 学习资源

### 适合初学者的学习路径

1. **Java基础** - 掌握Java语法和面向对象编程
2. **Spring Boot入门** - 学习Spring Boot基本概念
3. **数据库操作** - 了解JPA和数据库操作
4. **Web开发** - 学习RESTful API设计
5. **安全认证** - 理解JWT和Spring Security

### 推荐学习资源

- [Spring Boot官方文档](https://spring.io/projects/spring-boot)
- [Spring Security官方文档](https://spring.io/projects/spring-security)
- [JWT官方网站](https://jwt.io/)
- [Maven官方文档](https://maven.apache.org/guides/)

## 🤝 贡献指南

欢迎提交Issue和Pull Request！

1. Fork本项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 创建Pull Request

## 📄 许可证

本项目采用MIT许可证 - 查看 [LICENSE](LICENSE) 文件了解详情

## 📞 联系方式

如果您有任何问题或建议，请通过以下方式联系：

- 提交Issue: [GitHub Issues](https://github.com/your-username/youjia-yoga-system/issues)
- 邮箱: your-email@example.com

---

**祝您学习愉快！🎉**
