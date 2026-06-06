<template>
  <div class="homework">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>我的作业</span>
        </div>
      </template>
      <!-- 搜索栏 -->
      <el-form :inline="true" :model="queryForm" class="search-form">
        <el-form-item label="关键词">
          <el-input v-model="queryForm.keyword" placeholder="搜索作业标题" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="请选择状态" clearable>
            <el-option label="已发布" value="PUBLISHED" />
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
        <el-table-column prop="status" label="作业状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.status === 'PUBLISHED'" type="success">进行中</el-tag>
            <el-tag v-else type="info">已结束</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="startTime" label="开始时间" width="180" />
        <el-table-column prop="endTime" label="截止时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleDoHomework(row)">去答题</el-button>
            <el-button type="info" link size="small" @click="handleViewResult(row)">查看结果</el-button>
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
    </el-card>

    <!-- 答题对话框 -->
    <el-dialog v-model="doHomeworkVisible" :title="currentHomework?.title" width="800px" :close-on-click-modal="false">
      <div v-loading="doHomeworkLoading">
        <el-form v-if="currentHomework" :model="answerForm" label-width="100px">
          <div v-for="(question, index) in questionList" :key="question.id" class="question-item">
            <el-divider content-position="left">第 {{ index + 1 }} 题 ({{ getTypeText(question.type) }})</el-divider>
            <div class="question-title">{{ question.title }}</div>
            <div v-if="question.options" class="question-options">
              <div v-for="(opt, i) in JSON.parse(question.options)" :key="i" class="option-item">
                {{ opt }}
              </div>
            </div>
            <el-form-item :label="'我的答案'">
              <el-input
                v-if="question.type === 'ESSAY'"
                v-model="answerForm[question.id]"
                type="textarea"
                :rows="4"
                placeholder="请输入你的解答过程"
              />
              <el-input
                v-else
                v-model="answerForm[question.id]"
                placeholder="请输入答案"
              />
            </el-form-item>
          </div>
        </el-form>
      </div>
      <template #footer>
        <el-button @click="doHomeworkVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitHomework" :loading="submitHomeworkLoading">提交作业</el-button>
      </template>
    </el-dialog>

    <!-- 查看结果对话框 -->
    <el-dialog v-model="resultDialogVisible" title="答题结果" width="900px" :close-on-click-modal="false">
      <el-skeleton :rows="10" animated v-if="resultLoading" />
      <div v-else>
        <!-- 基本信息 -->
        <el-descriptions :column="3" border class="mb-4">
          <el-descriptions-item label="作业标题">{{ submissionDetail.homeworkTitle }}</el-descriptions-item>
          <el-descriptions-item label="学生姓名">{{ submissionDetail.studentName }}</el-descriptions-item>
          <el-descriptions-item label="提交状态">
            <el-tag v-if="submissionDetail.status === 'NOT_SUBMITTED'">未提交</el-tag>
            <el-tag v-else-if="submissionDetail.status === 'SUBMITTED'" type="warning">待批改</el-tag>
            <el-tag v-else-if="submissionDetail.status === 'GRADED'" type="success">已批改</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="总得分">{{ submissionDetail.totalScore ?? '-' }}</el-descriptions-item>
          <el-descriptions-item label="正确率">{{ submissionDetail.correctRate }}</el-descriptions-item>
          <el-descriptions-item label="提交时间">{{ submissionDetail.submitTime }}</el-descriptions-item>
        </el-descriptions>

        <!-- 答题详情 -->
        <div class="question-list">
          <h3>答题详情</h3>
          <div v-for="(question, index) in submissionDetail.questionList" :key="question.questionId" class="question-item">
            <div class="question-header">
              <span class="question-index">第 {{ index + 1 }} 题</span>
              <el-tag :type="question.isCorrect ? 'success' : 'danger'" size="small">
                {{ question.isCorrect ? '正确' : '错误' }}
              </el-tag>
              <el-tag type="info" size="small" class="ml-2">{{ getQuestionTypeText(question.type) }}</el-tag>
            </div>
            <div class="question-content">
              <p class="question-title">{{ question.title }}</p>
              <div v-if="question.options" class="question-options">
                <div v-for="option in parseOptions(question.options)" :key="option.key" class="option-item">
                  <span :class="{ 'correct-option': isCorrectOption(option.key, question.standardAnswer), 'student-option': isStudentOption(option.key, question.studentAnswer) }">
                    {{ option.key }}. {{ option.value }}
                  </span>
                </div>
              </div>
            </div>
            <div class="answer-section">
              <div class="answer-row">
                <span class="answer-label">我的答案：</span>
                <span :class="['answer-value', question.isCorrect ? 'correct' : 'wrong']">
                  {{ question.studentAnswer || '未作答' }}
                </span>
              </div>
              <div class="answer-row">
                <span class="answer-label">正确答案：</span>
                <span class="answer-value correct">{{ question.standardAnswer }}</span>
              </div>
              <div class="answer-row" v-if="question.analysis">
                <span class="answer-label">解析：</span>
                <span class="answer-value analysis">{{ question.analysis }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

const tableData = ref([])
const total = ref(0)
const doHomeworkVisible = ref(false)
const doHomeworkLoading = ref(false)
const submitHomeworkLoading = ref(false)
const currentHomework = ref(null)
const questionList = ref([])
const answerForm = reactive({})
const currentSubmissionId = ref(null)
const resultDialogVisible = ref(false)
const resultLoading = ref(false)
const submissionDetail = ref({
  homeworkTitle: '',
  studentName: '',
  totalScore: null,
  correctRate: '',
  submitTime: '',
  status: '',
  questionList: []
})

const queryForm = reactive({
  pageNum: 1,
  pageSize: 10,
  keyword: '',
  status: ''
})

const getHomeworkPage = async () => {
  try {
    const res = await request.get('/api/homework/student/page', { params: queryForm })
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

const getTypeText = (type) => {
  const map = {
    'SINGLE_CHOICE': '单选题',
    'MULTIPLE_CHOICE': '多选题',
    'FILL_BLANK': '填空题',
    'ESSAY': '解答题'
  }
  return map[type] || type
}

const getQuestionTypeText = (type) => {
  return getTypeText(type)
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
    console.error('解析选项失败:', error)
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

const handleDoHomework = async (row) => {
  currentHomework.value = row
  doHomeworkLoading.value = true
  doHomeworkVisible.value = true
  try {
    const res = await request.get(`/api/homework/detail/${row.id}`)
    questionList.value = res?.data?.questionList || []
    for (const key in answerForm) {
      delete answerForm[key]
    }
  } catch (error) {
    console.error(error)
    doHomeworkVisible.value = false
  } finally {
    doHomeworkLoading.value = false
  }
}

const handleSubmitHomework = async () => {
  try {
    await ElMessageBox.confirm('确定要提交作业吗？提交后无法修改！', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    // 检查是否有答案
    if (Object.keys(answerForm).length === 0) {
      ElMessage.warning({
        message: '请至少回答一题',
        duration: 1000
      })
      return
    }

    // 转换 answerForm 的键为数字类型
    const answers = {}
    for (const key in answerForm) {
      answers[Number(key)] = answerForm[key]
    }

    console.log('提交作业数据:', {
      homeworkId: currentHomework.value.id,
      answers: answers
    })

    // 检查 token
    const token = localStorage.getItem('token')
    console.log('当前 token:', token ? '存在' : '不存在')

    submitHomeworkLoading.value = true

    // 实际提交代码
    try {
      const res = await request.post('/api/homework/submit', {
        homeworkId: currentHomework.value.id,
        answers: answers
      })
      console.log('提交作业响应:', res)
      ElMessage.success({
        message: '提交成功',
        duration: 1000
      })
      doHomeworkVisible.value = false
      getHomeworkPage()
    } catch (error) {
      console.error('提交作业失败:', error)
      console.error('错误详情:', error.message, error.response)
      const errorMsg = error?.message || error?.toString() || '请稍后重试'
      ElMessage.error({
        message: `提交失败: ${errorMsg}`,
        duration: 1000
      })
    } finally {
      submitHomeworkLoading.value = false
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('提交作业失败:', error)
      const errorMsg = error?.message || error?.toString() || '请稍后重试'
      ElMessage.error({
        message: `提交失败: ${errorMsg}`,
        duration: 1000
      })
    }
  }
}

const handleViewResult = async (row) => {
  resultLoading.value = true
  try {
    // 首先获取作业的提交记录（使用学生专用的接口）
    const submissionRes = await request.get(`/api/homework/submission/student/list/${row.id}`, { params: { pageNum: 1, pageSize: 10 } })
    const submission = submissionRes?.data?.records?.[0]
    if (!submission) {
      ElMessage.warning({
        message: '暂无提交记录',
        duration: 1000
      })
      return
    }
    
    // 获取提交详情
    const res = await request.get(`/api/homework/submission/detail/${submission.id}`)
    submissionDetail.value = res?.data || {}
    resultDialogVisible.value = true
  } catch (error) {
    console.error('获取提交详情失败:', error)
    ElMessage.error({
      message: '获取提交详情失败: ' + (error.message || '请稍后重试'),
      duration: 1000
    })
  } finally {
    resultLoading.value = false
  }
}

onMounted(() => {
  getHomeworkPage()
})
</script>

<style scoped>
.homework {
  height: 100%;
}

.card-header {
  font-size: 18px;
  font-weight: bold;
}

.search-form {
  margin-bottom: 20px;
}

.question-item {
  margin-bottom: 20px;
}

.question-title {
  font-size: 16px;
  margin-bottom: 10px;
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.question-options {
  margin-bottom: 10px;
  padding-left: 20px;
}

/* 查看结果样式 */
.question-header {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.question-index {
  font-weight: bold;
  margin-right: 10px;
}

.question-list {
  margin-top: 20px;
}

.question-list h3 {
  margin-bottom: 15px;
  color: #303133;
}

.answer-section {
  margin-top: 15px;
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.answer-row {
  margin: 5px 0;
  display: flex;
  align-items: flex-start;
}

.answer-label {
  font-weight: bold;
  margin-right: 10px;
  min-width: 80px;
}

.answer-value {
  flex: 1;
  line-height: 1.5;
}

.answer-value.correct {
  color: #67c23a;
}

.answer-value.wrong {
  color: #f56c6c;
}

.answer-value.analysis {
  color: #409eff;
}

.correct-option {
  background-color: #f0f9eb;
  color: #67c23a;
  padding: 2px 5px;
  border-radius: 3px;
  margin-right: 5px;
}

.student-option {
  background-color: #ecf5ff;
  color: #409eff;
  padding: 2px 5px;
  border-radius: 3px;
  margin-right: 5px;
}

.ml-2 {
  margin-left: 8px;
}

.option-item {
  padding: 5px 0;
}
</style>
