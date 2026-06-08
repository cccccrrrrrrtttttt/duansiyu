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


-- ------------------------------
-- 插入更多用户数据（老师和学生）
-- ------------------------------
INSERT INTO sys_user (username, password, real_name, role, status) VALUES
('teacher2', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '王老师', 'TEACHER', 1),
('teacher3', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '李老师', 'TEACHER', 1),
('student2', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '赵同学', 'STUDENT', 1),
('student3', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '孙同学', 'STUDENT', 1),
('student4', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '周同学', 'STUDENT', 1)
ON DUPLICATE KEY UPDATE username=username;

-- ------------------------------
-- 插入知识点数据
-- ------------------------------
INSERT INTO knowledge_point (name, subject, parent_id, sort) VALUES
('函数与极限', '高等数学', 0, 1),
('导数与微分', '高等数学', 0, 2),
('不定积分', '高等数学', 0, 3),
('矩阵运算', '线性代数', 0, 1),
('线性方程组', '线性代数', 0, 2),
('概率论基础', '概率论', 0, 1),
('随机变量', '概率论', 0, 2),
('极限运算法则', '高等数学', 1, 1),
('复合函数求导', '高等数学', 2, 1),
('换元积分法', '高等数学', 3, 1)
ON DUPLICATE KEY UPDATE name=name;

-- ------------------------------
-- 插入题目数据
-- ------------------------------
INSERT INTO question (title, type, options, answer, analysis, difficulty, subject, knowledge_point_ids, creator_id) VALUES
('lim(x→0) sinx/x = ?', 'SINGLE_CHOICE', '["0","1","-1","∞"]', 'B', '这是重要极限公式，lim(x→0) sinx/x = 1', 'EASY', '高等数学', '[1]', 2),
('函数f(x)=x²的导数是？', 'SINGLE_CHOICE', '["2x","x²","2","x"]', 'A', '根据幂函数求导公式，(xⁿ)'' = nxⁿ⁻¹', 'EASY', '高等数学', '[2]', 2),
('以下哪些是奇函数？', 'MULTIPLE_CHOICE', '["sinx","cosx","x³","x²"]', '["A","C"]', 'sinx和x³满足f(-x)=-f(x)，是奇函数；cosx和x²是偶函数', 'MEDIUM', '高等数学', '[1]', 2),
('∫x²dx = ____', 'FILL_BLANK', '[]', '(1/3)x³+C', '根据幂函数积分公式，∫xⁿdx = xⁿ⁺¹/(n+1) + C', 'EASY', '高等数学', '[3]', 2),
('矩阵A=[1,2;3,4]的行列式值为？', 'SINGLE_CHOICE', '["-2","2","-4","4"]', 'A', 'det(A) = 1×4 - 2×3 = 4-6 = -2', 'MEDIUM', '线性代数', '[4]', 3),
('设随机变量X~N(0,1)，P(X<0)=？', 'SINGLE_CHOICE', '["0.25","0.5","0.75","1"]', 'B', '标准正态分布关于x=0对称，故P(X<0)=0.5', 'MEDIUM', '概率论', '[7]', 3),
('求极限lim(x→∞) 1/x = 0，请说明理由', 'ESSAY', '[]', '当x趋于无穷大时，1/x趋于0', '本题考查极限的定义理解', 'EASY', '高等数学', '[1]', 2),
('已知y=ln(sinx)，求y''', 'ESSAY', '[]', 'y'' = cosx/sinx = cotx', '利用复合函数求导法则', 'HARD', '高等数学', '[2,9]', 2)
ON DUPLICATE KEY UPDATE title=title;

-- ------------------------------
-- 插入作业数据
-- ------------------------------
INSERT INTO homework (title, description, question_ids, creator_id, start_time, end_time, status) VALUES
('高等数学第一次作业', '极限与连续基础练习题', '[1,3,7]', 2, '2026-03-01 00:00:00', '2026-03-15 23:59:59', 'PUBLISHED'),
('导数与微分练习', '导数计算专项训练', '[2,8]', 2, '2026-03-10 00:00:00', '2026-03-25 23:59:59', 'PUBLISHED'),
('线性代数基础作业', '矩阵与行列式练习', '[5]', 3, '2026-03-15 00:00:00', '2026-04-01 23:59:59', 'PUBLISHED'),
('概率论入门作业', '概率论基础概念练习', '[6]', 3, '2026-04-01 00:00:00', '2026-04-15 23:59:59', 'DRAFT')
ON DUPLICATE KEY UPDATE title=title;

-- ------------------------------
-- 插入教学评价数据
-- ------------------------------
INSERT INTO teaching_evaluation (title, teaching_date, class_name, content, effectiveness, participation, highlights, weaknesses, improvement_plan, creator_id) VALUES
('极限概念教学', '2026-03-02 08:00:00', '数学与应用数学2101班', '讲解了极限的定义和基本运算法则，通过多种实例帮助学生理解', 4, 4, '使用图形结合的方法讲解，学生理解效果较好', '部分学生对ε-δ语言掌握不够', '下一节课增加更多互动练习', 2),
('导数公式推导', '2026-03-09 08:00:00', '数学与应用数学2101班', '推导了常见函数的导数公式，讲解了复合函数求导法则', 5, 4, '课堂练习设计合理，学生参与度高', '时间安排略紧', '适当调整课时安排', 2),
('矩阵运算教学', '2026-03-16 10:00:00', '信息与计算科学2101班', '讲解了矩阵的加法、乘法及转置运算', 4, 5, '实例丰富，学生兴趣浓厚', '乘法运算法则部分学生对顺序易混淆', '设计更多针对性练习', 3)
ON DUPLICATE KEY UPDATE title=title;