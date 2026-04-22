<template>
  <div class="community-detail">
    <!-- 顶部导航栏 -->
    <el-header height="60px" class="header">
      <div class="logo">办公软件学习平台</div>
      <div class="nav">
        <router-link to="/" class="nav-item">首页</router-link>
        <router-link to="/courses" class="nav-item">课程学习</router-link>
        <router-link to="/community" class="nav-item">互动社区</router-link>
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

    <!-- 帖子详情 -->
    <div class="detail-container">
      <el-card class="post-card">
        <h1>{{ post.title }}</h1>
        <div class="post-meta">
          <span class="post-author">{{ post.userNickname }}</span>
          <span class="post-time">{{ formatDate(post.createtime) }}</span>
          <span class="post-type">{{ post.type }}</span>
        </div>
        <div class="post-content">
          {{ post.content }}
        </div>
        <div class="post-actions">
          <el-button @click="like" v-if="token && !isLiked">
            <i class="el-icon-thumb"></i> 点赞 ({{ post.likeCount }})
          </el-button>
          <el-button type="primary" @click="unlike" v-if="token && isLiked">
            <i class="el-icon-thumb"></i> 已点赞 ({{ post.likeCount }})
          </el-button>
          <el-button v-else>
            <i class="el-icon-thumb"></i> 点赞 ({{ post.likeCount }})
          </el-button>
          <el-button @click="collect" v-if="token && !isCollected" style="margin-left: 10px">
            <i class="el-icon-star-off"></i> 收藏
          </el-button>
          <el-button type="warning" @click="uncollect" v-if="token && isCollected" style="margin-left: 10px">
            <i class="el-icon-star-on"></i> 已收藏
          </el-button>
          <el-button v-else style="margin-left: 10px">
            <i class="el-icon-star-off"></i> 收藏
          </el-button>
        </div>
      </el-card>

      <!-- 评论列表 -->
      <el-card class="comment-card">
        <h2>评论 ({{ comments.length }})</h2>
        <div class="comment-list">
          <div class="comment-item" v-for="(comment, index) in comments" :key="index">
            <div class="comment-header">
              <span class="comment-author">{{ comment.userNickname }}</span>
              <span class="comment-time">{{ formatDate(comment.createtime) }}</span>
            </div>
            <div class="comment-content">
              {{ comment.content }}
            </div>
          </div>
        </div>

        <!-- 发表评论 -->
        <div class="comment-form" v-if="token">
          <el-form :model="commentForm" :rules="commentRules" ref="commentForm">
            <el-form-item label="发表评论" prop="content">
              <el-input type="textarea" v-model="commentForm.content" placeholder="请输入评论内容" rows="3"></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="submitComment">发表评论</el-button>
            </el-form-item>
          </el-form>
        </div>
        <div class="login-tip" v-else>
          <el-button @click="$router.push('/login')">登录后发表评论</el-button>
        </div>
      </el-card>
    </div>

    <!-- 底部 -->
    <el-footer height="100px" class="footer">
      <p>© 2026 办公软件学习平台 版权所有</p>
    </el-footer>
  </div>
</template>

<script>
import request from '@/utils/request'

export default {
  name: 'CommunityDetail',
  data() {
    return {
      token: localStorage.getItem('token'),
      post: {},
      comments: [],
      isLiked: false,
      isCollected: false,
      commentForm: {
        content: ''
      },
      commentRules: {
        content: [
          { required: true, message: '请输入评论内容', trigger: 'blur' }
        ]
      }
    }
  },
  mounted() {
    this.loadPost()
    this.loadComments()
    if (this.token) {
      this.checkLike()
    }
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
    loadPost() {
      const id = this.$route.params.id
      request.get(`/community/detail/${id}`).then(res => {
        this.post = res.data || res
        this.comments = this.post.comments || []
      }).catch(error => {
        this.$message.error('获取帖子详情失败')
        console.error(error)
      })
    },
    loadComments() {
      this.loadPost()
    },
    checkLike() {
      const id = this.$route.params.id
      // 暂时简化处理
    },
    like() {
      const id = this.$route.params.id
      request.post(`/community/like/${id}`).then(res => {
        this.isLiked = true
        this.post.likeCount++
        this.$message.success('点赞成功')
      })
    },
    unlike() {
      const id = this.$route.params.id
      request.post(`/community/unlike/${id}`).then(res => {
        this.isLiked = false
        this.post.likeCount--
        this.$message.success('取消点赞成功')
      })
    },
    collect() {
      const id = this.$route.params.id
      request.post(`/community/collect/${id}`).then(res => {
        this.isCollected = true
        this.$message.success('收藏成功')
      })
    },
    uncollect() {
      const id = this.$route.params.id
      request.post(`/community/uncollect/${id}`).then(res => {
        this.isCollected = false
        this.$message.success('取消收藏成功')
      })
    },
    submitComment() {
      this.$refs.commentForm.validate((valid) => {
        if (valid) {
          const id = this.$route.params.id
          request.post('/community/comment', {
            postId: id,
            content: this.commentForm.content
          }).then(res => {
            this.$message.success('评论成功')
            this.commentForm.content = ''
            this.loadComments()
          }).catch(error => {
            this.$message.error(error.message || '评论失败')
          })
        }
      })
    }
  }
}
</script>

<style scoped>
.community-detail {
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

.nav-item:hover {
  color: #e6f7ff;
}

.detail-container {
  flex: 1;
  margin: 20px;
}

.post-card {
  margin-bottom: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.post-card h1 {
  margin-bottom: 20px;
  font-size: 24px;
  color: #333;
}

.post-meta {
  margin-bottom: 20px;
  font-size: 14px;
  color: #666;
}

.post-author,
.post-time,
.post-type {
  margin-right: 20px;
}

.post-content {
  margin-bottom: 30px;
  font-size: 16px;
  color: #333;
  line-height: 1.6;
  white-space: pre-wrap;
}

.post-actions {
  margin-top: 20px;
  border-top: 1px solid #eaeaea;
  padding-top: 20px;
}

.comment-card {
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.comment-card h2 {
  margin-bottom: 20px;
  font-size: 18px;
  color: #333;
  border-bottom: 1px solid #eaeaea;
  padding-bottom: 10px;
}

.comment-item {
  margin-bottom: 20px;
  padding-bottom: 20px;
  border-bottom: 1px solid #f0f0f0;
}

.comment-header {
  margin-bottom: 10px;
  font-size: 14px;
  color: #666;
}

.comment-author {
  margin-right: 20px;
  font-weight: bold;
}

.comment-content {
  font-size: 14px;
  color: #333;
  line-height: 1.6;
}

.comment-form {
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #eaeaea;
}

.login-tip {
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #eaeaea;
  text-align: center;
}

.footer {
  background-color: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: 40px;
}
</style>