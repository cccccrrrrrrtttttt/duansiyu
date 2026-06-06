package com.shusixue.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shusixue.dto.HomeworkGradeDTO;
import com.shusixue.dto.HomeworkSubmitDTO;
import com.shusixue.entity.Homework;
import com.shusixue.entity.HomeworkSubmission;
import com.shusixue.entity.Question;
import com.shusixue.entity.SysUser;
import com.shusixue.exception.BusinessException;
import com.shusixue.mapper.HomeworkMapper; // 新增：直接注入Mapper
import com.shusixue.mapper.HomeworkSubmissionMapper;
import com.shusixue.service.*;
import com.shusixue.vo.SubmissionDetailVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class HomeworkSubmissionServiceImpl extends ServiceImpl<HomeworkSubmissionMapper, HomeworkSubmission> implements HomeworkSubmissionService {

    private final ObjectMapper objectMapper;
    // 核心修改：移除 HomeworkService，改为注入 HomeworkMapper
    private final HomeworkMapper homeworkMapper;
    private final QuestionService questionService;
    private final SysUserService sysUserService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitHomework(HomeworkSubmitDTO dto, Long studentId) {
        // 1. 直接用 Mapper 查询作业，不再依赖 Service
        Homework homework = homeworkMapper.selectById(dto.getHomeworkId());
        if (homework == null) {
            throw new BusinessException("作业不存在");
        }

        // 2. 校验提交权限和时间
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(homework.getStartTime())) {
            throw new BusinessException("作业还未开始，无法提交");
        }
        if (now.isAfter(homework.getEndTime())) {
            throw new BusinessException("作业已截止，无法提交");
        }

        // 3. 查询学生的提交记录
        HomeworkSubmission submission = this.getOne(new LambdaQueryWrapper<HomeworkSubmission>()
                .eq(HomeworkSubmission::getHomeworkId, dto.getHomeworkId())
                .eq(HomeworkSubmission::getStudentId, studentId));
        
        // 如果提交记录不存在，自动创建
        if (submission == null) {
            submission = new HomeworkSubmission();
            submission.setHomeworkId(dto.getHomeworkId());
            submission.setStudentId(studentId);
            submission.setStatus("NOT_SUBMITTED");
            submission.setTotalCount(0);
            submission.setCorrectCount(0);
            this.save(submission);
            System.out.println("自动创建作业提交记录: 学生ID=" + studentId + ", 作业ID=" + dto.getHomeworkId());
        }
        
        // 检查是否已提交
        if ("SUBMITTED".equals(submission.getStatus()) || "GRADED".equals(submission.getStatus())) {
            throw new BusinessException("你已提交过该作业，无需重复提交");
        }

        // 4. 解析题目ID列表，获取标准答案
        List<Long> questionIds;
        try {
            questionIds = objectMapper.readValue(homework.getQuestionIds(), new TypeReference<List<Long>>() {});
        } catch (Exception e) {
            throw new BusinessException("作业题目解析失败");
        }
        List<Question> questionList = questionService.listByIds(questionIds);

        // 5. 自动批改客观题
        int correctCount = 0;
        int totalCount = questionList.size();
        Map<Long, String> studentAnswers = dto.getAnswers();

        for (Question question : questionList) {
            String studentAnswer = studentAnswers.get(question.getId());
            if ("SINGLE_CHOICE".equals(question.getType())
                    || "MULTIPLE_CHOICE".equals(question.getType())
                    || "FILL_BLANK".equals(question.getType())) {
                if (question.getAnswer().equals(studentAnswer)) {
                    correctCount++;
                }
            }
        }

        // 6. 更新提交记录
        try {
            submission.setAnswers(objectMapper.writeValueAsString(studentAnswers));
        } catch (Exception e) {
            throw new BusinessException("答案数据保存失败");
        }
        submission.setCorrectCount(correctCount);
        submission.setTotalCount(totalCount);
        submission.setSubmitTime(now);
        boolean hasEssay = questionList.stream().anyMatch(q -> "ESSAY".equals(q.getType()));
        submission.setStatus(hasEssay ? "SUBMITTED" : "GRADED");
        if (!hasEssay) {
            BigDecimal score = BigDecimal.valueOf(correctCount * 100.0 / totalCount).setScale(2, RoundingMode.HALF_UP);
            submission.setScore(score);
        }

        this.updateById(submission);
    }

    @Override
    public void gradeHomework(HomeworkGradeDTO dto, Long teacherId) {
        // 1. 校验提交记录
        HomeworkSubmission submission = this.getById(dto.getSubmissionId());
        if (submission == null) {
            throw new BusinessException("提交记录不存在");
        }
        // 2. 直接用 Mapper 查询作业，校验权限
        Homework homework = homeworkMapper.selectById(submission.getHomeworkId());
        if (!homework.getCreatorId().equals(teacherId)) {
            throw new BusinessException("无权限批改该作业");
        }
        // 3. 更新分数和状态
        submission.setScore(dto.getScore());
        submission.setStatus("GRADED");
        this.updateById(submission);
    }

    @Override
    public IPage<HomeworkSubmission> getSubmissionListByHomework(Long homeworkId, Integer pageNum, Integer pageSize) {
        Page<HomeworkSubmission> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<HomeworkSubmission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HomeworkSubmission::getHomeworkId, homeworkId);
        wrapper.orderByDesc(HomeworkSubmission::getSubmitTime);
        return this.page(page, wrapper);
    }

    @Override
    public IPage<HomeworkSubmission> getStudentSubmissionList(Long homeworkId, Long studentId, Integer pageNum, Integer pageSize) {
        Page<HomeworkSubmission> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<HomeworkSubmission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HomeworkSubmission::getHomeworkId, homeworkId);
        wrapper.eq(HomeworkSubmission::getStudentId, studentId);
        wrapper.orderByDesc(HomeworkSubmission::getSubmitTime);
        return this.page(page, wrapper);
    }

    @Override
    public SubmissionDetailVO getSubmissionDetail(Long submissionId, Long userId) {
        // 1. 查询提交记录
        HomeworkSubmission submission = this.getById(submissionId);
        if (submission == null) {
            throw new BusinessException("提交记录不存在");
        }

        // 2. 直接用 Mapper 查询作业
        Homework homework = homeworkMapper.selectById(submission.getHomeworkId());
        SysUser user = sysUserService.getById(userId);
        if (!submission.getStudentId().equals(userId)
                && !homework.getCreatorId().equals(userId)
                && !"ADMIN".equals(user.getRole())) {
            throw new BusinessException("无权限查看该提交记录");
        }

        // 3. 解析数据
        List<Long> questionIds;
        Map<Long, String> studentAnswers;
        try {
            questionIds = objectMapper.readValue(homework.getQuestionIds(), new TypeReference<List<Long>>() {});
            studentAnswers = objectMapper.readValue(submission.getAnswers(), new TypeReference<Map<Long, String>>() {});
        } catch (Exception e) {
            throw new BusinessException("数据解析失败");
        }

        // 4. 封装题目+答案详情
        List<Question> questionList = questionService.listByIds(questionIds);
        List<SubmissionDetailVO.QuestionAnswerVO> answerVOList = new ArrayList<>();

        for (Question question : questionList) {
            SubmissionDetailVO.QuestionAnswerVO answerVO = new SubmissionDetailVO.QuestionAnswerVO();
            answerVO.setQuestionId(question.getId());
            answerVO.setTitle(question.getTitle());
            answerVO.setType(question.getType());
            answerVO.setOptions(question.getOptions());
            answerVO.setStandardAnswer(question.getAnswer());
            answerVO.setStudentAnswer(studentAnswers.get(question.getId()));
            answerVO.setAnalysis(question.getAnalysis());
            answerVO.setIsCorrect(question.getAnswer().equals(studentAnswers.get(question.getId())));
            answerVOList.add(answerVO);
        }

        // 5. 封装最终返回对象
        SysUser student = sysUserService.getById(submission.getStudentId());
        SubmissionDetailVO vo = new SubmissionDetailVO();
        vo.setSubmissionId(submission.getId());
        vo.setHomeworkId(homework.getId());
        vo.setHomeworkTitle(homework.getTitle());
        vo.setStudentId(student.getId());
        vo.setStudentName(student.getRealName());
        vo.setQuestionList(answerVOList);
        vo.setTotalScore(submission.getScore());
        vo.setCorrectRate(submission.getCorrectCount() + "/" + submission.getTotalCount());
        vo.setSubmitTime(submission.getSubmitTime());
        vo.setStatus(submission.getStatus());

        return vo;
    }

    @Override
    public void initSubmissionRecord(Long homeworkId, Long studentId) {
        HomeworkSubmission submission = new HomeworkSubmission();
        submission.setHomeworkId(homeworkId);
        submission.setStudentId(studentId);
        submission.setStatus("NOT_SUBMITTED");
        submission.setTotalCount(0);
        submission.setCorrectCount(0);
        submission.setScore(BigDecimal.ZERO);
        submission.setAnswers("{}");
        this.save(submission);
    }
}