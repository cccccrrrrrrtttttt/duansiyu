package com.shusixue.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 系统操作日志实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("sys_operation_log")
public class SysOperationLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 操作人ID
     */
    private Long userId;

    /**
     * 操作人名称
     */
    private String userName;

    /**
     * 角色（ADMIN/TEACHER/STUDENT）
     */
    private String role;

    /**
     * 请求接口路径
     */
    private String requestUri;

    /**
     * 请求方法（GET/POST/PUT/DELETE）
     */
    private String requestMethod;

    /**
     * 请求IP地址
     */
    private String requestIp;

    /**
     * 操作类型（如：登录、发布作业、提交作业）
     */
    private String operationType;

    /**
     * 操作描述
     */
    private String operationDesc;

    /**
     * 请求参数（JSON格式）
     */
    private String requestParams;

    /**
     * 响应结果（JSON格式）
     */
    private String responseResult;

    /**
     * 执行时间（毫秒）
     */
    private Long executionTime;

    /**
     * 操作状态（SUCCESS/FAIL）
     */
    private String status;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
