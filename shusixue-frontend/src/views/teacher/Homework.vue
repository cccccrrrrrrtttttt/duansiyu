<template>
  <div class="homework-teacher">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">作业管理</h1>
        <p class="page-desc">发布和管理作业，查看学生提交情况并批改</p>
      </div>
      <el-button type="primary" @click="handleCreate" class="add-btn">
        <el-icon><Plus /></el-icon>
        发布新作业
      </el-button>
    </div>

    <!-- 搜索栏 -->
    <div class="card">
      <el-form :inline="true" :model="queryForm" class="search-form">
        <el-form-item label="关键词">
          <el-input v-model="queryForm.keyword" placeholder="搜索作业标题" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="请选择状态" clearable>
            <el-option label="草稿" value="DRAFT" />
            <el-option label="进行中" value="PUBLISHED" />
            <el-option label="已结束" value="ENDED" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="getHomeworkPage">搜索</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 表格 -->
      <el-table :data="tableData" border stripe style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="作业标题" show-overflow-tooltip />
        <el-table-column prop="description" label="描述" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.status === 'PUBLISHED'" type="success">进行中</el-tag>
            <el-tag v-else-if="row.status === 'DRAFT'" type="warning">草稿</el-tag>
            <el-tag v-else type="info">已结束</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="startTime" label="开始时间" width="170">
          <template #default="{ row }">{{ formatDate(row.startTime) }}</template>
        </el-table-column>
        <el-table-column prop="endTime" label="截止时间" width="170">
          <template #default="{ row }">{{ formatDate(row.endTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="260" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="success" link size="small" @click="handleViewSubmissions(row)">批改</el-button>
            <el-button v-if="row.status === 'PUBLISHED'" type="warning" link size="small" @click="handleEnd(row)">结束</el-button>
            <el-button v-if="row.status === 'DRAFT'" type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="queryForm.pageNum"
        v-model:page-size="queryForm.pageSize"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="getHomeworkPage"
        @current-change="getHomeworkPage"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </div>

    <!-- 创建/编辑作业对话框 -->
    <el-dialog
      v-model="createDialogVisible"
      :title="isEditing ? '编辑作业' : '发布新作业'"
      width="800px"
      :close-on-click-modal="false"
      @closed="resetCreateForm"
    >
      <el-form :model="homeworkForm" :rules="homeworkRules" ref="homeworkFormRef" label-width="100px">
        <el-form-item label="作业标题" prop="title">
          <el-input v-model="homeworkForm.title" placeholder="请输入作业标题" />
        </el-form-item>
        <el-form-item label="作业描述" prop="description">
          <el-input v-model="homeworkForm.description" type="textarea" :rows="3" placeholder="请输入作业描述（可选）" />
        </el-form-item>
        <el-form-item label="开始时间" prop="startTime">
          <el-date-picker
            v-model="homeworkForm.startTime"
            type="datetime"
            placeholder="选择开始时间"
            style="width: 100%"
            value-format="YYYY-MM-DDTHH:mm:ss"
          />
        </el-form-item>
        <el-form-item label="截止时间" prop="endTime">
          <el-date-picker
            v-model="homeworkForm.endTime"
            type="datetime"
            placeholder="选择截止时间"
            style="width: 100%"
            value-format="YYYY-MM-DDTHH:mm:ss"
          />
        </el-form-item>
        <el-form-item label="选择题目" prop="questionIds">
          <div class="question-select-area">
            <el-button type="info" @click="showQuestionSelector = true">
              <el-icon><Plus /></el-icon>
              从题库选择题目 (已选 {{ homeworkForm.questionIds.length }} 题)
            </el-button>
            <div v-if="selectedQuestions.length > 0" class="selected-questions">
              <el-tag
                v-for="(q, i) in selectedQuestions"
                :key="q.id"
                closable
                @close="removeQuestion(q.id)"
                style="margin: 4px"
              >
                第{{ i + 1 }}题: {{ q.title?.substring(0, 20) }}{{ q.title?.length > 20 ? '...' : '' }}
              </el-tag>
            </div>
          </div>
        </el-form-item>
        <el-form-item label="发布方式">
          <el-radio-group v-model="homeworkForm.publishNow">
            <el-radio :value="true">立即发布</el-radio>
            <el-radio :value="false">存为草稿</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveHomework" :loading="saveLoading">保存</el-button>
      </template>
    </el-dialog>

    <!-- 选题对话框 -->
    <el-dialog v-model="showQuestionSelector" title="选择题目" width="800px" :close-on-click-modal="false">
      <el-form :inline="true" class="search-form">
        <el-form-item label="关键词">
          <el-input v-model="questionQuery.keyword" placeholder="搜索题目内容" clearable />
        </el-form-item>
        <el-form-item label="科目">
          <el-select v-model="questionQuery.subject" placeholder="选择科目" clearable>
            <el-option label="高等数学" value="高等数学" />
            <el-option label="线性代数" value="线性代数" />
            <el-option label="概率论" value="概率论" />
            <el-option label="数学建模" value="数学建模" />
          </el-select>
        </el-form-item>
        <el-form-item label="题型">
          <el-select v-model="questionQuery.type" placeholder="选择题型" clearable>
            <el-option label="单选题" value="SINGLE_CHOICE" />
            <el-option label="多选题" value="MULTIPLE_CHOICE" />
            <el-option label="填空题" value="FILL_BLANK" />
            <el-option label="解答题" value="ESSAY" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="searchQuestions">搜索</el-button>
        </el-form-item>
      </el-form>
      <el-table
        :data="questionTableData"
        border
        stripe
        style="width: 100%"
        @selection-change="handleSelectionChange"
        ref="questionTableRef"
      >
        <el-table-column type="selection" width="50" />
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="title" label="题目内容" show-overflow-tooltip />
        <el-table-column prop="type" label="题型" width="90">
          <template #default="{ row }">
            <el-tag v-if="row.type === 'SINGLE_CHOICE'" size="small">单选题</el-tag>
            <el-tag v-else-if="row.type === 'MULTIPLE_CHOICE'" size="small" type="success">多选题</el-tag>
            <el-tag v-else-if="row.type === 'FILL_BLANK'" size="small" type="warning">填空题</el-tag>
            <el-tag v-else size="small" type="danger">解答题</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="difficulty" label="难度" width="70">
          <template #default="{ row }">
            <el-tag v-if="row.difficulty === 'EASY'" size="small">易</el-tag>
            <el-tag v-else-if="row.difficulty === 'MEDIUM'" size="small" type="warning">中</el-tag>
            <el-tag v-else size="small" type="danger">难</el-tag>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        v-model:current-page="questionQuery.pageNum"
        v-model:page-size="questionQuery.pageSize"
        :total="questionTotal"
        :page-sizes="[5, 10, 20]"
        layout="total, sizes, prev, pager, next"
        @size-change="searchQuestions"
        @current-change="searchQuestions"
        style="margin-top: 10px; justify-content: flex-end"
      />
      <template #footer>
        <el-button @click="showQuestionSelector = false">取消</el-button>
        <el-button type="primary" @click="confirmQuestionSelection">确定选择 ({{ tempSelectedQuestions.length }})</el-button>
      </template>
    </el-dialog>

    <!-- 批改作业对话框 -->
    <el-dialog
      v-model="submissionDialogVisible"
      :title="'批改作业 - ' + (currentHomework?.title || '')"
      width="90%"
      :close-on-click-modal="false"
    >
      <div v-loading="submissionLoading">
        <el-table :data="submissionList" border stripe style="width: 100%">
          <el-table-column prop="id" label="提交ID" width="80" />
          <el-table-column label="学生姓名" width="120">
            <template #default="{ row }">{{ getStudentName(row.studentId) }}</template>
          </el-table-column>
          <el-table-column prop="totalCount" label="总题数" width="80" />
          <el-table-column prop="correctCount" label="正确数" width="80" />
          <el-table-column prop="score" label="得分" width="80">
            <template #default="{ row }">{{ row.score ?? '-' }}</template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag v-if="row.status === 'NOT_SUBMITTED'">未提交</el-tag>
              <el-tag v-else-if="row.status === 'SUBMITTED'" type="warning">待批改</el-tag>
              <el-tag v-else-if="row.status === 'GRADED'" type="success">已批改</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="submitTime" label="提交时间" width="170">
            <template #default="{ row }">{{ row.submitTime ? formatDate(row.submitTime) : '-' }}</template>
          </el-table-column>
          <el-table-column label="操作" width="160" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" link size="small" @click="viewSubmissionDetail(row)">查看详情</el-button>
              <el-button
                v-if="row.status === 'SUBMITTED' || row.status === 'GRADED'"
                type="success"
                link
                size="small"
                @click="handleGrade(row)"
              >{{ row.status === 'GRADED' ? '重新评分' : '批改' }}</el-button>
            </template>
          </el-table-column>
        </el-table>

        <el-pagination
          v-model:current-page="submissionPageNum"
          v-model:page-size="submissionPageSize"
          :total="submissionTotal"
          layout="total, prev, pager, next"
          @current-change="loadSubmissions"
          style="margin-top: 10px; justify-content: flex-end"
        />
      </div>
    </el-dialog>

    <!-- 批改评分对话框 -->
    <el-dialog v-model="gradeDialogVisible" :title="'批改评分'" width="400px" :close-on-click-modal="false">
      <el-form :model="gradeForm" label-width="100px">
        <el-form-item label="提交ID">
          <span>{{ gradeForm.submissionId }}</span>
        </el-form-item>
        <el-form-item label="得分" prop="score">
          <el-input-number v-model="gradeForm.score" :min="0" :max="100" :precision="2" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="gradeDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmGrade" :loading="gradeLoading">确认批改</el-button>
      </template>
    </el-dialog>

    <!-- 提交详情对话框 -->
    <el-dialog v-model="detailDialogVisible" title="提交详情" width="850px" :close-on-click-modal="false">
      <div v-loading="detailLoading">
        <el-descriptions :column="2" border class="mb-4">
          <el-descriptions-item label="学生姓名">{{ submissionDetail.studentName }}</el-descriptions-item>
          <el-descriptions-item label="提交状态">
            <el-tag v-if="submissionDetail.status === 'NOT_SUBMITTED'">未提交</el-tag>
            <el-tag v-else-if="submissionDetail.status === 'SUBMITTED'" type="warning">待批改</el-tag>
            <el-tag v-else-if="submissionDetail.status === 'GRADED'" type="success">已批改</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="总得分">{{ submissionDetail.totalScore ?? '-' }}</el-descriptions-item>
          <el-descriptions-item label="正确率">{{ submissionDetail.correctRate }}</el-descriptions-item>
          <el-descriptions-item label="提交时间">{{ submissionDetail.submitTime ? formatDate(submissionDetail.submitTime) : '-' }}</el-descriptions-item>
        </el-descriptions>

        <h3 style="margin-top: 20px; margin-bottom: 15px;">答题详情</h3>
        <div v-for="(question, index) in submissionDetail.questionList" :key="question.questionId" class="question-review-item">
          <el-divider content-position="left">第 {{ index + 1 }} 题 ({{ getTypeText(question.type) }})</el-divider>
          <div class="question-review-title">{{ question.title }}</div>
          <div v-if="question.options" class="question-review-options">
            <div v-for="opt in parseOptions(question.options)" :key="opt.key" class="option-item">
              <span :class="{ 'option-correct': isCorrectOption(opt.key, question.standardAnswer), 'option-student': isStudentOption(opt.key, question.studentAnswer) }">
                {{ opt.key }}. {{ opt.value }}
              </span>
            </div>
          </div>
          <div class="answer-review-section">
            <div class="answer-review-row">
              <span class="answer-review-label">学生答案：</span>
              <span :class="['answer-review-value', question.isCorrect ? 'text-correct' : 'text-wrong']">
                {{ question.studentAnswer || '未作答' }}
              </span>
            </div>
            <div class="answer-review-row">
              <span class="answer-review-label">正确答案：</span>
              <span class="answer-review-value text-correct">{{ question.standardAnswer }}</span>
            </div>
            <div v-if="question.analysis" class="answer-review-row">
              <span class="answer-review-label">解析：</span>
              <span class="answer-review-value text-analysis">{{ question.analysis }}</span>
            </div>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import request from '@/utils/request'

