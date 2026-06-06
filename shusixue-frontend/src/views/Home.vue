<template>
  <div class="home-page">
    <!-- 欢迎区域 -->
    <div class="welcome-section">
      <div class="welcome-content">
        <h1>欢迎回来，{{ userStore.userInfo.realName || userStore.userInfo.username }}�</h1>
        <p>{{ welcomeMessage }}</p>
      </div>
      <div class="welcome-stats">
        <div class="stat-card">
          <div class="stat-icon">
            <el-icon><Tickets /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ taskCount }}</div>
            <div class="stat-label">{{ isTeacher ? '待批改作业' : '待完成作业' }}</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon">
            <el-icon><Reading /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ knowledgeCount }}</div>
            <div class="stat-label">知识'</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon">
            <el-icon><Document /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ questionCount }}</div>
            <div class="stat-label">题目数量</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 教师专用内容 -->
    <div v-if="isTeacher" class="teacher-content">
      <div class="section">
        <h2>作业管理</h2>
        <div class="action-buttons">
          <el-button type="primary" @click="navigateTo('/homework-teacher')">
            <el-icon><Tickets /></el-icon>
            查看所有作'          </el-button>
          <el-button type="success" @click="navigateTo('/homework-teacher')">
            <el-icon><Plus /></el-icon>
            发布新作'          </el-button>
        </div>
        <div class="recent-homeworks">
          <el-card v-for="homework in recentHomeworks" :key="homework.id" class="homework-card">
            <div class="homework-info">
              <h3>{{ homework.title }}</h3>
              <p class="homework-desc">{{ homework.description || '无描' }}</p>
              <div class="homework-meta">
                <span class="meta-item">
                  <el-icon><Timer /></el-icon>
                  {{ formatDate(homework.startTime) }} �?{{ formatDate(homework.endTime) }}
                </span>
                <span class="meta-item">
                  <el-tag :type="getStatusType(homework.status)">
                    {{ getStatusText(homework.status) }}
                  </el-tag>
                </span>
              </div>
            </div>
            <div class="homework-actions">
              <el-button size="small" @click="navigateTo(`/homework-teacher`)" type="primary" link>
                查看详情
              </el-button>
            </div>
          </el-card>
          <div v-if="recentHomeworks.length === 0" class="empty-state">
            <el-icon class="empty-icon"><Document /></el-icon>
            <p>暂无作业，点击发布新作业开'</p>
          </div>
        </div>
      </div>

      <div class="section">
        <h2>学情分析</h2>
        <div class="analysis-cards">
          <el-card class="analysis-card">
            <div class="analysis-header">
              <h3>学生作业完成情况</h3>
              <el-button size="small" @click="navigateTo('/diagnosis')">
                查看详情
              </el-button>
            </div>
            <div class="analysis-content">
              <div class="progress-item">
                <span>已提'</span>
                <el-progress :percentage="submissionRate" :color="submissionRateColor" />
              </div>
              <div class="progress-item">
                <span>平均'</span>
                <el-progress :percentage="averageScore" :color="averageScoreColor" />
              </div>
            </div>
          </el-card>
        </div>
      </div>
    </div>

    <!-- 学生专用内容 -->
    <div v-else class="student-content">
      <div class="section">
        <h2>我的作业</h2>
        <div class="action-buttons">
          <el-button type="primary" @click="navigateTo('/homework-student')">
            <el-icon><Tickets /></el-icon>
            查看所有作'          </el-button>
        </div>
        <div class="recent-homeworks">
          <el-card v-for="homework in recentHomeworks" :key="homework.id" class="homework-card">
            <div class="homework-info">
              <h3>{{ homework.title }}</h3>
              <p class="homework-desc">{{ homework.description || '无描' }}</p>
              <div class="homework-meta">
                <span class="meta-item">
                  <el-icon><Timer /></el-icon>
                  {{ formatDate(homework.startTime) }} �?{{ formatDate(homework.endTime) }}
                </span>
                <span class="meta-item">
                  <el-tag :type="getStatusType(homework.status)">
                    {{ getStatusText(homework.status) }}
                  </el-tag>
                </span>
              </div>
            </div>
            <div class="homework-actions">
              <el-button size="small" @click="navigateTo(`/homework-student`)" type="primary">
                去完'              </el-button>
            </div>
          </el-card>
          <div v-if="recentHomeworks.length === 0" class="empty-state">
            <el-icon class="empty-icon"><Tickets /></el-icon>
            <p>暂无作业，老师还没有发布新作业</p>
          </div>
        </div>
      </div>

      <div class="section">
        <h2>推荐知识'</h2>
        <div class="knowledge-cards">
          <el-card v-for="knowledge in recommendedKnowledge" :key="knowledge.id" class="knowledge-card">
            <h3>{{ knowledge.title }}</h3>
            <p class="knowledge-desc">{{ knowledge.description || '无描' }}</p>
            <el-button size="small" @click="navigateTo('/knowledge')" type="primary" link>
              查看详情
            </el-button>
          </el-card>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'
