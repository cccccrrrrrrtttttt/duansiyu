package com.shusixue.controller;

import com.shusixue.common.Result;
import com.shusixue.service.LLMService;
import com.shusixue.utils.DocxParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * 教案管理接口控制器
 */
@RestController
@RequestMapping("/api/lesson-plan")
@RequiredArgsConstructor
@Slf4j
public class LessonPlanController {

    private final LLMService llmService;

    /**
     * 分析教案
     */
    @PostMapping("/analyze")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public Result<Map<String, Object>> analyzeLessonPlan(@RequestBody Map<String, String> request) {
        String fileName = request.get("fileName");

        try {
            // 读取文件内容
            String uploadDir = System.getProperty("user.dir") + File.separator + "uploads" + File.separator + "lesson-plan" + File.separator;
            String filePath = uploadDir + fileName;
            String content = DocxParser.parseDocx(filePath);

            // 调用大模型分析
            Map<String, Object> result = llmService.analyzeLessonPlan(content);
            return Result.success(result);
        } catch (IOException e) {
            log.error("解析教案文件失败", e);
            return Result.fail("解析教案文件失败: " + e.getMessage());
        } catch (Exception e) {
            log.error("分析教案失败", e);
            return Result.fail("分析教案失败: " + e.getMessage());
        }
    }

    /**
     * 改进教案
     */
    @PostMapping("/improve")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public Result<Map<String, Object>> improveLessonPlan(@RequestBody Map<String, String> request) {
        String fileName = request.get("fileName");

        try {
            // 读取文件内容
            String uploadDir = System.getProperty("user.dir") + File.separator + "uploads" + File.separator + "lesson-plan" + File.separator;
            String filePath = uploadDir + fileName;
            String content = DocxParser.parseDocx(filePath);

            // 调用大模型改进
            Map<String, Object> result = llmService.improveLessonPlan(content);
            return Result.success(result);
        } catch (IOException e) {
            log.error("解析教案文件失败", e);
            return Result.fail("解析教案文件失败: " + e.getMessage());
        } catch (Exception e) {
            log.error("改进教案失败", e);
            return Result.fail("改进教案失败: " + e.getMessage());
        }
    }

    /**
     * 生成教案
     */
    @PostMapping("/generate")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public Result<Map<String, Object>> generateLessonPlan(@RequestBody Map<String, String> request) {
        String topic = request.get("topic");

        try {
            // 调用大模型生成
            Map<String, Object> result = llmService.generateLessonPlan(topic);
            return Result.success(result);
        } catch (Exception e) {
            log.error("生成教案失败", e);
            return Result.fail("生成教案失败: " + e.getMessage());
        }
    }
}