<template>
  <div class="login">
    <el-card class="login-card">
      <h2 class="login-title">登录</h2>
      <el-form :model="loginForm" :rules="loginRules" ref="loginForm" label-width="80px">
        <el-form-item label="登录类型" prop="loginType">
          <el-radio-group v-model="loginForm.loginType">
            <el-radio label="user">普通用户</el-radio>
            <el-radio label="admin">管理员</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="用户名" prop="username">
          <el-input v-model="loginForm.username" placeholder="请输入用户名"></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input type="password" v-model="loginForm.password" placeholder="请输入密码"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="login" style="width: 100%">登录</el-button>
        </el-form-item>
        <el-form-item>
          <el-link type="primary" @click="$router.push('/register')">注册新账号</el-link>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import request from '@/utils/request'

export default {
  name: 'Login',
  data() {
    return {
      loginForm: {
        loginType: 'user',
        username: '',
        password: ''
      },
      loginRules: {
        loginType: [
          { required: true, message: '请选择登录类型', trigger: 'change' }
        ],
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    login() {
      this.$refs.loginForm.validate((valid) => {
        if (valid) {
          const { username, password } = this.loginForm
          request.post('/user/login', {
            username,
            password
          }).then(res => {
            localStorage.setItem('token', res.token)
            localStorage.setItem('user', JSON.stringify(res.user))
            this.$message.success('登录成功')
            if (username === 'admin') {
              this.$router.push('/admin/dashboard')
            } else {
              this.$router.push('/')
            }
          }).catch(error => {
            this.$message.error(error.message || '登录失败')
          })
        }
      })
    }
  }
}
</script>

<style scoped>
.login {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f8f9fa;
  padding: 20px;
}

.login-card {
  width: 400px;
  padding: 40px 32px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  border-radius: 8px;
  background-color: white;
  border: 1px solid #eaeaea;
}

.login-title {
  text-align: center;
  margin-bottom: 28px;
  color: #333;
  font-size: 24px;
  font-weight: 600;
  position: relative;
  padding-bottom: 12px;
}

.login-title::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 60px;
  height: 2px;
  background: #409EFF;
  border-radius: 1px;
}

.el-form-item {
  margin-bottom: 20px;
}

.el-form-item__label {
  font-weight: 500;
  color: #555;
  font-size: 14px;
}

.el-input /deep/ .el-input__inner {
  border-radius: 4px;
  padding-left: 12px;
  height: 40px;
  border: 1px solid #dcdfe6;
}

.el-input /deep/ .el-input__inner:focus {
  border-color: #409EFF;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
}

.el-button {
  width: 100%;
  height: 40px;
  font-size: 14px;
  font-weight: 500;
  border-radius: 4px;
  background: #409EFF;
  border: none;
  color: white;
  transition: all 0.3s ease;
  box-shadow: 0 2px 4px rgba(64, 158, 255, 0.2);
}

.el-button:hover {
  background: #66b1ff;
  box-shadow: 0 3px 6px rgba(64, 158, 255, 0.3);
}

.el-link {
  display: block;
  text-align: center;
  margin-top: 16px;
  font-size: 14px;
  color: #409EFF;
  font-weight: 500;
  transition: all 0.3s ease;
}

.el-link:hover {
  color: #66b1ff;
}

.el-radio-group {
  display: flex;
  gap: 16px;
}

.el-radio /deep/ .el-radio__input.is-checked .el-radio__inner {
  background: #409EFF;
  border-color: #409EFF;
}

.el-radio /deep/ .el-radio__input.is-checked + .el-radio__label {
  color: #409EFF;
}

@media screen and (max-width: 480px) {
  .login-card {
    width: 100%;
    padding: 32px 24px;
  }

  .login-title {
    font-size: 20px;
  }
}
</style>
