package com.shusixue.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shusixue.annotation.OperationLog;
import com.shusixue.entity.SysOperationLog;
import com.shusixue.entity.SysUser;
import com.shusixue.mapper.SysOperationLogMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 操作日志AOP切面
 */
@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class OperationLogAspect {

    private final SysOperationLogMapper operationLogMapper;
    private final ObjectMapper objectMapper;

    @Around("@annotation(com.shusixue.annotation.OperationLog)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取方法签名和注解信息
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        OperationLog operationLogAnnotation = method.getAnnotation(OperationLog.class);

        // 获取请求信息
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes != null ? attributes.getRequest() : null;

        // 获取用户信息
        SysUser user = getCurrentUser();

        // 构建日志实体
        SysOperationLog operationLog = SysOperationLog.builder()
                .userId(user != null ? user.getId() : null)
                .userName(user != null ? user.getUsername() : "匿名用户")
                .role(user != null ? user.getRole() : "ANONYMOUS")
                .requestUri(request != null ? request.getRequestURI() : "")
                .requestMethod(request != null ? request.getMethod() : "")
                .requestIp(request != null ? getClientIp(request) : "")
                .operationType(operationLogAnnotation.type())
                .operationDesc(operationLogAnnotation.desc())
                .status("SUCCESS")
                .createTime(LocalDateTime.now())
                .build();

        // 记录请求参数
        if (operationLogAnnotation.logParams()) {
            try {
                Object[] args = joinPoint.getArgs();
                String[] paramNames = signature.getParameterNames();
                if (args != null && args.length > 0) {
                    StringBuilder params = new StringBuilder("{");
                    for (int i = 0; i < args.length; i++) {
                        if (i > 0) {
                            params.append(", ");
                        }
                        // 敏感参数脱敏处理
                        if ("password".equals(paramNames[i]) || "token".equals(paramNames[i])) {
                            params.append("\"").append(paramNames[i]).append("\": \"******\"");
                        } else {
                            try {
                                params.append("\"").append(paramNames[i]).append("\": ");
                                params.append(objectMapper.writeValueAsString(args[i]));
                            } catch (Exception e) {
                                params.append("\"").append(paramNames[i]).append("\": \"[无法序列化]\"");
                            }
                        }
                    }
                    params.append("}");
                    operationLog.setRequestParams(params.toString());
                }
            } catch (Exception e) {
                log.warn("记录请求参数失败", e);
            }
        }

        long startTime = System.currentTimeMillis();
        Object result = null;
        try {
            // 执行目标方法
            result = joinPoint.proceed();

            // 记录响应结果
            if (operationLogAnnotation.logResult() && result != null) {
                try {
                    String resultJson = objectMapper.writeValueAsString(result);
                    // 限制响应结果长度
                    if (resultJson.length() > 2000) {
                        resultJson = resultJson.substring(0, 2000) + "...(truncated)";
                    }
                    operationLog.setResponseResult(resultJson);
                } catch (Exception e) {
                    log.warn("记录响应结果失败", e);
                }
            }

            return result;
        } catch (Throwable throwable) {
            // 记录失败状态和错误信息
            operationLog.setStatus("FAIL");
            String errorMessage = throwable.getMessage();
            if (errorMessage != null && errorMessage.length() > 500) {
                errorMessage = errorMessage.substring(0, 500);
            }
            operationLog.setErrorMessage(errorMessage);
            throw throwable;
        } finally {
            // 计算执行时间
            long executionTime = System.currentTimeMillis() - startTime;
            operationLog.setExecutionTime(executionTime);

            // 异步保存日志（使用try-catch防止日志记录失败影响主业务）
            try {
                operationLogMapper.insert(operationLog);
                log.debug("操作日志记录成功: type={}, desc={}, time={}ms",
                        operationLog.getOperationType(),
                        operationLog.getOperationDesc(),
                        executionTime);
            } catch (Exception e) {
                log.error("操作日志保存失败", e);
            }
        }
    }

    /**
     * 获取当前登录用户
     */
    private SysUser getCurrentUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof SysUser) {
                return (SysUser) authentication.getPrincipal();
            }
        } catch (Exception e) {
            log.debug("获取当前用户失败", e);
        }
        return null;
    }

    /**
     * 获取客户端真实IP
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 多个代理时，第一个IP为真实IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
