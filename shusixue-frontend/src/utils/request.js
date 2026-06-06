import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

// 创建axios实例
const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8081',
  timeout: 60000
})

// 请求拦截器
request.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code !== 200) {
      ElMessage.error({
        message: res.msg || '请求失败',
        duration: 1000
      })
      if (res.code === 1005) {
        localStorage.removeItem('token')
        localStorage.removeItem('userInfo')
        router.push('/login')
      }
      return Promise.reject(new Error(res.msg || '请求失败'))
    } else {
      return res
    }
  },
  error => {
    console.error('请求错误:', error)
    let errorMsg = '网络错误'
    if (error.response) {
      if (error.response.data && error.response.data.msg) {
        errorMsg = error.response.data.msg
      } else {
        errorMsg = error.response.statusText || '请求失败'
      }
    } else if (error.message) {
      errorMsg = error.message
    }
    ElMessage.error({
      message: errorMsg,
      duration: 1000
    })
    return Promise.reject(error)
  }
)

export default request
