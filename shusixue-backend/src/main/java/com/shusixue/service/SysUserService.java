package com.shusixue.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shusixue.entity.SysUser;
import com.shusixue.dto.*;

/**
 * 用户业务接口
 */
public interface SysUserService extends IService<SysUser> {

    /**
     * 用户注册
     * @param registerDTO 注册参数
     * @return 注册成功的用户ID
     */
    Long register(UserRegisterDTO registerDTO);

    /**
     * 用户登录
     * @param loginDTO 登录参数
     * @return 登录成功返回的token
     */
    String login(UserLoginDTO loginDTO);

    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户实体
     */
    void resetPassword(String username, String newPassword);
    SysUser getUserByUsername(String username);
}