const route = useRoute()

// ============ 作业列表 ============
const tableData = ref([])
const total = ref(0)
const queryForm = reactive({
  pageNum: 1,
  pageSize: 10,
  keyword: '',
  status: ''
})

const getHomeworkPage = async () => {
  try {
    const res = await request.get('/api/homework/teacher/page', { params: queryForm })
    tableData.value = res?.data?.records || []
    total.value = res?.data?.total || 0
  } catch (error) {
    console.error(error)
  }
}

const resetQuery = () => {
  queryForm.keyword = ''
  queryForm.status = ''
  queryForm.pageNum = 1
  getHomeworkPage()
}

const formatDate = (date) => {
  if (!date) return ''
  const d = new Date(date)
  return d.toLocaleString('zh-CN', {
    year: 'numeric', month: '2-digit', day: '2-digit',
    hour: '2-digit', minute: '2-digit'
  })
}

const getTypeText = (type) => {
  const map = {
    'SINGLE_CHOICE': '单选题',
    'MULTIPLE_CHOICE': '多选题',
    'FILL_BLANK': '填空题',
    'ESSAY': '解答题'
  }
  return map[type] || type
}

const parseOptions = (options) => {
  try {
    const parsed = JSON.parse(options)
    if (Array.isArray(parsed)) {
      return parsed.map((opt, index) => ({
        key: String.fromCharCode(65 + index),
        value: opt
      }))
    }
    return []
  } catch (error) {
    return []
  }
}

