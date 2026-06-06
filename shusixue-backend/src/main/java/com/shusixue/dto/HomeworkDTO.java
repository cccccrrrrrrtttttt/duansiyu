package com.shusixue.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 发布/修改作业请求参数
 */
@Data
public class HomeworkDTO {

    private Long id;

    @NotBlank(message = "作业标题不能为空")
    private String title;

    private String description;

    @NotEmpty(message = "作业题目不能为空")
    private List<Long> questionIds;

    @NotNull(message = "作业开始时间不能为空")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime startTime;

    @NotNull(message = "作业截止时间不能为空")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime endTime;

    /**
     * 发布状态：true直接发布，false存为草稿
     */
    private Boolean publishNow = false;
}