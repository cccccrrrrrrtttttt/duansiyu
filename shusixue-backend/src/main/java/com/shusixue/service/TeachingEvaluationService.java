package com.shusixue.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.shusixue.dto.TeachingEvaluationDTO;
import com.shusixue.entity.TeachingEvaluation;

/**
 * 教学评价服务接口
 */
public interface TeachingEvaluationService {

    /**
     * 保存教学评价
     */
    void saveEvaluation(TeachingEvaluationDTO dto, Long creatorId);

    /**
     * 删除教学评价
     */
    void deleteEvaluation(Long id, Long creatorId);

    /**
     * 获取教学评价详情
     */
    TeachingEvaluation getEvaluationById(Long id, Long creatorId);

    /**
     * 分页查询教师的教学评价列表
     */
    IPage<TeachingEvaluation> getEvaluationPage(Long creatorId, Integer pageNum, Integer pageSize);
}