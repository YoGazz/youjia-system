package com.yoga.youjia.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户实体类
 *
 * 这个类代表数据库中的用户表，用于存储用户的基本信息。
 * 在JPA中，这被称为实体(Entity)，它与数据库表形成一对一的映射关系。
 *
 * 数据库表名：users
 * 主要功能：
 * - 存储用户的基本信息（用户名、密码、邮箱等）
 * - 管理用户的状态和角色
 * - 记录用户的创建时间
 *
 * 设计模式：
 * - 使用了JavaBean模式（getter/setter方法）
 * - 使用了Builder模式（通过Lombok的@Data注解）
 * - 使用了JPA注解进行ORM映射
 */
@Entity         // @Entity注解的作用：
                // 1. 标识这是一个JPA实体类
                // 2. 告诉JPA这个类需要映射到数据库表
                // 3. JPA会根据这个类自动创建对应的数据库表
                // 4. 实体类必须有无参构造函数（Lombok的@NoArgsConstructor提供）

@Table(name = "users")  // @Table注解的作用：
                        // 1. 指定实体类对应的数据库表名
                        // 2. 如果不指定，JPA会使用类名作为表名
                        // 3. 这里指定为"users"是因为"user"在某些数据库中是保留字
                        // 4. 可以指定schema、catalog等更多属性

@Data           // @Data注解的作用（Lombok提供）：
                // 1. 自动生成所有字段的getter方法
                // 2. 自动生成所有非final字段的setter方法
                // 3. 自动生成toString()方法
                // 4. 自动生成equals()和hashCode()方法
                // 5. 大大减少了样板代码，提高开发效率

@AllArgsConstructor  // @AllArgsConstructor注解的作用（Lombok提供）：
                     // 1. 自动生成包含所有字段的构造函数
                     // 2. 参数顺序按照字段在类中的声明顺序
                     // 3. 方便创建完整的对象实例

@NoArgsConstructor   // @NoArgsConstructor注解的作用（Lombok提供）：
                     // 1. 自动生成无参构造函数
                     // 2. JPA实体类必须有无参构造函数
                     // 3. Spring在创建Bean时也需要无参构造函数
public class User {

    /**
     * 用户主键ID
     *
     * 这是用户表的主键，用于唯一标识每个用户。
     * 数据库会自动为每个新用户生成一个唯一的ID。
     */
    @Id                                     // @Id注解的作用：
                                            // 1. 标识这个字段是数据库表的主键
                                            // 2. 每个JPA实体类必须有且只能有一个@Id字段
                                            // 3. 主键用于唯一标识表中的每一行数据

    @GeneratedValue(strategy = GenerationType.IDENTITY)  // @GeneratedValue注解的作用：
                                                         // 1. 指定主键的生成策略
                                                         // 2. IDENTITY策略：使用数据库的自增长字段
                                                         // 3. 插入数据时不需要手动设置ID值
                                                         // 4. 数据库会自动分配一个递增的ID
    private Long id;

    /**
     * 用户名
     *
     * 用户的登录名，必须唯一。
     * 用于用户登录和身份识别。
     *
     * 验证规则：
     * - 不能为空
     * - 长度必须在3-20个字符之间
     * - 在数据库中必须唯一
     */
    @NotBlank(message = "用户名不能为空")    // @NotBlank注解的作用（Bean Validation）：
                                          // 1. 验证字符串不能为null、空字符串或只包含空白字符
                                          // 2. 比@NotNull更严格，专门用于字符串验证
                                          // 3. message属性指定验证失败时的错误信息
                                          // 4. 在Controller接收参数时会自动进行验证

    @Size(min = 3, max = 20, message = "用户名长度必须在3到20之间")  // @Size注解的作用：
                                                              // 1. 验证字符串、集合、数组的长度
                                                              // 2. min: 最小长度，max: 最大长度
                                                              // 3. 确保用户名既不会太短也不会太长
                                                              // 4. 有助于数据库性能和用户体验

    @Column(unique = true)              // @Column注解的作用：
                                        // 1. 指定字段在数据库中的列属性
                                        // 2. unique = true: 在数据库层面确保该字段值唯一
                                        // 3. 防止重复的用户名被插入数据库
                                        // 4. 可以指定列名、长度、是否可空等属性
    private String username;

