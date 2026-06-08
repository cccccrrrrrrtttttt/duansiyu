<template>
  <div class="register-container">
    <el-card class="register-card">
      <template #header>
        <div class="card-header">
          <div class="logo">
            <div class="logo-icon">
              <el-icon><EditPen /></el-icon>
            </div>
            注册账号
          </div>
          <div class="subtitle">加入数思学 - 数学教学辅助平台</div>
        </div>
      </template>
      <el-form :model="registerForm" :rules="rules" ref="registerFormRef" class="register-form">
        <el-form-item prop="username">
          <el-input v-model="registerForm.username" placeholder="请输入用户名（3-20位）" prefix-icon="User" />
        </el-form-item>
        <el-form-item prop="realName">
          <el-input v-model="registerForm.realName" placeholder="请输入真实姓名" prefix-icon="Edit" />
        </el-form-item>
        <el-form-item prop="role">
          <el-radio-group v-model="registerForm.role" class="role-group">
            <el-radio value="STUDENT" size="large">
              <el-icon><UserFilled /></el-icon>
              <span>学生账号</span>
            </el-radio>
            <el-radio value="TEACHER" size="large">
              <el-icon><Avatar /></el-icon>
              <span>老师账号</span>
            </el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="registerForm.password" type="password" placeholder="请输入密码（6-20位）" prefix-icon="Lock" show-password />
        </el-form-item>
        <el-form-item prop="confirmPassword">
          <el-input v-model="registerForm.confirmPassword" type="password" placeholder="请确认密码" prefix-icon="Lock" show-password />
        </el-form-item>
        <el-form-item prop="email">
          <el-input v-model="registerForm.email" placeholder="请输入邮箱（选填）" prefix-icon="Message" />
        </el-form-item>
        <el-form-item prop="phone">
          <el-input v-model="registerForm.phone" placeholder="请输入手机号（选填）" prefix-icon="Iphone" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" style="width: 100%" @click="handleRegister" :loading="loading">注册</el-button>
        </el-form-item>
        <div class="form-footer">
          已有账号？
          <router-link to="/login" class="login-link">立即登录</router-link>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'
import { EditPen, UserFilled, Avatar } from '@element-plus/icons-vue'

const router = useRouter()
const registerFormRef = ref(null)
const loading = ref(false)

const registerForm = reactive({
  username: '',
  realName: '',
  role: 'STUDENT',
  password: '',
  confirmPassword: '',
  email: '',
  phone: ''
})

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== registerForm.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度3-20位', trigger: 'blur' }
  ],
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' }
  ],
  role: [
    { required: true, message: '请选择角色', trigger: 'change' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度6-20位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ]
}

const handleRegister = async () => {
  await registerFormRef.value.validate()
  loading.value = true
  try {
    const res = await request.post('/api/user/register', {
      username: registerForm.username,
      password: registerForm.password,
      realName: registerForm.realName,
      role: registerForm.role,
      email: registerForm.email || undefined,
      phone: registerForm.phone || undefined
    })
    ElMessage.success({
      message: '注册成功，请登录',
      duration: 1500
    })
    setTimeout(() => {
      router.push('/login')
    }, 1500)
  } catch (error) {
    ElMessage.error({
      message: '注册失败：' + (error.response?.data?.msg || error.message || '未知错误'),
      duration: 2000
    })
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: var(--bg-color);
  padding: 2rem 0;
}

.register-card {
  width: 480px;
  border-radius: var(--radius-lg);
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
  overflow: hidden;
}

.card-header {
  padding: 1.5rem 0 0.5rem;
  text-align: center;
}

.logo {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  font-weight: 700;
  font-size: 1.5rem;
  color: var(--primary-color);
}

.logo-icon {
  width: 40px;
  height: 40px;
  background: var(--primary-color);
  border-radius: var(--radius-sm);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 1.4rem;
}

.subtitle {
  margin-top: 8px;
  font-size: 0.9rem;
  color: var(--text-secondary);
}

.register-form {
  padding: 0 2rem 1.5rem;
}

.role-group {
  display: flex;
  gap: 16px;
  width: 100%;
}

.role-group .el-radio {
  flex: 1;
  display: flex;
  justify-content: center;
  padding: 12px;
  border: 1px solid var(--border-color);
  border-radius: var(--radius-sm);
  transition: all 0.3s;
}

.role-group .el-radio.is-checked {
  border-color: var(--primary-color);
  background: rgba(59, 130, 246, 0.05);
}

.form-footer {
  text-align: center;
  font-size: 0.9rem;
  color: var(--text-secondary);
}

.login-link {
  color: var(--primary-color);
  text-decoration: none;
  font-weight: 600;
}

.login-link:hover {
  text-decoration: underline;
}

@media (max-width: 768px) {
  .register-card {
    width: 90%;
    max-width: 480px;
  }

  .register-form {
    padding: 0 1.5rem 1.25rem;
  }
}
</style>