const isCorrectOption = (optionKey, standardAnswer) => {
  if (!standardAnswer) return false
  return standardAnswer.includes(optionKey)
}

const isStudentOption = (optionKey, studentAnswer) => {
  if (!studentAnswer) return false
  return studentAnswer.includes(optionKey)
}

// ============ 创建/编辑作业 ============
const createDialogVisible = ref(false)
const isEditing = ref(false)
const saveLoading = ref(false)
const homeworkFormRef = ref(null)
const showQuestionSelector = ref(false)
const homeworkForm = reactive({
  id: null,
  title: '',
  description: '',
  startTime: '',
  endTime: '',
  questionIds: [],
  publishNow: true
})

const homeworkRules = {
  title: [{ required: true, message: '请输入作业标题', trigger: 'blur' }],
  startTime: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
  endTime: [{ required: true, message: '请选择截止时间', trigger: 'change' }],
  questionIds: [{ required: true, message: '请选择至少一道题目', trigger: 'change' }]
}

const selectedQuestions = ref([])

const handleCreate = () => {
  isEditing.value = false
  resetCreateForm()
  createDialogVisible.value = true
}

const handleEdit = async (row) => {
  isEditing.value = true
  try {
    const res = await request.get(`/api/homework/detail/${row.id}`)
    const data = res.data
    homeworkForm.id = data.id
    homeworkForm.title = data.title
    homeworkForm.description = data.description || ''
    homeworkForm.startTime = data.startTime
    homeworkForm.endTime = data.endTime
    homeworkForm.questionIds = data.questionList?.map(q => q.id) || []
    homeworkForm.publishNow = false
    selectedQuestions.value = data.questionList || []
    createDialogVisible.value = true
  } catch (error) {
    console.error(error)
    ElMessage.error('获取作业详情失败')
  }
}

