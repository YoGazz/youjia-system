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
 * 映射数据库表：users
 */
@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    /**
     * 用户主键ID，自动生成
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 用户名，唯一标识，长度3-20字符
     */
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20, message = "用户名长度必须在3到20之间")
    @Column(unique = true)
    private String username;

    /**
     * 用户密码，BCrypt加密存储
     */
    @NotBlank(message = "密码不能为空")
    @Column(length = 100)
    private String password;

    /**
     * 用户真实姓名
     */
    @Column(length = 20)
    @Size(min = 1, max = 20, message = "真实姓名长度必须在1到20之间")
    private String realName;

    /**
     * 用户邮箱，唯一标识
     */
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    @Column(unique = true)
    private String email;

    /**
     * 用户头像URL或文件路径
     */
    private String avatar;

    /**
     * 用户状态，默认启用
     */
    @Column(columnDefinition = "varchar(20) default 'ENABLE'")
    private String status;

    /**
     * 用户角色，默认开发人员
     */
    @Column(columnDefinition = "varchar(20) default 'ROLE_DEVELOPER'")
    private String role;

    /**
     * 账户创建时间，不可修改
     */
    @Column(updatable = false)
    private LocalDateTime createdAt;


    /**
     * 用户角色枚举
     */
    public enum Roles {
        ADMIN("管理员", "ROLE_ADMIN"),
        PROJECT_MANAGER("项目经理", "ROLE_PM"),
        TESTER("测试人员", "ROLE_TESTER"),
        DEVELOPER("开发人员", "ROLE_DEVELOPER");

        private String name;
        private String code;

        Roles(String name, String code) {
            this.name = name;
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public String getCode() {
            return code;
        }

        /**
         * 根据枚举名称获取中文名称（用于数据库存储枚举名称的情况）
         */
        public static String getNameByEnumName(String enumName) {
            try {
                Roles role = Roles.valueOf(enumName);
                return role.getName();
            } catch (IllegalArgumentException e) {
                return enumName;
            }
        }

        /**
         * 根据角色代码获取角色名称
         */
        public static String getNameByCode(String code) {
            for (Roles role : Roles.values()) {
                if (role.getCode().equals(code)) {
                    return role.getName();
                }
            }
            return code;
        }

        /**
         * 根据角色名称获取角色代码
         */
        public static String getCodeByName(String name) {
            for (Roles role : Roles.values()) {
                if (role.getName().equals(name)) {
                    return role.getCode();
                }
            }
            return name;
        }
    }

    /**
     * 用户状态枚举
     */
    public enum Status {
        ENABLE(0, "启用"),
        DISABLE(1, "禁用"),
        LOCKED(2, "锁定");

        private int code;
        private String description;

        Status(int code, String description) {
            this.code = code;
            this.description = description;
        }

        public int getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }

        /**
         * 根据枚举名称获取描述（用于数据库存储枚举名称的情况）
         */
        public static String getDescriptionByName(String name) {
            try {
                Status status = Status.valueOf(name);
                return status.getDescription();
            } catch (IllegalArgumentException e) {
                return name;
            }
        }

        public static String getDescriptionByCode(String codeStr) {
            try {
                int code = Integer.parseInt(codeStr);
                for (Status status : Status.values()) {
                    if (status.getCode() == code) {
                        return status.getDescription();
                    }
                }
            } catch (NumberFormatException e) {
                return codeStr;
            }
            return codeStr;
        }
    }
}


