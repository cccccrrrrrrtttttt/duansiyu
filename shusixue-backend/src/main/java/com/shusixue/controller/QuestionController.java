package com.shusixue.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.shusixue.common.Result;
import com.shusixue.dto.QuestionDTO;
import com.shusixue.dto.QuestionQueryDTO;
import com.shusixue.entity.Question;
import com.shusixue.entity.SysUser;
import com.shusixue.service.QuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * 题库接口控制器
 */
@RestController
@RequestMapping("/api/question")
@RequiredArgsConstructor
@Tag(name = "题库管理", description = "题目新增、修改、删除、查询等接口")
public class QuestionController {

    private final QuestionService questionService;

    /**
     * 新增/修改题目（仅教师/管理员）
     */
    @PostMapping("/save")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    @Operation(summary = "保存题目", description = "新增或修改题目，支持选择题、填空题、解答题")
    public Result<Void> saveOrUpdate(@Valid @RequestBody QuestionDTO dto) {
        SysUser user = (SysUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        questionService.saveOrUpdateQuestion(dto, user.getId());
        return Result.success();
    }

    /**
     * 删除题目（仅教师/管理员）
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    @Operation(summary = "删除题目", description = "删除指定ID的题目")
    public Result<Void> delete(@Parameter(description = "题目ID") @PathVariable Long id) {
        questionService.deleteQuestion(id);
        return Result.success();
    }

    /**
     * 根据ID查询题目详情（所有登录用户）
     */
    @GetMapping("/{id}")
    @Operation(summary = "题目详情", description = "根据题目ID查询题目详细信息")
    public Result<Question> getDetail(@Parameter(description = "题目ID") @PathVariable Long id) {
        Question question = questionService.getById(id);
        return Result.success(question);
    }

    /**
     * 分页查询题目（所有登录用户，支持多维度筛选）
     */
    @GetMapping("/page")
    @Operation(summary = "题目列表", description = "分页查询题目，支持按科目、类型、难度、关键词筛选")
    public Result<IPage<Question>> getPage(QuestionQueryDTO queryDTO) {
        IPage<Question> page = questionService.getQuestionPage(queryDTO);
        return Result.success(page);
    }
}
