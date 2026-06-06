package com.shusixue.service;

import java.util.Map;

/**
 * 统计服务接口
 */
public interface StatsService {

    /**
     * 获取首页统计数据
     * @return 包含知识点数量和题目数量的Map
     */
    Map<String, Long> getHomeStats();
}