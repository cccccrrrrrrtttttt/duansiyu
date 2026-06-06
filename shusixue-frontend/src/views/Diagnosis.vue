<template>
  <div class="diagnosis-page">
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">学情分析</h1>
        <p class="page-desc">基于学生作业数据，智能分析学习状况和薄弱点</p>
      </div>
      <div class="header-actions">
        <el-button type="primary" size="large" @click="exportReport">
          <el-icon><Download /></el-icon>
          导出分析报告
        </el-button>
        <el-button size="large" @click="refreshData" :loading="loading">
          <el-icon><Refresh /></el-icon>
          刷新数据
        </el-button>
      </div>
    </div>

    <div class="analysis-grid" v-loading="loading">
      <div class="card">
        <h2 class="section-title">
          <el-icon><TrendCharts /></el-icon>
          学习趋势分析
        </h2>
        <div class="chart-container">
          <div v-if="learningTrend" class="chart-content">
            <canvas ref="trendChart"></canvas>
          </div>
            <div v-else class="chart-placeholder">
            <el-icon :size="60" class="chart-icon">
              <DataLine />
            </el-icon>
            <p class="chart-text">学习趋势图表</p>
            <p class="chart-hint">加载中...</p>
          </div>
        </div>
      </div>

      <div class="card">
        <h2 class="section-title">
          <el-icon><PieChart /></el-icon>
          知识点掌握情况
        </h2>
        <div class="knowledge-stats">
          <div
            v-for="item in knowledgeStats"
            :key="item.id"
            class="stat-item"
          >
            <div class="stat-header">
              <span class="stat-name">{{ item.name }}</span>
              <span :class="['badge', item.badgeClass]">{{ item.level }}</span>
            </div>
            <div class="stat-bar">
              <div
                class="stat-progress"
                :style="{ width: item.progress + '%' }"
              ></div>
            </div>
            <div class="stat-footer">
              <span class="stat-score">{{ item.score }}</span>
              <span class="stat-count">{{ item.count }}</span>
            </div>
          </div>
        </div>
      </div>

      <div class="card">
        <h2 class="section-title">
          <el-icon><Warning /></el-icon>
          薄弱点分析
        </h2>
        <div class="weak-points">
          <div
            v-for="point in weakPoints"
            :key="point.id"
            class="weak-point-item"
          >
            <div class="point-header">
              <el-icon class="point-icon">
                <WarningFilled />
              </el-icon>
              <div class="point-info">
                <h3 class="point-title">{{ point.title }}</h3>
                <p class="point-desc">{{ point.description }}</p>
              </div>
            </div>
              <div class="point-stats">
                <div class="point-stat">
                  <span class="stat-label">错误率</span>
                  <span class="stat-value error">{{ point.errorRate }}%</span>
                </div>
                <div class="point-stat">
                  <span class="stat-label">影响学生</span>
                  <span class="stat-value">{{ point.affectedStudents }}</span>
                </div>
              </div>
            <div class="point-suggestions">
              <h4 class="suggestions-title">改进建议</h4>
              <ul class="suggestions-list">
                <li v-for="(suggestion, index) in point.suggestions" :key="index">
                  {{ suggestion }}
                </li>
              </ul>
            </div>
          </div>
        </div>
      </div>

      <div class="card">
        <h2 class="section-title">
          <el-icon><User /></el-icon>
          学生学习画像
        </h2>
        <div class="student-profiles">
          <div
            v-for="student in studentProfiles"
            :key="student.id"
            class="student-card"
          >
            <div class="student-avatar">
              {{ student.name.charAt(0) }}
            </div>
            <div class="student-info">
              <h3 class="student-name">{{ student.name }}</h3>
              <p class="student-class">{{ student.className }}</p>
              <div class="student-stats">
                <div class="mini-stat">
                  <span class="mini-label">平均分</span>
                  <span class="mini-value">{{ student.avgScore }}</span>
                </div>
                <div class="mini-stat">
                  <span class="mini-label">完成率</span>
                  <span class="mini-value">{{ student.completionRate }}%</span>
                </div>
              </div>
            </div>
            <div class="student-tags">
              <el-tag
                v-for="tag in student.tags"
                :key="tag.label"
                :type="tag.type"
                size="small"
              >
                {{ tag.label }}
              </el-tag>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  TrendCharts,
  PieChart,
  Warning,
  WarningFilled,
  User,
  Download,
  Refresh,
  DataLine
} from '@element-plus/icons-vue'
import request from '@/utils/request'
import axios from 'axios'

