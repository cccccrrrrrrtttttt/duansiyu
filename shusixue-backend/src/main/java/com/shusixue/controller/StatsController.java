package com.shusixue.controller;

import com.shusixue.common.Result;
import com.shusixue.service.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 统计数据接口控制器
 */
@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;

    /**
     * 获取首页统计数据
     */
    @GetMapping("/home")
    @PreAuthorize("hasAnyRole('TEACHER', 'STUDENT', 'ADMIN')")
    public Result<Map<String, Long>> getHomeStats() {
        Map<String, Long> stats = statsService.getHomeStats();
        return Result.success(stats);
    }
}