package com.shusixue.annotation;

import java.lang.annotation.*;

/**
 * 幂等性注解
 * 标注在需要保证幂等性的接口方法上
 * 
 * 实现原理：
 * 1. 前端请求时携带X-Idempotent-Token请求头
 * 2. 后端拦截器验证Token，验证通过则执行，否则返回重复提交提示
 * 3. 使用Redis存储Token，保证分布式环境下的幂等性
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Idempotent {

    /**
     * Token所在的请求头名称，默认X-Idempotent-Token
     */
    String headerName() default "X-Idempotent-Token";

    /**
     * Token过期时间（秒），默认5分钟
     */
    long expireSeconds() default 300;

    /**
     * 重复提交时的错误提示信息
     */
    String message() default "请勿重复提交";
}
