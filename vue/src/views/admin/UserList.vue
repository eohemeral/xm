<template>
  <div class="admin-users">
    <!-- 顶部导航栏 -->
    <el-header height="60px" class="header">
      <div class="logo">办公软件学习平台 - 管理员后台</div>
      <div class="nav">
        <router-link to="/admin/dashboard" class="nav-item">控制台</router-link>
        <router-link to="/admin/users" class="nav-item active">用户管理</router-link>
        <router-link to="/admin/courses" class="nav-item">课程管理</router-link>
        <router-link to="/admin/posts" class="nav-item">帖子管理</router-link>
        <el-button type="text" @click="logout" class="nav-item">退出登录</el-button>
      </div>
    </el-header>

    <!-- 主体内容 -->
    <div class="main">
      <h1>用户管理</h1>
      <el-card shadow="hover">
        <el-table :data="users" style="width: 100%">
          <el-table-column prop="id" label="ID" width="80"></el-table-column>
          <el-table-column prop="username" label="用户名"></el-table-column>
          <el-table-column prop="nickname" label="昵称"></el-table-column>
          <el-table-column prop="email" label="邮箱"></el-table-column>
          <el-table-column prop="phone" label="手机号"></el-table-column>
          <el-table-column prop="status" label="状态">
            <template slot-scope="scope">
              <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
                {{ scope.row.status === 1 ? '启用' : '禁用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createtime" label="创建时间">
            <template slot-scope="scope">
              {{ formatDate(scope.row.createtime) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="250">
            <template slot-scope="scope">
              <el-button size="small" @click="showEditDialog(scope.row)">编辑</el-button>
              <el-button size="small" @click="toggleStatus(scope.row)" :type="scope.row.status === 1 ? 'danger' : 'success'">
                {{ scope.row.status === 1 ? '禁用' : '启用' }}
              </el-button>
              <el-button size="small" type="danger" @click="deleteUser(scope.row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <div class="pagination-container">
          <el-pagination
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
            :current-page="currentPage"
            :page-sizes="[10, 20, 50, 100]"
            :page-size="pageSize"
            layout="total, sizes, prev, pager, next, jumper"
            :total="total">
          </el-pagination>
        </div>
      </el-card>
    </div>

    <!-- 编辑用户对话框 -->
    <el-dialog title="编辑用户" :visible.sync="editDialogVisible" width="600px">
      <el-form :model="userForm" :rules="userRules" ref="userForm">
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
        <el-form-item label="状态" prop="status">
          <el-select v-model="userForm.status">
            <el-option label="启用" value="1"></el-option>
            <el-option label="禁用" value="0"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="updateUser">保存</el-button>
      </span>
    </el-dialog>

    <!-- 底部 -->
    <el-footer height="100px" class="footer">
      <p>© 2026 办公软件学习平台 版权所有</p>
    </el-footer>
  </div>
</template>

<script>
import request, { getToken } from '@/utils/request'

export default {
  name: 'AdminUserList',
  data() {
    return {
      users: [],
      currentPage: 1,
      pageSize: 10,
      total: 0,
      editDialogVisible: false,
      userForm: {
        id: '',
        username: '',
        nickname: '',
        email: '',
        phone: '',
        avatar: '',
        status: 1
      },
      avatarFileList: [],
      userRules: {
        nickname: [
          { required: true, message: '请输入昵称', trigger: 'blur' }
        ],
        email: [
          { required: true, message: '请输入邮箱', trigger: 'blur' },
          { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
        ],
        status: [
          { required: true, message: '请选择状态', trigger: 'blur' }
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
    this.loadUsers()
  },
  methods: {

    logout() {
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      this.$router.push('/login')
    },
    formatDate(date) {
      return new Date(date).toLocaleString()
    },
    toggleStatus(user) {
      const newStatus = user.status === 1 ? 0 : 1
      request.put('/user/status', {
        id: user.id,
        status: newStatus
      }).then(res => {
        user.status = newStatus
        this.$message.success('状态已更新')
      }).catch(error => {
        this.$message.error('状态更新失败')
        console.error(error)
      })
    },
    handleSizeChange(size) {
      this.pageSize = size
      this.loadUsers()
    },
    handleCurrentChange(current) {
      this.currentPage = current
      this.loadUsers()
    },
    loadUsers() {
      request.get('/user/list').then(res => {
        this.users = res
        this.total = res.length
      }).catch(error => {
        this.$message.error('获取用户列表失败')
        console.error(error)
      })
    },
    showEditDialog(user) {
      this.userForm = { ...user }
      // 重置文件列表
      this.avatarFileList = []
      // 如果有头像，添加到文件列表
      if (user.avatar) {
        this.avatarFileList.push({
          name: 'avatar.jpg',
          url: user.avatar
        })
      }
      this.editDialogVisible = true
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
            this.editDialogVisible = false
            this.$message.success('用户信息更新成功')
            this.loadUsers()
          }).catch(error => {
            this.$message.error('用户信息更新失败')
            console.error(error)
          })
        }
      })
    },
    deleteUser(id) {
      this.$confirm('确定要删除这个用户吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        request.delete(`/user/delete/${id}`).then(res => {
          this.$message.success('用户删除成功')
          this.loadUsers()
        }).catch(error => {
          this.$message.error('用户删除失败')
          console.error(error)
        })
      }).catch(() => {
        // 取消删除
      })
    }
  }
}
</script>

<style scoped>
.admin-users {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e8eb 100%);
}

.header {
  background: linear-gradient(135deg, #2c3e50 0%, #34495e 100%);
  color: white;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 40px;
  position: sticky;
  top: 0;
  z-index: 100;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
}

.logo {
  font-size: 20px;
  font-weight: bold;
  letter-spacing: 1px;
}

.nav {
  display: flex;
  align-items: center;
}

.nav-item {
  color: white;
  text-decoration: none;
  margin-left: 25px;
  font-size: 14px;
  padding: 8px 15px;
  border-radius: 20px;
  transition: all 0.3s ease;
}

.nav-item:hover,
.nav-item.active {
  background-color: rgba(255, 255, 255, 0.2);
  color: #fff;
}

.main {
  flex: 1;
  margin: 30px 40px;
  padding: 30px;
  background: white;
  border-radius: 15px;
  box-shadow: 0 5px 20px rgba(0, 0, 0, 0.08);
}

.main h1 {
  margin-bottom: 30px;
  color: #2c3e50;
  font-size: 26px;
  font-weight: bold;
  padding-bottom: 15px;
  border-bottom: 3px solid #409EFF;
}

.pagination-container {
  margin-top: 25px;
  text-align: right;
  padding: 15px 0;
}

.footer {
  background: linear-gradient(135deg, #2c3e50 0%, #34495e 100%);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: auto;
  padding: 25px;
}

/* 头像上传样式 */
.avatar-upload {
  margin-top: 10px;
}

.avatar-uploader {
  border: 2px dashed #409EFF;
  border-radius: 12px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: all 0.3s ease;
  width: 120px;
  height: 120px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f0f9ff;
}

.avatar-uploader:hover {
  border-color: #66b1ff;
  transform: scale(1.02);
}

.avatar-uploader-icon {
  font-size: 32px;
  color: #409EFF;
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
  border-radius: 12px;
}

.el-upload__tip {
  margin-top: 10px;
  font-size: 12px;
  color: #999;
}
</style>