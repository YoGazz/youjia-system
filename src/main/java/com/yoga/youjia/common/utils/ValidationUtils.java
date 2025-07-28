package com.yoga.youjia.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;
import java.util.Collection;
import java.util.Map;

/**
 * 验证工具类
 * 
 * 提供各种数据验证方法
 */
@Slf4j
public final class ValidationUtils {
    
    // ========== 正则表达式模式 ==========
    
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,20}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z\\d@$!%*?&]{6,20}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");
    private static final Pattern PROJECT_CODE_PATTERN = Pattern.compile("^[a-zA-Z][a-zA-Z0-9_]{2,19}$");
    
    // ========== 基础验证方法 ==========
    
    /**
     * 检查字符串是否为空或null
     */
    public static boolean isEmpty(String str) {
        return !StringUtils.hasText(str);
    }
    
    /**
     * 检查字符串是否不为空
     */
    public static boolean isNotEmpty(String str) {
        return StringUtils.hasText(str);
    }
    
    /**
     * 检查集合是否为空
     */
    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }
    
    /**
     * 检查Map是否为空
     */
    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }
    
    /**
     * 检查数组是否为空
     */
    public static boolean isEmpty(Object[] array) {
        return array == null || array.length == 0;
    }
    
    // ========== 长度验证方法 ==========
    
    /**
     * 检查字符串长度是否在指定范围内
     */
    public static boolean isLengthValid(String str, int minLength, int maxLength) {
        if (str == null) {
            return false;
        }
        int length = str.length();
        return length >= minLength && length <= maxLength;
    }
    
    /**
     * 检查字符串长度是否不超过最大长度
     */
    public static boolean isLengthValid(String str, int maxLength) {
        return str != null && str.length() <= maxLength;
    }
    
    // ========== 数值验证方法 ==========
    
    /**
     * 检查数值是否在指定范围内
     */
    public static boolean isNumberInRange(Number number, Number min, Number max) {
        if (number == null) {
            return false;
        }
        double value = number.doubleValue();
        double minValue = min != null ? min.doubleValue() : Double.MIN_VALUE;
        double maxValue = max != null ? max.doubleValue() : Double.MAX_VALUE;
        return value >= minValue && value <= maxValue;
    }
    
    /**
     * 检查数值是否为正数
     */
    public static boolean isPositive(Number number) {
        return number != null && number.doubleValue() > 0;
    }
    
    /**
     * 检查数值是否为非负数
     */
    public static boolean isNonNegative(Number number) {
        return number != null && number.doubleValue() >= 0;
    }
    
    // ========== 正则表达式验证方法 ==========
    
    /**
     * 验证用户名格式
     */
    public static boolean isValidUsername(String username) {
        return isNotEmpty(username) && USERNAME_PATTERN.matcher(username).matches();
    }
    
    /**
     * 验证密码强度
     */
    public static boolean isValidPassword(String password) {
        return isNotEmpty(password) && PASSWORD_PATTERN.matcher(password).matches();
    }
    
    /**
     * 验证邮箱格式
     */
    public static boolean isValidEmail(String email) {
        return isNotEmpty(email) && EMAIL_PATTERN.matcher(email).matches();
    }
    
    /**
     * 验证手机号格式
     */
    public static boolean isValidPhone(String phone) {
        return isNotEmpty(phone) && PHONE_PATTERN.matcher(phone).matches();
    }
    
    /**
     * 验证项目编码格式
     */
    public static boolean isValidProjectCode(String projectCode) {
        return isNotEmpty(projectCode) && PROJECT_CODE_PATTERN.matcher(projectCode).matches();
    }
    
    /**
     * 验证自定义正则表达式
     */
    public static boolean isValidPattern(String input, String regex) {
        if (isEmpty(input) || isEmpty(regex)) {
            return false;
        }
        try {
            return Pattern.compile(regex).matcher(input).matches();
        } catch (Exception e) {
            log.warn("正则表达式验证失败: {}", e.getMessage());
            return false;
        }
    }
    
    // ========== 日期时间验证方法 ==========
    
    /**
     * 检查日期是否在未来
     */
    public static boolean isFutureDate(LocalDateTime dateTime) {
        return dateTime != null && dateTime.isAfter(LocalDateTime.now());
    }
    
    /**
     * 检查日期是否在过去
     */
    public static boolean isPastDate(LocalDateTime dateTime) {
        return dateTime != null && dateTime.isBefore(LocalDateTime.now());
    }
    
    /**
     * 检查日期范围是否有效（开始日期小于结束日期）
     */
    public static boolean isValidDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null || endDate == null) {
            return false;
        }
        return startDate.isBefore(endDate);
    }
    
    /**
     * 验证日期格式字符串
     */
    public static boolean isValidDateFormat(String dateStr, String pattern) {
        if (isEmpty(dateStr) || isEmpty(pattern)) {
            return false;
        }
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            LocalDateTime.parse(dateStr, formatter);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    // ========== 业务验证方法 ==========
    
    /**
     * 验证ID是否有效（正整数且大于0）
     */
    public static boolean isValidId(Long id) {
        return id != null && id > 0;
    }
    
    /**
     * 验证ID列表是否有效
     */
    public static boolean isValidIds(Collection<Long> ids) {
        if (isEmpty(ids)) {
            return false;
        }
        return ids.stream().allMatch(ValidationUtils::isValidId);
    }
    
    /**
     * 验证分页参数
     */
    public static boolean isValidPageParams(Integer page, Integer size) {
        return page != null && page >= 0 && size != null && size > 0 && size <= 100;
    }
    
    /**
     * 验证排序参数
     */
    public static boolean isValidSortDirection(String direction) {
        if (isEmpty(direction)) {
            return false;
        }
        String upperDirection = direction.toUpperCase();
        return "ASC".equals(upperDirection) || "DESC".equals(upperDirection);
    }
    
    /**
     * 验证文件类型
     */
    public static boolean isValidFileType(String contentType, String[] allowedTypes) {
        if (isEmpty(contentType) || isEmpty(allowedTypes)) {
            return false;
        }
        for (String allowedType : allowedTypes) {
            if (contentType.toLowerCase().contains(allowedType.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 验证文件大小
     */
    public static boolean isValidFileSize(long fileSize, long maxSize) {
        return fileSize > 0 && fileSize <= maxSize;
    }
    
    // ========== 组合验证方法 ==========
    
    /**
     * 验证用户注册信息
     */
    public static class UserValidation {
        
        public static boolean isValidUserInfo(String username, String password, String email) {
            return isValidUsername(username) && isValidPassword(password) && isValidEmail(email);
        }
        
        public static boolean isValidUserProfile(String realName, String phone) {
            boolean realNameValid = isEmpty(realName) || isLengthValid(realName, 1, 50);
            boolean phoneValid = isEmpty(phone) || isValidPhone(phone);
            return realNameValid && phoneValid;
        }
    }
    
    /**
     * 验证项目信息
     */
    public static class ProjectValidation {
        
        public static boolean isValidProjectInfo(String name, String code, String description) {
            boolean nameValid = isNotEmpty(name) && isLengthValid(name, 1, 100);
            boolean codeValid = isValidProjectCode(code);
            boolean descValid = isEmpty(description) || isLengthValid(description, 0, 500);
            return nameValid && codeValid && descValid;
        }
    }
    
    // ========== 私有构造函数 ==========
    
    private ValidationUtils() {
        throw new AssertionError("不能实例化工具类");
    }
}