import {
  Tickets,
  Reading,
  Document,
  Timer,
  Plus
} from '@element-plus/icons-vue'
import request from '@/utils/request'

const router = useRouter()
const userStore = useUserStore()

// 计算属性
const isTeacher = computed(() => {
  const role = userStore.userInfo.role
  return role === 'TEACHER' || role === 'ADMIN'
})

const welcomeMessage = computed(() => {
  if (isTeacher.value) {
    return '您可以在这里管理作业、查看学情分析和发布新内'
  } else {
    return '您可以在这里查看作业、学习知识点和提交作'
  }
})

// 统计数据
const taskCount = ref(0)
const knowledgeCount = ref(0)
const questionCount = ref(0)
const recentHomeworks = ref([])
const recommendedKnowledge = ref([])
const submissionRate = ref(0)
const averageScore = ref(0)

// 计算属'
const submissionRateColor = computed(() => {
  if (submissionRate.value >= 80) return '#67c23a'
  if (submissionRate.value >= 60) return '#e6a23c'
  return '#f56c6c'
})

const averageScoreColor = computed(() => {
  if (averageScore.value >= 80) return '#67c23a'
  if (averageScore.value >= 60) return '#e6a23c'
  return '#f56c6c'
})

// 方法
const navigateTo = (path) => {
  router.push(path)
}

const formatDate = (date) => {
  if (!date) return ''
  const d = new Date(date)
  return d.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const getStatusType = (status) => {
  switch (status) {
    case 'PUBLISHED':
      return 'success'
    case 'ENDED':
      return 'info'
    case 'DRAFT':
      return 'warning'
    default:
      return ''
  }
}

const getStatusText = (status) => {
  switch (status) {
    case 'PUBLISHED':
      return '进行'
    case 'ENDED':
      return '已结'
    case 'DRAFT':
      return '草稿'
    default:
      return status
  }
}

// 数据加载
const loadData = async () => {
  try {
    // 获取首页统计数据
    const statsRes = await request.get('/api/stats/home')
    if (statsRes?.data) {
      knowledgeCount.value = statsRes.data.knowledgeCount || 0
      questionCount.value = statsRes.data.questionCount || 0
    }

    if (isTeacher.value) {
      // 教师端：获取待批改作业数'      const homeworkRes = await request.get('/api/homework/teacher/page', { params: { pageNum: 1, pageSize: 100 } })
      const homeworks = homeworkRes?.data?.records || []

      // 获取最近作'      recentHomeworks.value = homeworks.slice(0, 10)

      // 获取学情分析数据
      try {
        const diagnosisRes = await request.get('/api/diagnosis/data')
        if (diagnosisRes?.data) {
          submissionRate.value = diagnosisRes.data.submissionRate || 0
          averageScore.value = diagnosisRes.data.averageScore || 0
        }
      } catch (e) {
        console.warn('获取学情分析数据失败:', e)
        submissionRate.value = 0
        averageScore.value = 0
      }
    } else {
      // 学生端：获取待完成作业数'      const homeworkRes = await request.get('/api/homework/student/page', { params: { pageNum: 1, pageSize: 100 } })
      const homeworks = homeworkRes?.data?.records || []

      // 统计待完成作业数量（状态为PUBLISHED的作业）
      taskCount.value = homeworks.filter(hw => hw.status === 'PUBLISHED').length

      // 获取最近作'      recentHomeworks.value = homeworks.slice(0, 10)

      // 获取推荐知识'      try {
        const knowledgeRes = await request.get('/api/knowledge/list')
        recommendedKnowledge.value = (knowledgeRes?.data || []).slice(0, 5)
      } catch (e) {
        console.warn('获取推荐知识点失'', e)
        recommendedKnowledge.value = []
      }
    }
  } catch (error) {
    console.error('加载数据失败:', error)
    // 加载失败时清空数'    taskCount.value = 0
    knowledgeCount.value = 0
    questionCount.value = 0
    recentHomeworks.value = []
    recommendedKnowledge.value = []
    submissionRate.value = 0
    averageScore.value = 0
  }
}

// 生命周期
onMounted(() => {
  loadData()
})
</script>

<style scoped>


.home-page {
  min-height: 100%;
}

.welcome-section {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: var(--radius-lg);
  padding: 2rem;
  margin-bottom: 2rem;
  color: white;
}

.welcome-content {
  margin-bottom: 2rem;
}

.welcome-content h1 {
  font-size: 2rem;
  font-weight: 700;
  margin-bottom: 0.5rem;
}

.welcome-content p {
  font-size: 1.1rem;
  opacity: 0.9;
}

.welcome-stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1rem;
}

