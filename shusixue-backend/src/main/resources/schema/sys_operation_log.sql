-- ==============================================
-- 数思学-高校数学教学辅助平台 - 操作日志表
-- ==============================================

CREATE TABLE IF NOT EXISTS sys_operation_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '操作人ID',
    user_name VARCHAR(100) NOT NULL COMMENT '操作人名称',
    role VARCHAR(20) NOT NULL COMMENT '角色（ADMIN/TEACHER/STUDENT）',
    request_uri VARCHAR(500) NOT NULL COMMENT '请求接口路径',
    request_method VARCHAR(10) NOT NULL COMMENT '请求方法（GET/POST/PUT/DELETE）',
    request_ip VARCHAR(50) COMMENT '请求IP地址',
    operation_type VARCHAR(50) COMMENT '操作类型（如：登录、发布作业、提交作业）',
    operation_desc VARCHAR(500) COMMENT '操作描述',
    request_params TEXT COMMENT '请求参数（JSON格式）',
    response_result TEXT COMMENT '响应结果（JSON格式）',
    execution_time BIGINT COMMENT '执行时间（毫秒）',
    status VARCHAR(20) NOT NULL DEFAULT 'SUCCESS' COMMENT '操作状态（SUCCESS/FAIL）',
    error_message TEXT COMMENT '错误信息',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user_id (user_id),
    INDEX idx_create_time (create_time),
    INDEX idx_operation_type (operation_type),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统操作日志表';
