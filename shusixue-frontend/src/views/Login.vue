<template>
  <div class="login-container">
    <el-card class="login-card">
      <template #header>
        <div class="card-header">
          <div class="logo">
            <div class="logo-icon">
              <el-icon><Reading /></el-icon>
            </div>
            数思学 - 数学教学辅助平台
          </div>
        </div>
      </template>
      <el-form :model="loginForm" :rules="rules" ref="loginFormRef" class="login-form">
        <el-form-item prop="username">
          <el-input v-model="loginForm.username" placeholder="请输入用户名" prefix-icon="User" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" prefix-icon="Lock" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" style="width: 100%" @click="handleLogin" :loading="loading">登录</el-button>
        </el-form-item>
        <div class="form-footer">
          还没有账号？
          <router-link to="/register" class="register-link">立即注册</router-link>
        </div>
      </el-form>
      <div class="login-footer">
        <p>© 2026 数思学 - 数学教学辅助平台</p>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/user'
import request from '@/utils/request'
import { Reading } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const loginFormRef = ref(null)
const loading = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  await loginFormRef.value.validate()
  loading.value = true
  try {
    // 登录前清除旧 token，确保登录请求不携带任何 token
    userStore.logout()
    console.log('开始登录，数据', loginForm)
    const res = await request.post('/api/user/login', loginForm)
    console.log('登录响应', res)
    // 后端返回结构：{ code: 200, msg: "操作成功", data: { token: "xxx" } }
    // 注意：响应拦截器已经返回了完整的res对象
    const token = res.data.token
    if (!token) {
      throw new Error('登录响应中未找到token')
    }
    userStore.setToken(token)
    console.log('设置token成功:', token)
    const userRes = await request.get('/api/user/info')
    console.log('获取用户信息响应', userRes)
    // 后端返回结构：{ code: 200, msg: "操作成功", data: { ...用户信息 } }
    // 注意：响应拦截器已经返回了完整的userRes对象
    userStore.setUserInfo(userRes.data)
    console.log('设置用户信息成功')
    ElMessage.success({
      message: '登录成功',
      duration: 1000
    })
    console.log('准备跳转到首页..')
    try {
      await router.push('/home')
      console.log('跳转成功')
    } catch (err) {
      console.error('跳转失败:', err)
    }
  } catch (error) {
    console.error('登录失败', error)
    console.error('错误详情', error.response)
    ElMessage.error({
      message: '登录失败' + (error.message || '未知错误'),
      duration: 1000
    })
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>


.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: var(--bg-color);
}

.login-card {
  width: 420px;
  border-radius: var(--radius-lg);
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
  overflow: hidden;
}

.card-header {
  padding: 2rem 0;
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

.login-form {
  padding: 0 2rem 1.5rem;
}

.login-footer {
  border-top: 1px solid var(--border-color);
  padding: 1.5rem 2rem 2rem;
  font-size: 0.85rem;
  color: var(--text-secondary);
  text-align: center;
}

.form-footer {
  text-align: center;
  font-size: 0.9rem;
  color: var(--text-secondary);
  padding-bottom: 1.5rem;
}

.register-link {
  color: var(--primary-color);
  text-decoration: none;
  font-weight: 600;
}

.register-link:hover {
  text-decoration: underline;
}

@media (max-width: 768px) {
  .login-card {
    width: 90%;
    max-width: 420px;
  }
  
  .card-header {
    padding: 1.5rem 0;
  }
  
  .logo {
    font-size: 1.25rem;
  }
  
  .logo-icon {
    width: 36px;
    height: 36px;
    font-size: 1.2rem;
  }
  
  .login-form {
    padding: 0 1.5rem 1.25rem;
  }
  
  .login-footer {
    padding: 1.25rem 1.5rem 1.5rem;
  }
}
</style>
