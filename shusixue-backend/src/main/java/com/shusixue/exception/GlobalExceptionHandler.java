package com.shusixue.exception;

import com.shusixue.common.Result;
import com.shusixue.common.ResultCode;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.stream.Collectors;

/**
 * 全局异常处理器，统一处理所有异常
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理自定义业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public <T> Result<T> handleBusinessException(BusinessException e) {
        log.warn("业务异常：{}", e.getMessage());
        return Result.fail(e.getMessage());
    }

    /**
     * 处理参数校验异常（RequestBody参数校验）
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public <T> Result<T> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.warn("参数校验失败（RequestBody）：{}", message);
        return Result.fail(ResultCode.PARAM_ERROR.getCode(), message);
    }

    /**
     * 处理参数绑定异常（RequestParam/PathVariable参数校验）
     */
    @ExceptionHandler(BindException.class)
    public <T> Result<T> handleBindException(BindException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        log.warn("参数绑定失败：{}", message);
        return Result.fail(ResultCode.PARAM_ERROR.getCode(), message);
    }

    /**
     * 处理约束违反异常
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public <T> Result<T> handleConstraintViolationException(ConstraintViolationException e) {
        String message = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
        log.warn("约束校验失败：{}", message);
        return Result.fail(ResultCode.PARAM_ERROR.getCode(), message);
    }

    /**
     * 处理缺少请求参数异常
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public <T> Result<T> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        String message = "缺少必填参数：" + e.getParameterName();
        log.warn(message);
        return Result.fail(ResultCode.PARAM_MISSING.getCode(), message);
    }

    /**
     * 处理参数类型不匹配异常
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public <T> Result<T> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        String message = "参数格式错误：" + e.getName() + " 期望类型：" + 
                (e.getRequiredType() != null ? e.getRequiredType().getSimpleName() : "unknown");
        log.warn(message);
        return Result.fail(ResultCode.PARAM_FORMAT_ERROR.getCode(), message);
    }

    /**
     * 处理请求体解析异常
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public <T> Result<T> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.warn("请求体解析失败：{}", e.getMessage());
        return Result.fail(ResultCode.PARAM_FORMAT_ERROR.getCode(), "请求体格式错误，请检查JSON格式");
    }

    /**
     * 处理HTTP方法不支持异常
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public <T> Result<T> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        String message = "不支持的请求方法：" + e.getMethod() + "，支持的方法：" + 
                (e.getSupportedHttpMethods() != null ? e.getSupportedHttpMethods() : "未知");
        log.warn(message);
        return Result.fail(HttpStatus.METHOD_NOT_ALLOWED.value(), message);
    }

    /**
     * 处理媒体类型不支持异常
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public <T> Result<T> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException e) {
        log.warn("不支持的媒体类型：{}", e.getMessage());
        return Result.fail(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(), "不支持的媒体类型");
    }

    /**
     * 处理404异常
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public <T> Result<T> handleNoHandlerFoundException(NoHandlerFoundException e) {
        String message = "接口不存在：" + e.getRequestURL();
        log.warn(message);
        return Result.fail(HttpStatus.NOT_FOUND.value(), message);
    }

    /**
     * 处理认证异常（登录失败等）
     */
    @ExceptionHandler(BadCredentialsException.class)
    public <T> Result<T> handleBadCredentialsException(BadCredentialsException e) {
        log.warn("认证失败：{}", e.getMessage());
        return Result.fail(ResultCode.PASSWORD_ERROR.getCode(), "用户名或密码错误");
    }

    /**
     * 处理认证异常
     */
    @ExceptionHandler(AuthenticationException.class)
    public <T> Result<T> handleAuthenticationException(AuthenticationException e) {
        log.warn("认证异常：{}", e.getMessage());
        return Result.fail(ResultCode.LOGIN_REQUIRED.getCode(), "请先登录");
    }

    /**
     * 处理权限拒绝异常
     */
    @ExceptionHandler(AccessDeniedException.class)
    public <T> Result<T> handleAccessDeniedException(AccessDeniedException e) {
        log.warn("权限拒绝：{}", e.getMessage());
        return Result.fail(ResultCode.NO_PERMISSION.getCode(), "无操作权限");
    }

    /**
     * 处理数据库异常
     */
    @ExceptionHandler(DataAccessException.class)
    public <T> Result<T> handleDataAccessException(DataAccessException e) {
        log.error("数据库操作异常：", e);
        return Result.fail(ResultCode.DATABASE_ERROR.getCode(), "数据库操作异常，请稍后重试");
    }

    /**
     * 处理其他所有未知异常
     */
    @ExceptionHandler(Exception.class)
    public <T> Result<T> handleException(Exception e) {
        log.error("系统异常：", e);
        // 生产环境不暴露详细错误信息
        return Result.fail(ResultCode.SYSTEM_ERROR.getCode(), "系统内部错误，请联系管理员");
    }
}