const loading = ref(false)
const knowledgeStats = ref([])
const weakPoints = ref([])
const studentProfiles = ref([])
const learningTrend = ref(null)
const trendChart = ref(null)
let chartInstance = null

const exportReport = async () => {
  try {
    // 直接使用axios发起请求，绕过响应拦截器
    const res = await axios.get(`${import.meta.env.VITE_API_BASE_URL || "http://localhost:8081"}/api/diagnosis/export`, {
      responseType: 'blob',
      headers: {
        Authorization: `Bearer ${localStorage.getItem('token')}`
      }
    })

    // 创建下载链接
    const url = window.URL.createObjectURL(new Blob([res.data]))
    const link = document.createElement('a')
    link.href = url
    link.setAttribute('download', `学情分析报告_${new Date().toISOString().split('T')[0]}.pdf`)
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)

    ElMessage.success('分析报告导出成功')
  } catch (error) {
    console.error('导出分析报告失败:', error)
    ElMessage.error('导出分析报告失败: ' + (error.message || '请稍后重'))
  }
}

const refreshData = () => {
  loadDiagnosisData()
}

const loadDiagnosisData = async () => {
  loading.value = true
  try {
    const res = await request.get('/api/diagnosis/data')
    const data = res.data

    knowledgeStats.value = data.knowledgeStats || []
    weakPoints.value = data.weakPoints || []
    studentProfiles.value = data.studentProfiles || []
    learningTrend.value = data.learningTrend || null

    // 渲染学习趋势图表
    if (learningTrend.value) {
      renderTrendChart()
    }
  } catch (error) {
    console.error('加载学情分析数据失败:', error)
    // 不显示错误提示，保持与其他模块一'    // 使用默认数据作为备用
    if (knowledgeStats.value.length === 0) {
      knowledgeStats.value = getDefaultKnowledgeStats()
    }
    if (weakPoints.value.length === 0) {
      weakPoints.value = getDefaultWeakPoints()
    }
    if (studentProfiles.value.length === 0) {
      studentProfiles.value = getDefaultStudentProfiles()
    }
    if (!learningTrend.value) {
      learningTrend.value = getDefaultLearningTrend()
    }
    if (learningTrend.value) {
      renderTrendChart()
    }
  } finally {
    loading.value = false
  }
}

const getDefaultKnowledgeStats = () => {
  return [
    { id: 1, name: '极限与连', level: '良好', badgeClass: 'badge-success', progress: 75, score: 85, count: 24 },
    { id: 2, name: '导数与微', level: '需加强', badgeClass: 'badge-warning', progress: 60, score: 68, count: 18 },
    { id: 3, name: '积分', level: '优秀', badgeClass: 'badge-info', progress: 90, score: 92, count: 30 },
    { id: 4, name: '级数', level: '薄弱', badgeClass: 'badge-danger', progress: 45, score: 52, count: 15 }
  ]
}

const getDefaultWeakPoints = () => {
  return [
    {
      id: 1,
      title: '定积分计',
      description: '学生在定积分公式应用和换元法计算中存在较多错',
      errorRate: 35,
      affectedStudents: 12,
      suggestions: ['加强定积分公式推导过程教', '增加换元法的专项练习', '提供更多实际应用场景题目', '针对易错点进行专项训']
    },
    {
      id: 2,
      title: '级数收敛性判',
      description: '比较审敛法和比值审敛法的应用是主要错误',
      errorRate: 42,
      affectedStudents: 15,
      suggestions: ['强化级数收敛性的概念理解']
    }
  ]
}

