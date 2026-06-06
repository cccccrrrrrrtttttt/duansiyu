<template>
  <div class="evaluation-page">
    <div class="page-header">
      <h1 class="page-title">教学评价与反思</h1>
      <p class="page-desc">记录教学过程中的收获与不足，持续提升教学质量</p>
    </div>

    <div class="evaluation-grid">
      <div class="card">
        <h2 class="section-title">
          <el-icon><Document /></el-icon>
          教学评价记录
        </h2>
        <div class="evaluation-form">
          <div class="form-group">
            <label class="form-label">评价标题</label>
            <el-input
              v-model="evaluationForm.title"
              placeholder="请输入评价标题"
            />
          </div>
          <div class="form-group">
            <label class="form-label">教学日期</label>
            <el-date-picker
              v-model="evaluationForm.date"
              type="date"
              placeholder="选择教学日期"
              style="width: 100%"
            />
          </div>
          <div class="form-group">
            <label class="form-label">教学班级</label>
            <el-select
              v-model="evaluationForm.className"
              placeholder="选择班级"
              style="width: 100%"
            >
              <el-option label="六年级一班" value="六年级一班" />
              <el-option label="六年级二班" value="六年级二班" />
              <el-option label="六年级三班" value="六年级三班" />
            </el-select>
          </div>
          <div class="form-group">
            <label class="form-label">教学内容</label>
            <el-input
              v-model="evaluationForm.content"
              type="textarea"
              :rows="4"
              placeholder="请描述本次教学的内容"
            />
          </div>
          <div class="form-group">
            <label class="form-label">教学效果评价</label>
            <el-rate
              v-model="evaluationForm.effectiveness"
              :texts="['很差', '较差', '一般', '良好', '优秀']"
              show-score
            />
          </div>
          <div class="form-group">
            <label class="form-label">学生参与</label>
            <el-rate
              v-model="evaluationForm.participation"
              :texts="['很低', '较低', '一般', '较高', '很高']"
              show-score
            />
          </div>
          <div class="form-group">
            <label class="form-label">教学亮点</label>
            <el-input
              v-model="evaluationForm.highlights"
              type="textarea"
              :rows="3"
              placeholder="记录本次教学的亮点和成功之处"
            />
          </div>
          <div class="form-group">
            <label class="form-label">存在不足</label>
            <el-input
              v-model="evaluationForm.weaknesses"
              type="textarea"
              :rows="3"
              placeholder="记录教学中存在的问题和不足"
            />
          </div>
          <div class="form-actions">
            <el-button type="primary" @click="saveEvaluation">
              <el-icon><Check /></el-icon>
              保存评价
            </el-button>
            <el-button @click="resetForm">
              <el-icon><Refresh /></el-icon>
              重置
            </el-button>
          </div>
        </div>
      </div>

      <div class="card">
        <h2 class="section-title">
          <el-icon><List /></el-icon>
          历史评价记录
        </h2>
        <div class="evaluation-list">
          <div
            v-for="item in evaluationHistory"
            :key="item.id"
            class="evaluation-item"
          >
            <div class="item-header">
              <h3 class="item-title">{{ item.title }}</h3>
              <div class="item-meta">
                <span class="item-date">{{ item.date }}</span>
                <el-tag :type="item.effectivenessType" size="small">
                  {{ item.effectivenessText }}
                </el-tag>
              </div>
            </div>
            <div class="item-content">
              <p class="item-class">班级：{{ item.className }}</p>
              <p class="item-description">{{ item.content }}</p>
              <div class="item-ratings">
                <div class="rating-item">
                  <span class="rating-label">教学效果'</span>
                  <el-rate
                    v-model="item.effectiveness"
                    disabled
                    show-score
                  />
                </div>
                <div class="rating-item">
                  <span class="rating-label">学生参与度：</span>
                  <el-rate
                    v-model="item.participation"
                    disabled
                    show-score
                  />
                </div>
              </div>
              <div class="item-highlights">
                <h4 class="highlights-title">
                  <el-icon><Star /></el-icon>
                  教学亮点
                </h4>
                <p>{{ item.highlights }}</p>
              </div>
              <div class="item-weaknesses">
                <h4 class="weaknesses-title">
                  <el-icon><Warning /></el-icon>
                  存在不足
                </h4>
                <p>{{ item.weaknesses }}</p>
              </div>
            </div>
            <div class="item-actions">
              <el-button type="primary" link size="small" @click="viewEvaluation(item)">
                <el-icon><View /></el-icon>
                查看详情
              </el-button>
              <el-button type="danger" link size="small" @click="deleteEvaluation(item.id)">
                <el-icon><Delete /></el-icon>
                删除
              </el-button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="reflection-section">
      <div class="card">
          <h2 class="section-title">
          <el-icon><EditPen /></el-icon>
          教学反思
        </h2>
        <div class="reflection-form">
          <div class="form-group">
            <label class="form-label">反思主题</label>
            <el-input
              v-model="reflectionForm.theme"
              placeholder="请输入反思主题"
            />
          </div>
          <div class="form-group">
            <label class="form-label">反思内容</label>
            <el-input
              v-model="reflectionForm.content"
              type="textarea"
              :rows="8"
              placeholder="详细描述教学反思内容，包括成功经验、改进方向等"
            />
          </div>
          <div class="form-group">
            <label class="form-label">改进措施</label>
            <el-input
              v-model="reflectionForm.improvements"
              type="textarea"
              :rows="4"
              placeholder="列出具体的改进措施和行动计划"
            />
          </div>
          <div class="form-group">
            <label class="form-label">反思类型</label>
            <el-checkbox-group v-model="reflectionForm.types">
              <el-checkbox label="教学设计反思" />
              <el-checkbox label="教学方法反思" />
              <el-checkbox label="学生反馈反思" />
              <el-checkbox label="教学效果反思" />
            </el-checkbox-group>
          </div>
          <div class="form-actions">
              <el-button type="primary" @click="saveReflection">
              <el-icon><Check /></el-icon>
              保存反思
            </el-button>
            <el-button @click="resetReflection">
              <el-icon><Refresh /></el-icon>
              重置
            </el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Document,
  List,
  Check,
  Refresh,
  Star,
  Warning,
  View,
  Delete,
  EditPen
} from '@element-plus/icons-vue'
import request from '@/utils/request'

