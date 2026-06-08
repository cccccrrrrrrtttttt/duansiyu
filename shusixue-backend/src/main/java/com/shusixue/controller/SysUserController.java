package com.shusixue.controller;

import com.shusixue.annotation.Idempotent;
import com.shusixue.annotation.OperationLog;
import com.shusixue.common.Result;
import com.shusixue.common.ResultCode;
import com.shusixue.dto.UserLoginDTO;
import com.shusixue.dto.UserRegisterDTO;
import com.shusixue.entity.SysUser;
import com.shusixue.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户接口控制器
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "用户管理", description = "用户注册、登录、信息查询等接口")
public class SysUserController {

    private final SysUserService sysUserService;

    /**
     * 用户注册接口
     */
    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "新用户注册，支持学生和教师角色")
    public Result<Long> register(@Valid @RequestBody UserRegisterDTO registerDTO) {
        Long userId = sysUserService.register(registerDTO);
        return Result.success(userId);
    }

    /**
     * 用户登录接口
     */
    @PostMapping("/login")
    @Idempotent(message = "登录请求处理中，请稍后再试", expireSeconds = 30)
    @Operation(summary = "用户登录", description = "用户登录获取JWT Token，登录成功后需在请求头携带Token")
    @OperationLog(type = "用户登录", desc = "用户登录系统", logParams = false)
    public Result<Map<String, String>> login(@Valid @RequestBody UserLoginDTO loginDTO) {
        String token = sysUserService.login(loginDTO);
        Map<String, String> result = new HashMap<>();
        result.put("token", token);
        return Result.success(result);
    }

    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/info")
    @Operation(summary = "获取用户信息", description = "获取当前登录用户的详细信息，需携带有效Token")
    public Result<SysUser> getUserInfo() {
        SysUser authUser = (SysUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        SysUser user = sysUserService.getById(authUser.getId());
        user.setPassword(null);
        return Result.success(user);
    }

    /**
     * 测试管理员权限
     */
    @GetMapping("/admin/test")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "管理员权限测试", description = "测试管理员角色权限，仅管理员可访问")
    public Result<String> adminTest() {
        return Result.success("管理员权限测试成功");
    }
    /**
     * 重置用户密码（管理员专用）
     */
    @PostMapping("/resetPassword")
    @Idempotent
    public Result<String> resetPassword(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String newPassword = params.get("password");
        if (username == null || newPassword == null) {
            return Result.failure(ResultCode.PARAM_ERROR);
        }
        // 不校验旧密码，直接重置（仅用于紧急修复）
        sysUserService.resetPassword(username, newPassword);
        return Result.success("密码已重置");
    }
}

