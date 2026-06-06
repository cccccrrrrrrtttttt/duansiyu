<template>
  <div class="lesson-plan-page">
    <div class="split-layout">
      <div class="sidebar">
        <div class="sidebar-header">
          <h3 class="sidebar-title">教案管理</h3>
        </div>
        <div class="sidebar-items">
          <div
            v-for="item in sidebarItems"
            :key="item.id"
            :class="['sidebar-item', { active: activeTab === item.id }]"
            @click="activeTab = item.id"
          >
            <el-icon :size="18" style="margin-right: 8px;">
              <component :is="item.icon" />
            </el-icon>
            {{ item.label }}
          </div>
        </div>
      </div>

      <div class="main-content">
        <div v-if="activeTab === 'analysis'" class="content-section">
          <div class="card">
            <h2 class="section-title">
              <el-icon><DataAnalysis /></el-icon>
              教案分析
            </h2>
            <p class="section-desc">上传教案文件，系统将自动分析教学内容和结'</p>
            <div
              class="upload-area"
              @click="handleUploadClick('analysis')"
              @dragover.prevent
              @drop.prevent="(e) => handleFileDrop(e, 'analysis')"
            >
              <el-icon :size="48" class="upload-icon">
                <Upload />
              </el-icon>
              <div class="upload-text">点击或拖拽上传教案文'</div>
              <div class="upload-hint">支持 .docx 格式，文件大小不超过 10MB</div>
              <input
                ref="analysisFileInput"
                type="file"
                class="file-input"
                accept=".docx"
                @change="(e) => handleFileChange(e, 'analysis')"
              />
            </div>

            <div v-if="uploadedFiles.analysis" class="uploaded-file">
              <el-icon :size="32" class="file-icon">
                <Document />
              </el-icon>
              <div class="file-info">
                <div class="file-name">{{ uploadedFiles.analysis.name }}</div>
                <div class="file-size">{{ formatFileSize(uploadedFiles.analysis.size) }}</div>
              </div>
              <el-icon @click="removeFile('analysis')" class="file-remove">
                <Close />
              </el-icon>
            </div>

            <div v-if="uploadedFiles.analysis" style="margin-top: 1.5rem;">
              <el-button
                type="primary"
                @click="analyzeLessonPlan"
                :loading="loading.analysis"
                :disabled="loading.analysis"
              >
                <template v-if="loading.analysis">
                  <el-icon class="el-icon--left"><Loading /></el-icon>
                  分析'..
                </template>
                <template v-else>
                  分析教案
                </template>
              </el-button>
            </div>

            <div v-if="analysisResult" class="analysis-result" style="margin-top: 1.5rem;">
              <h3>分析结果</h3>
              <div class="analysis-content">
                <div v-for="(section, key) in analysisResult" :key="key" class="analysis-section">
                  <h4>{{ section.title }}</h4>
                  <p>{{ section.content }}</p>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div v-if="activeTab === 'improvement'" class="content-section">
          <div class="card" style="margin-bottom: 1.5rem;">
            <h2 class="section-title">
              <el-icon><Edit /></el-icon>
              教案改进
            </h2>
            <p class="section-desc">上传教案文件，获取智能改进建'</p>
            <div
              class="upload-area"
              @click="handleUploadClick('improvement')"
              @dragover.prevent
              @drop.prevent="(e) => handleFileDrop(e, 'improvement')"
            >
              <el-icon :size="48" class="upload-icon">
                <Document />
              </el-icon>
              <div class="upload-text">点击或拖拽上传教案文'</div>
              <div class="upload-hint">支持 .docx 格式，文件大小不超过 10MB</div>
              <input
                ref="improvementFileInput"
                type="file"
                class="file-input"
                accept=".docx"
                @change="(e) => handleFileChange(e, 'improvement')"
              />
            </div>

            <div v-if="uploadedFiles.improvement" class="uploaded-file">
              <el-icon :size="32" class="file-icon">
                <Document />
              </el-icon>
              <div class="file-info">
                <div class="file-name">{{ uploadedFiles.improvement.name }}</div>
                <div class="file-size">{{ formatFileSize(uploadedFiles.improvement.size) }}</div>
              </div>
              <el-icon @click="removeFile('improvement')" class="file-remove">
                <Close />
              </el-icon>
            </div>

            <div v-if="uploadedFiles.improvement" style="margin-top: 1.5rem;">
              <el-button
                type="primary"
                @click="improveLessonPlan"
                :loading="loading.improvement"
                :disabled="loading.improvement"
              >
                <template v-if="loading.improvement">
                  <el-icon class="el-icon--left"><Loading /></el-icon>
                  分析'..
                </template>
                <template v-else>
                  获取改进建议
                </template>
              </el-button>
            </div>
          </div>

          <div class="card" style="margin-bottom: 1.5rem;">
            <h2 class="section-title">
              <el-icon><TrendCharts /></el-icon>
              教案改进分析
            </h2>
            <p class="section-desc">点击按钮查看详细的教案分析和改进建议</p>

            <div style="margin-top: 1.5rem;">
              <div
                v-for="section in analysisSections"
                :key="section.id"
                class="content-toggle-btn"
                :class="{ active: section.expanded }"
                @click="toggleSection(section.id)"
              >
                <div class="btn-content">
                  <div class="btn-icon" :style="{ background: section.iconBg }">
                    <el-icon :size="20">
                      <component :is="section.icon" />
                    </el-icon>
                  </div>
                  <div class="btn-text">
                    <div class="btn-title">{{ section.title }}</div>
                    <div class="btn-subtitle">{{ section.subtitle }}</div>
                  </div>
                </div>
                <el-icon class="btn-arrow">
                  <component :is="section.expanded ? 'ArrowUp' : 'ArrowDown'" />
                </el-icon>
              </div>

              <div
                v-for="section in analysisSections"
                :key="section.id"
                class="expandable-content"
                :class="{ expanded: section.expanded }"
              >
                <div class="content-section">
                  <h3>{{ section.contentTitle }}</h3>
                  <p v-for="(paragraph, index) in section.paragraphs" :key="index">
                    {{ paragraph }}
                  </p>
                  <div v-if="section.highlight" class="highlight-box">
                    <strong>总体评估'</strong>{{ section.highlight }}
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div v-if="activeTab === 'generation'" class="content-section">
          <div class="card">
            <h2 class="section-title">
              <el-icon><MagicStick /></el-icon>
              教案生成
            </h2>
            <p class="section-desc">输入教学内容，自动生成教案模'</p>
            <div class="form-group">
              <el-input
                v-model="lessonTopic"
                type="textarea"
                :rows="4"
                placeholder="请输入教学内容和目标..."
              />
            </div>
            <el-button
              type="primary"
              class="btn"
              @click="generateLessonPlan"
              :loading="loading.generation"
              :disabled="loading.generation"
            >
              <template v-if="loading.generation">
                <el-icon class="el-icon--left"><Loading /></el-icon>
                生成'..
              </template>
              <template v-else>
                生成教案
              </template>
            </el-button>

            <div v-if="generationResult" class="generation-result" style="margin-top: 1.5rem;">
              <h3>生成的教'</h3>
              <div class="lesson-plan-content">
                <h4>{{ generationResult.title }}</h4>
                <div class="plan-section">
                  <strong>教学目标'</strong>
                  <pre>{{ generationResult.teachingObjectives }}</pre>
                </div>
                <div class="plan-section">
                  <strong>教学重难点：</strong>
                  <pre>{{ generationResult.keyPoints }}</pre>
                </div>
                <div class="plan-section">
                  <strong>教学方法'</strong>
                  <pre>{{ generationResult.teachingMethods }}</pre>
                </div>
                <div class="plan-section">
                  <strong>教学过程'</strong>
                  <pre>{{ generationResult.teachingProcess }}</pre>
                </div>
                <div class="plan-section">
                  <strong>作业布置'</strong>
                  <pre>{{ generationResult.homework }}</pre>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import {
  DataAnalysis,
  Edit,
  Upload,
  Document,
  Close,
  TrendCharts,
  MagicStick,
  ArrowDown,
  ArrowUp,
  Loading
} from '@element-plus/icons-vue'
import request from '@/utils/request'

