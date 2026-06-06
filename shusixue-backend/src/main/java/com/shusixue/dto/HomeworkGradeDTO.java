package com.shusixue.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 教师批改作业请求参数
 */
@Data
public class HomeworkGradeDTO {

    @NotNull(message = "提交记录ID不能为空")
    private Long submissionId;

    @NotNull(message = "最终得分不能为空")
    private BigDecimal score;
}