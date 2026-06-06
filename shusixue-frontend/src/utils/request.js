import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

// 创建axios实例
const request = axios.create({
  baseURL: 'http://localhost:8081', // 后端接口地址
  timeout: 60000 // 请求超时时间，大模型API需要更长时间
})

// 请求拦截器
request.interceptors.request.use(
  config => {
    // 从localStorage获取token
    const token = localStorage.getItem('token')
    if (token) {
      // 在请求头中添加token
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
    // 如果返回的状态码不是200，说明出错了
    if (res.code !== 200) {
      ElMessage.error({
        message: res.msg || '请求失败',
        duration: 1000 // 1秒后自动消失
      })
      // 如果是token无效，跳转到登录页
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
      duration: 1000 // 1秒后自动消失
    })
    return Promise.reject(error)
  }
)

export default request
