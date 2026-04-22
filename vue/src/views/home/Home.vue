<template>
  <div class="home">
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

    <!-- 轮播图 -->
    <el-carousel :interval="5000" type="card" height="400px" class="carousel">
      <el-carousel-item v-for="(item, index) in carouselItems" :key="index">
        <div class="carousel-item">
          <h2>{{ item.title }}</h2>
          <p>{{ item.description }}</p>
        </div>
      </el-carousel-item>
    </el-carousel>

    <!-- 热门课程 -->
    <div class="section">
      <h2 class="section-title">热门课程</h2>
      <el-row :gutter="20">
        <el-col :span="8" v-for="(course, index) in hotCourses" :key="index">
          <el-card :body-style="{ padding: '0px' }" class="course-card">
            <div v-if="course.cover" class="course-cover-container">
              <img :src="course.cover" class="course-cover">
            </div>
            <div v-else class="course-cover-placeholder">
              <i class="el-icon-picture-outline"></i>
              <span>暂无封面</span>
            </div>
            <div class="course-info">
              <h3>{{ course.title }}</h3>
              <p class="course-desc">{{ course.description }}</p>
              <div class="course-meta">
                <span class="course-type">{{ course.type }}</span>
                <span class="course-level">{{ course.level }}</span>
              </div>
              <router-link :to="'/course/' + course.id" class="course-link">查看详情</router-link>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 热门帖子 -->
    <div class="section">
      <h2 class="section-title">热门帖子</h2>
      <el-card class="post-card" v-for="(post, index) in hotPosts" :key="index">
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

    <!-- 猜你喜欢 -->
    <div class="section" v-if="recommendedCourses.length > 0">
      <h2 class="section-title">猜你喜欢</h2>
      <el-row :gutter="20">
        <el-col :span="8" v-for="(course, index) in recommendedCourses" :key="index">
          <el-card :body-style="{ padding: '0px' }" class="course-card">
            <div v-if="course.cover" class="course-cover-container">
              <img :src="course.cover" class="course-cover">
            </div>
            <div v-else class="course-cover-placeholder">
              <i class="el-icon-picture-outline"></i>
              <span>暂无封面</span>
            </div>
            <div class="course-info">
              <h3>{{ course.title }}</h3>
              <p class="course-desc">{{ course.description }}</p>
              <div class="course-meta">
                <span class="course-type">{{ course.type }}</span>
                <span class="course-level">{{ course.level }}</span>
              </div>
              <router-link :to="'/course/' + course.id" class="course-link">查看详情</router-link>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 底部 -->
    <el-footer height="100px" class="footer">
      <p>© 2026 办公软件学习平台 版权所有</p>
    </el-footer>
  </div>
</template>

<script>
import request, { getToken } from '@/utils/request'