const activeTab = ref('improvement')
const analysisFileInput = ref(null)
const improvementFileInput = ref(null)
const uploadedFiles = ref({ analysis: null, improvement: null })
const lessonTopic = ref('')
const analysisResult = ref(null)
const generationResult = ref(null)
const loading = ref({ analysis: false, improvement: false, generation: false })

const sidebarItems = [
  { id: 'analysis', label: '教案分析', icon: 'DataAnalysis' },
  { id: 'improvement', label: '教案改进', icon: 'Edit' },
  { id: 'generation', label: '教案生成', icon: 'MagicStick' }
]

const analysisSections = ref([
  {
    id: 'deviation',
    title: '地区教学偏差分析',
    subtitle: '查看教案与当地教学实际的偏差',
    icon: 'TrendCharts',
    iconBg: '#fce7f3',
    expanded: false,
    contentTitle: '一、教案与当地课程标准的契合度分析',
    paragraphs: [
      '经过系统分析，该教案在以下几个方面与当地课程标准存在一定偏差：',
      '原教案的教学目标过于宽泛，未能精准对接当地课程标准中对于该知识点的具体要求。建议将教学目标细化为可观测、可评估的具体行为目标',
      '教案中的教学内容深度略低于当地考试要求，需要适当增加拓展内容，以满足不同层次学生的学习需求',
      '采用的教学方法与当地学生的实际学习习惯存在一定差距，建议增加互动环节和小组合作学习，提高课堂参与度'
    ],
    highlight: '教案整体设计合理，但在与当地教学实际的对接上存在中等程度的偏差，需要进行针对性调整以提高教学效果'
  },
  {
    id: 'evaluation',
    title: '输出教学评估',
    subtitle: '查看完整的教学评估报',
    icon: 'DataAnalysis',
    iconBg: '#fef3c7',
    expanded: false,
    contentTitle: '教学评估报告',
    paragraphs: [
      '教学目标明确，符合课程标准要',
      '教学环节设计完整，逻辑清晰',
      '注重学生主体地位，设计了多种互动活动',
      '教学资源准备充分，考虑了多媒体运用',
      '评价方式多样化，关注过程性评'
    ],
    highlight: '综合得分'5�?
  },
  {
    id: 'improvement',
    title: '改进教案',
    subtitle: '获取智能改进建议',
    icon: 'Edit',
    iconBg: '#dbeafe',
    expanded: false,
    contentTitle: '改进建议',
    paragraphs: [
      '优化教学目标表述，将教学目标改为可观测、可评估的行为目标，使用"学生能够..."的表述方式',
      '细化重难点突破策略，针对教学重难点，设计具体的教学活动和教学方法，提供详细的操作步骤',
      '增加分层教学设计，针对不同层次学生设计差异化的学习任务和评价标准',
      '预设课堂生成性问题，预判学生可能提出的问题和遇到的困难，准备相应的应对策略',
      '设计层次化作业，设计基础题、提高题和拓展题三个层次的作业，满足不同学生的需求'
    ],
    highlight: null
  }
])

