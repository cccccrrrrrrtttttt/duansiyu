package com.shusixue.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 题库实体类，对应question表
 */
@Data
@TableName("question")
public class Question {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 题目内容
     */
    private String title;

    /**
     * 题型：SINGLE_CHOICE/MULTIPLE_CHOICE/FILL_BLANK/ESSAY
     */
    private String type;

    /**
     * 选项（JSON格式，选择题用）
     */
    private String options;

    /**
     * 答案
     */
    private String answer;

    /**
     * 解析
     */
    private String analysis;

    /**
     * 难度：EASY/MEDIUM/HARD
     */
    private String difficulty;

    /**
     * 关联知识点ID（逗号分隔）
     */
    private String knowledgePointIds;

    /**
     * 科目
     */
    private String subject;

    /**
     * 创建人ID
     */
    private Long creatorId;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}