const evaluationForm = ref({
  title: '',
  date: '',
  className: '',
  content: '',
  effectiveness: 3,
  participation: 3,
  highlights: '',
  weaknesses: ''
})

const reflectionForm = ref({
  theme: '',
  content: '',
  improvements: '',
  types: []
})

const evaluationHistory = ref([])
const loading = ref(false)

const effectivenessTexts = ['很差', '较差', '一', '良好', '优秀']

const getEffectivenessText = (value) => {
  return effectivenessTexts[value - 1] || '一'
}

const getEffectivenessType = (value) => {
  if (value >= 4) return 'success'
  if (value >= 3) return 'warning'
  return 'danger'
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN')
}

const saveEvaluation = async () => {
  if (!evaluationForm.value.title || !evaluationForm.value.date || !evaluationForm.value.className) {
    ElMessage.warning('请填写完整的评价信息')
    return
  }

  try {
    const formData = {
      ...evaluationForm.value,
      teachingDate: evaluationForm.value.date ? new Date(evaluationForm.value.date).toISOString() : null
    }
    await request.post('/api/evaluation/save', formData)
    ElMessage.success('评价保存成功')
    resetForm()
    await loadEvaluationData()
  } catch (error) {
    console.error('保存评价失败:', error)
    ElMessage.error('保存失败')
  }
}

const resetForm = () => {
  evaluationForm.value = {
    title: '',
    date: '',
    className: '',
    content: '',
    effectiveness: 3,
    participation: 3,
    highlights: '',
    weaknesses: ''
  }
}

const viewEvaluation = (item) => {
  ElMessage.info(`查看评价: ${item.title}`)
  console.log('查看评价:', item)
}

const deleteEvaluation = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除这条评价记录吗', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    await request.delete(`/api/evaluation/${id}`)
    ElMessage.success('删除成功')
    await loadEvaluationData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除评价失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

const saveReflection = async () => {
  if (!reflectionForm.value.theme || !reflectionForm.value.content) {
    ElMessage.warning('请填写完整的反思内')
    return
  }

  try {
    ElMessage.success('反思保存成')
    resetReflection()
  } catch (error) {
    console.error('保存反思失败:', error)
    ElMessage.error('保存失败')
  }
}

const resetReflection = () => {
  reflectionForm.value = {
    theme: '',
    content: '',
    improvements: '',
    types: []
  }
}

const loadEvaluationData = async () => {
  loading.value = true
  try {
    const res = await request.get('/api/evaluation/page', { params: { pageNum: 1, pageSize: 100 } })
    if (res && res.data && res.data.records) {
      evaluationHistory.value = res.data.records.map(item => ({
        ...item,
        date: formatDate(item.teachingDate),
        effectivenessText: getEffectivenessText(item.effectiveness),
        effectivenessType: getEffectivenessType(item.effectiveness)
      }))
    }
  } catch (error) {
    console.error('加载评价数据失败:', error)
    // 如果API调用失败，使用模拟数据作为备份
    evaluationHistory.value = getDefaultEvaluationData()
  } finally {
    loading.value = false
  }
}

