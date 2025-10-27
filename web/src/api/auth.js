import axios from 'axios'

// 创建 axios 实例
const api = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 登录 API
export const login = async (username, password) => {
  try {
    const response = await api.post('/login', { username, password })
    return response.data
  } catch (error) {
    if (error.response) {
      throw new Error(error.response.data?.message || '登录失败')
    } else if (error.request) {
      throw new Error('网络错误，请检查后端服务')
    } else {
      throw new Error('请求配置错误')
    }
  }
}