export default {
  name: 'Home',
  data() {
    return {
      carouselItems: [
        {
          title: 'Word基础教程',
          description: '从零开始学习Word，掌握文档编辑的基本技巧'
        },
        {
          title: 'Excel函数进阶',
          description: '深入学习Excel函数，提升数据处理能力'
        },
        {
          title: 'PPT制作技巧',
          description: '学习PPT制作的高级技巧，打造专业演示文稿'
        }
      ],
      hotCourses: [],
      hotPosts: [],
      recommendedCourses: []
    }
  },
  computed: {
    token() {
      return getToken()
    }
  },
  mounted() {
    this.loadHotCourses()
    this.loadHotPosts()
    if (this.token) {
      this.loadRecommendedCourses()
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
    loadHotCourses() {
      request.get('/course/list').then(res => {
        // 按浏览量排序，取前3个作为热门课程
        this.hotCourses = res.sort((a, b) => b.viewCount - a.viewCount).slice(0, 3)
      }).catch(error => {
        console.error('获取热门课程失败', error)
      })
    },
    loadHotPosts() {
      request.get('/community/list').then(res => {
        // 按浏览量排序，取前3个作为热门帖子
        this.hotPosts = res.sort((a, b) => b.viewCount - a.viewCount).slice(0, 3)
      }).catch(error => {
        console.error('获取热门帖子失败', error)
      })
    },
    loadRecommendedCourses() {
      request.get('/course/recommend').then(res => {
        this.recommendedCourses = res
      }).catch(error => {
        console.error('获取推荐课程失败', error)
      })
    }
  }
}
</script>

<style scoped>
.home {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: #f8f9fa;
}

.header {
  background: white;
  color: #333;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 40px;
  position: sticky;
  top: 0;
  z-index: 100;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.logo {
  font-size: 20px;
  font-weight: bold;
  color: #409EFF;
  letter-spacing: 1px;
}

.nav {
  display: flex;
  align-items: center;
}

.nav-item {
  color: #333;
  text-decoration: none;
  margin-left: 30px;
  font-size: 14px;
  padding: 6px 12px;
  border-radius: 4px;
  transition: all 0.3s ease;
  position: relative;
}

.nav-item:hover {
  color: #409EFF;
}

.nav-item::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 0;
  height: 2px;
  background: #409EFF;
  transition: all 0.3s ease;
}

.nav-item:hover::after {
  width: 80%;
}

.carousel {
  margin: 20px 40px;
}

.carousel-item {
  background: #409EFF;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 0 40px;
  text-align: center;
  border-radius: 8px;
  box-shadow: 0 4px 16px rgba(64, 158, 255, 0.2);
}

.carousel-item h2 {
  margin-bottom: 16px;
  color: white;
  font-size: 28px;
  font-weight: bold;
}

.carousel-item p {
  color: rgba(255, 255, 255, 0.9);
  font-size: 16px;
}

.section {
  margin: 32px 40px;
  flex: 1;
}

.section-title {
  font-size: 22px;
  margin-bottom: 24px;
  color: #333;
  border-bottom: 2px solid #409EFF;
  padding-bottom: 12px;
  position: relative;
}

.section-title::after {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 0;
  width: 80px;
  height: 2px;
  background: #409EFF;
}

.course-card {
  margin-bottom: 16px;
  transition: all 0.3s ease;
  border-radius: 6px;
  overflow: hidden;
  border: 1px solid #eaeaea;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.course-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.1);
  border-color: #409EFF;
}

.course-cover-container {
  width: 100%;
  height: 160px;
  overflow: hidden;
  transition: transform 0.3s ease;
}

.course-cover {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.course-cover-placeholder {
  width: 100%;
  height: 160px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background-color: #f0f0f0;
  border: 1px dashed #d9d9d9;
  transition: all 0.3s ease;
}

.course-cover-placeholder i {
  font-size: 32px;
  color: #999;
  margin-bottom: 8px;
}

.course-cover-placeholder span {
  font-size: 14px;
  color: #999;
}

.course-card:hover .course-cover-container {
  transform: scale(1.03);
}

.course-card:hover .course-cover-placeholder {
  background-color: #e6f7ff;
  border-color: #409EFF;
  color: #409EFF;
}

.course-card:hover .course-cover-placeholder i,
.course-card:hover .course-cover-placeholder span {
  color: #409EFF;
}

.course-info {
  padding: 16px;
}

.course-info h3 {
  margin-bottom: 8px;
  font-size: 16px;
  color: #333;
  font-weight: 600;
  transition: all 0.3s ease;
}

.course-card:hover h3 {
  color: #409EFF;
}

.course-desc {
  margin-bottom: 12px;
  font-size: 13px;
  color: #666;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  min-height: 36px;
}

.course-meta {
  margin-bottom: 12px;
  display: flex;
  gap: 8px;
}

.course-type,
.course-level {
  display: inline-block;
  padding: 3px 10px;
  background: #ecf5ff;
  color: #409EFF;
  font-size: 11px;
  border-radius: 12px;
  border: 1px solid #d9ecff;
}

.course-link {
  display: inline-block;
  padding: 6px 16px;
  background: #409EFF;
  color: white;
  text-decoration: none;
  border-radius: 4px;
  font-size: 13px;
  transition: all 0.3s ease;
  box-shadow: 0 2px 4px rgba(64, 158, 255, 0.2);
}

.course-link:hover {
  background: #66b1ff;
  transform: translateY(-1px);
  box-shadow: 0 3px 8px rgba(64, 158, 255, 0.3);
  color: white;
}

.post-card {
  margin-bottom: 16px;
  transition: all 0.3s ease;
  border-radius: 6px;
  border: 1px solid #eaeaea;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.post-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 5px 12px rgba(0, 0, 0, 0.08);
  border-color: #409EFF;
}

.post-link {
  text-decoration: none;
  color: #333;
}

.post-link:hover {
  color: #409EFF;
}

.post-content {
  margin: 8px 0;
  font-size: 13px;
  color: #666;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.post-meta {
  font-size: 11px;
  color: #999;
  margin-top: 8px;
}

.post-author {
  color: #409EFF;
  font-weight: 600;
}

.post-time,
.post-type {
  margin-left: 12px;
}

.post-type {
  background-color: #f0f9ff;
  color: #409EFF;
  padding: 2px 6px;
  border-radius: 8px;
  font-size: 10px;
}

.post-stats {
  float: right;
  color: #999;
}

.post-stats i {
  margin-left: 8px;
  font-size: 12px;
}

.footer {
  background: #333;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: 48px;
  padding: 24px;
  border-radius: 8px 8px 0 0;
  box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.1);
}

.footer p {
  font-size: 12px;
  letter-spacing: 0.5px;
  color: rgba(255, 255, 255, 0.7);
}
</style>