const resetCreateForm = () => {
  homeworkForm.id = null
  homeworkForm.title = ''
  homeworkForm.description = ''
  homeworkForm.startTime = ''
  homeworkForm.endTime = ''
  homeworkForm.questionIds = []
  homeworkForm.publishNow = true
  selectedQuestions.value = []
}

const removeQuestion = (id) => {
  homeworkForm.questionIds = homeworkForm.questionIds.filter(qId => qId !== id)
  selectedQuestions.value = selectedQuestions.value.filter(q => q.id !== id)
}

const handleSaveHomework = async () => {
  await homeworkFormRef.value.validate()
  saveLoading.value = true
  try {
    await request.post('/api/homework/save', {
      id: homeworkForm.id,
      title: homeworkForm.title,
      description: homeworkForm.description,
      startTime: homeworkForm.startTime,
      endTime: homeworkForm.endTime,
      questionIds: homeworkForm.questionIds,
      publishNow: homeworkForm.publishNow
    })
    ElMessage.success(homeworkForm.id ? '作业更新成功' : '作业发布成功')
    createDialogVisible.value = false
    getHomeworkPage()
  } catch (error) {
    console.error(error)
  } finally {
    saveLoading.value = false
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该作业吗？', '提示', {
      confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
    })
    await request.delete(`/api/homework/${row.id}`)
    ElMessage.success('删除成功')
    getHomeworkPage()
  } catch (error) {
    if (error !== 'cancel') console.error(error)
  }
}

