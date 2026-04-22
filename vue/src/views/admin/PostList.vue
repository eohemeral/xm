<template>
  <div class="admin-posts">
    <!-- 顶部导航栏 -->
    <el-header height="60px" class="header">
      <div class="logo">办公软件学习平台 - 管理员后台</div>
      <div class="nav">
        <router-link to="/admin/dashboard" class="nav-item">控制台</router-link>
        <router-link to="/admin/users" class="nav-item">用户管理</router-link>
        <router-link to="/admin/courses" class="nav-item">课程管理</router-link>
        <router-link to="/admin/posts" class="nav-item active">帖子管理</router-link>
        <el-button type="text" @click="logout" class="nav-item">退出登录</el-button>
      </div>
    </el-header>

    <!-- 主体内容 -->
    <div class="main">
      <h1>帖子管理</h1>
      <el-card shadow="hover">
        <el-table :data="posts" style="width: 100%">
          <el-table-column prop="id" label="ID" width="80"></el-table-column>
          <el-table-column prop="title" label="帖子标题"></el-table-column>
          <el-table-column prop="userNickname" label="作者"></el-table-column>
          <el-table-column prop="type" label="帖子类型"></el-table-column>
          <el-table-column prop="viewCount" label="浏览量"></el-table-column>
          <el-table-column prop="likeCount" label="点赞量"></el-table-column>
          <el-table-column prop="commentCount" label="评论量"></el-table-column>
          <el-table-column prop="createtime" label="创建时间">
            <template slot-scope="scope">
              {{ formatDate(scope.row.createtime) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="250">
            <template slot-scope="scope">
              <router-link :to="'/community/' + scope.row.id" target="_blank" class="view-link">
                <el-button size="small">查看</el-button>
              </router-link>
              <el-button size="small" type="primary" @click="editPost(scope.row)">修改</el-button>
              <el-button size="small" type="danger" @click="deletePost(scope.row.id)">删除</el-button>
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

    <!-- 修改帖子对话框 -->
    <el-dialog title="修改帖子" :visible.sync="dialogVisible" width="600px">
      <el-form :model="editForm" :rules="editRules" ref="editForm" label-width="80px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="editForm.title" placeholder="请输入帖子标题"></el-input>
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input type="textarea" v-model="editForm.content" placeholder="请输入帖子内容" rows="5"></el-input>
        </el-form-item>
        <el-form-item label="类型" prop="type">
          <el-select v-model="editForm.type" placeholder="请选择帖子类型">
            <el-option label="Word" value="Word"></el-option>
            <el-option label="Excel" value="Excel"></el-option>
            <el-option label="PPT" value="PPT"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitEdit">确定</el-button>
      </span>
    </el-dialog>

    <!-- 底部 -->
    <el-footer height="100px" class="footer">
      <p>© 2026 办公软件学习平台 版权所有</p>
    </el-footer>
  </div>
</template>

<script>
import request from '@/utils/request'

export default {
  name: 'AdminPostList',
  data() {
    return {
      posts: [],
      currentPage: 1,
      pageSize: 10,
      total: 0,
      dialogVisible: false,
      editForm: {
        id: '',
        title: '',
        content: '',
        type: ''
      },
      editRules: {
        title: [
          { required: true, message: '请输入帖子标题', trigger: 'blur' }
        ],
        content: [
          { required: true, message: '请输入帖子内容', trigger: 'blur' }
        ],
        type: [
          { required: true, message: '请选择帖子类型', trigger: 'blur' }
        ]
      }
    }
  },
  mounted() {
    this.loadPosts()
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
    handleSizeChange(size) {
      this.pageSize = size
      this.loadPosts()
    },
    handleCurrentChange(current) {
      this.currentPage = current
      this.loadPosts()
    },
    loadPosts() {
      request.get('/community/admin/list').then(res => {
        this.posts = res
        this.total = res.length
      }).catch(error => {
        this.$message.error('获取帖子列表失败')
        console.error(error)
      })
    },
    deletePost(id) {
      this.$confirm('确定要删除这篇帖子吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        request.delete(`/community/admin/delete/${id}`).then(res => {
          this.posts = this.posts.filter(item => item.id !== id)
          this.total--
          this.$message.success('帖子删除成功')
        }).catch(error => {
          this.$message.error('帖子删除失败')
          console.error(error)
        })
      }).catch(() => {
        // 取消删除
      })
    },
    editPost(post) {
      this.editForm = {
        id: post.id,
        title: post.title,
        content: post.content,
        type: post.type
      }
      this.dialogVisible = true
    },
    submitEdit() {
      this.$refs.editForm.validate((valid) => {
        if (valid) {
          request.put('/community/admin/update', this.editForm).then(res => {
            this.dialogVisible = false
            this.$message.success('帖子修改成功')
            this.loadPosts()
          }).catch(error => {
            this.$message.error('帖子修改失败')
            console.error(error)
          })
        }
      })
    }
  }
}
</script>

<style scoped>
.admin-posts {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.header {
  background-color: #409EFF;
  color: white;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  position: sticky;
  top: 0;
  z-index: 100;
}

.logo {
  font-size: 20px;
  font-weight: bold;
}

.nav {
  display: flex;
  align-items: center;
}

.nav-item {
  color: white;
  text-decoration: none;
  margin-left: 20px;
  font-size: 14px;
}

.nav-item:hover,
.nav-item.active {
  color: #e6f7ff;
}

.main {
  flex: 1;
  margin: 20px;
}

.main h1 {
  margin-bottom: 30px;
  color: #333;
}

.pagination-container {
  margin-top: 20px;
  text-align: right;
}

.view-link {
  text-decoration: none;
}

.footer {
  background-color: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: 40px;
}
</style>