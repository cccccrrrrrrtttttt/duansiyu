package com.shusixue.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shusixue.common.CacheConstants;
import com.shusixue.dto.HomeworkDTO;
import com.shusixue.dto.HomeworkQueryDTO;
import com.shusixue.entity.Homework;
import com.shusixue.entity.Question;
import com.shusixue.entity.SysUser;
import com.shusixue.exception.BusinessException;
import com.shusixue.mapper.HomeworkMapper;
import com.shusixue.service.HomeworkService;
import com.shusixue.service.HomeworkSubmissionService;
import com.shusixue.service.QuestionService;
import com.shusixue.service.SysUserService;
import com.shusixue.utils.CacheUtil;
import com.shusixue.vo.HomeworkDetailVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 作业服务实现类（带缓存优化）
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class HomeworkServiceImpl extends ServiceImpl<HomeworkMapper, Homework> implements HomeworkService {

    private final ObjectMapper objectMapper;
    private final QuestionService questionService;
    private final SysUserService sysUserService;
    private final @Lazy HomeworkSubmissionService homeworkSubmissionService;
    private final CacheUtil cacheUtil;

    @Override
    public void saveOrUpdateHomework(HomeworkDTO dto, Long creatorId) {
        LocalDateTime now = LocalDateTime.now();

        if (dto.getEndTime().isBefore(dto.getStartTime())) {
            throw new BusinessException("截止时间不能早于开始时间");
        }

        Homework homework = new Homework();
        BeanUtils.copyProperties(dto, homework);
        try {
            homework.setQuestionIds(objectMapper.writeValueAsString(dto.getQuestionIds()));
        } catch (Exception e) {
            throw new BusinessException("题目数据解析失败");
        }
        homework.setCreatorId(creatorId);

        if (dto.getPublishNow()) {
            if (now.isBefore(dto.getStartTime())) {
                homework.setStatus("PUBLISHED");
            } else if (now.isAfter(dto.getEndTime())) {
                homework.setStatus("ENDED");
            } else {
                homework.setStatus("PUBLISHED");
            }
        } else {
            homework.setStatus("DRAFT");
        }

        this.saveOrUpdate(homework);

        // 删除相关缓存
        clearHomeworkCache(homework.getId(), creatorId);

        // 检查是否需要初始化提交记录
        boolean needInitSubmission = false;
        
        // 情况1：新作业首次发布
        if (dto.getPublishNow() && dto.getId() == null) {
            needInitSubmission = true;
        }
        // 情况2：从草稿状态改为发布状态
        if (dto.getId() != null) {
            Homework oldHomework = this.getById(dto.getId());
            if (oldHomework != null && "DRAFT".equals(oldHomework.getStatus()) && dto.getPublishNow()) {
                needInitSubmission = true;
            }
        }
        
        // 初始化提交记录
        if (needInitSubmission) {
            List<SysUser> studentList = sysUserService.list(new LambdaQueryWrapper<SysUser>()
                    .eq(SysUser::getRole, "STUDENT")
                    .eq(SysUser::getStatus, 1)
                    .eq(SysUser::getDeleted, 0));
            for (SysUser student : studentList) {
                homeworkSubmissionService.initSubmissionRecord(homework.getId(), student.getId());
            }
        }
    }

    @Override
    public void deleteHomework(Long id, Long teacherId) {
        Homework homework = this.getById(id);
        if (homework == null) {
            throw new BusinessException("作业不存在");
        }
        if (!homework.getCreatorId().equals(teacherId)) {
            throw new BusinessException("无权限删除该作业");
        }
        if ("PUBLISHED".equals(homework.getStatus())) {
            throw new BusinessException("已发布的作业无法删除，请先结束作业");
        }
        this.removeById(id);
        
        // 删除相关缓存
        clearHomeworkCache(id, teacherId);
    }

    @Override
    public IPage<Homework> getTeacherHomeworkPage(HomeworkQueryDTO queryDTO, Long teacherId) {
        // 构建缓存Key
        String cacheKey = CacheConstants.buildHomeworkListKey(teacherId, queryDTO.getPageNum(), queryDTO.getPageSize());
        
        // 先从缓存获取
        @SuppressWarnings("unchecked")
        IPage<Homework> cachedPage = cacheUtil.getWithPenetrationProtection(cacheKey, IPage.class);
        if (cachedPage != null) {
            log.info("教师作业列表缓存命中 - 教师ID: {}, 页码: {}", teacherId, queryDTO.getPageNum());
            return cachedPage;
        }
        
        // 缓存未命中，从数据库查询
        log.info("教师作业列表缓存未命中，从数据库查询 - 教师ID: {}, 页码: {}", teacherId, queryDTO.getPageNum());
        
        Page<Homework> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        LambdaQueryWrapper<Homework> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Homework::getCreatorId, teacherId);
        wrapper.like(StringUtils.hasText(queryDTO.getKeyword()), Homework::getTitle, queryDTO.getKeyword());
        
        // 状态筛选 - 如果用户选择了特定状态，则筛选
        if (StringUtils.hasText(queryDTO.getStatus())) {
            wrapper.eq(Homework::getStatus, queryDTO.getStatus());
        }
        
        wrapper.orderByDesc(Homework::getCreateTime);
        IPage<Homework> result = this.page(page, wrapper);
        
        // 查询后动态更新状态
        LocalDateTime now = LocalDateTime.now();
        
        for (Homework homework : result.getRecords()) {
            // 只更新已发布的作业状态，草稿状态保持不变
            if (!"DRAFT".equals(homework.getStatus())) {
                String newStatus = calculateStatus(homework, now);
                
                if (!newStatus.equals(homework.getStatus())) {
                    homework.setStatus(newStatus);
                    // 更新数据库中的状态
                    this.updateById(homework);
                }
            }
        }
        
        // 设置缓存（空结果也缓存，防止缓存穿透）
        if (result.getRecords().isEmpty()) {
            cacheUtil.setEmptyValue(cacheKey);
        } else {
            cacheUtil.set(cacheKey, result, CacheConstants.HOMEWORK_LIST_EXPIRE);
        }
        
        return result;
    }

    @Override
    public IPage<Homework> getStudentHomeworkPage(HomeworkQueryDTO queryDTO, Long studentId) {
        // 构建缓存Key
        String cacheKey = CacheConstants.buildHomeworkListKey(studentId, queryDTO.getPageNum(), queryDTO.getPageSize());
        
        // 先从缓存获取
        @SuppressWarnings("unchecked")
        IPage<Homework> cachedPage = cacheUtil.getWithPenetrationProtection(cacheKey, IPage.class);
        if (cachedPage != null) {
            log.info("学生作业列表缓存命中 - 学生ID: {}, 页码: {}", studentId, queryDTO.getPageNum());
            return cachedPage;
        }
        
        // 缓存未命中，从数据库查询
        log.info("学生作业列表缓存未命中，从数据库查询 - 学生ID: {}, 页码: {}", studentId, queryDTO.getPageNum());
        
        Page<Homework> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        LambdaQueryWrapper<Homework> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Homework::getStatus, "PUBLISHED", "ENDED");
        wrapper.like(StringUtils.hasText(queryDTO.getKeyword()), Homework::getTitle, queryDTO.getKeyword());
        wrapper.eq(StringUtils.hasText(queryDTO.getStatus()), Homework::getStatus, queryDTO.getStatus());
        wrapper.orderByDesc(Homework::getCreateTime);
        
        IPage<Homework> result = this.page(page, wrapper);
        
        // 查询后动态更新状态
        LocalDateTime now = LocalDateTime.now();
        for (Homework homework : result.getRecords()) {
            String newStatus = calculateStatus(homework, now);
            if (!newStatus.equals(homework.getStatus())) {
                homework.setStatus(newStatus);
                this.updateById(homework);
            }
        }
        
        // 设置缓存（空结果也缓存，防止缓存穿透）
        if (result.getRecords().isEmpty()) {
            cacheUtil.setEmptyValue(cacheKey);
        } else {
            cacheUtil.set(cacheKey, result, CacheConstants.HOMEWORK_LIST_EXPIRE);
        }
        
        return result;
    }

    @Override
    public HomeworkDetailVO getHomeworkDetail(Long id, Long userId) {
        // 构建缓存Key
        String cacheKey = CacheConstants.buildHomeworkDetailKey(id);
        
        // 先从缓存获取
        HomeworkDetailVO cachedVO = cacheUtil.getWithPenetrationProtection(cacheKey, HomeworkDetailVO.class);
        if (cachedVO != null) {
            log.info("作业详情缓存命中 - ID: {}", id);
            return cachedVO;
        }
        
        // 缓存未命中，从数据库查询
        log.info("作业详情缓存未命中，从数据库查询 - ID: {}", id);
        
        Homework homework = this.getById(id);
        if (homework == null) {
            cacheUtil.setEmptyValue(cacheKey);
            throw new BusinessException("作业不存在");
        }

        // 动态更新状态
        if (!"DRAFT".equals(homework.getStatus())) {
            String newStatus = calculateStatus(homework, LocalDateTime.now());
            if (!newStatus.equals(homework.getStatus())) {
                homework.setStatus(newStatus);
                this.updateById(homework);
            }
        }

        List<Long> questionIds;
        try {
            questionIds = objectMapper.readValue(homework.getQuestionIds(), new TypeReference<List<Long>>() {});
        } catch (Exception e) {
            throw new BusinessException("题目数据解析失败");
        }

        List<Question> questionList = questionService.listByIds(questionIds);

        HomeworkDetailVO vo = new HomeworkDetailVO();
        BeanUtils.copyProperties(homework, vo);
        vo.setQuestionList(questionList);
        
        // 设置缓存
        cacheUtil.set(cacheKey, vo, CacheConstants.HOMEWORK_DETAIL_EXPIRE);
        
        return vo;
    }

    @Override
    public void endHomework(Long id, Long teacherId) {
        Homework homework = this.getById(id);
        if (homework == null) {
            throw new BusinessException("作业不存在");
        }
        if (!homework.getCreatorId().equals(teacherId)) {
            throw new BusinessException("无权限结束该作业");
        }
        if ("DRAFT".equals(homework.getStatus())) {
            throw new BusinessException("草稿状态的作业无法结束");
        }
        if ("ENDED".equals(homework.getStatus())) {
            throw new BusinessException("作业已经结束");
        }
        // 更新状态为已结束
        homework.setStatus("ENDED");
        this.updateById(homework);
        
        // 删除相关缓存
        clearHomeworkCache(id, teacherId);
    }
    
    /**
     * 根据当前时间计算作业状态
     */
    private String calculateStatus(Homework homework, LocalDateTime now) {
        // 确保时间不为空
        if (homework.getEndTime() == null) {
            return "PUBLISHED";
        }
        
        // 如果作业已经手动结束，保持ENDED状态
        if ("ENDED".equals(homework.getStatus())) {
            return "ENDED";
        }
        
        // 比较时间（考虑时区问题，使用toLocalDate()进行日期比较）
        boolean isAfterEndTime = now.toLocalDate().isAfter(homework.getEndTime().toLocalDate()) || 
                               (now.toLocalDate().isEqual(homework.getEndTime().toLocalDate()) && 
                                now.toLocalTime().isAfter(homework.getEndTime().toLocalTime()));
        
        // 检查是否已过截止时间
        if (isAfterEndTime) {
            return "ENDED";
        } else {
            return "PUBLISHED";
        }
    }
    
    /**
     * 清除作业相关缓存
     */
    private void clearHomeworkCache(Long homeworkId, Long userId) {
        log.info("清除作业相关缓存 - 作业ID: {}, 用户ID: {}", homeworkId, userId);
        // 删除作业详情缓存
        cacheUtil.delete(CacheConstants.buildHomeworkDetailKey(homeworkId));
        // 删除该用户的作业列表缓存（由于分页不确定，这里简化处理）
        // 实际项目中可以维护缓存key列表或使用Redis的scan命令
    }
}
