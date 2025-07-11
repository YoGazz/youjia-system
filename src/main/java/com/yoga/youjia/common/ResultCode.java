package com.yoga.youjia.common;

/**
 * 业务状态码常量类
 * 
 * 定义系统中所有业务场景的状态码，用于API响应中的code字段。
 * 统一管理状态码的优势：
 * - 避免魔法数字，提高代码可读性
 * - 便于维护和修改
 * - 前后端可以共享状态码定义
 * - 支持国际化和多语言
 * 
 * 状态码设计规范：
 * - 1000-1999: 通用状态码
 * - 2000-2999: 用户相关
 * - 3000-3999: 课程相关
 * - 4000-4999: 预约相关
 * - 5000-5999: 系统相关
 * 
 * 每个状态码都包含：
 * - code: 数字状态码
 * - message: 默认消息（可以被覆盖）
 */
public class ResultCode {

    // ================================
    // 通用状态码 (1000-1999)
    // ================================
    
    /**
     * 操作成功
     * 通用的成功状态码，适用于大部分成功场景
     */
    public static final int SUCCESS = 1000;
    public static final String SUCCESS_MSG = "操作成功";

    /**
     * 操作失败
     * 通用的失败状态码，适用于未明确分类的失败场景
     */
    public static final int FAIL = 1001;
    public static final String FAIL_MSG = "操作失败";

    /**
     * 参数验证失败
     * 用于Bean Validation验证失败的场景
     */
    public static final int PARAM_INVALID = 1002;
    public static final String PARAM_INVALID_MSG = "参数验证失败";

    /**
     * 参数缺失
     * 用于必需参数未提供的场景
     */
    public static final int PARAM_MISSING = 1003;
    public static final String PARAM_MISSING_MSG = "缺少必需参数";

    /**
     * 数据不存在
     * 用于查询数据不存在的场景
     */
    public static final int DATA_NOT_FOUND = 1004;
    public static final String DATA_NOT_FOUND_MSG = "数据不存在";

    /**
     * 权限不足
     * 用于用户权限不足的场景
     */
    public static final int PERMISSION_DENIED = 1005;
    public static final String PERMISSION_DENIED_MSG = "权限不足";

    // ================================
    // 用户相关状态码 (2000-2999)
    // ================================

    /**
     * 用户注册成功
     */
    public static final int USER_REGISTER_SUCCESS = 2000;
    public static final String USER_REGISTER_SUCCESS_MSG = "用户注册成功";

    /**
     * 用户名已存在
     * 用户注册时用户名重复的场景
     */
    public static final int USERNAME_EXISTS = 2001;
    public static final String USERNAME_EXISTS_MSG = "用户名已存在";

    /**
     * 邮箱已存在
     * 用户注册时邮箱重复的场景
     */
    public static final int EMAIL_EXISTS = 2002;
    public static final String EMAIL_EXISTS_MSG = "邮箱已存在";

    /**
     * 用户登录成功
     */
    public static final int USER_LOGIN_SUCCESS = 2003;
    public static final String USER_LOGIN_SUCCESS_MSG = "登录成功";

    /**
     * 用户名或密码错误
     * 用户登录时凭据错误的场景
     */
    public static final int LOGIN_FAILED = 2004;
    public static final String LOGIN_FAILED_MSG = "用户名或密码错误";

    /**
     * 用户账户被禁用
     * 用户登录时账户状态为禁用的场景
     */
    public static final int USER_DISABLED = 2005;
    public static final String USER_DISABLED_MSG = "用户账户已被禁用";

    /**
     * 用户不存在
     * 查询用户信息时用户不存在的场景
     */
    public static final int USER_NOT_FOUND = 2006;
    public static final String USER_NOT_FOUND_MSG = "用户不存在";

    /**
     * 密码格式不正确
     * 密码不符合安全要求的场景
     */
    public static final int PASSWORD_INVALID = 2007;
    public static final String PASSWORD_INVALID_MSG = "密码格式不正确";

    /**
     * 邮箱格式不正确
     * 邮箱格式验证失败的场景
     */
    public static final int EMAIL_INVALID = 2008;
    public static final String EMAIL_INVALID_MSG = "邮箱格式不正确";

    /**
     * 用户信息更新成功
     */
    public static final int USER_UPDATE_SUCCESS = 2009;
    public static final String USER_UPDATE_SUCCESS_MSG = "用户信息更新成功";

    // ================================
    // 课程相关状态码 (3000-3999)
    // ================================
    // 预留给将来的课程管理功能

    /**
     * 课程创建成功
     */
    public static final int COURSE_CREATE_SUCCESS = 3000;
    public static final String COURSE_CREATE_SUCCESS_MSG = "课程创建成功";

    /**
     * 课程不存在
     */
    public static final int COURSE_NOT_FOUND = 3001;
    public static final String COURSE_NOT_FOUND_MSG = "课程不存在";

    /**
     * 课程已满员
     */
    public static final int COURSE_FULL = 3002;
    public static final String COURSE_FULL_MSG = "课程已满员";

    // ================================
    // 预约相关状态码 (4000-4999)
    // ================================
    // 预留给将来的预约管理功能

