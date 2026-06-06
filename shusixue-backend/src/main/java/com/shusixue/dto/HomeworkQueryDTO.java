package com.shusixue.dto;

import lombok.Data;

/**
 * 作业分页查询请求参数
 */
@Data
public class HomeworkQueryDTO {

    private Integer pageNum = 1;

    private Integer pageSize = 10;

    /**
     * 关键词（搜索作业标题）
     */
    private String keyword;

    /**
     * 作业状态
     */
    private String status;
}