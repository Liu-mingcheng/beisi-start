<template>
  <div class="login-container">
    <!-- 登录表单 -->
    <div v-if="!isLoggedIn" class="login-box">
      <h2>用户登录</h2>
      
      <form @submit.prevent="handleLogin">
        <div class="form-group">
          <input
            type="text"
            v-model="form.username"
            placeholder="用户名"
            :disabled="loading"
            required
          >
        </div>
        
        <div class="form-group">
          <input
            type="password"
            v-model="form.password"
            placeholder="密码"
            :disabled="loading"
            required
          >
        </div>
        
        <button type="submit" :disabled="loading" class="login-btn">
          {{ loading ? '登录中...' : '登录' }}
        </button>
      </form>
      
      <div class="demo-info">
        <p>演示账号: admin / 123456</p>
      </div>
    </div>
    
    <!-- 欢迎页面 -->
    <div v-else class="welcome-box">
      <h2>登录成功!</h2>
      <p class="welcome-msg">欢迎，{{ user.username }}！</p>
      <div class="token-info">
        <p>Token: {{ user.token }}</p>
        <button @click="copyToken" class="copy-btn">复制</button>
      </div>
      <button @click="handleLogout" class="logout-btn">退出登录</button>
    </div>
    
    <!-- 消息提示 -->
    <div v-if="message" :class="['message', messageType]">
      {{ message }}
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { login } from '../api/auth.js'

// 响应式数据
const isLoggedIn = ref(false)
const loading = ref(false)
const message = ref('')
const messageType = ref('')

const form = reactive({
  username: '',
  password: ''
})

const user = reactive({
  username: '',
  token: ''
})

// 方法
const showMessage = (text, type = 'success') => {
  message.value = text
  messageType.value = type
  setTimeout(() => {
    message.value = ''
    messageType.value = ''
  }, 3000)
}

const handleLogin = async () => {
  if (!form.username || !form.password) {
    showMessage('请填写用户名和密码', 'error')
    return
  }

  loading.value = true

  try {
    const response = await login(form.username, form.password)
    
    if (response.code === 0) {
      user.username = form.username
      user.token = response.data.token
      isLoggedIn.value = true
      
      // 保存到 localStorage
      localStorage.setItem('userInfo', JSON.stringify(user))
      showMessage('登录成功！')
      
      // 清空表单
      form.username = ''
      form.password = ''
    } else {
      showMessage(response.message, 'error')
    }
  } catch (error) {
    showMessage(error.message, 'error')
  } finally {
    loading.value = false
  }
}

const handleLogout = () => {
  isLoggedIn.value = false
  user.username = ''
  user.token = ''
  localStorage.removeItem('userInfo')
  showMessage('已退出登录')
}

const copyToken = async () => {
  try {
    await navigator.clipboard.writeText(user.token)
    showMessage('Token 已复制')
  } catch (err) {
    showMessage('复制失败', 'error')
  }
}

const checkLoginStatus = () => {
  const savedUserInfo = localStorage.getItem('userInfo')
  if (savedUserInfo) {
    try {
      const parsedInfo = JSON.parse(savedUserInfo)
      user.username = parsedInfo.username
      user.token = parsedInfo.token
      isLoggedIn.value = true
    } catch (error) {
      localStorage.removeItem('userInfo')
    }
  }
}

// 生命周期
onMounted(() => {
  checkLoginStatus()
})
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.login-box, .welcome-box {
  background: white;
  padding: 40px;
  border-radius: 12px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 400px;
}

h2 {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
}

.form-group {
  margin-bottom: 20px;
}

input {
  width: 100%;
  padding: 12px 16px;
  border: 2px solid #e1e5e9;
  border-radius: 8px;
  font-size: 16px;
  transition: border-color 0.3s;
}

input:focus {
  outline: none;
  border-color: #667eea;
}

input:disabled {
  background-color: #f5f5f5;
  cursor: not-allowed;
}

.login-btn, .logout-btn {
  width: 100%;
  padding: 12px;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  cursor: pointer;
  transition: background 0.3s;
  margin-top: 10px;
}

.login-btn:hover:not(:disabled), .logout-btn:hover {
  background: #5a6fd8;
}

.login-btn:disabled {
  background: #a0aec0;
  cursor: not-allowed;
}

.demo-info {
  margin-top: 20px;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 6px;
  text-align: center;
  font-size: 14px;
  color: #666;
}

.welcome-msg {
  text-align: center;
  margin-bottom: 20px;
  font-size: 18px;
}

.token-info {
  background: #f8f9fa;
  padding: 15px;
  border-radius: 8px;
  margin-bottom: 20px;
  display: flex;
  justify-content: between;
  align-items: center;
  gap: 10px;
}

.token-info p {
  flex: 1;
  word-break: break-all;
  font-family: monospace;
  font-size: 14px;
  margin: 0;
}

.copy-btn {
  padding: 6px 12px;
  background: #48bb78;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
}

.copy-btn:hover {
  background: #38a169;
}

.logout-btn {
  background: #e53e3e;
}

.logout-btn:hover {
  background: #c53030;
}

.message {
  position: fixed;
  top: 20px;
  right: 20px;
  padding: 12px 20px;
  border-radius: 6px;
  color: white;
  font-weight: 500;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.message.success {
  background: #48bb78;
}

.message.error {
  background: #e53e3e;
}

@media (max-width: 480px) {
  .login-box, .welcome-box {
    padding: 30px 20px;
    margin: 10px;
  }
}
</style>