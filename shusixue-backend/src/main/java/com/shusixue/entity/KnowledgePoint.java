package com.shusixue.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 知识点实体类，对应knowledge_point表
 */
@Data
@TableName("knowledge_point")
public class KnowledgePoint {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 知识点名称
     */
    private String name;

    /**
     * 科目：高等数学/线性代数/概率论/数学建模
     */
    private String subject;

    /**
     * 父知识点ID，0表示顶级科目
     */
    private Long parentId;

    /**
     * 排序
     */
    private Integer sort;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 前端使用的title字段，与name保持一致
     */
    public String getTitle() {
        return name;
    }

    public void setTitle(String title) {
        this.name = title;
    }
}