const handleUploadClick = (type) => {
  if (type === 'analysis') {
    analysisFileInput.value?.click()
  } else if (type === 'improvement') {
    improvementFileInput.value?.click()
  }
}

const handleFileChange = async (event, type) => {
  const file = event.target.files[0]
  if (file) {
    processFile(file, type)
  }
}

const handleFileDrop = async (event, type) => {
  const file = event.dataTransfer.files[0]
  if (file) {
    processFile(file, type)
  }
}

const processFile = async (file, type) => {
  if (file.size > 10 * 1024 * 1024) {
    ElMessage.error('文件大小不能超过10MB')
    return
  }

  // 上传文件到后'  try {
    const formData = new FormData()
    formData.append('file', file)

    const res = await request.post('/api/file/upload/lesson-plan', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })

    uploadedFiles.value[type] = {
      ...file,
      fileName: res.data,
      size: file.size, // 明确存储文件大小
      name: file.name // 明确存储文件'    }
    ElMessage.success('文件上传成功')
  } catch (error) {
    console.error('文件上传失败:', error)
    ElMessage.error('文件上传失败: ' + (error.message || '请稍后重'))
  }
}

const removeFile = (type) => {
  uploadedFiles.value[type] = null
  if (type === 'analysis' && analysisFileInput.value) {
    analysisFileInput.value.value = ''
  } else if (type === 'improvement' && improvementFileInput.value) {
    improvementFileInput.value.value = ''
  }
  analysisResult.value = null
}

