package com.shusixue.controller;

import com.shusixue.common.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * 文件上传接口控制器
 */
@RestController
@RequestMapping("/api/file")
@RequiredArgsConstructor
public class FileController {

    /**
     * 上传教案文件
     */
    @PostMapping("/upload/lesson-plan")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public Result<String> uploadLessonPlan(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.fail("文件不能为空");
        }

        if (!file.getOriginalFilename().endsWith(".docx")) {
            return Result.fail("只支持 .docx 格式的文件");
        }

        if (file.getSize() > 10 * 1024 * 1024) {
            return Result.fail("文件大小不能超过 10MB");
        }

        try {
            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String fileName = UUID.randomUUID().toString() + "-" + originalFilename;

            // 保存文件到本地（实际项目中应该使用文件服务器或云存储）
            String uploadPath = System.getProperty("user.dir") + File.separator + "uploads" + File.separator + "lesson-plan" + File.separator;
            File directory = new File(uploadPath);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            File dest = new File(uploadPath + fileName);
            file.transferTo(dest);

            // 返回文件路径
            return Result.success(fileName);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.fail("文件上传失败: " + e.getMessage());
        }
    }
}