    /**
     * 用户密码
     *
     * 用户的登录密码，用于身份验证。
     * 注意：密码以加密形式存储（使用BCrypt）。
     *
     * 验证规则：
     * - 不能为空
     * - 原始密码长度必须在6-20个字符之间（加密后会更长）
     */
    @NotBlank(message = "密码不能为空")
    @Column(length = 100)  // 增加列长度以适应加密后的密码
    private String password;

    /**
     * 用户真实姓名
     *
     * 用户的真实姓名，用于：
     * - 个人信息展示
     */

    @Column(length = 20)  // 限制真实姓名长度为20个字符
    @Size(min = 1, max = 20, message = "真实姓名长度必须在1到20之间")  // 验证真实姓名长度
    private String realName;

    /**
     * 用户邮箱
     *
     * 用户的电子邮箱地址，用于：
     * - 账户验证和找回密码
     * - 接收系统通知
     * - 作为备用登录方式
     *
     * 验证规则：
     * - 不能为空
     * - 必须符合邮箱格式
     * - 在数据库中必须唯一
     */
    @NotBlank(message = "邮箱不能为空")    // 确保用户必须提供邮箱
    @Email(message = "邮箱格式不正确")     // @Email注解的作用：
                                        // 1. 验证字符串是否符合邮箱格式
                                        // 2. 使用正则表达式检查邮箱格式
                                        // 3. 确保邮箱地址的有效性
                                        // 4. 提高数据质量和用户体验
    @Column(unique = true)              // 确保邮箱地址唯一，防止重复注册
    private String email;

    /**
     * 用户头像
     *
     * 存储用户头像的URL或文件路径。
     * 这个字段是可选的，用户可以选择不设置头像。
     *
     * 可能的值：
     * - null: 用户没有设置头像，使用默认头像
     * - URL: 头像图片的网络地址
     * - 文件路径: 服务器上头像文件的路径
     */
    private String avatar;

    /**
     * 用户状态
     *
     * 表示用户账户的当前状态，用于账户管理。
     * 管理员可以通过修改状态来启用或禁用用户账户。
     *
     * 可能的值：
     * - "启用": 用户可以正常登录和使用系统
     * - "禁用": 用户被禁止登录，账户被冻结
     */
    @Column(columnDefinition = "varchar(10) default 'enable'")  // @Column的columnDefinition属性：
                                                                // 1. 直接指定数据库列的DDL定义
                                                                // 2. varchar(10): 字符串类型，最大长度10
                                                                // 3. default 'enable': 设置默认值为'enable'
                                                                // 4. 新用户注册时如果不指定状态，默认为启用
    private String status;

    /**
     * 用户角色
     *
     * 定义用户在系统中的权限级别。
     * 不同角色拥有不同的功能访问权限。
     *
     * 可能的值：
     * - "普通用户": 基本功能权限，如预约课程、查看个人信息
     * - "管理员": 完整管理权限，如用户管理、课程管理、系统配置
     */
    @Column(columnDefinition = "varchar(10) default 'user'")    // 默认角色为普通用户
    private String role;

    /**
     * 账户创建时间
     *
     * 记录用户注册的时间戳，用于：
     * - 数据统计和分析
     * - 审计和日志记录
     * - 用户账户历史追踪
     *
     * 特点：
     * - 一旦设置就不能修改（updatable = false）
     * - 在用户注册时自动设置为当前时间
     */
    @Column(updatable = false)  // updatable = false的作用：
                                // 1. 防止该字段在UPDATE操作中被修改
                                // 2. 确保创建时间的准确性和不可篡改性
                                // 3. 提高数据的完整性和可信度
    private LocalDateTime createdAt;


