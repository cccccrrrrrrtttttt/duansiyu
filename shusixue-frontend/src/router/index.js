import { createRouter, createWebHashHistory } from 'vue-router'
import { useUserStore } from '@/store/user'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue')
  },
  {
    path: '/',
    name: 'Layout',
    component: () => import('@/views/Layout.vue'),
    children: [
      {
        path: 'home',
        name: 'Home',
        component: () => import('@/views/Home.vue')
      },
      {
        path: 'question',
        name: 'Question',
        component: () => import('@/views/teacher/Question.vue'),
        meta: { roles: ['TEACHER', 'ADMIN'] }
      },
      {
        path: 'homework-teacher',
        name: 'HomeworkTeacher',
        component: () => import('@/views/teacher/Homework.vue'),
        meta: { roles: ['TEACHER', 'ADMIN'] }
      },
      {
        path: 'homework-student',
        name: 'HomeworkStudent',
        component: () => import('@/views/student/Homework.vue'),
        meta: { roles: ['STUDENT'] }
      },
      {
        path: 'knowledge',
        name: 'Knowledge',
        component: () => import('@/views/Knowledge.vue')
      },
      {
        path: 'lesson-plan',
        name: 'LessonPlan',
        component: () => import('@/views/LessonPlan.vue'),
        meta: { roles: ['TEACHER', 'ADMIN'] }
      },
      {
        path: 'diagnosis',
        name: 'Diagnosis',
        component: () => import('@/views/Diagnosis.vue'),
        meta: { roles: ['TEACHER', 'ADMIN'] }
      },
      {
        path: 'evaluation',
        name: 'Evaluation',
        component: () => import('@/views/Evaluation.vue'),
        meta: { roles: ['TEACHER', 'ADMIN'] }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  const token = userStore.token
  const userInfo = userStore.userInfo

  if (to.path === '/login') {
    next()
  } else if (to.path === '/') {
    next('/login')
  } else {
    if (!token || !userInfo || !userInfo.id) {
      next('/login')
    } else {
      if (to.meta.roles && to.meta.roles.length > 0) {
        if (userInfo && userInfo.role && to.meta.roles.includes(userInfo.role)) {
          next()
        } else {
          next('/home')
        }
      } else {
        next()
      }
    }
  }
})

export default router