const formatFileSize = (bytes) => {
  if (!bytes || bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return Math.round(bytes / Math.pow(k, i) * 100) / 100 + ' ' + sizes[i]
}

const toggleSection = (id) => {
  const section = analysisSections.value.find(s => s.id === id)
  if (section) {
    section.expanded = !section.expanded
  }
}

const analyzeLessonPlan = async () => {
  if (!uploadedFiles.value.analysis) {
    ElMessage.warning('请先上传教案文件')
    return
  }

  try {
    loading.value.analysis = true
    const res = await request.post('/api/lesson-plan/analyze', {
      fileName: uploadedFiles.value.analysis.fileName
    })
    analysisResult.value = res.data
    ElMessage.success('教案分析成功')
  } catch (error) {
    console.error('教案分析失败:', error)
    ElMessage.error('教案分析失败: ' + (error.message || '请稍后重'))
  } finally {
    loading.value.analysis = false
  }
}

const improveLessonPlan = async () => {
  if (!uploadedFiles.value.improvement) {
    ElMessage.warning('请先上传教案文件')
    return
  }

  try {
    loading.value.improvement = true
    const res = await request.post('/api/lesson-plan/improve', {
      fileName: uploadedFiles.value.improvement.fileName
    })
    // 更新分析章节的内容为API返回的结'    if (res.data) {
      analysisSections.value = [
        {
          id: 'deviation',
          title: '地区教学偏差分析',
          subtitle: '查看教案与当地教学实际的偏差',
          icon: 'TrendCharts',
          iconBg: '#fce7f3',
          expanded: false,
          contentTitle: '一、教案与当地课程标准的契合度分析',
          paragraphs: res.data.deviation?.content?.split('\n').filter(p => p.trim()) || [
            '经过系统分析，该教案在以下几个方面与当地课程标准存在一定偏差：',
            '原教案的教学目标过于宽泛，未能精准对接当地课程标准中对于该知识点的具体要求。建议将教学目标细化为可观测、可评估的具体行为目标',
            '教案中的教学内容深度略低于当地考试要求，需要适当增加拓展内容，以满足不同层次学生的学习需求',
            '采用的教学方法与当地学生的实际学习习惯存在一定差距，建议增加互动环节和小组合作学习，提高课堂参与度'
          ],
          highlight: res.data.deviation?.highlight || '教案整体设计合理，但在与当地教学实际的对接上存在中等程度的偏差，需要进行针对性调整以提高教学效果'
        },
        {
          id: 'evaluation',
          title: '输出教学评估',
          subtitle: '查看完整的教学评估报',
          icon: 'DataAnalysis',
          iconBg: '#fef3c7',
          expanded: false,
          contentTitle: '教学评估报告',
          paragraphs: res.data.evaluation?.content?.split('\n').filter(p => p.trim()) || [
            '教学目标明确，符合课程标准要',
            '教学环节设计完整，逻辑清晰',
            '注重学生主体地位，设计了多种互动活动',
            '教学资源准备充分，考虑了多媒体运用',
            '评价方式多样化，关注过程性评'
          ],
          highlight: res.data.evaluation?.highlight || '综合得分'5�?
        },
        {
          id: 'improvement',
          title: '改进教案',
          subtitle: '获取智能改进建议',
          icon: 'Edit',
          iconBg: '#dbeafe',
          expanded: false,
          contentTitle: '改进建议',
          paragraphs: res.data.improvement?.content?.split('\n').filter(p => p.trim()) || [
            '优化教学目标表述，将教学目标改为可观测、可评估的行为目标，使用"学生能够..."的表述方式',
            '细化重难点突破策略，针对教学重难点，设计具体的教学活动和教学方法，提供详细的操作步骤',
            '增加分层教学设计，针对不同层次学生设计差异化的学习任务和评价标准',
            '预设课堂生成性问题，预判学生可能提出的问题和遇到的困难，准备相应的应对策略',
            '设计层次化作业，设计基础题、提高题和拓展题三个层次的作业，满足不同学生的需求'
          ],
          highlight: res.data.improvement?.highlight || null
        }
      ]
    }
    ElMessage.success('获取改进建议成功')
  } catch (error) {
    console.error('获取改进建议失败:', error)
    ElMessage.error('获取改进建议失败: ' + (error.message || '请稍后重'))
  } finally {
    loading.value.improvement = false
  }
}

