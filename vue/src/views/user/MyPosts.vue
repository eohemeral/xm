<template>
  <div class="my-posts">
    <h2>我的帖子</h2>
    <div class="post-list">
      <el-card class="post-card" v-for="(post, index) in posts" :key="index">
        <router-link :to="'/community/' + post.id" class="post-link">
          <h3>{{ post.title }}</h3>
          <p class="post-content">{{ post.content }}</p>
          <div class="post-meta">
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
    <div class="empty-tip" v-if="posts.length === 0">
      <el-empty description="暂无发布记录" />
    </div>
  </div>
</template>

<script>
import request from '@/utils/request'

export default {
  name: 'MyPosts',
  data() {
    return {
      posts: []
    }
  },
  mounted() {
    this.loadPosts()
  },
  methods: {
    loadPosts() {
      request.get('/community/myPosts').then(res => {
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
.my-posts {
  padding: 20px;
}

.post-list {
  margin-top: 20px;
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

.empty-tip {
  margin-top: 50px;
  text-align: center;
}
</style>