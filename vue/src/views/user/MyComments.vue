<template>
  <div class="my-comments">
    <h2>我的评论</h2>
    <div class="comment-list">
      <el-card class="comment-card" v-for="(comment, index) in comments" :key="index">
        <div class="comment-header">
          <span class="comment-time">{{ formatDate(comment.createtime) }}</span>
          <router-link :to="'/community/' + comment.postId" class="post-link">{{ comment.postTitle }}</router-link>
        </div>
        <div class="comment-content">
          {{ comment.content }}
        </div>
      </el-card>
    </div>
    <div class="empty-tip" v-if="comments.length === 0">
      <el-empty description="暂无评论记录" />
    </div>
  </div>
</template>

<script>
import request from '@/utils/request'

export default {
  name: 'MyComments',
  data() {
    return {
      comments: []
    }
  },
  mounted() {
    this.loadComments()
  },
  methods: {
    loadComments() {
      request.get('/community/myComments').then(res => {
        this.comments = res.data || res
      })
    },
    formatDate(date) {
      return new Date(date).toLocaleString()
    }
  }
}
</script>

<style scoped>
.my-comments {
  padding: 20px;
}

.comment-list {
  margin-top: 20px;
}

.comment-card {
  margin-bottom: 20px;
  transition: all 0.3s ease;
}

.comment-card:hover {
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
}

.comment-header {
  margin-bottom: 10px;
  font-size: 14px;
  color: #666;
}

.post-link {
  margin-left: 20px;
  color: #409EFF;
  text-decoration: none;
}

.post-link:hover {
  text-decoration: underline;
}

.comment-content {
  font-size: 14px;
  color: #333;
  line-height: 1.6;
}

.empty-tip {
  margin-top: 50px;
  text-align: center;
}
</style>