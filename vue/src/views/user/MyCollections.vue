<template>
  <div class="my-collections">
    <h2>我的收藏</h2>
    <el-tabs v-model="activeTab">
      <el-tab-pane label="课程" name="courses">
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
                <router-link :to="'/course/' + course.id" class="course-link">查看详情</router-link>
              </div>
            </div>
          </el-card>
        </div>
        <div class="empty-tip" v-if="courses.length === 0">
          <el-empty description="暂无课程收藏" />
        </div>
      </el-tab-pane>
      <el-tab-pane label="帖子" name="posts">
        <div class="post-list">
          <el-card class="post-card" v-for="(post, index) in posts" :key="index">
            <router-link :to="'/community/' + post.id" class="post-link">
              <h3>{{ post.title }}</h3>
              <p class="post-content">{{ post.content }}</p>
              <div class="post-meta">
                <span class="post-author">{{ post.userNickname }}</span>
                <span class="post-time">{{ formatDate(post.createtime) }}</span>
                <span class="post-type">{{ post.type }}</span>
              </div>
            </router-link>
          </el-card>
        </div>
        <div class="empty-tip" v-if="posts.length === 0">
          <el-empty description="暂无帖子收藏" />
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
import request from '@/utils/request'

export default {
  name: 'MyCollections',
  data() {
    return {
      activeTab: 'courses',
      courses: [],
      posts: []
    }
  },
  mounted() {
    this.loadCourses()
    this.loadPosts()
  },
  methods: {
    loadCourses() {
      request.get('/course/myCollections').then(res => {
        this.courses = res.data || res
      })
    },
    loadPosts() {
      request.get('/community/myCollections').then(res => {
        this.posts = res.data || res
      })
    },
    formatDate(date) {
      return new Date(date).toLocaleString()
    }
  }
}
</script>

<style scoped>
.my-collections {
  padding: 20px;
}

.course-list,
.post-list {
  margin-top: 20px;
}

.course-card,
.post-card {
  margin-bottom: 20px;
  transition: all 0.3s ease;
}

.course-card:hover,
.post-card:hover {
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

.empty-tip {
  margin-top: 50px;
  text-align: center;
}
</style>