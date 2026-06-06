package com.shusixue.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * 知识点操作请求参数
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class KnowledgePointDTO {

    private Long id;

    private String name;

    private String title;

    @NotBlank(message = "科目不能为空")
    private String subject;

    private Long parentId;

    @NotNull(message = "排序不能为空")
    private Integer sort;

    public String getEffectiveName() {
        return name != null && !name.isEmpty() ? name : title;
    }
}