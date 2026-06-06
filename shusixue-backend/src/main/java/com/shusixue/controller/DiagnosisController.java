package com.shusixue.controller;

import jakarta.servlet.http.HttpServletResponse;
import com.shusixue.common.Result;
import com.shusixue.service.DiagnosisService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 学情分析接口控制器
 */
@RestController
@RequestMapping("/api/diagnosis")
@RequiredArgsConstructor
public class DiagnosisController {

    private final DiagnosisService diagnosisService;

    /**
     * 获取学情分析数据（仅教师/管理员）
     */
    @GetMapping("/data")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public Result<?> getDiagnosisData() {
        return Result.success(diagnosisService.getDiagnosisData());
    }

    /**
     * 获取知识点掌握情况（仅教师/管理员）
     */
    @GetMapping("/knowledge-stats")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public Result<?> getKnowledgeStats() {
        return Result.success(diagnosisService.getKnowledgeStats());
    }

    /**
     * 获取薄弱点分析（仅教师/管理员）
     */
    @GetMapping("/weak-points")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public Result<?> getWeakPoints() {
        return Result.success(diagnosisService.getWeakPoints());
    }

    /**
     * 获取学生学习画像（仅教师/管理员）
     */
    @GetMapping("/student-profiles")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public Result<?> getStudentProfiles() {
        return Result.success(diagnosisService.getStudentProfiles());
    }

    /**
     * 导出学情分析报告（仅教师/管理员）
     */
    @GetMapping("/export")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public void exportReport(HttpServletResponse response) {
        diagnosisService.exportReport(response);
    }
}
