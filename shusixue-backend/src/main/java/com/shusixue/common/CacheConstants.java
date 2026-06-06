package com.shusixue.common;

/**
 * 缓存常量类
 * 定义所有缓存Key前缀和超时时间
 */
public class CacheConstants {

    // ==================== 缓存Key前缀 ====================
    
    /**
     * 知识点树形结构缓存前缀
     * 格式: knowledge:tree:[subject]
     */
    public static final String KNOWLEDGE_TREE_PREFIX = "knowledge:tree:";
    
    /**
     * 题目详情缓存前缀
     * 格式: question:detail:[questionId]
     */
    public static final String QUESTION_DETAIL_PREFIX = "question:detail:";
    
    /**
     * 作业列表缓存前缀（分页）
     * 格式: homework:list:[userId]:[pageNum]:[pageSize]
     */
    public static final String HOMEWORK_LIST_PREFIX = "homework:list:";
    
    /**
     * 作业详情缓存前缀
     * 格式: homework:detail:[homeworkId]
     */
    public static final String HOMEWORK_DETAIL_PREFIX = "homework:detail:";
    
    /**
     * 用户信息缓存前缀
     * 格式: user:info:[userId]
     */
    public static final String USER_INFO_PREFIX = "user:info:";
    
    /**
     * 幂等性Token缓存前缀
     * 格式: idempotent:token:[token]
     */
    public static final String IDEMPOTENT_TOKEN_PREFIX = "idempotent:token:";

    // ==================== 缓存超时时间（秒） ====================
    
    /**
     * 知识点树形结构缓存超时时间（24小时）
     */
    public static final long KNOWLEDGE_TREE_EXPIRE = 24 * 60 * 60;
    
    /**
     * 题目详情缓存超时时间（12小时）
     */
    public static final long QUESTION_DETAIL_EXPIRE = 12 * 60 * 60;
    
    /**
     * 作业列表缓存超时时间（1小时）
     */
    public static final long HOMEWORK_LIST_EXPIRE = 60 * 60;
    
    /**
     * 作业详情缓存超时时间（2小时）
     */
    public static final long HOMEWORK_DETAIL_EXPIRE = 2 * 60 * 60;
    
    /**
     * 用户信息缓存超时时间（30分钟）
     */
    public static final long USER_INFO_EXPIRE = 30 * 60;
    
    /**
     * 幂等性Token缓存超时时间（5分钟）
     */
    public static final long IDEMPOTENT_TOKEN_EXPIRE = 5 * 60;
    
    /**
     * 空值缓存超时时间（5分钟）- 防止缓存穿透
     */
    public static final long EMPTY_VALUE_EXPIRE = 5 * 60;

    // ==================== 缓存Key构建方法 ====================
    
    /**
     * 构建知识点树形结构缓存Key
     */
    public static String buildKnowledgeTreeKey(String subject) {
        return KNOWLEDGE_TREE_PREFIX + (subject != null ? subject : "all");
    }
    
    /**
     * 构建题目详情缓存Key
     */
    public static String buildQuestionDetailKey(Long questionId) {
        return QUESTION_DETAIL_PREFIX + questionId;
    }
    
    /**
     * 构建作业列表缓存Key
     */
    public static String buildHomeworkListKey(Long userId, Integer pageNum, Integer pageSize) {
        return HOMEWORK_LIST_PREFIX + userId + ":" + pageNum + ":" + pageSize;
    }
    
    /**
     * 构建作业详情缓存Key
     */
    public static String buildHomeworkDetailKey(Long homeworkId) {
        return HOMEWORK_DETAIL_PREFIX + homeworkId;
    }
    
    /**
     * 构建用户信息缓存Key
     */
    public static String buildUserInfoKey(Long userId) {
        return USER_INFO_PREFIX + userId;
    }
    
    /**
     * 构建幂等性Token缓存Key
     */
    public static String buildIdempotentTokenKey(String token) {
        return IDEMPOTENT_TOKEN_PREFIX + token;
    }
}
