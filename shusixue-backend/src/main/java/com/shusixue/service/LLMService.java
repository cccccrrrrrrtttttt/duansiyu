package com.shusixue.service;

import java.util.Map;

/**
 * 大语言模型服务接口
 */
public interface LLMService {

    /**
     * 分析教案
     * @param content 教案内容
     * @return 分析结果
     */
    Map<String, Object> analyzeLessonPlan(String content);

    /**
     * 改进教案
     * @param content 教案内容
     * @return 改进建议
     */
    Map<String, Object> improveLessonPlan(String content);

    /**
     * 生成教案
     * @param topic 教学主题
     * @return 生成的教案
     */
    Map<String, Object> generateLessonPlan(String topic);
}
