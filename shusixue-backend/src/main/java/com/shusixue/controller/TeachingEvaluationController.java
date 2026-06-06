package com.shusixue.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.shusixue.common.Result;
import com.shusixue.dto.TeachingEvaluationDTO;
import com.shusixue.entity.SysUser;
import com.shusixue.entity.TeachingEvaluation;
import com.shusixue.service.TeachingEvaluationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * 教学评价接口控制器
 */
@RestController
@RequestMapping("/api/evaluation")
@RequiredArgsConstructor
public class TeachingEvaluationController {

    private final TeachingEvaluationService teachingEvaluationService;

    /**
     * 保存教学评价（新增或更新）
     */
    @PostMapping("/save")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public Result<Void> saveEvaluation(@Valid @RequestBody TeachingEvaluationDTO dto) {
        SysUser user = (SysUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        teachingEvaluationService.saveEvaluation(dto, user.getId());
        return Result.success();
    }

    /**
     * 删除教学评价
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public Result<Void> deleteEvaluation(@PathVariable Long id) {
        SysUser user = (SysUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        teachingEvaluationService.deleteEvaluation(id, user.getId());
        return Result.success();
    }

    /**
     * 获取教学评价详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public Result<TeachingEvaluation> getEvaluation(@PathVariable Long id) {
        SysUser user = (SysUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        TeachingEvaluation evaluation = teachingEvaluationService.getEvaluationById(id, user.getId());
        return Result.success(evaluation);
    }

    /**
     * 分页查询教师的教学评价列表
     */
    @GetMapping("/page")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public Result<IPage<TeachingEvaluation>> getEvaluationPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        SysUser user = (SysUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        IPage<TeachingEvaluation> page = teachingEvaluationService.getEvaluationPage(user.getId(), pageNum, pageSize);
        return Result.success(page);
    }
}