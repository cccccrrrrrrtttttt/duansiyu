package com.shusixue.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 学生作业提交实体类，对应homework_submission表
 */
@Data
@TableName("homework_submission")
public class HomeworkSubmission {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 作业ID
     */
    private Long homeworkId;

    /**
     * 学生ID
     */
    private Long studentId;

    /**
     * 学生答案（JSON格式，key:题目ID, value:学生答案）
     */
    private String answers;

    /**
     * 最终得分
     */
    private BigDecimal score;

    /**
     * 正确题数
     */
    private Integer correctCount;

    /**
     * 总题数
     */
    private Integer totalCount;

    /**
     * 提交时间
     */
    private LocalDateTime submitTime;

    /**
     * 提交状态：NOT_SUBMITTED未提交/SUBMITTED已提交/GRADED已批改
     */
    private String status;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}