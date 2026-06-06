package com.shusixue.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shusixue.dto.*;
import com.shusixue.entity.Homework;
import com.shusixue.vo.HomeworkDetailVO;

public interface HomeworkService extends IService<Homework> {

    /**
     * 发布/保存作业
     */
    void saveOrUpdateHomework(HomeworkDTO dto, Long creatorId);

    /**
     * 删除作业
     */
    void deleteHomework(Long id, Long teacherId);

    /**
     * 分页查询教师发布的作业列表
     */
    IPage<Homework> getTeacherHomeworkPage(HomeworkQueryDTO queryDTO, Long teacherId);

    /**
     * 分页查询学生可查看的作业列表
     */
    IPage<Homework> getStudentHomeworkPage(HomeworkQueryDTO queryDTO, Long studentId);

    /**
     * 获取作业详情（含题目信息）
     */
    HomeworkDetailVO getHomeworkDetail(Long id, Long userId);

    /**
     * 结束作业
     */
    void endHomework(Long id, Long teacherId);
}