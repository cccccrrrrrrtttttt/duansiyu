package com.shusixue.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shusixue.dto.TeachingEvaluationDTO;
import com.shusixue.entity.TeachingEvaluation;
import com.shusixue.exception.BusinessException;
import com.shusixue.mapper.TeachingEvaluationMapper;
import com.shusixue.service.TeachingEvaluationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 教学评价服务实现
 */
@Service
@RequiredArgsConstructor
public class TeachingEvaluationServiceImpl implements TeachingEvaluationService {

    private final TeachingEvaluationMapper teachingEvaluationMapper;

    @Override
    @Transactional
    public void saveEvaluation(TeachingEvaluationDTO dto, Long creatorId) {
        TeachingEvaluation evaluation;
        
        if (dto.getId() != null) {
            // 更新
            evaluation = teachingEvaluationMapper.selectById(dto.getId());
            if (evaluation == null) {
                throw new BusinessException("评价记录不存在");
            }
            if (!evaluation.getCreatorId().equals(creatorId)) {
                throw new BusinessException("无权修改该评价");
            }
        } else {
            // 新增
            evaluation = new TeachingEvaluation();
            evaluation.setCreatorId(creatorId);
        }
        
        // 复制属性
        evaluation.setTitle(dto.getTitle());
        evaluation.setTeachingDate(dto.getTeachingDate());
        evaluation.setClassName(dto.getClassName());
        evaluation.setContent(dto.getContent());
        evaluation.setEffectiveness(dto.getEffectiveness());
        evaluation.setParticipation(dto.getParticipation());
        evaluation.setHighlights(dto.getHighlights());
        evaluation.setWeaknesses(dto.getWeaknesses());
        evaluation.setImprovementPlan(dto.getImprovementPlan());
        
        if (dto.getId() != null) {
            teachingEvaluationMapper.updateById(evaluation);
        } else {
            teachingEvaluationMapper.insert(evaluation);
        }
    }

    @Override
    @Transactional
    public void deleteEvaluation(Long id, Long creatorId) {
        TeachingEvaluation evaluation = teachingEvaluationMapper.selectById(id);
        if (evaluation == null) {
            throw new BusinessException("评价记录不存在");
        }
        if (!evaluation.getCreatorId().equals(creatorId)) {
            throw new BusinessException("无权删除该评价");
        }
        teachingEvaluationMapper.deleteById(id);
    }

    @Override
    public TeachingEvaluation getEvaluationById(Long id, Long creatorId) {
        TeachingEvaluation evaluation = teachingEvaluationMapper.selectById(id);
        if (evaluation == null) {
            throw new BusinessException("评价记录不存在");
        }
        if (!evaluation.getCreatorId().equals(creatorId)) {
            throw new BusinessException("无权查看该评价");
        }
        return evaluation;
    }

    @Override
    public IPage<TeachingEvaluation> getEvaluationPage(Long creatorId, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<TeachingEvaluation> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TeachingEvaluation::getCreatorId, creatorId)
                   .orderByDesc(TeachingEvaluation::getCreateTime);
        
        Page<TeachingEvaluation> page = new Page<>(pageNum, pageSize);
        return teachingEvaluationMapper.selectPage(page, queryWrapper);
    }
}