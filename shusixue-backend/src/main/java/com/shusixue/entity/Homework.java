package com.shusixue.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 作业实体类，对应homework表
 */
@Data
@TableName("homework")
public class Homework {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 作业标题
     */
    private String title;

    /**
     * 作业描述
     */
    private String description;

    /**
     * 题目ID列表（JSON格式数组）
     */
    private String questionIds;

    /**
     * 创建人ID（教师）
     */
    private Long creatorId;

    /**
     * 班级ID（后续扩展用，当前先预留）
     */
    private Long classId;

    /**
     * 作业开始时间
     */
    private LocalDateTime startTime;

    /**
     * 作业截止时间
     */
    private LocalDateTime endTime;

    /**
     * 作业状态：DRAFT草稿/PUBLISHED已发布/ENDED已结束
     */
    private String status;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}