package com.shusixue.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shusixue.entity.Question;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface QuestionMapper extends BaseMapper<Question> {
}