package com.shusixue.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 作业提交详情返回对象
 */
@Data
public class SubmissionDetailVO {

    private Long submissionId;

    private Long homeworkId;

    private String homeworkTitle;

    private Long studentId;

    private String studentName;

    /**
     * 题目详情列表
     */
    private List<QuestionAnswerVO> questionList;

    /**
     * 总得分
     */
    private BigDecimal totalScore;

    /**
     * 正确题数/总题数
     */
    private String correctRate;

    private LocalDateTime submitTime;

    private String status;

    /**
     * 题目+答案+批改结果内部类
     */
    @Data
    public static class QuestionAnswerVO {
        private Long questionId;
        private String title;
        private String type;
        private String options;
        private String standardAnswer;
        private String studentAnswer;
        private String analysis;
        private Boolean isCorrect;
    }
}