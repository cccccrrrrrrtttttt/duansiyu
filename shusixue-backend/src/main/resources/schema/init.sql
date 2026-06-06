-- ==============================================
-- 数思学-高校数学教学辅助平台 - 完整建表脚本
-- 适用版本: MySQL 8.0+
-- ==============================================

CREATE DATABASE IF NOT EXISTS shusixue DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE shusixue;

-- ------------------------------
-- 用户表
-- ------------------------------
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    username VARCHAR(100) NOT NULL COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码（加密存储）',
    real_name VARCHAR(100) DEFAULT NULL COMMENT '真实姓名',
    role VARCHAR(20) NOT NULL DEFAULT 'STUDENT' COMMENT '角色：STUDENT/TEACHER/ADMIN',
    email VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    phone VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    avatar VARCHAR(500) DEFAULT NULL COMMENT '头像',
    status INT DEFAULT 1 COMMENT '状态：0禁用 1正常',
    deleted INT DEFAULT 0 COMMENT '逻辑删除：0未删除 1已删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_username (username),
    INDEX idx_role (role),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ------------------------------
-- 知识点表
-- ------------------------------
CREATE TABLE IF NOT EXISTS knowledge_point (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    name VARCHAR(200) NOT NULL COMMENT '知识点名称',
    subject VARCHAR(50) NOT NULL COMMENT '科目：高等数学/线性代数/概率论/数学建模',
    parent_id BIGINT DEFAULT 0 COMMENT '父知识点ID，0表示顶级',
    sort INT DEFAULT 0 COMMENT '排序',
    deleted INT DEFAULT 0 COMMENT '逻辑删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_subject (subject),
    INDEX idx_parent_id (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='知识点表';

-- ------------------------------
-- 题目表
-- ------------------------------
CREATE TABLE IF NOT EXISTS question (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    title TEXT NOT NULL COMMENT '题目内容',
    type VARCHAR(30) NOT NULL COMMENT '题型：SINGLE_CHOICE/MULTIPLE_CHOICE/FILL_BLANK/ESSAY',
    options TEXT DEFAULT NULL COMMENT '选项（JSON格式）',
    answer TEXT DEFAULT NULL COMMENT '答案',
    analysis TEXT DEFAULT NULL COMMENT '解析',
    difficulty VARCHAR(10) DEFAULT 'MEDIUM' COMMENT '难度：EASY/MEDIUM/HARD',
    knowledge_point_ids VARCHAR(500) DEFAULT NULL COMMENT '关联知识点ID列表（JSON数组）',
    subject VARCHAR(50) DEFAULT NULL COMMENT '科目',
    creator_id BIGINT DEFAULT NULL COMMENT '创建人ID',
    deleted INT DEFAULT 0 COMMENT '逻辑删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_subject (subject),
    INDEX idx_type (type),
    INDEX idx_difficulty (difficulty),
    INDEX idx_creator_id (creator_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='题目表';

-- ------------------------------
-- 作业表
-- ------------------------------
CREATE TABLE IF NOT EXISTS homework (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    title VARCHAR(200) NOT NULL COMMENT '作业标题',
    description TEXT DEFAULT NULL COMMENT '作业描述',
    question_ids TEXT DEFAULT NULL COMMENT '题目ID列表（JSON数组）',
    creator_id BIGINT DEFAULT NULL COMMENT '创建人ID（教师）',
    class_id BIGINT DEFAULT NULL COMMENT '班级ID',
    start_time DATETIME DEFAULT NULL COMMENT '作业开始时间',
    end_time DATETIME DEFAULT NULL COMMENT '作业截止时间',
    status VARCHAR(20) DEFAULT 'DRAFT' COMMENT '状态：DRAFT/PUBLISHED/ENDED',
    deleted INT DEFAULT 0 COMMENT '逻辑删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_creator_id (creator_id),
    INDEX idx_status (status),
    INDEX idx_start_time (start_time),
    INDEX idx_end_time (end_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='作业表';

-- ------------------------------
-- 作业提交表
-- ------------------------------
CREATE TABLE IF NOT EXISTS homework_submission (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    homework_id BIGINT NOT NULL COMMENT '作业ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    answers TEXT DEFAULT NULL COMMENT '学生答案（JSON格式）',
    score DECIMAL(5,2) DEFAULT NULL COMMENT '最终得分',
    correct_count INT DEFAULT 0 COMMENT '正确题数',
    total_count INT DEFAULT 0 COMMENT '总题数',
    submit_time DATETIME DEFAULT NULL COMMENT '提交时间',
    feedback TEXT DEFAULT NULL COMMENT '教师评语',
    status VARCHAR(20) DEFAULT 'NOT_SUBMITTED' COMMENT '提交状态：NOT_SUBMITTED/SUBMITTED/GRADED',
    deleted INT DEFAULT 0 COMMENT '逻辑删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_homework_id (homework_id),
    INDEX idx_student_id (student_id),
    INDEX idx_status (status),
    UNIQUE KEY uk_homework_student (homework_id, student_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='作业提交表';

-- ------------------------------
-- 教学评价表
-- ------------------------------
CREATE TABLE IF NOT EXISTS teaching_evaluation (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    title VARCHAR(200) NOT NULL COMMENT '评价标题',
    teaching_date DATETIME DEFAULT NULL COMMENT '教学日期',
    class_name VARCHAR(100) DEFAULT NULL COMMENT '教学班级',
    content TEXT DEFAULT NULL COMMENT '教学内容',
    effectiveness INT DEFAULT 0 COMMENT '教学效果评价（1-5分）',
    participation INT DEFAULT 0 COMMENT '学生参与度（1-5分）',
    highlights TEXT DEFAULT NULL COMMENT '教学亮点',
    weaknesses TEXT DEFAULT NULL COMMENT '存在不足',
    improvement_plan TEXT DEFAULT NULL COMMENT '改进计划',
    creator_id BIGINT DEFAULT NULL COMMENT '创建人ID（教师）',
    deleted INT DEFAULT 0 COMMENT '逻辑删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_creator_id (creator_id),
    INDEX idx_teaching_date (teaching_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='教学评价表';

-- ------------------------------
-- 系统操作日志表
-- ------------------------------
CREATE TABLE IF NOT EXISTS sys_operation_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '操作人ID',
    user_name VARCHAR(100) NOT NULL COMMENT '操作人名称',
    role VARCHAR(20) NOT NULL COMMENT '角色',
    request_uri VARCHAR(500) NOT NULL COMMENT '请求接口路径',
    request_method VARCHAR(10) NOT NULL COMMENT '请求方法',
    request_ip VARCHAR(50) DEFAULT NULL COMMENT '请求IP地址',
    operation_type VARCHAR(50) DEFAULT NULL COMMENT '操作类型',
    operation_desc VARCHAR(500) DEFAULT NULL COMMENT '操作描述',
    request_params TEXT DEFAULT NULL COMMENT '请求参数',
    response_result TEXT DEFAULT NULL COMMENT '响应结果',
    execution_time BIGINT DEFAULT NULL COMMENT '执行时间（毫秒）',
    status VARCHAR(20) DEFAULT 'SUCCESS' COMMENT '操作状态（SUCCESS/FAIL）',
    error_message TEXT DEFAULT NULL COMMENT '错误信息',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user_id (user_id),
    INDEX idx_create_time (create_time),
    INDEX idx_operation_type (operation_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统操作日志表';

-- ------------------------------
-- 插入默认管理员账户（密码: admin123）
-- ------------------------------
INSERT INTO sys_user (username, password, real_name, role, status) VALUES
('admin', '\\\.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '系统管理员', 'ADMIN', 1),
('teacher1', '\\\.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '张老师', 'TEACHER', 1),
('student1', '\\\.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '李同学', 'STUDENT', 1)
ON DUPLICATE KEY UPDATE username=username;

