package com.shusixue.controller;

import com.shusixue.common.Result;
import com.shusixue.dto.KnowledgePointDTO;
import com.shusixue.entity.KnowledgePoint;
import com.shusixue.service.KnowledgePointService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 知识点接口控制器
 */
@RestController
@RequestMapping("/api/knowledge-point")
@RequiredArgsConstructor
@Tag(name = "知识点管理", description = "知识点新增、修改、删除、查询等接口")
public class KnowledgePointController {

    private final KnowledgePointService knowledgePointService;

    /**
     * 新增/修改知识点（仅教师/管理员）
     */
    @PostMapping("/save")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    @Operation(summary = "保存知识点", description = "新增或修改知识点，支持树形结构")
    public Result<Void> saveOrUpdate(@Valid @RequestBody KnowledgePointDTO dto) {
        knowledgePointService.saveOrUpdateKnowledgePoint(dto);
        return Result.success();
    }

    /**
     * 删除知识点（仅教师/管理员）
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    @Operation(summary = "删除知识点", description = "删除指定ID的知识点")
    public Result<Void> delete(@Parameter(description = "知识点ID") @PathVariable Long id) {
        knowledgePointService.deleteKnowledgePoint(id);
        return Result.success();
    }

    /**
     * 根据科目查询知识点列表（所有登录用户）
     */
    @GetMapping("/list")
    @Operation(summary = "知识点列表", description = "按科目查询知识点树形结构，不传科目则查询所有")
    public Result<List<KnowledgePoint>> list(@Parameter(description = "科目（如：高等数学）") @RequestParam(required = false) String subject) {
        List<KnowledgePoint> list = knowledgePointService.getKnowledgePointTree(subject);
        return Result.success(list);
    }
}
