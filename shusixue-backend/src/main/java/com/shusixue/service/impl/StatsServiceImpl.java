package com.shusixue.service.impl;

import com.shusixue.mapper.KnowledgePointMapper;
import com.shusixue.mapper.QuestionMapper;
import com.shusixue.service.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 统计服务实现类
 */
@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final KnowledgePointMapper knowledgePointMapper;
    private final QuestionMapper questionMapper;

    @Override
    public Map<String, Long> getHomeStats() {
        Map<String, Long> stats = new HashMap<>();
        
        // 获取知识点数量
        Long knowledgeCount = knowledgePointMapper.selectCount(null);
        stats.put("knowledgeCount", knowledgeCount);
        
        // 获取题目数量
        Long questionCount = questionMapper.selectCount(null);
        stats.put("questionCount", questionCount);
        
        return stats;
    }
}