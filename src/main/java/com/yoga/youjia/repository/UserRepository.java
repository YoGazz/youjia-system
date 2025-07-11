package com.yoga.youjia.repository;

import com.yoga.youjia.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 用户数据访问层接口
 *
 * 这是用户实体的数据访问层（Repository/DAO层），负责与数据库进行交互。
 * 继承了Spring Data JPA的JpaRepository接口，自动获得了基本的CRUD操作。
 *
 * Repository层的职责：
 * - 封装数据访问逻辑
 * - 提供数据库操作方法
 * - 隔离业务逻辑和数据访问逻辑
 * - 提供数据持久化服务
 *
 * Spring Data JPA的优势：
 * - 自动实现基本CRUD操作
 * - 通过方法名自动生成查询
 * - 支持自定义查询
 * - 减少样板代码
 *
 * 泛型参数说明：
 * - User: 实体类型，表示这个Repository操作User实体
 * - Long: 主键类型，User实体的主键id是Long类型
 */
public interface UserRepository extends JpaRepository<User, Long> {
    // JpaRepository<User, Long>继承关系：
    // JpaRepository -> PagingAndSortingRepository -> CrudRepository -> Repository
    //
    // 自动提供的方法包括：
    // - save(User user): 保存或更新用户
    // - findById(Long id): 根据ID查找用户
    // - findAll(): 查找所有用户
    // - deleteById(Long id): 根据ID删除用户
    // - count(): 统计用户总数
    // - existsById(Long id): 检查ID是否存在
    // 还有分页、排序等高级功能

    /**
     * 根据用户名查找用户
     *
     * 这是一个Spring Data JPA的查询方法，通过方法名自动生成查询。
     * Spring Data JPA会解析方法名并生成对应的SQL查询。
     *
     * 方法名解析规则：
     * - findBy: 查询前缀，表示这是一个查找方法
     * - Username: 实体类中的字段名（首字母大写）
     * - 生成的SQL: SELECT * FROM users WHERE username = ?
     *
     * 使用场景：
     * - 用户登录时验证用户名
     * - 检查用户名是否已存在
     * - 根据用户名获取用户信息
     *
     * @param username 要查找的用户名
     * @return Optional<User> 包装的用户对象
     *         - 如果找到用户，Optional.isPresent()返回true
     *         - 如果没找到，Optional.isEmpty()返回true
     *         - 使用Optional避免了空指针异常
     */
    Optional<User> findByUsername(String username);

    /**
     * 根据邮箱查找用户
     *
     * 通过邮箱地址查找用户，支持邮箱登录功能。
     *
     * 方法名解析：
     * - findBy: 查询前缀
     * - Email: 对应User实体中的email字段
     * - 生成的SQL: SELECT * FROM users WHERE email = ?
     *
     * 使用场景：
     * - 邮箱登录
     * - 找回密码功能
     * - 邮箱唯一性验证
     * - 用户信息查询
     *
     * @param email 要查找的邮箱地址
     * @return Optional<User> 包装的用户对象
     *         - Optional提供了安全的空值处理
     *         - 避免了传统的null检查
     */
    Optional<User> findByEmail(String email);

    /**
     * 检查用户名是否已存在
     *
     * 这是一个存在性检查方法，只返回boolean值，不返回具体数据。
     * 相比findByUsername()更高效，因为不需要加载完整的用户对象。
     *
     * 方法名解析：
     * - existsBy: 存在性查询前缀
     * - Username: 对应User实体中的username字段
     * - 生成的SQL: SELECT COUNT(*) > 0 FROM users WHERE username = ?
     *
     * 性能优势：
     * - 只查询是否存在，不加载完整对象
     * - 数据库只需要返回计数结果
     * - 减少内存使用和网络传输
     *
     * 使用场景：
     * - 用户注册时检查用户名重复
     * - 表单验证
     * - 业务逻辑判断
     *
     * @param username 要检查的用户名
     * @return boolean true表示用户名已存在，false表示不存在
     */
    boolean existsByUsername(String username);

    /**
     * 检查邮箱是否已存在
     *
     * 检查指定邮箱是否已被其他用户注册。
     * 这是一个高效的存在性查询方法。
     *
     * 方法名解析：
     * - existsBy: 存在性查询前缀
     * - Email: 对应User实体中的email字段
     * - 生成的SQL: SELECT COUNT(*) > 0 FROM users WHERE email = ?
     *
     * 应用场景：
     * - 用户注册时验证邮箱唯一性
     * - 防止重复邮箱注册
     * - 邮箱格式验证后的业务验证
     * - 找回密码前的邮箱验证
     *
     * 最佳实践：
     * - 在Service层调用此方法进行业务验证
     * - 结合@Email注解进行格式验证
     * - 在数据库层面也设置unique约束作为最后防线
     *
     * @param email 要检查的邮箱地址
     * @return boolean true表示邮箱已存在，false表示不存在
     */
    boolean existsByEmail(String email);

    // 注意：这个接口不需要实现类！
    // Spring Data JPA会在运行时自动创建实现类
    // 实现类会根据方法名自动生成对应的SQL查询
    // 这就是Spring Data JPA的"约定优于配置"理念
}