const handleEnd = async (row) => {
  try {
    await ElMessageBox.confirm('确定要结束该作业吗？结束后学生无法再提交。', '提示', {
      confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
    })
    await request.post(`/api/homework/end/${row.id}`)
    ElMessage.success('作业已结束')
    getHomeworkPage()
  } catch (error) {
    if (error !== 'cancel') console.error(error)
  }
}

// ============ 选题 ============
const questionQuery = reactive({
  pageNum: 1,
  pageSize: 10,
  keyword: '',
  subject: '',
  type: ''
})
const questionTableData = ref([])
const questionTotal = ref(0)
const questionTableRef = ref(null)
const tempSelectedQuestions = ref([])

const searchQuestions = async () => {
  try {
    const res = await request.get('/api/question/page', { params: questionQuery })
    questionTableData.value = res?.data?.records || []
    questionTotal.value = res?.data?.total || 0
    // 恢复已选状态
    tempSelectedQuestions.value = homeworkForm.questionIds.map(id => ({ id }))
  } catch (error) {
    console.error(error)
  }
}

const handleSelectionChange = (selection) => {
  tempSelectedQuestions.value = selection
}

const confirmQuestionSelection = () => {
  homeworkForm.questionIds = tempSelectedQuestions.value.map(q => q.id)
  selectedQuestions.value = [...tempSelectedQuestions.value]
  showQuestionSelector.value = false
  ElMessage.success(`已选择 ${homeworkForm.questionIds.length} 道题目`)
}

// ============ 批改作业 ============
const submissionDialogVisible = ref(false)
const submissionLoading = ref(false)
const currentHomework = ref(null)
const submissionList = ref([])
const submissionTotal = ref(0)
const submissionPageNum = ref(1)
const submissionPageSize = ref(10)
const studentNameMap = ref({})

const handleViewSubmissions = async (row) => {
  currentHomework.value = row
  submissionPageNum.value = 1
  submissionDialogVisible.value = true
  loadSubmissions()
}

const loadSubmissions = async () => {
  if (!currentHomework.value) return
  submissionLoading.value = true
  try {
    const res = await request.get(`/api/homework/submission/list/${currentHomework.value.id}`, {
      params: { pageNum: submissionPageNum.value, pageSize: submissionPageSize.value }
    })
    submissionList.value = res?.data?.records || []
    submissionTotal.value = res?.data?.total || 0

    // 加载学生姓名
    for (const sub of submissionList.value) {
      if (!studentNameMap.value[sub.studentId]) {
        try {
          const userRes = await request.get(`/api/user/info`)
          // Fallback: we can't query other users' info, use studentId as name
          studentNameMap.value[sub.studentId] = `学生${sub.studentId}`
        } catch {
          studentNameMap.value[sub.studentId] = `学生${sub.studentId}`
        }
      }
    }
  } catch (error) {
    console.error(error)
  } finally {
    submissionLoading.value = false
  }
}

