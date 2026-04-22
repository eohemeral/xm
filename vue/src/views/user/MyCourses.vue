<template>
  <div class="my-courses">
    <h2>我的课程</h2>
    <div class="course-list">
      <el-card class="course-card" v-for="(course, index) in courses" :key="index">
        <div class="course-header">
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
            <div class="course-progress">
              <el-progress :percentage="course.progress" :format="formatProgress"></el-progress>
              <span class="progress-text">{{ course.progress }}% 完成</span>
            </div>
            <router-link :to="'/course/' + course.id" class="course-link">继续学习</router-link>
          </div>
        </div>
      </el-card>
    </div>
    <div class="empty-tip" v-if="courses.length === 0">
      <el-empty description="暂无学习记录" />
    </div>
  </div>
</template>

<script>
import request from '@/utils/request'

export default {
  name: 'MyCourses',
  data() {
    return {
      courses: []
    }
  },
  mounted() {
    this.loadCourses()
  },
  methods: {
    loadCourses() {
      request.get('/course/myCourses').then(res => {
        this.courses = res.data || res
      })
    },
    formatProgress(percentage) {
      return percentage + '%'
    }
  }
}
</script>

<style scoped>
.my-courses {
  padding: 20px;
}

.course-list {
  margin-top: 20px;
}

.course-card {
  margin-bottom: 20px;
  transition: all 0.3s ease;
}

.course-card:hover {
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
}

.course-header {
  display: flex;
}

.course-cover-container {
  width: 200px;
  height: 150px;
  overflow: hidden;
  margin-right: 20px;
}

.course-cover {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.course-cover-placeholder {
  width: 200px;
  height: 150px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background-color: #f0f0f0;
  border: 1px dashed #d9d9d9;
  margin-right: 20px;
  transition: all 0.3s ease;
}

.course-cover-placeholder i {
  font-size: 24px;
  color: #999;
  margin-bottom: 6px;
}

.course-cover-placeholder span {
  font-size: 12px;
  color: #999;
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
  flex: 1;
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
  margin-bottom: 15px;
}

.course-type,
.course-level {
  display: inline-block;
  padding: 2px 8px;
  background-color: #f0f9ff;
  color: #409EFF;
  font-size: 12px;
  border-radius: 4px;
  margin-right: 10px;
  margin-bottom: 5px;
}

.course-progress {
  margin-bottom: 15px;
}

.progress-text {
  display: block;
  margin-top: 5px;
  font-size: 12px;
  color: #666;
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

.empty-tip {
  margin-top: 50px;
  text-align: center;
}
</style>