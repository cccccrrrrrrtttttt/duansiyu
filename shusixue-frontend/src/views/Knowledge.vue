<template>
  <div class="knowledge-page">
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">知识点管'</h1>
        <p class="page-desc">系统化管理数学核心知识点，支持多级分'</p>
      </div>
      <el-button type="primary" @click="handleAdd" class="add-btn">
        <el-icon><Plus /></el-icon>
        新增知识'      </el-button>
    </div>

    <div class="search-section">
      <el-input
        v-model="searchKeyword"
        placeholder="搜索知识'.."
        clearable
        class="search-input"
        @input="handleSearch"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>
      <el-select v-model="subjectFilter" placeholder="选择科目" clearable class="subject-select">
        <el-option label="高等数学" value="高等数学" />
        <el-option label="线性代' value="线性代' />
        <el-option label="概率' value="概率' />
        <el-option label="数学建模" value="数学建模" />
      </el-select>
    </div>

    <div class="card">
      <el-table :data="filteredKnowledge" border stripe style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="知识点名' />
        <el-table-column prop="subject" label="科目" width="120" />
        <el-table-column prop="parentId" label="父知识点" width="120">
          <template #default="{ row }">
            {{ getParentName(row.parentId) }}
          </template>
        </el-table-column>
        <el-table-column prop="sort" label="排序" width="80" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <div v-if="!loading && filteredKnowledge.length === 0" class="empty-state">
      <el-icon :size="60" class="empty-icon">
        <Document />
      </el-icon>
      <p class="empty-text">暂无相关知识'</p>
    </div>

    <!-- 新增/编辑对话'-->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="知识点名' prop="title">
          <el-input v-model="form.title" placeholder="请输入知识点名称" />
        </el-form-item>
        <el-form-item label="科目" prop="subject">
          <el-select v-model="form.subject" placeholder="请选择科目" style="width: 100%">
            <el-option label="高等数学" value="高等数学" />
            <el-option label="线性代' value="线性代' />
            <el-option label="概率' value="概率' />
            <el-option label="数学建模" value="数学建模" />
          </el-select>
        </el-form-item>
        <el-form-item label="父知识点" prop="parentId">
          <el-select v-model="form.parentId" placeholder="选择父知识点（可选）" style="width: 100%">
            <el-option label="�? value="0" />
            <el-option v-for="item in knowledgeList" :key="item.id" :label="item.title" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="form.sort" :min="0" style="width: 100%" />
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
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Document, Plus } from '@element-plus/icons-vue'
import request from '@/utils/request'

const searchKeyword = ref('')
const subjectFilter = ref('')
const loading = ref(false)
const submitLoading = ref(false)
const knowledgeList = ref([])
const dialogVisible = ref(false)
const formRef = ref(null)

const form = ref({
  id: null,
  title: '',
  subject: '',
  parentId: 0,
  sort: 0
})

const rules = {
  title: [{ required: true, message: '请输入知识点名称', trigger: 'blur' }],
  subject: [{ required: true, message: '请选择科目', trigger: 'change' }],
  sort: [{ required: true, message: '请输入排', trigger: 'blur' }]
}

const dialogTitle = computed(() => form.value.id ? '编辑知识' : '新增知识')

const filteredKnowledge = computed(() => {
  let filtered = knowledgeList.value
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    filtered = filtered.filter(item =>
      item.title.toLowerCase().includes(keyword)
    )
  }
  if (subjectFilter.value) {
    filtered = filtered.filter(item => item.subject === subjectFilter.value)
  }
  return filtered
})

const getParentName = (parentId) => {
  if (parentId === 0) return '�?
  const parent = knowledgeList.value.find(item => item.id === parentId)
  return parent ? parent.title : '未知'
}

const handleSearch = () => {
  // 搜索功能已通过computed实现
}

const handleAdd = () => {
  form.value = {
    id: null,
    title: '',
    subject: '',
    parentId: 0,
    sort: 0
  }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  form.value = { ...row }
  dialogVisible.value = true
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该知识点吗', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await request.delete(`/api/knowledge-point/${row.id}`)
    ElMessage.success('删除成功')
    loadKnowledgeData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

const handleSubmit = async () => {
  await formRef.value.validate()
  submitLoading.value = true
  try {
    await request.post('/api/knowledge-point/save', form.value)
    ElMessage.success('操作成功')
    dialogVisible.value = false
    loadKnowledgeData()
  } catch (error) {
    console.error(error)
    ElMessage.error('操作失败: ' + (error.message || '请稍后重'))
  } finally {
    submitLoading.value = false
  }
}

const loadKnowledgeData = async () => {
  loading.value = true
  try {
    const res = await request.get('/api/knowledge-point/list', {
      params: { subject: subjectFilter.value }
    })
    knowledgeList.value = res.data || []
  } catch (error) {
    console.error('加载知识点失'', error)
    ElMessage.error('加载知识点失' ' + (error.message || '请稍后重'))
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadKnowledgeData()
})
</script>

<style scoped>
.knowledge-page { min-height: 100%; }

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

.search-section {
  display: flex;
  gap: 1rem;
  margin-bottom: 1.5rem;
  flex-wrap: wrap;
}

.search-input {
  flex: 1;
  min-width: 300px;
  font-size: 1rem;
}

.subject-select {
  min-width: 200px;
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

.empty-state {
  text-align: center;
  padding: 4rem 1rem;
}

.empty-icon {
  color: var(--border-color);
  margin-bottom: 1rem;
}

.empty-text {
  font-size: 1rem;
  color: var(--text-secondary);
}

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 1rem;
  }
  
  .search-section {
    flex-direction: column;
  }
  
  .search-input,
  .subject-select {
    width: 100%;
    min-width: unset;
  }
  
  .header-content h1 {
    font-size: 1.25rem;
  }
}
</style>
