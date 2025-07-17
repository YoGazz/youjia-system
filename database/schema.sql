-- =====================================================
-- 数据库设计参考文件 - 请勿执行！
-- =====================================================
--
-- 注意：此文件仅作为数据库设计参考，不应在当前瑜伽项目中执行
-- 当前项目使用JPA自动建表，只需要users表
--
-- 文件用途：
-- 1. 数据库设计参考
-- 2. 未来功能扩展的表结构模板
-- 3. 学习复杂数据库设计的示例
--
-- 如需执行，请：
-- 1. 仔细评估是否需要这些表
-- 2. 修改表结构以适应当前项目
-- 3. 手动执行，不要放在src/main/resources/目录下
--
-- =====================================================

-- 项目管理模块
CREATE TABLE projects (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL COMMENT '项目名称',
    code VARCHAR(50) UNIQUE NOT NULL COMMENT '项目编码',
    description TEXT COMMENT '项目描述',
    owner_id BIGINT NOT NULL COMMENT '项目负责人',
    status ENUM('active', 'inactive', 'archived') DEFAULT 'active',
    start_date DATE COMMENT '项目开始日期',
    end_date DATE COMMENT '项目结束日期',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_owner (owner_id),
    INDEX idx_status (status)
);

-- 需求管理
CREATE TABLE requirements (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    project_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL COMMENT '需求标题',
    description TEXT COMMENT '需求描述',
    type ENUM('functional', 'non_functional', 'interface') DEFAULT 'functional',
    priority ENUM('P0', 'P1', 'P2', 'P3') DEFAULT 'P2',
    status ENUM('draft', 'reviewing', 'approved', 'rejected', 'changed') DEFAULT 'draft',
    version VARCHAR(20) DEFAULT '1.0',
    creator_id BIGINT NOT NULL,
    assignee_id BIGINT COMMENT '负责人',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (project_id) REFERENCES projects(id),
    INDEX idx_project_status (project_id, status),
    INDEX idx_assignee (assignee_id)
);

-- 测试用例模块优化
CREATE TABLE test_modules (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    project_id BIGINT NOT NULL,
    parent_id BIGINT COMMENT '父模块ID',
    name VARCHAR(100) NOT NULL COMMENT '模块名称',
    description TEXT COMMENT '模块描述',
    sort_order INT DEFAULT 0 COMMENT '排序',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (project_id) REFERENCES projects(id),
    FOREIGN KEY (parent_id) REFERENCES test_modules(id),
    INDEX idx_project_parent (project_id, parent_id)
);

CREATE TABLE test_cases (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    project_id BIGINT NOT NULL,
    module_id BIGINT NOT NULL,
    requirement_id BIGINT COMMENT '关联需求',
    title VARCHAR(200) NOT NULL COMMENT '用例标题',
    priority ENUM('P0', 'P1', 'P2', 'P3') DEFAULT 'P2',
    type ENUM('functional', 'interface', 'performance', 'security', 'compatibility') DEFAULT 'functional',
    method ENUM('manual', 'auto') DEFAULT 'manual',
    status ENUM('draft', 'reviewing', 'approved', 'deprecated') DEFAULT 'draft',
    precondition TEXT COMMENT '前置条件',
    expected_result TEXT COMMENT '预期结果',
    remark TEXT COMMENT '备注',
    creator_id BIGINT NOT NULL,
    maintainer_id BIGINT COMMENT '维护人',
    is_ai_generated BOOLEAN DEFAULT FALSE,
    ai_confidence DECIMAL(3,2) COMMENT 'AI生成置信度',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (project_id) REFERENCES projects(id),
    FOREIGN KEY (module_id) REFERENCES test_modules(id),
    FOREIGN KEY (requirement_id) REFERENCES requirements(id),
    INDEX idx_project_module (project_id, module_id),
    INDEX idx_status_type (status, type),
    INDEX idx_creator (creator_id)
);

