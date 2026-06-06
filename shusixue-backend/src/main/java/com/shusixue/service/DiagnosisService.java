package com.shusixue.service;

import java.util.List;
import java.util.Map;

/**
 * 学情分析服务接口
 */
public interface DiagnosisService {

    /**
     * 获取完整的学情分析数据
     */
    Map<String, Object> getDiagnosisData();

    /**
     * 获取知识点掌握情况
     */
    List<Map<String, Object>> getKnowledgeStats();

    /**
     * 获取薄弱点分析
     */
    List<Map<String, Object>> getWeakPoints();

    /**
     * 获取学生学习画像
     */
    List<Map<String, Object>> getStudentProfiles();

    /**
     * 导出学情分析报告
     */
    void exportReport(jakarta.servlet.http.HttpServletResponse response);
}
