package com.yoga.youjia.common.constants;

/**
 * 系统常量类
 * 
 * 定义系统中使用的各种常量
 */
public final class SystemConstants {
    
    // ========== 系统通用常量 ==========
    
    /**
     * 系统名称
     */
    public static final String SYSTEM_NAME = "佑珈测试管理平台";
    
    /**
     * 系统版本
     */
    public static final String SYSTEM_VERSION = "1.0.0";
    
    /**
     * 系统编码
     */
    public static final String SYSTEM_ENCODING = "UTF-8";
    
    /**
     * 默认时区
     */
    public static final String DEFAULT_TIMEZONE = "Asia/Shanghai";
    
    /**
     * 默认语言
     */
    public static final String DEFAULT_LOCALE = "zh_CN";
    
    // ========== 用户相关常量 ==========
    
    /**
     * 默认密码最小长度
     */
    public static final int DEFAULT_PASSWORD_MIN_LENGTH = 6;
    
    /**
     * 默认密码最大长度
     */
    public static final int DEFAULT_PASSWORD_MAX_LENGTH = 20;
    
    /**
     * 最大密码错误次数
     */
    public static final int MAX_PASSWORD_ERROR_COUNT = 5;
    
    /**
     * 账户锁定时间（分钟）
     */
    public static final int ACCOUNT_LOCK_MINUTES = 30;
    
    /**
     * 默认用户头像
     */
    public static final String DEFAULT_AVATAR = "/static/images/default-avatar.png";
    
    // ========== JWT相关常量 ==========
    
    /**
     * JWT令牌前缀
     */
    public static final String JWT_TOKEN_PREFIX = "Bearer ";
    
    /**
     * JWT令牌请求头名称
     */
    public static final String JWT_HEADER_NAME = "Authorization";
    
    /**
     * JWT令牌默认过期时间（毫秒） - 24小时
     */
    public static final long JWT_DEFAULT_EXPIRATION = 24 * 60 * 60 * 1000L;
    
    /**
     * JWT令牌最长过期时间（毫秒） - 7天
     */
    public static final long JWT_MAX_EXPIRATION = 7 * 24 * 60 * 60 * 1000L;
    
    // ========== 数据库相关常量 ==========
    
    /**
     * 默认分页大小
     */
    public static final int DEFAULT_PAGE_SIZE = 20;
    
    /**
     * 最大分页大小
     */
    public static final int MAX_PAGE_SIZE = 100;
    
    /**
     * 默认排序方向
     */
    public static final String DEFAULT_SORT_DIRECTION = "DESC";
    
    /**
     * 默认排序字段
     */
    public static final String DEFAULT_SORT_FIELD = "createdAt";
    
    // ========== 文件相关常量 ==========
    
    /**
     * 文件上传最大尺寸（字节） - 10MB
     */
    public static final long MAX_FILE_SIZE = 10 * 1024 * 1024L;
    
    /**
     * 允许的图片文件类型
     */
    public static final String[] ALLOWED_IMAGE_TYPES = {
        "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
    };
    
    /**
     * 允许的文档文件类型
     */
    public static final String[] ALLOWED_DOCUMENT_TYPES = {
        "application/pdf", "application/msword", 
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
        "application/vnd.ms-excel",
        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
        "text/plain", "text/csv"
    };
    
    // ========== 缓存相关常量 ==========
    
    /**
     * 缓存键前缀
     */
    public static final String CACHE_PREFIX = "youjia:";
    
    /**
     * 用户缓存键前缀
     */
    public static final String USER_CACHE_PREFIX = CACHE_PREFIX + "user:";
    
    /**
     * 项目缓存键前缀
     */
    public static final String PROJECT_CACHE_PREFIX = CACHE_PREFIX + "project:";
    
    /**
     * 默认缓存过期时间（秒） - 1小时
     */
    public static final long DEFAULT_CACHE_EXPIRATION = 60 * 60L;
    
    // ========== 日志相关常量 ==========
    
    /**
     * 操作日志类型
     */
    public static final class OperationLogType {
        public static final String LOGIN = "LOGIN";
        public static final String LOGOUT = "LOGOUT";
        public static final String CREATE = "CREATE";
        public static final String UPDATE = "UPDATE";
        public static final String DELETE = "DELETE";
        public static final String QUERY = "QUERY";
        public static final String EXPORT = "EXPORT";
        public static final String IMPORT = "IMPORT";
    }
    
    // ========== 测试相关常量 ==========
    
    /**
     * 测试用例最大步骤数
     */
    public static final int MAX_TEST_CASE_STEPS = 50;
    
    /**
     * 测试计划最大用例数
     */
    public static final int MAX_TEST_PLAN_CASES = 1000;
    
    /**
     * 项目最大成员数
     */
    public static final int MAX_PROJECT_MEMBERS = 100;
    
    // ========== 正则表达式常量 ==========
    
    /**
     * 用户名正则表达式（字母、数字、下划线）
     */
    public static final String USERNAME_REGEX = "^[a-zA-Z0-9_]{3,20}$";
    
    /**
     * 密码强度正则表达式（至少包含字母和数字）
     */
    public static final String PASSWORD_REGEX = "^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z\\d@$!%*?&]{6,20}$";
    
    /**
     * 手机号正则表达式
     */
    public static final String PHONE_REGEX = "^1[3-9]\\d{9}$";
    
    /**
     * 项目编码正则表达式（字母开头，字母数字下划线）
     */
    public static final String PROJECT_CODE_REGEX = "^[a-zA-Z][a-zA-Z0-9_]{2,19}$";
    
    // ========== API相关常量 ==========
    
    /**
     * API版本前缀
     */
    public static final String API_VERSION_PREFIX = "/api/v1";
    
    /**
     * 公开API路径前缀
     */
    public static final String PUBLIC_API_PREFIX = API_VERSION_PREFIX + "/public";
    
    /**
     * 私有API路径前缀
     */
    public static final String PRIVATE_API_PREFIX = API_VERSION_PREFIX + "/private";
    
    /**
     * 管理员API路径前缀
     */
    public static final String ADMIN_API_PREFIX = API_VERSION_PREFIX + "/admin";
    
    // ========== 私有构造函数 ==========
    
    private SystemConstants() {
        throw new AssertionError("不能实例化常量类");
    }
}