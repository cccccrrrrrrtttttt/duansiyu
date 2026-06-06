package com.shusixue.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shusixue.dto.QuestionDTO;
import com.shusixue.dto.QuestionQueryDTO;
import com.shusixue.entity.Question;

public interface QuestionService extends IService<Question> {

    /**
     * 新增/修改题目
     */
    void saveOrUpdateQuestion(QuestionDTO dto, Long creatorId);

    /**
     * 删除题目
     */
    void deleteQuestion(Long id);

    /**
     * 分页查询题目
     */
    IPage<Question> getQuestionPage(QuestionQueryDTO queryDTO);

    /**
     * 根据ID查询题目（带缓存）
     */
    Question getQuestionById(Long id);
}