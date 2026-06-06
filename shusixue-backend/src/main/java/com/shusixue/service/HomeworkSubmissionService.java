package com.shusixue.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shusixue.dto.*;
import com.shusixue.entity.HomeworkSubmission;
import com.shusixue.vo.SubmissionDetailVO;

public interface HomeworkSubmissionService extends IService<HomeworkSubmission> {

    /**
     * 学生提交作业
     */
    void submitHomework(HomeworkSubmitDTO dto, Long studentId);

    /**
     * 教师批改作业
     */
    void gradeHomework(HomeworkGradeDTO dto, Long teacherId);

    /**
     * 查询某份作业的所有提交记录（教师用）
     */
    IPage<HomeworkSubmission> getSubmissionListByHomework(Long homeworkId, Integer pageNum, Integer pageSize);

    /**
     * 查询学生针对某个作业的提交记录（学生用）
     */
    IPage<HomeworkSubmission> getStudentSubmissionList(Long homeworkId, Long studentId, Integer pageNum, Integer pageSize);

    /**
     * 获取学生作业提交详情（含题目、答案、批改结果）
     */
    SubmissionDetailVO getSubmissionDetail(Long submissionId, Long userId);

    /**
     * 初始化学生作业提交记录（发布作业时调用）
     */
    void initSubmissionRecord(Long homeworkId, Long studentId);
}