const getStudentName = (studentId) => {
  return studentNameMap.value[studentId] || `学生${studentId}`
}

// ============ 评分 ============
const gradeDialogVisible = ref(false)
const gradeLoading = ref(false)
const gradeForm = reactive({
  submissionId: null,
  score: 0
})

const handleGrade = (row) => {
  gradeForm.submissionId = row.id
  gradeForm.score = row.score ? Number(row.score) : 0
  gradeDialogVisible.value = true
}

const confirmGrade = async () => {
  gradeLoading.value = true
  try {
    await request.post('/api/homework/grade', {
      submissionId: gradeForm.submissionId,
      score: gradeForm.score
    })
    ElMessage.success('批改成功')
    gradeDialogVisible.value = false
    loadSubmissions()
  } catch (error) {
    console.error(error)
  } finally {
    gradeLoading.value = false
  }
}

// ============ 提交详情 ============
const detailDialogVisible = ref(false)
const detailLoading = ref(false)
const submissionDetail = ref({
  studentName: '',
  homeworkTitle: '',
  totalScore: null,
  correctRate: '',
  submitTime: '',
  status: '',
  questionList: []
})

const viewSubmissionDetail = async (row) => {
  detailLoading.value = true
  try {
    const res = await request.get(`/api/homework/submission/detail/${row.id}`)
    submissionDetail.value = res?.data || {}
    detailDialogVisible.value = true
  } catch (error) {
    console.error(error)
    ElMessage.error('获取提交详情失败')
  } finally {
    detailLoading.value = false
  }
}

// 立即加载数据 + 路由变化时重新加载
getHomeworkPage()

// 监听路由变化，确保返回此页面时重新加载
watch(() => route.path, (newPath) => {
  if (newPath === '/homework-teacher') {
    getHomeworkPage()
  }
})
</script>

<style scoped>
.homework-teacher { min-height: 100%; }

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
  padding: 1.5rem;
  background: var(--card-bg);
  border-radius: var(--radius-lg);
  border: 1px solid var(--border-color);
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

.add-btn {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.card {
  background: var(--card-bg);
  border-radius: var(--radius-lg);
  border: 1px solid var(--border-color);
  padding: 1.5rem;
}

.search-form {
  margin-bottom: 20px;
}

.question-select-area {
  width: 100%;
}

.selected-questions {
  margin-top: 10px;
  display: flex;
  flex-wrap: wrap;
}

.question-review-item {
  margin-bottom: 20px;
}

.question-review-title {
  font-size: 15px;
  padding: 10px;
  background: #f5f7fa;
  border-radius: 4px;
  margin-bottom: 10px;
}

.question-review-options {
  padding-left: 20px;
  margin-bottom: 10px;
}

.option-item {
  padding: 3px 0;
}

.option-correct {
  background-color: #f0f9eb;
  color: #67c23a;
  padding: 2px 5px;
  border-radius: 3px;
}

.option-student {
  background-color: #ecf5ff;
  color: #409eff;
  padding: 2px 5px;
  border-radius: 3px;
}

.answer-review-section {
  margin-top: 10px;
  padding: 10px;
  background: #f5f7fa;
  border-radius: 4px;
}

.answer-review-row {
  margin: 5px 0;
  display: flex;
  align-items: flex-start;
}

.answer-review-label {
  font-weight: bold;
  margin-right: 10px;
  min-width: 80px;
}

.answer-review-value {
  flex: 1;
  line-height: 1.5;
}

.text-correct { color: #67c23a; }
.text-wrong { color: #f56c6c; }
.text-analysis { color: #409eff; }

.mb-4 { margin-bottom: 1rem; }

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 1rem;
  }
}
</style>