.stat-card {
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border-radius: var(--radius-md);
  padding: 1.5rem;
  display: flex;
  align-items: center;
  gap: 1rem;
  transition: transform 0.2s;
}

.stat-card:hover {
  transform: translateY(-2px);
}

.stat-icon {
  width: 48px;
  height: 48px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.5rem;
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 1.8rem;
  font-weight: 700;
  margin-bottom: 0.25rem;
}

.stat-label {
  font-size: 0.9rem;
  opacity: 0.9;
}

.section {
  background: var(--card-bg);
  border-radius: var(--radius-lg);
  padding: 2rem;
  margin-bottom: 2rem;
  box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
}

.section h2 {
  font-size: 1.5rem;
  font-weight: 700;
  margin-bottom: 1.5rem;
  color: var(--text-main);
}

.action-buttons {
  display: flex;
  gap: 1rem;
  margin-bottom: 1.5rem;
}

.recent-homeworks {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 1.5rem;
}

.homework-card {
  border-radius: var(--radius-md);
  transition: box-shadow 0.2s;
}

.homework-card:hover {
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
}

.homework-info {
  margin-bottom: 1rem;
}

.homework-info h3 {
  font-size: 1.1rem;
  font-weight: 600;
  margin-bottom: 0.5rem;
  color: var(--text-main);
}

.homework-desc {
  font-size: 0.9rem;
  color: var(--text-secondary);
  margin-bottom: 1rem;
  line-height: 1.4;
}

.homework-meta {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.85rem;
  color: var(--text-secondary);
}

.homework-actions {
  display: flex;
  justify-content: flex-end;
  gap: 0.5rem;
  margin-top: 1rem;
  padding-top: 1rem;
  border-top: 1px solid var(--border-color);
}

.analysis-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 1.5rem;
}

.analysis-card {
  border-radius: var(--radius-md);
}

.analysis-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.analysis-header h3 {
  font-size: 1.1rem;
  font-weight: 600;
  color: var(--text-main);
}

.analysis-content {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.progress-item {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.progress-item span {
  font-size: 0.9rem;
  color: var(--text-secondary);
}

.knowledge-cards {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 1.5rem;
}

.knowledge-card {
  border-radius: var(--radius-md);
  transition: box-shadow 0.2s;
}

.knowledge-card:hover {
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
}

.knowledge-card h3 {
  font-size: 1.1rem;
  font-weight: 600;
  margin-bottom: 0.5rem;
  color: var(--text-main);
}

.knowledge-desc {
  font-size: 0.9rem;
  color: var(--text-secondary);
  margin-bottom: 1rem;
  line-height: 1.4;
}

.empty-state {
  grid-column: 1 / -1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 3rem;
  background: #f9fafb;
  border-radius: var(--radius-md);
  color: var(--text-secondary);
}

.empty-icon {
  font-size: 3rem;
  margin-bottom: 1rem;
  opacity: 0.5;
}

@media (max-width: 768px) {
  .welcome-section {
    padding: 1.5rem;
  }

  .welcome-content h1 {
    font-size: 1.5rem;
  }

  .welcome-stats {
    grid-template-columns: 1fr;
  }

  .section {
    padding: 1.5rem;
  }

  .recent-homeworks,
  .analysis-cards,
  .knowledge-cards {
    grid-template-columns: 1fr;
  }

  .action-buttons {
    flex-direction: column;
  }
}
</style>

