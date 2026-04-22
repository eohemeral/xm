<template>
  <div class="user-info">
    <h2>个人信息</h2>
    <el-form :model="userForm" :rules="userRules" ref="userForm" label-width="100px">
      <el-form-item label="用户名" prop="username">
        <el-input v-model="userForm.username" disabled></el-input>
      </el-form-item>
      <el-form-item label="昵称" prop="nickname">
        <el-input v-model="userForm.nickname"></el-input>
      </el-form-item>
      <el-form-item label="邮箱" prop="email">
        <el-input v-model="userForm.email"></el-input>
      </el-form-item>
      <el-form-item label="手机号" prop="phone">
        <el-input v-model="userForm.phone"></el-input>
      </el-form-item>
      <el-form-item label="头像">
        <div class="avatar-upload">
          <el-upload
            class="avatar-uploader"
            action="/api/video/upload"
            :headers="{ token: token }"
            :on-success="handleAvatarUploadSuccess"
            :on-error="handleUploadError"
            :file-list="avatarFileList"
            :auto-upload="true"
          >
            <img v-if="userForm.avatar" :src="userForm.avatar" class="avatar">
            <i v-else class="el-icon-plus avatar-uploader-icon"></i>
          </el-upload>
          <div class="el-upload__tip">点击上传头像，支持jpg/png格式，不超过2MB</div>
        </div>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="updateUser">保存修改</el-button>
      </el-form-item>
    </el-form>
    <el-divider></el-divider>
    <h2>修改密码</h2>
    <el-form :model="passwordForm" :rules="passwordRules" ref="passwordForm" label-width="100px">
      <el-form-item label="原密码" prop="oldPassword">
        <el-input type="password" v-model="passwordForm.oldPassword"></el-input>
      </el-form-item>
      <el-form-item label="新密码" prop="newPassword">
        <el-input type="password" v-model="passwordForm.newPassword"></el-input>
      </el-form-item>
      <el-form-item label="确认密码" prop="confirmPassword">
        <el-input type="password" v-model="passwordForm.confirmPassword"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="updatePassword">修改密码</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import request, { getToken } from '@/utils/request'

export default {
  name: 'UserInfo',
  data() {
    return {
      userForm: {
        username: '',
        nickname: '',
        email: '',
        phone: '',
        avatar: ''
      },
      avatarFileList: [],
      userRules: {
        nickname: [
          { required: true, message: '请输入昵称', trigger: 'blur' }
        ]
      },
      passwordForm: {
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
      },
      passwordRules: {
        oldPassword: [
          { required: true, message: '请输入原密码', trigger: 'blur' }
        ],
        newPassword: [
          { required: true, message: '请输入新密码', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: '请确认密码', trigger: 'blur' },
          {
            validator: (rule, value, callback) => {
              if (value !== this.passwordForm.newPassword) {
                callback(new Error('两次输入的密码不一致'))
              } else {
                callback()
              }
            },
            trigger: 'blur'
          }
        ]
      }
    }
  },
  computed: {
    token() {
      return getToken()
    }
  },
  mounted() {
    this.loadUserInfo()
  },
  methods: {

    loadUserInfo() {
      request.get('/user/info').then(res => {
        this.userForm = res
        // 如果有头像，添加到文件列表
        if (res.avatar) {
          this.avatarFileList = [{
            name: 'avatar.jpg',
            url: res.avatar
          }]
        }
      })
    },
    handleAvatarUploadSuccess(response) {
      this.userForm.avatar = response
      this.avatarFileList = [{
        name: 'avatar.jpg',
        url: response
      }]
      this.$message.success('头像上传成功')
    },
    handleUploadError(error) {
      this.$message.error('上传失败')
      console.error(error)
    },
    updateUser() {
      this.$refs.userForm.validate((valid) => {
        if (valid) {
          request.put('/user/update', this.userForm).then(res => {
            this.$message.success('修改成功')
            localStorage.setItem('user', JSON.stringify(this.userForm))
          }).catch(error => {
            this.$message.error(error.message || '修改失败')
          })
        }
      })
    },
    updatePassword() {
      this.$refs.passwordForm.validate((valid) => {
        if (valid) {
          request.put('/user/updatePassword', this.passwordForm).then(res => {
            this.$message.success('密码修改成功')
            this.passwordForm = {
              oldPassword: '',
              newPassword: '',
              confirmPassword: ''
            }
          }).catch(error => {
            this.$message.error(error.message || '密码修改失败')
          })
        }
      })
    }
  }
}
</script>

<style scoped>
.user-info {
  padding: 20px;
}

.user-info h2 {
  margin-bottom: 20px;
  font-size: 18px;
  color: #333;
  border-bottom: 1px solid #eaeaea;
  padding-bottom: 10px;
}

/* 头像上传样式 */
.avatar-upload {
  margin-top: 10px;
}

.avatar-uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: all .3s;
  width: 120px;
  height: 120px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar-uploader:hover {
  border-color: #409EFF;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 120px;
  height: 120px;
  text-align: center;
  line-height: 120px;
}

.avatar {
  width: 120px;
  height: 120px;
  display: block;
  object-fit: cover;
}

.el-upload__tip {
  margin-top: 10px;
  font-size: 12px;
  color: #999;
}
</style>
