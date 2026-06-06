<template>
  <div class="question-page">
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">题库管理</h1>
        <p class="page-desc">管理您的题目库，支持多种题型和科目分'</p>
      </div>
      <el-button type="primary" @click="handleAdd" class="add-btn">
        <el-icon><Plus /></el-icon>
        新增题目
      </el-button>
    </div>

    <div class="card">
      <!-- 搜索'-->
      <el-form :inline="true" :model="queryForm" class="search-form">
        <el-form-item label="关键词">
          <el-input v-model="queryForm.keyword" placeholder="搜索题目内容" clearable />
        </el-form-item>
        <el-form-item label="科目">
          <el-select v-model="queryForm.subject" placeholder="请选择科目" clearable>
            <el-option label="高等数学" value="高等数学" />
            <el-option label="线性代' value="线性代' />
            <el-option label="概率' value="概率' />
            <el-option label="数学建模" value="数学建模" />
          </el-select>
        </el-form-item>
        <el-form-item label="题型">
          <el-select v-model="queryForm.type" placeholder="请选择题型" clearable>
            <el-option label="单选题" value="SINGLE_CHOICE" />
            <el-option label="多选题" value="MULTIPLE_CHOICE" />
            <el-option label="填空' value="FILL_BLANK" />
            <el-option label="解答' value="ESSAY" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="getQuestionPage">搜索</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
      <!-- 表格 -->
      <el-table :data="tableData" border stripe style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="题目内容" show-overflow-tooltip />
        <el-table-column prop="type" label="题型" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.type === 'SINGLE_CHOICE'">单选题</el-tag>
            <el-tag v-else-if="row.type === 'MULTIPLE_CHOICE'" type="success">多选题</el-tag>
            <el-tag v-else-if="row.type === 'FILL_BLANK'" type="warning">填空'</el-tag>
            <el-tag v-else type="danger">解答'</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="difficulty" label="难度" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.difficulty === 'EASY'" type="success">简'</el-tag>
            <el-tag v-else-if="row.difficulty === 'MEDIUM'" type="warning">中等</el-tag>
            <el-tag v-else type="danger">困难</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="subject" label="科目" width="120" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
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
        @size-change="getQuestionPage"
        @current-change="getQuestionPage"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </div>

    <!-- 新增/编辑对话'-->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="700px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="题目内容" prop="title">
          <el-input v-model="form.title" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="题型" prop="type">
          <el-select v-model="form.type" placeholder="请选择题型" style="width: 100%">
            <el-option label="单选题" value="SINGLE_CHOICE" />
            <el-option label="多选题" value="MULTIPLE_CHOICE" />
            <el-option label="填空' value="FILL_BLANK" />
            <el-option label="解答' value="ESSAY" />
          </el-select>
        </el-form-item>
        <el-form-item label="选项" v-if="form.type === 'SINGLE_CHOICE' || form.type === 'MULTIPLE_CHOICE'">
          <el-input v-model="optionsStr" type="textarea" placeholder="请输入选项，每行一个，格式：A. xxx" />
        </el-form-item>
        <el-form-item label="答案" prop="answer">
          <el-input v-model="form.answer" type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item label="解析">
          <el-input v-model="form.analysis" type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item label="难度" prop="difficulty">
          <el-select v-model="form.difficulty" placeholder="请选择难度" style="width: 100%">
            <el-option label="简' value="EASY" />
            <el-option label="中等" value="MEDIUM" />
            <el-option label="困难" value="HARD" />
          </el-select>
        </el-form-item>
        <el-form-item label="科目" prop="subject">
          <el-select v-model="form.subject" placeholder="请选择科目" style="width: 100%">
            <el-option label="高等数学" value="高等数学" />
            <el-option label="线性代' value="线性代' />
            <el-option label="概率' value="概率' />
            <el-option label="数学建模" value="数学建模" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import request from '@/utils/request'

const tableData = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const dialogTitle = computed(() => form.id ? '编辑题目' : '新增题目')
const formRef = ref(null)
const submitLoading = ref(false)
const optionsStr = ref('')

const queryForm = reactive({
  pageNum: 1,
  pageSize: 10,
  keyword: '',
  subject: '',
  type: '',
  difficulty: ''
})

const form = reactive({
  id: null,
  title: '',
  type: '',
  options: '',
  answer: '',
  analysis: '',
  difficulty: 'MEDIUM',
  subject: '',
  knowledgePointIds: ''
})

const rules = {
  title: [{ required: true, message: '请输入题目内', trigger: 'blur' }],
  type: [{ required: true, message: '请选择题型', trigger: 'change' }],
  answer: [{ required: true, message: '请输入答', trigger: 'blur' }],
  difficulty: [{ required: true, message: '请选择难度', trigger: 'change' }],
  subject: [{ required: true, message: '请选择科目', trigger: 'change' }]
}

// 获取题目列表
const getQuestionPage = async () => {
  try {
    const res = await request.get('/api/question/page', { params: queryForm })
    tableData.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    console.error(error)
  }
}

// 重置查询
const resetQuery = () => {
  queryForm.keyword = ''
  queryForm.subject = ''
  queryForm.type = ''
  queryForm.difficulty = ''
  queryForm.pageNum = 1
  getQuestionPage()
}

// 新增
const handleAdd = () => {
  form.id = null
  form.title = ''
  form.type = ''
  form.options = ''
  form.answer = ''
  form.analysis = ''
  form.difficulty = 'MEDIUM'
  form.subject = ''
  optionsStr.value = ''
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row) => {
  Object.assign(form, row)
  optionsStr.value = row.options || ''
  dialogVisible.value = true
}

// 删除
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该题目吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await request.delete(`/api/question/${row.id}`)
    ElMessage.success('删除成功')
    getQuestionPage()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

// 提交
const handleSubmit = async () => {
  await formRef.value.validate()
  // 处理选项
  if (form.type === 'SINGLE_CHOICE' || form.type === 'MULTIPLE_CHOICE') {
    // 简单处理：把多行选项转成JSON数组
    const options = optionsStr.value.split('\n').filter(item => item.trim())
    form.options = JSON.stringify(options)
  } else {
    form.options = ''
  }
  submitLoading.value = true
  try {
    await request.post('/api/question/save', form)
    ElMessage.success('操作成功')
    dialogVisible.value = false
    getQuestionPage()
  } catch (error) {
    console.error(error)
  } finally {
    submitLoading.value = false
  }
}

onMounted(() => {
  getQuestionPage()
})
</script>

<style scoped>
.question-page {
  height: 100%;
}

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

.add-btn {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.6rem 1.2rem;
  border-radius: var(--radius-md);
  font-weight: 500;
}

.card {
  background: var(--card-bg);
  border-radius: var(--radius-lg);
  border: 1px solid var(--border-color);
  box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
  padding: 1.5rem;
  transition: box-shadow 0.2s;
}

.card:hover {
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
}

.search-form {
  margin-bottom: 1.5rem;
  padding: 1rem;
  background: #f9fafb;
  border-radius: var(--radius-md);
}


</style>
