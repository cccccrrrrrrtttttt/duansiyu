package com.shusixue.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shusixue.dto.KnowledgePointDTO;
import com.shusixue.entity.KnowledgePoint;

import java.util.List;

public interface KnowledgePointService extends IService<KnowledgePoint> {

    /**
     * 新增/修改知识点
     */
    void saveOrUpdateKnowledgePoint(KnowledgePointDTO dto);

    /**
     * 删除知识点
     */
    void deleteKnowledgePoint(Long id);

    /**
     * 根据科目查询知识点树
     */
    List<KnowledgePoint> getKnowledgePointTree(String subject);
}