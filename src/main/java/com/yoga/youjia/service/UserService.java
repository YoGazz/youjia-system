package com.yoga.youjia.service;

import com.yoga.youjia.entity.User;
import com.yoga.youjia.repository.UserRepository;
import org.hibernate.annotations.NotFound;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.NOT_FOUND;

/**
 * 用户服务类
 *
 * 这个类负责处理用户相关的业务逻辑，包括用户注册、登录等功能
 * 在Spring MVC架构中，Service层位于Controller层和Repository层之间，
 * 主要负责业务逻辑的处理和事务管理
 */
@Service  // @Service注解的作用：
          // 1. 标识这是一个Spring服务组件（Service层）
          // 2. Spring容器会自动扫描并创建这个类的实例（Bean）
          // 3. 可以被其他组件（如Controller）通过依赖注入使用
          // 4. 是@Component注解的特化版本，专门用于Service层
public class UserService {

    /**
     * 用户数据访问对象
     *
     * UserRepository是数据访问层，负责与数据库交互
     * 通过这个对象可以执行用户相关的数据库操作（增删改查）
     */
    @Autowired  // @Autowired注解的作用：
                // 1. 自动依赖注入，Spring会自动找到UserRepository的实现类并注入
                // 2. 不需要手动创建UserRepository对象
                // 3. Spring容器管理对象的生命周期
                // 4. 实现了控制反转（IoC），降低了代码耦合度
    private UserRepository userRepository;

    /**
     * 用户注册方法
     *
     * 这个方法处理用户注册的完整业务流程：
     * 1. 验证用户名和邮箱的唯一性
     * 2. 设置用户的默认状态和角色
     * 3. 设置创建时间
     * 4. 保存用户到数据库
     *
     * @param user 要注册的用户对象，包含用户名、密码、邮箱等信息
     * @return User 注册成功后的用户对象（包含数据库生成的ID等信息）
     * @throws IllegalArgumentException 当用户名或邮箱已存在时抛出此异常
     */
    public User register(User user){

        // 业务逻辑1：检查用户名是否已存在
        // userRepository.findByUsername() 返回Optional<User>对象
        // Optional.isPresent() 检查是否有值，如果有值说明用户名已存在
        if (userRepository.findByUsername(user.getUsername()).isPresent()){
            // 抛出运行时异常，告知调用者用户名重复
            throw new IllegalArgumentException("用户名已存在");
        }

        // 业务逻辑2：检查邮箱是否已存在
        // 同样使用Optional来安全地处理可能为空的查询结果
        if (userRepository.findByEmail(user.getEmail()).isPresent()){
            // 抛出运行时异常，告知调用者邮箱重复
            throw new IllegalArgumentException("邮箱已存在");
        }

        // 业务逻辑3：设置用户默认值
        // 使用枚举类来设置状态，确保数据的一致性和类型安全
        user.setStatus(User.Status.ENABLE.getDescription());  // 设置用户状态为"启用"
        user.setRole(User.Roles.USER.getDescription());       // 设置用户角色为"普通用户"
        user.setCreatedAt(java.time.LocalDateTime.now());     // 设置创建时间为当前时间

        // 业务逻辑4：保存用户到数据库
        // userRepository.save() 会执行SQL INSERT语句
        // 返回保存后的用户对象（包含数据库自动生成的ID）
        return userRepository.save(user);
    }

    /**
     * 用户登录方法（待实现）
     *
     * 这个方法将来会处理用户登录的业务逻辑：
     * 1. 验证用户名/邮箱和密码
     * 2. 检查用户状态是否为启用
     * 3. 生成登录令牌或会话
     * 4. 记录登录日志
     *
     * 注意：目前这个方法还没有实现具体功能，只是一个占位方法
     *
     * @return User 登录成功的用户对象，目前返回null
     *
     * TODO: 实现登录逻辑
     * - 添加用户名/密码参数
     * - 实现密码验证（需要密码加密）
     * - 添加异常处理（用户不存在、密码错误、账户被禁用等）
     * - 集成Spring Security进行安全认证
     */
    public User login(String username, String password){
        // 业务逻辑1：查找用户
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "用户不存在"));

        // 业务逻辑2：验证密码
        if(!user.getPassword().equals(password)){
            throw new IllegalArgumentException("密码错误");
        }

        // 业务逻辑3：检查用户状态
        if(user.getStatus().equals(User.Status.DISABLE)){
            throw new IllegalArgumentException("用户已被禁用");
        }

        // 业务逻辑4：返回用户信息
        return user;
    }
}
