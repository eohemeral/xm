<template>
  <div class="course-list">
    <!-- 顶部导航栏 -->
    <el-header height="60px" class="header">
      <div class="logo">办公软件学习平台</div>
      <div class="nav">
        <router-link to="/" class="nav-item">首页</router-link>
        <router-link to="/courses" class="nav-item active">课程学习</router-link>
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

    <!-- 筛选条件 -->
    <div class="filter">
      <el-select v-model="filter.type" placeholder="课程类型" class="filter-item">
        <el-option label="全部" value=""></el-option>
        <el-option label="Word" value="Word"></el-option>
        <el-option label="Excel" value="Excel"></el-option>
        <el-option label="PPT" value="PPT"></el-option>
      </el-select>
      <el-select v-model="filter.level" placeholder="难度等级" class="filter-item">
        <el-option label="全部" value=""></el-option>
        <el-option label="入门" value="入门"></el-option>
        <el-option label="进阶" value="进阶"></el-option>
      </el-select>
      <el-select v-model="filter.scenario" placeholder="应用场景" class="filter-item">
        <el-option label="全部" value=""></el-option>
        <el-option label="办公通用" value="办公通用"></el-option>
        <el-option label="财务" value="财务"></el-option>
        <el-option label="行政" value="行政"></el-option>
        <el-option label="人力" value="人力"></el-option>
        <el-option label="商务" value="商务"></el-option>
      </el-select>
      <el-button type="primary" @click="loadCourses" class="filter-item">筛选</el-button>
    </div>

    <!-- 课程列表 -->
    <div class="course-container">
      <el-row :gutter="20">
        <el-col :span="8" v-for="(course, index) in courses" :key="index">
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
                <span class="course-scenario">{{ course.scenario }}</span>
              </div>
              <div class="course-stats">
                <span><i class="el-icon-view">{{ course.viewCount }}</i></span>
                <span><i class="el-icon-star-on">{{ course.collectCount }}</i></span>
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
import request from '@/utils/request'

export default {
  name: 'CourseList',
  data() {
    return {
      token: localStorage.getItem('token'),
      courses: [],
      filter: {
        type: '',
        level: '',
        scenario: ''
      }
    }
  },
  mounted() {
    this.loadCourses()
  },
  methods: {
    logout() {
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      this.$router.push('/login')
    },
    loadCourses() {
      request.get('/course/list', {
        params: this.filter
      }).then(res => {
        this.courses = res.data || res
      }).catch(error => {
        this.$message.error('获取课程列表失败')
        console.error(error)
      })
    }
  }
}
</script>

<style scoped>
.course-list {
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

.filter {
  display: flex;
  padding: 20px;
  background-color: white;
  margin: 20px;
  border-radius: 4px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.filter-item {
  margin-right: 20px;
}

.course-container {
  flex: 1;
  margin: 0 20px;
}

.course-card {
  margin-bottom: 20px;
  transition: all 0.3s ease;
}

.course-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
}

.course-cover-container {
  width: 100%;
  height: 200px;
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
  height: 200px;
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
  padding: 15px;
}

.course-info h3 {
  margin-bottom: 10px;
  font-size: 16px;
  color: #333;
}

.course-desc {
  margin-bottom: 15px;
  font-size: 14px;
  color: #666;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.course-meta {
  margin-bottom: 10px;
}

.course-type,
.course-level,
.course-scenario {
  display: inline-block;
  padding: 2px 8px;
  background-color: #f0f9ff;
  color: #409EFF;
  font-size: 12px;
  border-radius: 4px;
  margin-right: 10px;
  margin-bottom: 5px;
}

.course-stats {
  margin-bottom: 15px;
  font-size: 12px;
  color: #999;
}

.course-stats span {
  margin-right: 15px;
}

.course-link {
  display: inline-block;
  padding: 5px 15px;
  background-color: #409EFF;
  color: white;
  text-decoration: none;
  border-radius: 4px;
  font-size: 14px;
}

.course-link:hover {
  background-color: #66b1ff;
}

.footer {
  background-color: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: 40px;
}
</style>