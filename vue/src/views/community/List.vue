<template>
  <div class="community-list">
    <!-- 顶部导航栏 -->
    <el-header height="60px" class="header">
      <div class="logo">办公软件学习平台</div>
      <div class="nav">
        <router-link to="/" class="nav-item">首页</router-link>
        <router-link to="/courses" class="nav-item">课程学习</router-link>
        <router-link to="/community" class="nav-item active">互动社区</router-link>
        <template v-if="token">
          <router-link to="/user/center" class="nav-item">个人中心</router-link>
          <el-button type="text" @click="logout" class="nav-item">退出登录</el-button>
        </template>
        <template v-else>
          <router-link to="/login" class="nav-item">登录</router-link>
          <router-link to="/register" class="nav-item">注册</router-link>
        </template>
      </div>
    </el-header>

    <!-- 发布帖子按钮 -->
    <div class="post-button-container">
      <el-button type="primary" @click="showPostDialog" v-if="token">
        <i class="el-icon-edit"></i> 发布帖子
      </el-button>
      <el-button @click="$router.push('/login')" v-else>
        <i class="el-icon-edit"></i> 登录发布帖子
      </el-button>
    </div>

    <!-- 筛选条件 -->
    <div class="filter">
      <el-select v-model="filter.type" placeholder="帖子类型" class="filter-item">
        <el-option label="全部" value=""></el-option>
        <el-option label="Word" value="Word"></el-option>
        <el-option label="Excel" value="Excel"></el-option>
        <el-option label="PPT" value="PPT"></el-option>
      </el-select>
      <el-button type="primary" @click="loadPosts" class="filter-item">筛选</el-button>
    </div>

    <!-- 帖子列表 -->
    <div class="post-container">
      <el-card class="post-card" v-for="(post, index) in posts" :key="index">
        <router-link :to="'/community/' + post.id" class="post-link">
          <h3>{{ post.title }}</h3>
          <p class="post-content">{{ post.content }}</p>
          <div class="post-meta">
            <span class="post-author">{{ post.userNickname }}</span>
            <span class="post-time">{{ formatDate(post.createtime) }}</span>
            <span class="post-type">{{ post.type }}</span>
            <span class="post-stats">
              <i class="el-icon-view">{{ post.viewCount }}</i>
              <i class="el-icon-thumb">{{ post.likeCount }}</i>
              <i class="el-icon-chat-line-round">{{ post.commentCount }}</i>
            </span>
          </div>
        </router-link>
      </el-card>
    </div>

    <!-- 发布帖子对话框 -->
    <el-dialog title="发布帖子" :visible.sync="postDialogVisible" width="600px">
      <el-form :model="postForm" :rules="postRules" ref="postForm">
        <el-form-item label="标题" prop="title">
          <el-input v-model="postForm.title" placeholder="请输入帖子标题"></el-input>
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input type="textarea" v-model="postForm.content" placeholder="请输入帖子内容" rows="5"></el-input>
        </el-form-item>
        <el-form-item label="类型" prop="type">
          <el-select v-model="postForm.type" placeholder="请选择帖子类型">
            <el-option label="Word" value="Word"></el-option>
            <el-option label="Excel" value="Excel"></el-option>
            <el-option label="PPT" value="PPT"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="封面图片">
          <el-upload
            class="upload-demo"
            action="/api/video/upload"
            :headers="{ token: token }"
            :on-success="handleCoverUploadSuccess"
            :on-error="handleUploadError"
            :file-list="coverFileList"
            :auto-upload="true"
          >
            <el-button size="small" type="primary">点击上传封面</el-button>
            <div slot="tip" class="el-upload__tip">只能上传jpg/png文件，且不超过2MB</div>
          </el-upload>
        </el-form-item>

      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="postDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitPost">发布</el-button>
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
  name: 'CommunityList',
  data() {
    return {
      posts: [],
      filter: {
        type: ''
      },
      postDialogVisible: false,
      postForm: {
        title: '',
        content: '',
        type: '',
        cover: '',
        videoUrl: ''
      },
      coverFileList: [],
      videoFileList: [],
      postRules: {
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
  computed: {
    token() {
      return getToken()
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
    loadPosts() {
      request.get('/community/list', {
        params: this.filter
      }).then(res => {
        this.posts = res.data || res
      }).catch(error => {
        this.$message.error('获取帖子列表失败')
        console.error(error)
      })
    },
    showPostDialog() {
      this.postForm = {
        title: '',
        content: '',
        type: '',
        cover: '',
        videoUrl: ''
      }
      this.coverFileList = []
      this.videoFileList = []
      this.postDialogVisible = true
    },
    handleCoverUploadSuccess(response) {
      this.postForm.cover = response
      this.coverFileList = [{
        name: 'cover.jpg',
        url: response
      }]
      this.$message.success('封面上传成功')
    },
    handleVideoUploadSuccess(response) {
      this.postForm.videoUrl = response
      this.videoFileList = [{
        name: 'video.mp4',
        url: response
      }]
      this.$message.success('视频上传成功')
    },
    handleUploadError(error) {
      this.$message.error('上传失败')
      console.error(error)
    },
    submitPost() {
      this.$refs.postForm.validate((valid) => {
        if (valid) {
          request.post('/community/post', this.postForm).then(res => {
            this.$message.success('发布成功')
            this.postDialogVisible = false
            this.postForm = {
              title: '',
              content: '',
              type: ''
            }
            this.loadPosts()
          }).catch(error => {
            this.$message.error('发布失败')
            console.error(error)
          })
        }
      })
    }
  }
}
</script>

<style scoped>
.community-list {
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

.post-button-container {
  margin: 20px;
  text-align: right;
}

.filter {
  display: flex;
  padding: 20px;
  background-color: white;
  margin: 0 20px 20px;
  border-radius: 4px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.filter-item {
  margin-right: 20px;
}

.post-container {
  flex: 1;
  margin: 0 20px;
}

.post-card {
  margin-bottom: 20px;
  transition: all 0.3s ease;
}

.post-card:hover {
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
}

.post-link {
  text-decoration: none;
  color: #333;
}

.post-link:hover {
  color: #409EFF;
}

.post-content {
  margin: 10px 0;
  font-size: 14px;
  color: #666;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.post-meta {
  font-size: 12px;
  color: #999;
  margin-top: 10px;
}

.post-author,
.post-time,
.post-type {
  margin-right: 15px;
}

.post-stats {
  float: right;
}

.post-stats i {
  margin-left: 10px;
}

.footer {
  background-color: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: 40px;
}
</style>