    /**
     * 用户角色枚举
     *
     * 定义系统中所有可能的用户角色类型。
     * 使用枚举的优势：
     * - 类型安全：编译时检查，避免无效值
     * - 代码可读性：明确的常量定义
     * - 易于维护：集中管理所有角色类型
     * - 防止魔法数字：避免在代码中直接使用数字代码
     *
     * 设计模式：
     * - 每个枚举值包含code（数字代码）和description（中文描述）
     * - code用于数据库存储和系统内部处理
     * - description用于前端显示和用户界面
     */
    public enum Roles {
        /**
         * 普通用户角色
         * - 代码：0
         * - 描述：普通用户
         * - 权限：基础功能权限，如查看课程、预约课程、管理个人信息
         */
        USER(0, "普通用户"),

        /**
         * 管理员角色
         * - 代码：1
         * - 描述：管理员
         * - 权限：完整管理权限，如用户管理、课程管理、系统配置、数据统计
         */
        ADMIN(1, "管理员");

        /**
         * 角色代码
         * 用于数据库存储和系统内部标识
         */
        private int code;

        /**
         * 角色描述
         * 用于前端显示和用户界面展示
         */
        private String description;

        /**
         * 枚举构造函数
         *
         * 创建枚举实例时设置代码和描述。
         * 枚举构造函数必须是private的（默认就是private）。
         *
         * @param code 角色代码
         * @param description 角色描述
         */
        Roles(int code, String description) {
            this.code = code;
            this.description = description;
        }

        /**
         * 获取角色代码
         *
         * @return 角色的数字代码
         */
        public int getCode() {
            return code;
        }

        /**
         * 获取角色描述
         *
         * @return 角色的中文描述
         */
        public String getDescription() {
            return description;
        }

        /**
         * 根据代码获取角色描述
         *
         * @param codeStr 角色代码
         * @return 角色的中文描述，如果代码无效则返回null
         */
        public static String getDescriptionByCode(String codeStr) {
            try {
                int code = Integer.parseInt(codeStr);
                for (Roles role : Roles.values()) {
                    if (role.getCode() == code) {
                        return role.getDescription();
                    }
                }
            } catch (NumberFormatException e) {
                // 如果代码不是数字格式，返回null
                return codeStr;
            }
            return codeStr;
        }
    }

    /**
     * 用户状态枚举
     *
     * 定义用户账户的所有可能状态。
     * 用于账户管理和访问控制。
     *
     * 使用场景：
     * - 管理员可以启用或禁用用户账户
     * - 系统可以根据状态控制用户登录权限
     * - 审计和监控用户账户状态变化
     *
     * 设计考虑：
     * - 使用枚举确保状态值的有效性
     * - 提供代码和描述两种表示方式
     * - 便于扩展（如添加"待审核"、"已注销"等状态）
     */
    public enum Status {
        /**
         * 启用状态
         * - 代码：0
         * - 描述：启用
         * - 含义：用户账户正常，可以登录和使用系统所有功能
         */
        ENABLE(0, "启用"),

        /**
         * 禁用状态
         * - 代码：1
         * - 描述：禁用
         * - 含义：用户账户被冻结，无法登录系统
         * - 使用场景：违规用户、临时冻结、管理员操作等
         */
        DISABLE(1, "禁用");

        /**
         * 状态代码
         * 用于数据库存储和系统内部处理
         */
        private int code;

        /**
         * 状态描述
         * 用于前端显示和管理界面
         */
        private String description;

        /**
         * 枚举构造函数
         * <p>
         * 初始化状态枚举的代码和描述。
         *
         * @param code        状态代码
         * @param description 状态描述
         */
        Status(int code, String description) {
            this.code = code;
            this.description = description;
        }

        /**
         * 获取状态代码
         *
         * @return 状态的数字代码
         */
        public int getCode() {
            return code;
        }

        /**
         * 获取状态描述
         *
         * @return 状态的中文描述
         */
        public String getDescription() {
            return description;
        }

        /**
         * 根据代码获取状态描述
         *
         * @param codeStr 状态代码
         * @return 状态的中文描述，如果代码无效则返回null
         */
        public static String getDescriptionByCode(String codeStr) {
            try {
                int code = Integer.parseInt(codeStr);
                for (Status status : Status.values()) {
                    if (status.getCode() == code) {
                        return status.getDescription();
                    }
                }
            } catch (NumberFormatException e) {
                // 如果代码不是数字格式，返回null
                return codeStr;
            }
            return codeStr;
        }
    }
}


