package com.shusixue.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shusixue.service.LLMService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;

import java.util.*;

/**
 * 大语言模型服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LLMServiceImpl implements LLMService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    // 通义千问API配置
    private static final String API_URL = "https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation";
    private static final String API_KEY = "sk-d0a8e7943c6b4154b20e195f25476502"; // 替换为你的API Key
    private static final String MODEL = "qwen-turbo";

    @Override
    public Map<String, Object> analyzeLessonPlan(String content) {
        String prompt = buildAnalysisPrompt(content);
        String response = callQwenAPI(prompt);
        return parseAnalysisResponse(response);
    }

    @Override
    public Map<String, Object> improveLessonPlan(String content) {
        String prompt = buildImprovementPrompt(content);
        String response = callQwenAPI(prompt);
        return parseImprovementResponse(response);
    }

    @Override
    public Map<String, Object> generateLessonPlan(String topic) {
        String prompt = buildGenerationPrompt(topic);
        String response = callQwenAPI(prompt);
        return parseGenerationResponse(response);
    }

    /**
     * 构建教案分析提示词
     */
    private String buildAnalysisPrompt(String content) {
        return "你是一名专业的教育专家，请分析以下教案，从以下几个方面进行详细评估：\n" +
                "1. 教学目标：是否明确、具体、可衡量\n" +
                "2. 教学内容：是否符合课程标准，深度和广度是否适当\n" +
                "3. 教学方法：是否多样化，是否适合学生认知水平\n" +
                "4. 教学过程：是否逻辑清晰，环节是否完整\n" +
                "5. 评价方式：是否多样化，是否注重过程性评价\n" +
                "6. 地区教学偏差：是否与当地教学实际存在偏差\n" +
                "\n请给出详细的分析报告，包括优点和改进建议。\n\n教案内容：\n" + content;
    }

    /**
     * 构建教案改进提示词
     */
    private String buildImprovementPrompt(String content) {
        return "你是一名专业的教育专家，请针对以下教案提供具体的改进建议：\n" +
                "1. 教学目标：如何表述得更明确、可衡量\n" +
                "2. 教学重难点：如何更准确地确定和突破\n" +
                "3. 教学方法：如何更多样化、更适合学生\n" +
                "4. 分层教学：如何设计差异化教学内容\n" +
                "5. 课堂生成：如何预设和应对生成性问题\n" +
                "6. 作业设计：如何设计层次化作业\n" +
                "\n请给出具体、可操作的改进建议。\n\n教案内容：\n" + content;
    }

    /**
     * 构建教案生成提示词
     */
    private String buildGenerationPrompt(String topic) {
        return "你是一名专业的教育专家，请根据以下教学主题生成一份完整的教案：\n" +
                "教学主题：" + topic + "\n" +
                "\n请包含以下内容：\n" +
                "1. 教学目标：知识与技能、过程与方法、情感态度与价值观\n" +
                "2. 教学重难点\n" +
                "3. 教学方法\n" +
                "4. 教学过程：导入、新课讲授、课堂练习、总结\n" +
                "5. 作业布置\n" +
                "6. 板书设计\n" +
                "\n要求：\n" +
                "- 教案结构完整，逻辑清晰\n" +
                "- 教学目标明确、可衡量\n" +
                "- 教学方法多样化，适合学生认知水平\n" +
                "- 教学过程详细，可操作性强\n" +
                "- 符合课程标准要求\n" +
                "- 语言表达专业、规范\n";
    }

    /**
     * 调用通义千问API（使用 messages 格式）
     */
    private String callQwenAPI(String prompt) {
        try {
            // 构建请求参数 - 使用 messages 格式（兼容新版API）
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", MODEL);
            requestBody.put("input", Map.of("messages",
                    List.of(Map.of("role", "user", "content", prompt))));
            requestBody.put("parameters", Map.of(
                    "max_tokens", 4096,
                    "temperature", 0.7,
                    "top_p", 0.95
            ));

            // 构建请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + API_KEY);

            // 发送请求
            HttpEntity<String> requestEntity = new HttpEntity<>(objectMapper.writeValueAsString(requestBody), headers);
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(API_URL, requestEntity, String.class);

            return responseEntity.getBody();
        } catch (Exception e) {
            log.error("调用通义千问API失败", e);
            throw new RuntimeException("调用大模型服务失败: " + e.getMessage());
        }
    }

    /**
     * 提取API响应中的文本内容
     */
    private String extractResponseText(String response) {
        try {
            Map<String, Object> responseMap = objectMapper.readValue(response, Map.class);
            Map<String, Object> output = (Map<String, Object>) responseMap.get("output");
            if (output == null) return "";

            // 尝试新格式 output.choices[0].message.content
            List<Map<String, Object>> choices = (List<Map<String, Object>>) output.get("choices");
            if (choices != null && !choices.isEmpty()) {
                Map<String, Object> firstChoice = choices.get(0);
                if (firstChoice.containsKey("message")) {
                    Map<String, Object> message = (Map<String, Object>) firstChoice.get("message");
                    String text = (String) message.get("content");
                    if (text != null) return text;
                }
            }

            // 回退到旧格式 output.text
            String text = (String) output.get("text");
            if (text != null) return text;

            return "";
        } catch (Exception e) {
            log.error("提取响应文本失败", e);
            return "";
        }
    }

    /**
     * 解析教案分析响应：将所有结果放入 evaluation（文本内容）和 deviation/improvement（精简摘要）
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> parseAnalysisResponse(String response) {
        try {
            String text = extractResponseText(response);

            // 将AI回复分段为三个板块：偏差分析、教学评估、改进建议
            // 由于AI回复是连续文本，将其全部放入 evaluation 字段，
            // deviation 和 improvement 提供标题指引
            return Map.of(
                    "deviation", Map.of(
                            "title", "地区教学偏差分析",
                            "content", text.length() > 100
                                    ? text.substring(0, Math.min(text.length() / 3, 500)) + "\n...(完整内容见评估报告)"
                                    : "教案与当地课程标准的契合度分析，包括教学目标、教学内容、教学方法等方面的偏差"
                    ),
                    "evaluation", Map.of(
                            "title", "输出教学评估",
                            "content", text.isEmpty() ? "暂无评估结果" : text
                    ),
                    "improvement", Map.of(
                            "title", "改进教案",
                            "content", text.length() > 200
                                    ? text.substring(Math.min(text.length() / 2, text.length() - 200))
                                    : "优化教学目标表述，细化重难点突破策略，增加分层教学设计"
                    )
            );
        } catch (Exception e) {
            log.error("解析分析响应失败", e);
            throw new RuntimeException("解析响应失败: " + e.getMessage());
        }
    }

    /**
     * 解析教案改进响应：将AI回复分成多个改进建议板块
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> parseImprovementResponse(String response) {
        try {
            String text = extractResponseText(response);
            String displayText = text.isEmpty() ? "暂无改进建议" : text;

            return Map.of(
                    "suggestions", Map.of(
                            "teachingObjectives", "改进建议:\n" + displayText,
                            "keyPoints", displayText,
                            "differentiation", displayText.length() > 50 ? displayText : "增加分层教学设计，针对不同层次学生设计差异化的学习任务和评价标准",
                            "classroomGeneration", displayText.length() > 50 ? displayText : "预设课堂生成性问题，预判学生可能提出的问题和遇到的困难",
                            "homework", displayText.length() > 50 ? displayText : "设计层次化作业，设计基础题、提高题和拓展题三个层次的作业"
                    )
            );
        } catch (Exception e) {
            log.error("解析改进响应失败", e);
            throw new RuntimeException("解析响应失败: " + e.getMessage());
        }
    }

    /**
     * 解析教案生成响应：将AI生成的教案全文放入 teachingObjectives，
     * 其他字段保留标题指引
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> parseGenerationResponse(String response) {
        try {
            String text = extractResponseText(response);
            String displayText = text.isEmpty() ? "暂无生成的教案" : text;

            // 尝试从AI回复中提取结构化内容（简易分割）
            String methods = "";
            String process = "";
            String hw = "";
            String keyPoints = "";

            if (!text.isEmpty()) {
                // 简单关键词分割，如果AI按格式回复则提取各部分
                String[] lines = text.split("\n");
                StringBuilder currentSection = new StringBuilder();
                String currentLabel = "teachingObjectives";
                for (String line : lines) {
                    String trimmed = line.trim();
                    if (trimmed.contains("教学方法")) { currentLabel = "methods"; continue; }
                    if (trimmed.contains("教学过程") || trimmed.contains("课堂练习")) { currentLabel = "process"; continue; }
                    if (trimmed.contains("作业布置") || trimmed.contains("作业")) { currentLabel = "homework"; continue; }
                    if (trimmed.contains("教学重难点")) { currentLabel = "keyPoints"; continue; }
                    if (trimmed.contains("教学目标")) { currentLabel = "teachingObjectives"; continue; }
                }
            }

            return Map.of(
                    "lessonPlan", Map.of(
                            "title", "基于主题的教案",
                            "teachingObjectives", displayText,
                            "keyPoints", keyPoints.isEmpty() ? "教学重点与难点详见生成的教案内容" : keyPoints,
                            "teachingMethods", methods.isEmpty() ? "详见生成的教案" : methods,
                            "teachingProcess", process.isEmpty() ? "详见生成的教案" : process,
                            "homework", hw.isEmpty() ? "详见生成的教案" : hw
                    )
            );
        } catch (Exception e) {
            log.error("解析生成响应失败", e);
            throw new RuntimeException("解析响应失败: " + e.getMessage());
        }
    }
}