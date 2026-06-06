package com.shusixue.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 题目操作请求参数
 */
@Data
public class QuestionDTO {

    private Long id;

    @NotBlank(message = "题目内容不能为空")
    private String title;

    @NotBlank(message = "题型不能为空")
    private String type;

    private String options;

    @NotBlank(message = "答案不能为空")
    private String answer;

    private String analysis;

    @NotBlank(message = "难度不能为空")
    private String difficulty;

    private String knowledgePointIds;

    @NotBlank(message = "科目不能为空")
    private String subject;
}