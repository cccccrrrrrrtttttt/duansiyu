package com.shusixue.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 教学评价实体类，对应teaching_evaluation表
 */
@Data
@TableName("teaching_evaluation")
public class TeachingEvaluation {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 评价标题
     */
    private String title;

    /**
     * 教学日期
     */
    private LocalDateTime teachingDate;

    /**
     * 教学班级
     */
    private String className;

    /**
     * 教学内容
     */
    private String content;

    /**
     * 教学效果评价（1-5分）
     */
    private Integer effectiveness;

    /**
     * 学生参与度（1-5分）
     */
    private Integer participation;

    /**
     * 教学亮点
     */
    private String highlights;

    /**
     * 存在不足
     */
    private String weaknesses;

    /**
     * 改进计划
     */
    private String improvementPlan;

    /**
     * 创建人ID（教师ID）
     */
    private Long creatorId;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}