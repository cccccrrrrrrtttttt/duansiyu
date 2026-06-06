package com.shusixue.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shusixue.entity.SysOperationLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 操作日志Mapper接口
 */
@Mapper
public interface SysOperationLogMapper extends BaseMapper<SysOperationLog> {
}