const getDefaultStudentProfiles = () => {
  return [
    {
      id: 1,
      name: '张三',
      className: '数学1班',
      avgScore: 85,
      completionRate: 95,
      tags: [{ label: '学习积极', type: 'success' }, { label: '基础扎实', type: 'info' }, { label: '需加强应用', type: 'warning' }]
    },
    {
      id: 2,
      name: '李四',
      className: '数学2班',
      avgScore: 72,
      completionRate: 88,
      tags: [{ label: '态度端正', type: 'success' }, { label: '计算能力', type: 'danger' }, { label: '需辅导', type: 'warning' }]
    },
    {
      id: 3,
      name: '王五',
      className: '数学3班',
      avgScore: 91,
      completionRate: 98,
      tags: [{ label: '学习优秀', type: 'success' }, { label: '思维活跃', type: 'info' }, { label: '乐于助人', type: 'success' }]
    }
  ]
}

const getDefaultLearningTrend = () => {
  const labels = []
  const scores = []
  const now = new Date()
    for (let i = 5; i >= 0; i--) {
    const date = new Date(now)
    date.setDate(date.getDate() - i * 7)
    labels.push(`${date.getMonth() + 1}月${date.getDate()}周`)
    scores.push(60 + i * 5 + Math.floor(Math.random() * 5))
  }
  return { labels, scores }
}

const renderTrendChart = () => {
  if (!trendChart.value || !learningTrend.value) return

  // 简单的Canvas渲染
  const canvas = trendChart.value
  const ctx = canvas.getContext('2d')

  // 清除画布
  ctx.clearRect(0, 0, canvas.width, canvas.height)

  const labels = learningTrend.value.labels
  const scores = learningTrend.value.scores

  if (!labels || !scores || labels.length === 0 || scores.length === 0) return

  // 计算坐标
  const padding = 40
  const chartWidth = canvas.width - padding * 2
  const chartHeight = canvas.height - padding * 2
  const stepX = chartWidth / (labels.length - 1)
  const maxScore = Math.max(...scores) * 1.1

  // 绘制坐标'  ctx.beginPath()
  ctx.moveTo(padding, padding)
  ctx.lineTo(padding, canvas.height - padding)
  ctx.lineTo(canvas.width - padding, canvas.height - padding)
  ctx.strokeStyle = '#e5e7eb'
  ctx.stroke()

  // 绘制数据点和连线
  ctx.beginPath()
  ctx.strokeStyle = '#2563eb'
  ctx.lineWidth = 2

  scores.forEach((score, index) => {
    const x = padding + index * stepX
    const y = canvas.height - padding - (score / maxScore) * chartHeight

    if (index === 0) {
      ctx.moveTo(x, y)
    } else {
      ctx.lineTo(x, y)
    }

    // 绘制数据'    ctx.beginPath()
    ctx.arc(x, y, 4, 0, Math.PI * 2)
    ctx.fillStyle = '#2563eb'
    ctx.fill()
  })

  // 绘制标签
  ctx.font = '12px Arial'
  ctx.fillStyle = '#6b7280'
  labels.forEach((label, index) => {
    const x = padding + index * stepX
    ctx.fillText(label, x - 15, canvas.height - padding + 20)
  })
}

onMounted(() => {
  loadDiagnosisData()
})

onUnmounted(() => {
  if (chartInstance) {
    chartInstance.destroy()
  }
})
</script>

<style scoped>
.diagnosis-page { min-height: 100%; }

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
  padding: 1.5rem;
  background: var(--card-bg);
  border-radius: var(--radius-lg);
  border: 1px solid var(--border-color);
  box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
}

.header-content h1 {
  font-size: 1.5rem;
  font-weight: 700;
  color: var(--text-main);
  margin: 0 0 0.25rem 0;
}

.header-content p {
  font-size: 0.9rem;
  color: var(--text-secondary);
  margin: 0;
}

.header-actions {
  display: flex;
  gap: 1rem;
}



