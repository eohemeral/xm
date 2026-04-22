import axios from 'axios'

// 导出getToken函数
export function getToken() {
  return localStorage.getItem('token') || ''
}

const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// 请求拦截器
request.interceptors.request.use(
  config => {
    const token = getToken()
    if (token) {
      config.headers.token = token
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
    if (res.code === '200' || res.code === 200) {
      return res.data
    } else {
      return Promise.reject(new Error(res.msg || '请求失败'))
    }
  },
  error => {
    if (error.response) {
      const res = error.response.data
      return Promise.reject(new Error(res.msg || '请求失败'))
    }
    return Promise.reject(new Error('网络错误'))
  }
)

export default request