-- 测试步骤
CREATE TABLE test_case_steps (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    case_id BIGINT NOT NULL,
    step_number INT NOT NULL COMMENT '步骤序号',
    action TEXT NOT NULL COMMENT '操作步骤',
    expected TEXT COMMENT '预期结果',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (case_id) REFERENCES test_cases(id) ON DELETE CASCADE,
    UNIQUE KEY uk_case_step (case_id, step_number)
);

-- 测试计划
CREATE TABLE test_plans (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    project_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL COMMENT '计划名称',
    description TEXT COMMENT '计划描述',
    version VARCHAR(20) NOT NULL COMMENT '测试版本',
    environment VARCHAR(50) COMMENT '测试环境',
    start_time DATETIME COMMENT '开始时间',
    end_time DATETIME COMMENT '结束时间',
    status ENUM('not_started', 'in_progress', 'completed', 'cancelled') DEFAULT 'not_started',
    creator_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (project_id) REFERENCES projects(id),
    INDEX idx_project_status (project_id, status),
    INDEX idx_creator (creator_id)
);

-- 测试计划用例关联
CREATE TABLE test_plan_cases (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    plan_id BIGINT NOT NULL,
    case_id BIGINT NOT NULL,
    executor_id BIGINT COMMENT '执行人',
    status ENUM('not_executed', 'passed', 'failed', 'blocked', 'skipped') DEFAULT 'not_executed',
    actual_result TEXT COMMENT '实际结果',
    execution_time DATETIME COMMENT '执行时间',
    duration INT COMMENT '执行耗时(分钟)',
    remark TEXT COMMENT '执行备注',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (plan_id) REFERENCES test_plans(id) ON DELETE CASCADE,
    FOREIGN KEY (case_id) REFERENCES test_cases(id),
    UNIQUE KEY uk_plan_case (plan_id, case_id),
    INDEX idx_executor_status (executor_id, status)
);

-- 缺陷管理优化
CREATE TABLE bugs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    project_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL COMMENT '缺陷标题',
    description TEXT NOT NULL COMMENT '缺陷描述',
    severity ENUM('blocker', 'critical', 'major', 'minor', 'trivial') DEFAULT 'major',
    priority ENUM('urgent', 'high', 'medium', 'low') DEFAULT 'medium',
    status ENUM('new', 'assigned', 'resolved', 'verified', 'closed', 'rejected', 'reopened') DEFAULT 'new',
    type ENUM('functional', 'performance', 'interface', 'compatibility', 'security', 'data') DEFAULT 'functional',
    reporter_id BIGINT NOT NULL COMMENT '报告人',
    assignee_id BIGINT COMMENT '指派人',
    resolver_id BIGINT COMMENT '解决人',
    verifier_id BIGINT COMMENT '验证人',
    found_version VARCHAR(20) COMMENT '发现版本',
    fixed_version VARCHAR(20) COMMENT '修复版本',
    environment VARCHAR(50) COMMENT '发现环境',
    steps_to_reproduce TEXT COMMENT '重现步骤',
    resolution ENUM('fixed', 'wont_fix', 'duplicate', 'invalid', 'works_for_me') COMMENT '解决方案',
    resolution_description TEXT COMMENT '解决描述',
    ai_analysis TEXT COMMENT 'AI分析结果',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    resolved_at TIMESTAMP NULL COMMENT '解决时间',
    verified_at TIMESTAMP NULL COMMENT '验证时间',
    FOREIGN KEY (project_id) REFERENCES projects(id),
    INDEX idx_project_status (project_id, status),
    INDEX idx_assignee_status (assignee_id, status),
    INDEX idx_severity_priority (severity, priority)
);

-- 自动化测试套件
CREATE TABLE automation_suites (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    project_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL COMMENT '套件名称',
    type ENUM('ui', 'api', 'performance', 'unit') DEFAULT 'ui',
    framework ENUM('selenium', 'playwright', 'cypress', 'postman', 'jmeter', 'junit') NOT NULL,
    description TEXT COMMENT '套件描述',
    repository_url VARCHAR(500) COMMENT '代码仓库地址',
    branch VARCHAR(50) DEFAULT 'main' COMMENT '代码分支',
    build_command TEXT COMMENT '构建命令',
    run_command TEXT COMMENT '执行命令',
    status ENUM('active', 'inactive') DEFAULT 'active',
    creator_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (project_id) REFERENCES projects(id),
    INDEX idx_project_type (project_id, type),
    INDEX idx_framework (framework)
);

