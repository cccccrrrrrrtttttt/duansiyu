package com.shusixue.utils;

import com.shusixue.common.CacheConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 缓存工具类
 * 提供统一的缓存操作方法，封装Redis操作
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CacheUtil {

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 获取缓存
     * @param key 缓存Key
     * @param clazz 返回类型
     */
    public <T> T get(String key, Class<T> clazz) {
        try {
            Object value = redisTemplate.opsForValue().get(key);
            if (value != null) {
                log.debug("缓存命中: {}", key);
                return clazz.cast(value);
            }
            log.debug("缓存未命中: {}", key);
            return null;
        } catch (Exception e) {
            log.error("获取缓存失败: {}", key, e);
            return null;
        }
    }

    /**
     * 设置缓存（带超时时间）
     * @param key 缓存Key
     * @param value 缓存值
     * @param expireSeconds 超时时间（秒）
     */
    public void set(String key, Object value, long expireSeconds) {
        try {
            redisTemplate.opsForValue().set(key, value, expireSeconds, TimeUnit.SECONDS);
            log.debug("设置缓存: {}, 超时时间: {}秒", key, expireSeconds);
        } catch (Exception e) {
            log.error("设置缓存失败: {}", key, e);
        }
    }

    /**
     * 设置缓存（永久）
     * @param key 缓存Key
     * @param value 缓存值
     */
    public void set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            log.debug("设置永久缓存: {}", key);
        } catch (Exception e) {
            log.error("设置缓存失败: {}", key, e);
        }
    }

    /**
     * 删除缓存
     * @param key 缓存Key
     */
    public void delete(String key) {
        try {
            redisTemplate.delete(key);
            log.debug("删除缓存: {}", key);
        } catch (Exception e) {
            log.error("删除缓存失败: {}", key, e);
        }
    }

    /**
     * 判断缓存是否存在
     * @param key 缓存Key
     */
    public boolean exists(String key) {
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            log.error("判断缓存是否存在失败: {}", key, e);
            return false;
        }
    }

    /**
     * 设置空值缓存（防止缓存穿透）
     * @param key 缓存Key
     */
    public void setEmptyValue(String key) {
        set(key, "", CacheConstants.EMPTY_VALUE_EXPIRE);
    }

    /**
     * 判断是否为空值缓存
     * @param key 缓存Key
     */
    public boolean isEmptyValue(Object value) {
        return value != null && "".equals(value);
    }

    /**
     * 更新缓存超时时间
     * @param key 缓存Key
     * @param expireSeconds 新的超时时间（秒）
     */
    public void expire(String key, long expireSeconds) {
        try {
            redisTemplate.expire(key, expireSeconds, TimeUnit.SECONDS);
            log.debug("更新缓存超时时间: {}, 新超时时间: {}秒", key, expireSeconds);
        } catch (Exception e) {
            log.error("更新缓存超时时间失败: {}", key, e);
        }
    }

    /**
     * 缓存穿透防护的获取方法
     * 若缓存为空值则返回null
     */
    public <T> T getWithPenetrationProtection(String key, Class<T> clazz) {
        Object value = redisTemplate.opsForValue().get(key);
        if (value == null) {
            return null;
        }
        // 空值缓存，返回null
        if (isEmptyValue(value)) {
            log.debug("空值缓存命中，防止穿透: {}", key);
            return null;
        }
        return clazz.cast(value);
    }
}