.analysis-grid {
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

.chart-container {
  height: 300px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.chart-placeholder {
  text-align: center;
  color: var(--text-secondary);
}

.chart-icon {
  color: var(--primary-color);
  margin-bottom: 1rem;
}

.chart-text {
  font-size: 1rem;
  margin-bottom: 0.5rem;
}

.chart-hint {
  font-size: 0.85rem;
  color: var(--text-secondary);
}

.knowledge-stats {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.stat-item {
  background: #f8fafc;
  border: 1px solid var(--border-color);
  border-radius: var(--radius-md);
  padding: 1rem;
}

.stat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.75rem;
}

.stat-name {
  font-weight: 600;
  color: var(--text-main);
}

.badge {
  padding: 0.25rem 0.6rem;
  border-radius: 9999px;
  font-size: 0.75rem;
  font-weight: 500;
}

.badge-success {
  background: #d1fae5;
  color: #065f46;
}

.badge-warning {
  background: #fef3c7;
  color: #92400e;
}

.badge-info {
  background: #dbeafe;
  color: #1e40af;
}

.badge-danger {
  background: #fee2e2;
  color: #991b1b;
}

.stat-bar {
  height: 8px;
  background: #e5e7eb;
  border-radius: 4px;
  overflow: hidden;
  margin-bottom: 0.75rem;
}

.stat-progress {
  height: 100%;
  background: var(--primary-color);
  transition: width 0.5s ease;
}

.stat-footer {
  display: flex;
  justify-content: space-between;
  font-size: 0.85rem;
  color: var(--text-secondary);
}

.stat-score {
  font-weight: 600;
}

.stat-count {
  color: var(--text-secondary);
}

.weak-points {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.weak-point-item {
  background: #fef2f2;
  border: 1px solid #fecaca;
  border-radius: var(--radius-md);
  padding: 1.25rem;
}

.point-header {
  display: flex;
  gap: 1rem;
  margin-bottom: 1rem;
}

.point-icon {
  color: #f59e0b;
  font-size: 1.5rem;
  flex-shrink: 0;
}

.point-info {
  flex: 1;
}

.point-title {
  font-size: 1rem;
  font-weight: 600;
  color: var(--text-main);
  margin-bottom: 0.5rem;
}

.point-desc {
  font-size: 0.9rem;
  color: var(--text-secondary);
}

.point-stats {
  display: flex;
  gap: 2rem;
  margin-bottom: 1rem;
}

.point-stat {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.stat-label {
  font-size: 0.8rem;
  color: var(--text-secondary);
}

.stat-value {
  font-size: 1.1rem;
  font-weight: 600;
}

.stat-value.error {
  color: #ef4444;
}

.point-suggestions {
  background: #f0f9ff;
  border-left: 4px solid var(--primary-color);
  padding: 1rem;
  border-radius: 0 var(--radius-sm) var(--radius-sm) 0;
}

.suggestions-title {
  font-size: 0.95rem;
  font-weight: 600;
  color: var(--primary-color);
  margin-bottom: 0.75rem;
}

.suggestions-list {
  list-style: none;
  padding-left: 0;
}

.suggestions-list li {
  padding-left: 1.5rem;
  position: relative;
  margin-bottom: 0.5rem;
  line-height: 1.6;
}

.suggestions-list li::before {
  content: "•";
  position: absolute;
  left: 0;
  color: var(--primary-color);
  font-weight: 600;
}

.student-profiles {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 1rem;
}

.student-card {
  background: var(--card-bg);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-lg);
  padding: 1.25rem;
  transition: all var(--transition-normal);
}

.student-card:hover {
  box-shadow: var(--shadow-md);
  transform: translateY(-2px);
}

.student-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: 600;
  font-size: 1.2rem;
  margin-bottom: 1rem;
}

.student-info {
  margin-bottom: 1rem;
}

.student-name {
  font-size: 1.1rem;
  font-weight: 600;
  color: var(--text-main);
  margin-bottom: 0.25rem;
}

.student-class {
  font-size: 0.9rem;
  color: var(--text-secondary);
  margin-bottom: 0.75rem;
}

.student-stats {
  display: flex;
  gap: 1rem;
  margin-bottom: 1rem;
}

.mini-stat {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.mini-label {
  font-size: 0.8rem;
  color: var(--text-secondary);
}

.mini-value {
  font-size: 1.2rem;
  font-weight: 600;
  color: var(--primary-color);
}

.student-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}



@media (max-width: 1024px) {
  .analysis-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .diagnosis-page {
    padding: 1rem;
  }

  .header-content h1 {
    font-size: 1.25rem;
  }

  .header-actions {
    flex-direction: column;
    gap: 0.5rem;
  }

  .student-profiles {
    grid-template-columns: 1fr;
  }
}
</style>
