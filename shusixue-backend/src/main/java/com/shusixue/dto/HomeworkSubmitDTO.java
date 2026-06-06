package com.shusixue.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

/**
 * 学生提交作业请求参数
 */
@Data
public class HomeworkSubmitDTO {

    @NotNull(message = "作业ID不能为空")
    private Long homeworkId;

    /**
     * 学生答案：key=题目ID，value=学生答案
     */
    @NotNull(message = "作业答案不能为空")
    private Map<Long, String> answers;
}