package com.shusixue.dto;

import lombok.Data;

/**
 * 题目分页查询请求参数
 */
@Data
public class QuestionQueryDTO {

    /**
     * 当前页码
     */
    private Integer pageNum = 1;

    /**
     * 每页条数
     */
    private Integer pageSize = 10;

    /**
     * 关键词（模糊搜索题目内容）
     */
    private String keyword;

    /**
     * 科目
     */
    private String subject;

    /**
     * 题型
     */
    private String type;

    /**
     * 难度
     */
    private String difficulty;

    /**
     * 知识点ID
     */
    private Long knowledgePointId;
}