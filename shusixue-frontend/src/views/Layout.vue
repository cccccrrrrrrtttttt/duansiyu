<template>
  <div class="app-container">
    <!-- 顶部导航 -->
    <div class="navbar">
      <div class="nav-container">
        <a href="#" class="logo" @click.prevent="navigateTo('/home')">
          <div class="logo-icon">
            <el-icon><Reading /></el-icon>
          </div>
          数思学
        </a>
        
        <div class="nav-links">
          <a 
            v-for="item in menuItems" 
            :key="item.path"
            class="nav-item" 
            :class="{ active: activeMenu === item.path }"
            @click.prevent="navigateTo(item.path)"
          >
            <el-icon v-if="item.icon" class="nav-icon">
              <component :is="item.icon" />
            </el-icon>
            {{ item.title }}
          </a>
        </div>

        <div class="nav-right">
          <el-dropdown @command="handleCommand">
            <div class="user-avatar">
              {{ userStore.userInfo.realName ? userStore.userInfo.realName.charAt(0) : userStore.userInfo.username.charAt(0) }}
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">
                  <el-icon><SwitchButton /></el-icon>
                  退出登'                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </div>

    <div class="main-content">
      <router-view />
    </div>

    <!-- 页脚 -->
    <div class="footer">
      <p>© 2026 数思学 - 数学教学辅助平台</p>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/store/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  House, 
  Document, 
  Tickets, 
  Reading, 
  Edit, 
  DataAnalysis, 
  DocumentChecked,
  SwitchButton
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)

const menuItems = computed(() => {
  const role = userStore.userInfo.role
  const items = [
    { path: '/home', title: '首页', icon: House }
  ]
  
  if (role === 'TEACHER' || role === 'ADMIN') {
    items.push(
      { path: '/question', title: '题库管理', icon: Document },
      { path: '/homework-teacher', title: '作业管理', icon: Tickets },
      { path: '/knowledge', title: '知识点讲解', icon: Reading },
      { path: '/lesson-plan', title: '教案助手', icon: Edit },
      { path: '/diagnosis', title: '学情分析', icon: DataAnalysis },
      { path: '/evaluation', title: '教学评价与反思', icon: DocumentChecked }
    )
  } else if (role === 'STUDENT') {
    items.push(
      { path: '/homework-student', title: '我的作业', icon: Tickets },
      { path: '/knowledge', title: '知识点讲解', icon: Reading }
    )
  }
  
  return items
})

const navigateTo = (path) => {
  router.push(path)
}

const handleCommand = async (command) => {
  if (command === 'logout') {
    try {
      await ElMessageBox.confirm('确定要退出登录吗', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
      userStore.logout()
      ElMessage.success('退出成功')
      router.push('/login')
    } catch (error) {
      console.log('取消退')
    }
  }
}
</script>

<style scoped>
:root {
  --primary-color: #2563eb;
  --primary-dark: #1e40af;
  --accent-color: #059669;
  --bg-color: #f3f4f6;
  --card-bg: #ffffff;
  --text-main: #1f2937;
  --text-secondary: #6b7280;
  --border-color: #e5e7eb;
  --radius-sm: 8px;
  --radius-md: 12px;
  --radius-lg: 16px;
}



.app-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: var(--bg-color);
}

.navbar {
  background: var(--card-bg);
  border-bottom: 1px solid var(--border-color);
  padding: 0 1.5rem;
  position: sticky;
  top: 0;
  z-index: 50;
  box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
}

.nav-container {
  display: flex;
  align-items: center;
  justify-content: space-between;
  max-width: 1400px;
  margin: 0 auto;
  height: 64px;
  width: 100%;
}

.logo {
  display: flex;
  align-items: center;
  gap: 10px;
  font-weight: 700;
  font-size: 1.25rem;
  color: var(--primary-color);
  text-decoration: none;
  cursor: pointer;
}

.logo-icon {
  width: 32px;
  height: 32px;
  background: var(--primary-color);
  border-radius: var(--radius-sm);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 1.2rem;
}

.nav-links {
  display: flex;
  gap: 0.5rem;
  flex: 1;
  justify-content: center;
}

.nav-item {
  padding: 0.5rem 1rem;
  font-size: 0.9rem;
  color: var(--text-secondary);
  cursor: pointer;
  border-radius: var(--radius-md);
  transition: all 0.2s;
  text-decoration: none;
  border: none;
  background: none;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.nav-item:hover {
  color: var(--text-main);
  background: #f9fafb;
}

.nav-item.active {
  color: var(--primary-color);
  background: #eff6ff;
  font-weight: 600;
}

.nav-icon {
  font-size: 1.1rem;
}

.nav-right {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.user-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: 600;
  font-size: 0.9rem;
  cursor: pointer;
  transition: transform 0.2s;
}

.user-avatar:hover {
  transform: scale(1.05);
}

.main-content {
  flex: 1;
  padding: 2rem 1.5rem;
  width: 100%;
  max-width: 1400px;
  margin: 0 auto;
}

.footer {
  border-top: 1px solid var(--border-color);
  padding: 2rem 1.5rem;
  margin-top: 2rem;
  font-size: 0.85rem;
  color: var(--text-secondary);
  text-align: center;
  background: var(--card-bg);
}

@media (max-width: 1024px) {
  .nav-links {
    gap: 0.25rem;
  }
  
  .nav-item {
    padding: 0.4rem 0.6rem;
    font-size: 0.85rem;
  }
  
  .main-content {
    padding: 1.5rem 1rem;
  }
}

@media (max-width: 768px) {
  .nav-links {
    display: none;
  }
  
  .main-content {
    padding: 1rem;
  }
}
</style>

