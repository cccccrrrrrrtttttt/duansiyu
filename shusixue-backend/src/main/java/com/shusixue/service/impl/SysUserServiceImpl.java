package com.shusixue.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shusixue.common.ResultCode;
import com.shusixue.dto.*;
import com.shusixue.entity.SysUser;
import com.shusixue.exception.BusinessException;
import com.shusixue.mapper.SysUserMapper;
import com.shusixue.service.SysUserService;
import com.shusixue.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 用户业务实现类
 */
@Service
@RequiredArgsConstructor
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public Long register(UserRegisterDTO registerDTO) {
        // 0. 校验角色：只允许注册学生或老师，不允许注册管理员
        String role = registerDTO.getRole();
        if (!"STUDENT".equals(role) && !"TEACHER".equals(role)) {
            throw new BusinessException("注册角色只能是学生或老师，不能注册管理员");
        }
        // 1. 校验用户名是否已存在
        SysUser existUser = getUserByUsername(registerDTO.getUsername());
        if (existUser != null) {
            throw new BusinessException(ResultCode.USER_ALREADY_EXIST);
        }

        // 2. 构建用户对象，密码加密存储
        SysUser user = new SysUser();
        user.setUsername(registerDTO.getUsername());
        // 密码用BCrypt加密，绝对不能明文存数据库！
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setRealName(registerDTO.getRealName());
        user.setRole(role);
        user.setEmail(registerDTO.getEmail());
        user.setPhone(registerDTO.getPhone());
        user.setStatus(1); // 默认正常状态

        // 3. 保存到数据库
        this.save(user);
        return user.getId();
    }

    @Override
    public String login(UserLoginDTO loginDTO) {
        // 1. 查询用户
        SysUser user = getUserByUsername(loginDTO.getUsername());
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_EXIST);
        }

        // 2. 校验账号状态
        if (user.getStatus() == 0) {
            throw new BusinessException(ResultCode.ACCOUNT_DISABLED);
        }

        // 3. 校验密码
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.PASSWORD_ERROR);
        }

        // 4. 生成JWT token返回
        return jwtUtil.generateToken(user.getUsername(), user.getId(), user.getRole());
    }

    @Override
    public SysUser getUserByUsername(String username) {
        return this.getOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, username)
                .eq(SysUser::getDeleted, 0));
    }
    @Override
    public void resetPassword(String username, String newPassword) {
        SysUser user = getOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, username));
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_EXIST);
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        this.updateById(user);
    }
}
