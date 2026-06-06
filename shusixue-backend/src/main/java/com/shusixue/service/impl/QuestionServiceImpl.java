package com.shusixue.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shusixue.common.CacheConstants;
import com.shusixue.dto.QuestionDTO;
import com.shusixue.dto.QuestionQueryDTO;
import com.shusixue.entity.Question;
import com.shusixue.mapper.QuestionMapper;
import com.shusixue.service.QuestionService;
import com.shusixue.utils.CacheUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 题目服务实现类（带缓存优化）
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question> implements QuestionService {

    private final CacheUtil cacheUtil;

    @Override
    public void saveOrUpdateQuestion(QuestionDTO dto, Long creatorId) {
        Question question = new Question();
        BeanUtils.copyProperties(dto, question);
        if (dto.getId() == null) {
            // 新增时设置创建人
            question.setCreatorId(creatorId);
        }
        this.saveOrUpdate(question);
        
        // 更新缓存：删除该题目相关缓存
        if (question.getId() != null) {
            cacheUtil.delete(CacheConstants.buildQuestionDetailKey(question.getId()));
        }
    }

    @Override
    public void deleteQuestion(Long id) {
        this.removeById(id);
        
        // 更新缓存：删除该题目相关缓存
        cacheUtil.delete(CacheConstants.buildQuestionDetailKey(id));
    }

    @Override
    public IPage<Question> getQuestionPage(QuestionQueryDTO queryDTO) {
        Page<Question> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());

        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();
        // 关键词模糊搜索
        wrapper.like(StringUtils.hasText(queryDTO.getKeyword()), Question::getTitle, queryDTO.getKeyword());
        // 精确匹配
        wrapper.eq(StringUtils.hasText(queryDTO.getSubject()), Question::getSubject, queryDTO.getSubject());
        wrapper.eq(StringUtils.hasText(queryDTO.getType()), Question::getType, queryDTO.getType());
        wrapper.eq(StringUtils.hasText(queryDTO.getDifficulty()), Question::getDifficulty, queryDTO.getDifficulty());
        // 知识点模糊匹配（逗号分隔的字符串中包含该ID）
        if (queryDTO.getKnowledgePointId() != null) {
            wrapper.like(Question::getKnowledgePointIds, "," + queryDTO.getKnowledgePointId() + ",")
                    .or()
                    .likeRight(Question::getKnowledgePointIds, queryDTO.getKnowledgePointId() + ",")
                    .or()
                    .likeLeft(Question::getKnowledgePointIds, "," + queryDTO.getKnowledgePointId())
                    .or()
                    .eq(Question::getKnowledgePointIds, String.valueOf(queryDTO.getKnowledgePointId()));
        }
        // 按创建时间倒序
        wrapper.orderByDesc(Question::getCreateTime);

        return this.page(page, wrapper);
    }

    @Override
    public Question getQuestionById(Long id) {
        // 构建缓存Key
        String cacheKey = CacheConstants.buildQuestionDetailKey(id);
        
        // 先从缓存获取
        Question cachedQuestion = cacheUtil.getWithPenetrationProtection(cacheKey, Question.class);
        if (cachedQuestion != null) {
            log.info("题目详情缓存命中 - ID: {}", id);
            return cachedQuestion;
        }
        
        // 缓存未命中，从数据库查询
        log.info("题目详情缓存未命中，从数据库查询 - ID: {}", id);
        Question question = this.getById(id);
        
        // 设置缓存（空结果也缓存，防止缓存穿透）
        if (question == null) {
            cacheUtil.setEmptyValue(cacheKey);
        } else {
            cacheUtil.set(cacheKey, question, CacheConstants.QUESTION_DETAIL_EXPIRE);
        }
        
        return question;
    }
}
