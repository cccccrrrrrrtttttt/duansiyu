package com.shusixue.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shusixue.common.CacheConstants;
import com.shusixue.dto.KnowledgePointDTO;
import com.shusixue.entity.KnowledgePoint;
import com.shusixue.mapper.KnowledgePointMapper;
import com.shusixue.service.KnowledgePointService;
import com.shusixue.utils.CacheUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 知识点服务实现类（带缓存优化）
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class KnowledgePointServiceImpl extends ServiceImpl<KnowledgePointMapper, KnowledgePoint> implements KnowledgePointService {

    private final CacheUtil cacheUtil;

    @Override
    public void saveOrUpdateKnowledgePoint(KnowledgePointDTO dto) {
        // 手动校验名称
        String name = dto.getEffectiveName();
        log.info("保存知识点 - 名称: {}, 科目: {}, 父ID: {}, 排序: {}", name, dto.getSubject(), dto.getParentId(), dto.getSort());
        
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("知识点名称不能为空");
        }
        
        KnowledgePoint knowledgePoint = new KnowledgePoint();
        BeanUtils.copyProperties(dto, knowledgePoint);
        knowledgePoint.setName(name);
        
        if (dto.getSort() == null) {
            knowledgePoint.setSort(0);
        }
        
        boolean result = this.saveOrUpdate(knowledgePoint);
        log.info("知识点保存结果: {}, 保存后的ID: {}", result, knowledgePoint.getId());
        
        // 更新缓存：删除所有知识点相关缓存，下次查询时重新加载
        clearKnowledgeCache();
    }

    @Override
    public void deleteKnowledgePoint(Long id) {
        // 简单逻辑删除，实际项目可检查是否有题目关联
        this.removeById(id);
        
        // 更新缓存：删除所有知识点相关缓存
        clearKnowledgeCache();
    }

    @Override
    public List<KnowledgePoint> getKnowledgePointTree(String subject) {
        // 构建缓存Key
        String cacheKey = CacheConstants.buildKnowledgeTreeKey(subject);
        
        // 先从缓存获取
        List<KnowledgePoint> cachedList = cacheUtil.getWithPenetrationProtection(cacheKey, List.class);
        if (cachedList != null) {
            log.info("知识点列表缓存命中 - 科目: {}, 数量: {}", subject, cachedList.size());
            return cachedList;
        }
        
        // 缓存未命中，从数据库查询
        log.info("知识点列表缓存未命中，从数据库查询 - 科目: {}", subject);
        List<KnowledgePoint> list = this.list(new LambdaQueryWrapper<KnowledgePoint>()
                .eq(StringUtils.hasText(subject), KnowledgePoint::getSubject, subject)
                .orderByAsc(KnowledgePoint::getSort)
                .orderByAsc(KnowledgePoint::getId));
        
        log.info("查询到知识点数量: {}", list.size());
        
        // 设置缓存（空结果也缓存，防止缓存穿透）
        if (list.isEmpty()) {
            cacheUtil.setEmptyValue(cacheKey);
        } else {
            cacheUtil.set(cacheKey, list, CacheConstants.KNOWLEDGE_TREE_EXPIRE);
        }
        
        return list;
    }

    /**
     * 清除所有知识点相关缓存
     */
    private void clearKnowledgeCache() {
        log.info("清除知识点相关缓存");
        // 删除所有知识点树形结构缓存
        // 由于Redis没有通配符删除的原子操作，这里可以使用RedisTemplate的keys方法
        // 但生产环境建议使用更高效的方式，如维护缓存key列表
        try {
            // 简化处理，删除常见的缓存key
            cacheUtil.delete(CacheConstants.KNOWLEDGE_TREE_PREFIX + "all");
            cacheUtil.delete(CacheConstants.KNOWLEDGE_TREE_PREFIX + "高等数学");
            cacheUtil.delete(CacheConstants.KNOWLEDGE_TREE_PREFIX + "线性代数");
            cacheUtil.delete(CacheConstants.KNOWLEDGE_TREE_PREFIX + "概率论");
            cacheUtil.delete(CacheConstants.KNOWLEDGE_TREE_PREFIX + "数学建模");
        } catch (Exception e) {
            log.error("清除知识点缓存失败", e);
        }
    }
}