    /**
     * 预约成功
     */
    public static final int BOOKING_SUCCESS = 4000;
    public static final String BOOKING_SUCCESS_MSG = "预约成功";

    /**
     * 预约失败
     */
    public static final int BOOKING_FAILED = 4001;
    public static final String BOOKING_FAILED_MSG = "预约失败";

    /**
     * 重复预约
     */
    public static final int BOOKING_DUPLICATE = 4002;
    public static final String BOOKING_DUPLICATE_MSG = "不能重复预约";

    /**
     * 取消预约成功
     */
    public static final int BOOKING_CANCEL_SUCCESS = 4003;
    public static final String BOOKING_CANCEL_SUCCESS_MSG = "取消预约成功";

    // ================================
    // 系统相关状态码 (5000-5999)
    // ================================

    /**
     * 系统内部错误
     * 用于未预期的系统异常
     */
    public static final int SYSTEM_ERROR = 5000;
    public static final String SYSTEM_ERROR_MSG = "系统内部错误，请稍后重试";

    /**
     * 数据库连接失败
     * 用于数据库连接异常的场景
     */
    public static final int DATABASE_ERROR = 5001;
    public static final String DATABASE_ERROR_MSG = "数据库连接失败";

    /**
     * 网络超时
     * 用于网络请求超时的场景
     */
    public static final int NETWORK_TIMEOUT = 5002;
    public static final String NETWORK_TIMEOUT_MSG = "网络请求超时";

    /**
     * 服务不可用
     * 用于服务暂时不可用的场景
     */
    public static final int SERVICE_UNAVAILABLE = 5003;
    public static final String SERVICE_UNAVAILABLE_MSG = "服务暂时不可用";

    /**
     * 文件上传失败
     * 用于文件上传相关的错误
     */
    public static final int FILE_UPLOAD_FAILED = 5004;
    public static final String FILE_UPLOAD_FAILED_MSG = "文件上传失败";

    /**
     * 文件格式不支持
     * 用于文件格式验证失败的场景
     */
    public static final int FILE_FORMAT_UNSUPPORTED = 5005;
    public static final String FILE_FORMAT_UNSUPPORTED_MSG = "文件格式不支持";

    // ================================
    // 工具方法
    // ================================

    /**
     * 私有构造函数
     * 防止实例化，这是一个工具类
     */
    private ResultCode() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * 根据状态码获取默认消息
     * 
     * 这个方法可以根据状态码返回对应的默认消息，
     * 便于统一管理和国际化处理。
     * 
     * @param code 状态码
     * @return String 对应的默认消息
     */
    public static String getMessageByCode(int code) {
        switch (code) {
            case SUCCESS: return SUCCESS_MSG;
            case FAIL: return FAIL_MSG;
            case PARAM_INVALID: return PARAM_INVALID_MSG;
            case PARAM_MISSING: return PARAM_MISSING_MSG;
            case DATA_NOT_FOUND: return DATA_NOT_FOUND_MSG;
            case PERMISSION_DENIED: return PERMISSION_DENIED_MSG;
            
            case USER_REGISTER_SUCCESS: return USER_REGISTER_SUCCESS_MSG;
            case USERNAME_EXISTS: return USERNAME_EXISTS_MSG;
            case EMAIL_EXISTS: return EMAIL_EXISTS_MSG;
            case USER_LOGIN_SUCCESS: return USER_LOGIN_SUCCESS_MSG;
            case LOGIN_FAILED: return LOGIN_FAILED_MSG;
            case USER_DISABLED: return USER_DISABLED_MSG;
            case USER_NOT_FOUND: return USER_NOT_FOUND_MSG;
            case PASSWORD_INVALID: return PASSWORD_INVALID_MSG;
            case EMAIL_INVALID: return EMAIL_INVALID_MSG;
            case USER_UPDATE_SUCCESS: return USER_UPDATE_SUCCESS_MSG;
            
            case COURSE_CREATE_SUCCESS: return COURSE_CREATE_SUCCESS_MSG;
            case COURSE_NOT_FOUND: return COURSE_NOT_FOUND_MSG;
            case COURSE_FULL: return COURSE_FULL_MSG;
            
            case BOOKING_SUCCESS: return BOOKING_SUCCESS_MSG;
            case BOOKING_FAILED: return BOOKING_FAILED_MSG;
            case BOOKING_DUPLICATE: return BOOKING_DUPLICATE_MSG;
            case BOOKING_CANCEL_SUCCESS: return BOOKING_CANCEL_SUCCESS_MSG;
            
            case SYSTEM_ERROR: return SYSTEM_ERROR_MSG;
            case DATABASE_ERROR: return DATABASE_ERROR_MSG;
            case NETWORK_TIMEOUT: return NETWORK_TIMEOUT_MSG;
            case SERVICE_UNAVAILABLE: return SERVICE_UNAVAILABLE_MSG;
            case FILE_UPLOAD_FAILED: return FILE_UPLOAD_FAILED_MSG;
            case FILE_FORMAT_UNSUPPORTED: return FILE_FORMAT_UNSUPPORTED_MSG;
            
            default: return "未知状态码";
        }
    }
}
