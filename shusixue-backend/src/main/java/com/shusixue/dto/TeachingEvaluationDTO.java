package com.shusixue.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 教学评价DTO
 */
@Data
public class TeachingEvaluationDTO {

    private Long id;

    @NotBlank(message = "评价标题不能为空")
    private String title;

    private LocalDateTime teachingDate;

    private String className;

    private String content;

    @NotNull(message = "教学效果评价不能为空")
    private Integer effectiveness;

    @NotNull(message = "学生参与度不能为空")
    private Integer participation;

    private String highlights;

    private String weaknesses;

    private String improvementPlan;
}