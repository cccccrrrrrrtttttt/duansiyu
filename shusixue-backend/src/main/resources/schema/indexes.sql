-- ==============================================
-- 数思学-高校数学教学辅助平台 - 数据库索引优化
-- 适用版本: MySQL 8.0+
-- ==============================================

-- ------------------------------
-- sys_user 表索引
-- ------------------------------

-- 用户登录索引：用户名+密码（高频查询）
CREATE INDEX idx_sys_user_username ON sys_user(username);

-- 角色索引：按角色筛选用户（高频查询）
CREATE INDEX idx_sys_user_role ON sys_user(role);

-- 状态索引：筛选活跃用户
CREATE INDEX idx_sys_user_status ON sys_user(status);

-- 联合索引：角色+状态（批量查询学生/教师）
CREATE INDEX idx_sys_user_role_status ON sys_user(role, status);

-- ------------------------------
-- knowledge_point 表索引
-- ------------------------------

-- 科目索引：按科目查询知识点树（高频查询）
CREATE INDEX idx_knowledge_point_subject ON knowledge_point(subject);

-- 父节点索引：查询子知识点
CREATE INDEX idx_knowledge_point_parent_id ON knowledge_point(parent_id);

-- 排序索引：知识点排序
CREATE INDEX idx_knowledge_point_sort ON knowledge_point(sort);

-- 联合索引：科目+父节点（树形结构查询）
CREATE INDEX idx_knowledge_point_subject_parent ON knowledge_point(subject, parent_id);

-- ------------------------------
-- question 表索引
-- ------------------------------

-- 创建人索引：查询教师创建的题目
CREATE INDEX idx_question_creator_id ON question(creator_id);

-- 科目索引：按科目筛选题目（高频查询）
CREATE INDEX idx_question_subject ON question(subject);

-- 类型索引：选择题/填空题/解答题
CREATE INDEX idx_question_type ON question(type);

-- 难度索引：简单/中等/困难
CREATE INDEX idx_question_difficulty ON question(difficulty);

-- 创建时间索引：最新题目排序
CREATE INDEX idx_question_create_time ON question(create_time);

-- 联合索引：科目+类型+难度（多维筛选）
CREATE INDEX idx_question_subject_type_diff ON question(subject, type, difficulty);

-- ------------------------------
-- homework 表索引
-- ------------------------------

-- 创建人索引：查询教师发布的作业（高频查询）
CREATE INDEX idx_homework_creator_id ON homework(creator_id);

-- 状态索引：草稿/已发布/已结束（高频查询）
CREATE INDEX idx_homework_status ON homework(status);

-- 科目索引：按科目筛选作业
CREATE INDEX idx_homework_subject ON homework(subject);

-- 创建时间索引：最新作业排序
CREATE INDEX idx_homework_create_time ON homework(create_time);

-- 联合索引：创建人+状态（教师作业列表）
CREATE INDEX idx_homework_creator_status ON homework(creator_id, status);

-- ------------------------------
-- homework_submission 表索引（核心高频表）
-- ------------------------------

-- 用户ID索引：查询学生提交记录（高频查询）
CREATE INDEX idx_homework_submission_user_id ON homework_submission(user_id);

-- 作业ID索引：查询作业的所有提交（高频查询）
CREATE INDEX idx_homework_submission_homework_id ON homework_submission(homework_id);

-- 状态索引：待批改/已批改
CREATE INDEX idx_homework_submission_status ON homework_submission(status);

-- 创建时间索引：最新提交排序
CREATE INDEX idx_homework_submission_create_time ON homework_submission(create_time);

-- 联合索引：作业ID+用户ID（唯一约束，防止重复提交）
CREATE UNIQUE INDEX uk_homework_submission_homework_user ON homework_submission(homework_id, user_id);

-- 联合索引：作业ID+状态（批改列表）
CREATE INDEX idx_homework_submission_homework_status ON homework_submission(homework_id, status);

-- 联合索引：用户ID+状态（学生提交列表）
CREATE INDEX idx_homework_submission_user_status ON homework_submission(user_id, status);

-- ------------------------------
-- teaching_evaluation 表索引
-- ------------------------------

-- 用户ID索引：查询用户的评价
CREATE INDEX idx_teaching_evaluation_user_id ON teaching_evaluation(user_id);

-- 作业ID索引：查询作业的评价
CREATE INDEX idx_teaching_evaluation_homework_id ON teaching_evaluation(homework_id);

-- 创建时间索引：最新评价排序
CREATE INDEX idx_teaching_evaluation_create_time ON teaching_evaluation(create_time);

-- ==============================================
-- 索引优化说明：
-- 1. 覆盖索引：对于频繁查询的字段组合，创建联合索引
-- 2. 唯一索引：保证数据唯一性（如作业提交）
-- 3. 前缀索引：对于长文本字段（如题目内容），可考虑使用前缀索引
-- 4. 索引维护：定期分析索引使用情况，删除未使用的索引
-- ==============================================
