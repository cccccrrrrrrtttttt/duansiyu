package com.shusixue.vo;

import com.shusixue.entity.Question;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 作业详情返回对象
 */
@Data
public class HomeworkDetailVO {

    private Long id;

    private String title;

    private String description;

    private List<Question> questionList;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private String status;

    private LocalDateTime createTime;
}