const generateLessonPlan = async () => {
  if (!lessonTopic.value) {
    ElMessage.warning('请输入教学内')
    return
  }

  try {
    loading.value.generation = true
    const res = await request.post('/api/lesson-plan/generate', {
      topic: lessonTopic.value
    })
    generationResult.value = res.data.lessonPlan
    ElMessage.success('教案生成成功')
  } catch (error) {
    console.error('教案生成失败:', error)
    ElMessage.error('教案生成失败: ' + (error.message || '请稍后重'))
  } finally {
    loading.value.generation = false
  }
}
</script>

<style scoped>
.lesson-plan-page { min-height: 100%; }

.split-layout {
  display: grid;
  grid-template-columns: 280px 1fr;
  gap: 1.5rem;
  min-height: 700px;
}

.sidebar {
  background: var(--card-bg);
  border-radius: var(--radius-lg);
  border: 1px solid var(--border-color);
  padding: 1.5rem;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.sidebar-header {
  margin-bottom: 1.5rem;
}

.sidebar-title {
  font-size: 1.1rem;
  font-weight: 600;
  color: var(--text-main);
}

.sidebar-items {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.sidebar-item {
  padding: 0.75rem 1rem;
  border-radius: var(--radius-md);
  cursor: pointer;
  font-size: 0.95rem;
  color: var(--text-secondary);
  transition: all var(--transition-normal);
  display: flex;
  align-items: center;
}

.sidebar-item:hover {
  background: #f9fafb;
  color: var(--text-main);
}

.sidebar-item.active {
  background: #eff6ff;
  color: var(--primary-color);
  font-weight: 600;
}

.main-content {
  flex: 1;
}

.content-section {
  animation: fadeIn 0.3s ease;
}

.section-title {
  font-size: 1.1rem;
  font-weight: 600;
  color: var(--text-main);
  margin-bottom: 0.5rem;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.section-desc {
  font-size: 0.9rem;
  color: var(--text-secondary);
  margin-bottom: 1.5rem;
}

.upload-area {
  border: 2px dashed var(--border-color);
  border-radius: var(--radius-lg);
  padding: 3rem 2rem;
  text-align: center;
  background: #fafafa;
  transition: all var(--transition-slow) ease;
  cursor: pointer;
}

.upload-area:hover {
  border-color: var(--primary-color);
  background: #f0f7ff;
}

.upload-icon {
  color: var(--primary-color);
  margin-bottom: 1rem;
}

.upload-text {
  font-size: 1rem;
  color: var(--text-main);
  margin-bottom: 0.5rem;
}

.upload-hint {
  font-size: 0.85rem;
  color: var(--text-secondary);
}

.file-input {
  display: none;
}

.uploaded-file {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1rem 1.5rem;
  background: #f0f9ff;
  border: 1px solid #bae6fd;
  border-radius: var(--radius-md);
  margin-top: 1.5rem;
}

.file-icon {
  color: #0284c7;
}

.file-info {
  flex: 1;
}

.file-name {
  font-weight: 600;
  color: var(--text-main);
  margin-bottom: 0.25rem;
}

.file-size {
  font-size: 0.85rem;
  color: var(--text-secondary);
}

.file-remove {
  color: #ef4444;
  cursor: pointer;
  padding: 0.5rem;
  border-radius: var(--radius-sm);
  transition: background var(--transition-fast);
}

.file-remove:hover {
  background: #fee2e2;
}

.content-toggle-btn {
  width: 100%;
  padding: 1rem 1.5rem;
  background: var(--card-bg);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-md);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: space-between;
  transition: all var(--transition-slow) ease;
  margin-bottom: 1rem;
}

.content-toggle-btn:hover {
  border-color: var(--primary-color);
  box-shadow: var(--shadow-md);
}

.content-toggle-btn.active {
  background: #eff6ff;
  border-color: var(--primary-color);
}

.btn-content {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.btn-icon {
  width: 40px;
  height: 40px;
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.2rem;
  color: white;
}

.btn-text {
  text-align: left;
}

.btn-title {
  font-weight: 600;
  color: var(--text-main);
  font-size: 1rem;
}

.btn-subtitle {
  font-size: 0.85rem;
  color: var(--text-secondary);
}

.btn-arrow {
  color: var(--text-secondary);
  transition: transform var(--transition-slow) ease;
}

.content-toggle-btn.active .btn-arrow {
  transform: rotate(180deg);
  color: var(--primary-color);
}

.expandable-content {
  max-height: 0;
  overflow: hidden;
  transition: max-height var(--transition-slow) ease, opacity var(--transition-normal) ease, padding var(--transition-normal) ease;
  opacity: 0;
  background: var(--card-bg);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-md);
  margin-bottom: 0;
}

.expandable-content.expanded {
  max-height: 5000px;
  opacity: 1;
  padding: 1.5rem;
  margin-bottom: 1.5rem;
}

.content-section {
  margin-bottom: 1.5rem;
}

.content-section:last-child {
  margin-bottom: 0;
}

.content-section h3 {
  font-size: 1.1rem;
  color: var(--primary-color);
  margin-bottom: 1rem;
  padding-bottom: 0.5rem;
  border-bottom: 2px solid #e0e7ff;
}

.content-section p {
  margin-bottom: 0.75rem;
  line-height: 1.8;
  color: var(--text-main);
}

.highlight-box {
  background: #f0f9ff;
  border-left: 4px solid var(--primary-color);
  padding: 1rem 1.25rem;
  margin: 1rem 0;
  border-radius: 0 var(--radius-sm) var(--radius-sm) 0;
}

.form-group {
  margin-bottom: 1.5rem;
}

.analysis-result,
.generation-result {
  background: #f8fafc;
  padding: 1.5rem;
  border-radius: var(--radius-md);
  border: 1px solid var(--border-color);
}

.analysis-section {
  margin-bottom: 1.5rem;
}

.analysis-section h4 {
  color: var(--primary-color);
  margin-bottom: 0.5rem;
}

.lesson-plan-content {
  line-height: 1.6;
}

.lesson-plan-content h4 {
  color: var(--primary-color);
  margin-bottom: 1rem;
}

.plan-section {
  margin-bottom: 1rem;
}

.plan-section pre {
  background: #f1f5f9;
  padding: 1rem;
  border-radius: var(--radius-sm);
  margin-top: 0.5rem;
  white-space: pre-wrap;
  font-family: inherit;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(8px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@media (max-width: 1024px) {
  .split-layout {
    grid-template-columns: 1fr;
  }

  .sidebar {
    flex-direction: row;
    overflow-x: auto;
    height: auto;
    padding: 1rem;
  }

  .sidebar-header {
    margin-bottom: 0;
    margin-right: 1.5rem;
    white-space: nowrap;
  }

  .sidebar-items {
    display: flex;
    gap: 0.5rem;
  }

  .sidebar-item {
    white-space: nowrap;
    margin-bottom: 0;
  }
}

@media (max-width: 640px) {
  .lesson-plan-page {
    padding: 1rem;
  }

  .upload-area {
    padding: 2rem 1rem;
  }
}
</style>

