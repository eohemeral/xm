<template>
  <div class="register">
    <el-card class="register-card">
      <h2 class="register-title">注册</h2>
      <el-form :model="registerForm" :rules="registerRules" ref="registerForm" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="registerForm.username" placeholder="请输入用户名"></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input type="password" v-model="registerForm.password" placeholder="请输入密码"></el-input>
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input type="password" v-model="registerForm.confirmPassword" placeholder="请确认密码"></el-input>
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="registerForm.nickname" placeholder="请输入昵称"></el-input>
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="registerForm.email" placeholder="请输入邮箱"></el-input>
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="registerForm.phone" placeholder="请输入手机号"></el-input>
        </el-form-item>
        <el-form-item label="头像">
          <el-upload
            class="upload-demo"
            action="/api/video/upload"
            :on-success="handleAvatarUploadSuccess"
            :on-error="handleUploadError"
            :file-list="avatarFileList"
            :auto-upload="true"
          >
            <el-button size="small" type="primary">点击上传头像</el-button>
            <div slot="tip" class="el-upload__tip">只能上传jpg/png文件，且不超过2MB</div>
          </el-upload>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="register" style="width: 100%">注册</el-button>
        </el-form-item>
        <el-form-item>
          <el-link type="primary" @click="$router.push('/login')">已有账号？去登录</el-link>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import request from '@/utils/request'

export default {
  name: 'Register',
  data() {
    return {
      registerForm: {
        username: '',
        password: '',
        confirmPassword: '',
        nickname: '',
        email: '',
        phone: '',
        avatar: ''
      },
      avatarFileList: [],
      registerRules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: '请确认密码', trigger: 'blur' },
          {
            validator: (rule, value, callback) => {
              if (value !== this.registerForm.password) {
                callback(new Error('两次输入的密码不一致'))
              } else {
                callback()
              }
            },
            trigger: 'blur'
          }
        ],
        nickname: [
          { required: true, message: '请输入昵称', trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    register() {
      this.$refs.registerForm.validate((valid) => {
        if (valid) {
          // 复制表单数据并删除confirmPassword字段
          const registerData = { ...this.registerForm }
          delete registerData.confirmPassword
          
          request.post('/user/register', registerData).then(res => {
            this.$message.success('注册成功')
            this.$router.push('/login')
          }).catch(error => {
            this.$message.error(error.message || '注册失败')
          })
        }
      })
    },
    handleAvatarUploadSuccess(response) {
      this.registerForm.avatar = response
      this.avatarFileList = [{
        name: 'avatar.jpg',
        url: response
      }]
      this.$message.success('头像上传成功')
    },
    handleUploadError(error) {
      this.$message.error('上传失败')
      console.error(error)
    }
  }
}
</script>

<style scoped>
.register {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
  position: relative;
  overflow: hidden;
}

.register::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle, rgba(255,255,255,0.1) 0%, transparent 60%);
  animation: pulse 15s linear infinite;
}

@keyframes pulse {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.register-card {
  width: 420px;
  padding: 40px 35px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  border-radius: 20px;
  background-color: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  animation: slideIn 0.6s ease-out;
  position: relative;
  z-index: 1;
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateY(-30px) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

.register-title {
  text-align: center;
  margin-bottom: 30px;
  color: #667eea;
  font-size: 30px;
  font-weight: bold;
  letter-spacing: 3px;
  position: relative;
}

.register-title::after {
  content: '';
  position: absolute;
  bottom: -10px;
  left: 50%;
  transform: translateX(-50%);
  width: 60px;
  height: 4px;
  background: linear-gradient(90deg, #667eea, #764ba2);
  border-radius: 2px;
}

.el-form-item {
  margin-bottom: 22px;
}

.el-form-item__label {
  font-weight: 600;
  color: #555;
  font-size: 14px;
}

.el-input {
  border-radius: 25px;
  transition: all 0.3s ease;
}

.el-input /deep/ .el-input__inner {
  border-radius: 25px;
  padding-left: 20px;
  height: 42px;
  border: 2px solid #e8e8e8;
}

.el-input /deep/ .el-input__inner:focus {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.el-button {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 25px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  color: white;
  letter-spacing: 2px;
  transition: all 0.3s ease;
  box-shadow: 0 5px 20px rgba(102, 126, 234, 0.4);
}

.el-button:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.5);
}

.el-link {
  display: block;
  text-align: center;
  margin-top: 20px;
  font-size: 14px;
  color: #667eea;
  font-weight: 500;
  transition: all 0.3s ease;
}

.el-link:hover {
  color: #764ba2;
  transform: scale(1.05);
}

/* 响应式设计 */
@media screen and (max-width: 480px) {
  .register-card {
    width: 100%;
    padding: 35px 25px;
  }

  .register-title {
    font-size: 26px;
  }
}
</style>