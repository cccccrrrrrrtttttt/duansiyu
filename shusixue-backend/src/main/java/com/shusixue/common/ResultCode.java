package com.shusixue.common;

import lombok.Getter;

/**
 * 全局响应状态码枚举
 */
@Getter
public enum ResultCode {

    // 通用成功失败
    SUCCESS(200, "操作成功"),
    FAIL(500, "操作失败"),

    // 用户相关 1000开头
    USER_NOT_EXIST(1001, "用户不存在"),
    USER_ALREADY_EXIST(1002, "用户名已存在"),
    PASSWORD_ERROR(1003, "密码错误"),
    ACCOUNT_DISABLED(1004, "账号已被禁用"),
    TOKEN_INVALID(1005, "token无效或已过期"),
    NO_PERMISSION(1006, "无操作权限"),
    LOGIN_REQUIRED(1007, "请先登录"),
    REPEAT_SUBMIT(1008, "请勿重复提交"),

    // 参数校验相关 2000开头
    PARAM_ERROR(2001, "参数校验失败"),
    PARAM_MISSING(2002, "缺少必填参数"),
    PARAM_FORMAT_ERROR(2003, "参数格式错误"),

    // 业务相关 3000开头
    BUSINESS_ERROR(3001, "业务异常"),
    RESOURCE_NOT_FOUND(3002, "资源不存在"),
    RESOURCE_ALREADY_EXIST(3003, "资源已存在"),
    OPERATION_NOT_ALLOWED(3004, "操作不允许"),
    DATA_INTEGRITY_ERROR(3005, "数据完整性错误"),

    // 系统相关 5000开头
    SYSTEM_ERROR(5001, "系统内部错误"),
    DATABASE_ERROR(5002, "数据库操作异常"),
    NETWORK_ERROR(5003, "网络异常"),
    SERVICE_UNAVAILABLE(5004, "服务暂不可用"),

    ;

    private final int code;
    private final String msg;

    ResultCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