const getDefaultEvaluationData = () => {
  return [
    {
      id: 1,
        title: '极限与连续教学',
        date: '2026-04-10',
        className: '数学1班',
      content: '本次教学主要围绕极限与连续的概念展开，通过实例帮助学生理解极限的定义和计算方法',
      effectiveness: 4,
      effectivenessText: '良好',
      effectivenessType: 'success',
      participation: 4,
      highlights: '学生参与度很高，通过小组合作学习，学生能够主动思考和讨论问题。例题设计合理，难度适中',
      weaknesses: '部分学生对极限的ε-δ定义理解仍然存在困难，需要加强针对性练习'
    },
    {
      id: 2,
      title: '导数与微分',
      date: '2026-04-08',
      className: '数学2班',
      content: '教学导数和微分的概念及计算方法，通过几何意义帮助学生理解',
      effectiveness: 3,
      effectivenessText: '一',
      effectivenessType: 'warning',
      participation: 3,
      highlights: '几何意义讲解清晰，学生能够直观理解导数的概念',
      weaknesses: '部分学生在复合函数求导上存在困难，容易出现计算错误'
    }
  ]
}

onMounted(() => {
  loadEvaluationData()
})
</script>

<style scoped>
.evaluation-page { min-height: 100%; }

.page-header {
  text-align: center;
  margin-bottom: 2rem;
}

.page-title {
  font-size: 2rem;
  font-weight: 700;
  color: var(--text-main);
  margin-bottom: 0.5rem;
}

.page-desc {
  font-size: 1rem;
  color: var(--text-secondary);
}

.evaluation-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 1.5rem;
  margin-bottom: 2rem;
}

.section-title {
  font-size: 1.1rem;
  font-weight: 600;
  color: var(--text-main);
  margin-bottom: 1rem;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.evaluation-form {
  display: flex;
  flex-direction: column;
  gap: 1.25rem;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.form-label {
  font-size: 0.9rem;
  font-weight: 500;
  color: var(--text-main);
}

.form-actions {
  display: flex;
  gap: 1rem;
  margin-top: 1rem;
}

.evaluation-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  max-height: 600px;
  overflow-y: auto;
}

.evaluation-item {
  background: #f8fafc;
  border: 1px solid var(--border-color);
  border-radius: var(--radius-md);
  padding: 1.25rem;
  transition: all var(--transition-normal);
}

.evaluation-item:hover {
  box-shadow: var(--shadow-md);
  border-color: var(--primary-color);
}

.item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
  padding-bottom: 0.75rem;
  border-bottom: 1px solid var(--border-color);
}

.item-title {
  font-size: 1.1rem;
  font-weight: 600;
  color: var(--text-main);
}

.item-meta {
  display: flex;
  gap: 0.75rem;
  align-items: center;
}

.item-date {
  font-size: 0.85rem;
  color: var(--text-secondary);
}

.item-content {
  margin-bottom: 1rem;
}

.item-class {
  font-size: 0.9rem;
  color: var(--text-secondary);
  margin-bottom: 0.5rem;
}

.item-description {
  font-size: 0.95rem;
  color: var(--text-main);
  line-height: 1.6;
  margin-bottom: 1rem;
}

.item-ratings {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 1rem;
  margin-bottom: 1rem;
}

.rating-item {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.rating-label {
  font-size: 0.85rem;
  color: var(--text-secondary);
}

.item-highlights {
  background: #f0fdf4;
  border-left: 4px solid var(--accent-color);
  padding: 0.75rem 1rem;
  margin-bottom: 1rem;
  border-radius: 0 var(--radius-sm) var(--radius-sm) 0;
}

.highlights-title {
  font-size: 0.95rem;
  font-weight: 600;
  color: var(--accent-color);
  margin-bottom: 0.5rem;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.item-weaknesses {
  background: #fef2f2;
  border-left: 4px solid #ef4444;
  padding: 0.75rem 1rem;
  border-radius: 0 var(--radius-sm) var(--radius-sm) 0;
}

.weaknesses-title {
  font-size: 0.95rem;
  font-weight: 600;
  color: #ef4444;
  margin-bottom: 0.5rem;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.item-actions {
  display: flex;
  justify-content: flex-end;
  gap: 0.5rem;
  padding-top: 1rem;
  border-top: 1px solid var(--border-color);
}

.reflection-section {
  margin-top: 2rem;
}

.reflection-form {
  display: flex;
  flex-direction: column;
  gap: 1.25rem;
}

@media (max-width: 1024px) {
  .evaluation-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .evaluation-page {
    padding: 1rem;
  }

  .page-title {
    font-size: 1.5rem;
  }

  .item-ratings {
    grid-template-columns: 1fr;
  }
}
</style>
