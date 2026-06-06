package com.shusixue.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shusixue.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户Mapper，继承MyBatis-Plus的BaseMapper，自带基础CRUD
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
}