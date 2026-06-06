package com.shusixue.config.filter;

import com.shusixue.annotation.Idempotent;
import com.shusixue.common.CacheConstants;
import com.shusixue.common.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * 幂等性拦截器
 * 拦截带有@Idempotent注解的请求，验证Token确保接口幂等性
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class IdempotentInterceptor implements HandlerInterceptor {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();

        if (!method.isAnnotationPresent(Idempotent.class)) {
            return true;
        }

        Idempotent idempotent = method.getAnnotation(Idempotent.class);
        String headerName = idempotent.headerName();
        String token = request.getHeader(headerName);

        // 幂等性Token为空时直接放行（兼容未传递幂等Token的客户端）
        if (token == null || token.isEmpty()) {
            return true;
        }

        String cacheKey = CacheConstants.buildIdempotentTokenKey(token);
        
        try {
            Boolean success = redisTemplate.opsForValue().setIfAbsent(
                    cacheKey, 
                    request.getRequestURI(), 
                    idempotent.expireSeconds(), 
                    TimeUnit.SECONDS
            );

            if (Boolean.FALSE.equals(success)) {
                log.warn("重复提交检测: token={}, uri={}", token, request.getRequestURI());
                responseError(response, idempotent.message());
                return false;
            }

            log.debug("幂等性Token验证通过: token={}, uri={}", token, request.getRequestURI());
            return true;

        } catch (Exception e) {
            log.error("幂等性验证异常: {}", token, e);
            return true;
        }
    }

    private void responseError(HttpServletResponse response, String message) throws Exception {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);
        
        Result<Void> result = Result.fail(message);
        response.getWriter().write(objectMapper.writeValueAsString(result));
        response.getWriter().flush();
    }
}