-- 自动化执行记录
CREATE TABLE automation_executions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    suite_id BIGINT NOT NULL,
    plan_id BIGINT COMMENT '关联测试计划',
    trigger_type ENUM('manual', 'scheduled', 'webhook', 'ci_cd') DEFAULT 'manual',
    trigger_user_id BIGINT COMMENT '触发用户',
    status ENUM('pending', 'running', 'success', 'failed', 'cancelled') DEFAULT 'pending',
    start_time DATETIME COMMENT '开始时间',
    end_time DATETIME COMMENT '结束时间',
    total_cases INT DEFAULT 0 COMMENT '总用例数',
    passed_cases INT DEFAULT 0 COMMENT '通过数',
    failed_cases INT DEFAULT 0 COMMENT '失败数',
    skipped_cases INT DEFAULT 0 COMMENT '跳过数',
    pass_rate DECIMAL(5,2) COMMENT '通过率',
    build_url VARCHAR(500) COMMENT '构建链接',
    report_url VARCHAR(500) COMMENT '报告链接',
    error_message TEXT COMMENT '错误信息',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (suite_id) REFERENCES automation_suites(id),
    FOREIGN KEY (plan_id) REFERENCES test_plans(id),
    INDEX idx_suite_status (suite_id, status),
    INDEX idx_start_time (start_time)
);

-- 测试环境管理
CREATE TABLE test_environments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    project_id BIGINT NOT NULL,
    name VARCHAR(50) NOT NULL COMMENT '环境名称',
    type ENUM('dev', 'test', 'staging', 'prod') DEFAULT 'test',
    url VARCHAR(200) COMMENT '环境地址',
    database_url VARCHAR(200) COMMENT '数据库地址',
    description TEXT COMMENT '环境描述',
    status ENUM('available', 'maintenance', 'unavailable') DEFAULT 'available',
    maintainer_id BIGINT COMMENT '维护人',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (project_id) REFERENCES projects(id),
    INDEX idx_project_type (project_id, type),
    INDEX idx_status (status)
);

-- AI功能简化配置
CREATE TABLE ai_configurations (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    project_id BIGINT NOT NULL,
    provider ENUM('openai', 'claude', 'wenxin', 'tongyi') NOT NULL,
    model_name VARCHAR(50) NOT NULL COMMENT '模型名称',
    api_key VARCHAR(200) NOT NULL COMMENT 'API密钥(加密存储)',
    config JSON COMMENT '其他配置参数',
    status ENUM('active', 'inactive') DEFAULT 'active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (project_id) REFERENCES projects(id),
    INDEX idx_project_provider (project_id, provider)
);

-- AI使用记录
CREATE TABLE ai_usage_logs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    project_id BIGINT NOT NULL,
    feature_type ENUM('case_generation', 'bug_analysis', 'test_optimization', 'code_review') NOT NULL,
    provider ENUM('openai', 'claude', 'wenxin', 'tongyi') NOT NULL,
    model_name VARCHAR(50) NOT NULL,
    prompt_tokens INT DEFAULT 0 COMMENT '输入Token数',
    completion_tokens INT DEFAULT 0 COMMENT '输出Token数',
    total_tokens INT DEFAULT 0 COMMENT '总Token数',
    cost DECIMAL(10,4) DEFAULT 0 COMMENT '费用',
    user_id BIGINT NOT NULL,
    status ENUM('success', 'failed') DEFAULT 'success',
    error_message TEXT COMMENT '错误信息',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (project_id) REFERENCES projects(id),
    INDEX idx_project_feature (project_id, feature_type),
    INDEX idx_user_date (user_id, created_at),
    INDEX idx_cost_date (created_at, cost)
);
