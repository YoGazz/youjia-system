package com.yoga.youjia.repository;

import com.yoga.youjia.entity.User;
import com.yoga.youjia.common.enums.UserStatus;
import com.yoga.youjia.common.enums.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
 * 泛型参数说明：
 * - User: 实体类型，表示这个Repository操作User实体
 * - Long: 主键类型，User实体的主键id是Long类型
 */
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * 根据用户名查找用户
     * @param username
     * @return
     */
    Optional<User> findByUsername(String username);

    /**
     * 根据邮箱查找用户
     * @param email
     * @return
     */
    Optional<User> findByEmail(String email);

    /**
     * 检查用户名是否已存在
     * @param username
     * @return
     */
    boolean existsByUsername(String username);

    /**
     * 检查邮箱是否已存在
     * @param email
     * @return
     */
    boolean existsByEmail(String email);

    // 注意：这个接口不需要实现类！
    // Spring Data JPA会在运行时自动创建实现类
    // 实现类会根据方法名自动生成对应的SQL查询
    // 这就是Spring Data JPA的"约定优于配置"理念

    /**
     * 根据条件分页查询用户
     *
     * @param realName 用户姓名（模糊查询）
     * @param status 用户状态
     * @param role 用户角色
     * @param pageable 分页参数
     * @return 分页用户列表
     */
    @Query("SELECT u FROM User u WHERE " +
           "(:realName IS NULL OR u.realName LIKE %:realName%) AND " +
           "(:status IS NULL OR u.status = :status) AND " +
           "(:role IS NULL OR u.role = :role)")
    Page<User> findByConditions(
            @Param("realName") String realName,
            @Param("status") UserStatus status,
            @Param("role") UserRole role,
            Pageable pageable);
}
