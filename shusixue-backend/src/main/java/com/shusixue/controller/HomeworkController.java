package com.shusixue.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.shusixue.annotation.Idempotent;
import com.shusixue.annotation.OperationLog;
import com.shusixue.common.Result;
import com.shusixue.dto.*;
import com.shusixue.entity.Homework;
import com.shusixue.entity.HomeworkSubmission;
import com.shusixue.entity.SysUser;
import com.shusixue.service.HomeworkService;
import com.shusixue.service.HomeworkSubmissionService;
import com.shusixue.vo.HomeworkDetailVO;
import com.shusixue.vo.SubmissionDetailVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * 作业管理接口控制器
 */
@RestController
@RequestMapping("/api/homework")
@RequiredArgsConstructor
@Tag(name = "作业管理", description = "作业发布、提交、批改、查询等接口")
public class HomeworkController {

    private final HomeworkService homeworkService;
    private final HomeworkSubmissionService homeworkSubmissionService;

    // ==================== 教师端接口 ====================

    /**
     * 发布/保存作业（仅教师/管理员）
     */
    @PostMapping("/save")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    @Idempotent(message = "作业已提交，请不要重复提交")
    @Operation(summary = "发布/保存作业", description = "教师发布新作业或保存草稿，支持定时发布")
    @OperationLog(type = "发布作业", desc = "教师发布或保存作业")
    public Result<Void> saveOrUpdate(@Valid @RequestBody HomeworkDTO dto) {
        SysUser user = (SysUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        homeworkService.saveOrUpdateHomework(dto, user.getId());
        return Result.success();
    }

    /**
     * 删除作业（仅教师/管理员）
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    @Operation(summary = "删除作业", description = "删除草稿状态的作业，已发布的作业无法删除")
    @OperationLog(type = "删除作业", desc = "教师删除作业")
    public Result<Void> delete(@Parameter(description = "作业ID") @PathVariable Long id) {
        SysUser user = (SysUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        homeworkService.deleteHomework(id, user.getId());
        return Result.success();
    }

    /**
     * 分页查询教师发布的作业列表（仅教师/管理员）
     */
    @GetMapping("/teacher/page")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    @Operation(summary = "教师作业列表", description = "分页查询当前教师发布的所有作业")
    public Result<IPage<Homework>> getTeacherPage(HomeworkQueryDTO queryDTO) {
        SysUser user = (SysUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        IPage<Homework> page = homeworkService.getTeacherHomeworkPage(queryDTO, user.getId());
        return Result.success(page);
    }

    /**
     * 查询某份作业的所有学生提交记录（仅教师/管理员）
     */
    @GetMapping("/submission/list/{homeworkId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    @Operation(summary = "学生提交记录列表", description = "查询指定作业的所有学生提交记录，用于批改")
    public Result<IPage<HomeworkSubmission>> getSubmissionList(
            @Parameter(description = "作业ID") @PathVariable Long homeworkId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize) {
        IPage<HomeworkSubmission> page = homeworkSubmissionService.getSubmissionListByHomework(homeworkId, pageNum, pageSize);
        return Result.success(page);
    }

    /**
     * 学生查询自己针对某个作业的提交记录（仅学生）
     */
    @GetMapping("/submission/student/list/{homeworkId}")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "我的提交记录", description = "学生查询自己针对某个作业的提交记录")
    public Result<IPage<HomeworkSubmission>> getStudentSubmissionList(
            @Parameter(description = "作业ID") @PathVariable Long homeworkId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize) {
        SysUser user = (SysUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        IPage<HomeworkSubmission> page = homeworkSubmissionService.getStudentSubmissionList(homeworkId, user.getId(), pageNum, pageSize);
        return Result.success(page);
    }

    /**
     * 教师批改作业（仅教师/管理员）
     */
    @PostMapping("/grade")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    @Operation(summary = "批改作业", description = "教师批改学生提交的作业，给出成绩和评语")
    @OperationLog(type = "批改作业", desc = "教师批改学生作业")
    public Result<Void> gradeHomework(@Valid @RequestBody HomeworkGradeDTO dto) {
        SysUser user = (SysUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        homeworkSubmissionService.gradeHomework(dto, user.getId());
        return Result.success();
    }

    /**
     * 教师结束作业（仅教师/管理员）
     */
    @PostMapping("/end/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    @Operation(summary = "结束作业", description = "教师手动结束作业，结束后学生无法再提交")
    public Result<Void> endHomework(@Parameter(description = "作业ID") @PathVariable Long id) {
        SysUser user = (SysUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        homeworkService.endHomework(id, user.getId());
        return Result.success();
    }

    // ==================== 学生端接口 ====================

    /**
     * 分页查询学生可查看的作业列表（所有登录用户）
     */
    @GetMapping("/student/page")
    @Operation(summary = "学生作业列表", description = "分页查询学生可见的已发布作业")
    public Result<IPage<Homework>> getStudentPage(HomeworkQueryDTO queryDTO) {
        SysUser user = (SysUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        IPage<Homework> page = homeworkService.getStudentHomeworkPage(queryDTO, user.getId());
        return Result.success(page);
    }

    /**
     * 学生提交作业（仅学生角色）
     */
    @PostMapping("/submit")
    @PreAuthorize("hasRole('STUDENT')")
    @Idempotent(message = "作业已提交，请不要重复提交", expireSeconds = 60)
    @Operation(summary = "提交作业", description = "学生提交作业答案，支持多次提交，以最后一次为准")
    @OperationLog(type = "提交作业", desc = "学生提交作业")
    public Result<Void> submitHomework(@Valid @RequestBody HomeworkSubmitDTO dto) {
        SysUser user = (SysUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        homeworkSubmissionService.submitHomework(dto, user.getId());
        return Result.success();
    }

    // ==================== 通用接口 ====================

    /**
     * 获取作业详情（含题目信息，所有登录用户）
     */
    @GetMapping("/detail/{id}")
    @Operation(summary = "作业详情", description = "获取作业详细信息，包含题目列表")
    public Result<HomeworkDetailVO> getDetail(@Parameter(description = "作业ID") @PathVariable Long id) {
        SysUser user = (SysUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        HomeworkDetailVO vo = homeworkService.getHomeworkDetail(id, user.getId());
        return Result.success(vo);
    }

    /**
     * 获取作业提交详情（含答案和批改结果，所有登录用户）
     */
    @GetMapping("/submission/detail/{submissionId}")
    @Operation(summary = "提交详情", description = "获取作业提交的详细信息，包含答案和批改结果")
    public Result<SubmissionDetailVO> getSubmissionDetail(@Parameter(description = "提交记录ID") @PathVariable Long submissionId) {
        SysUser user = (SysUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        SubmissionDetailVO vo = homeworkSubmissionService.getSubmissionDetail(submissionId, user.getId());
        return Result.success(vo);
    }
}
