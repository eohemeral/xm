<template>
  <div class="user-center">
    <!-- 顶部导航栏 -->
    <el-header height="60px" class="header">
      <div class="logo">办公软件学习平台</div>
      <div class="nav">
        <router-link to="/" class="nav-item">首页</router-link>
        <router-link to="/courses" class="nav-item">课程学习</router-link>
        <router-link to="/community" class="nav-item">互动社区</router-link>
        <template v-if="token">
          <router-link to="/user/center" class="nav-item active">个人中心</router-link>
          <el-button type="text" @click="logout" class="nav-item">退出登录</el-button>
        </template>
        <template v-else>
          <router-link to="/login" class="nav-item">登录</router-link>
          <router-link to="/register" class="nav-item">注册</router-link>
        </template>
      </div>
    </el-header>

    <!-- 个人中心内容 -->
    <div class="center-container">
      <el-row>
        <el-col :span="6">
          <!-- 左侧菜单 -->
          <el-menu :default-active="activeMenu" class="sidebar" router>
            <el-menu-item index="/user/center/info">
              <i class="el-icon-user"></i>
              <span>个人信息</span>
            </el-menu-item>
            <el-menu-item index="/user/center/courses">
              <i class="el-icon-book"></i>
              <span>我的课程</span>
            </el-menu-item>
            <el-menu-item index="/user/center/posts">
              <i class="el-icon-edit"></i>
              <span>我的帖子</span>
            </el-menu-item>
            <el-menu-item index="/user/center/comments">
              <i class="el-icon-chat-line-round"></i>
              <span>我的评论</span>
            </el-menu-item>
            <el-menu-item index="/user/center/collections">
              <i class="el-icon-star-on"></i>
              <span>我的收藏</span>
            </el-menu-item>
          </el-menu>
        </el-col>
        <el-col :span="18">
          <!-- 右侧内容 -->
          <el-card class="content-card">
            <router-view />
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
export default {
  name: 'UserCenter',
  data() {
    return {
      activeMenu: '/user/center/info'
    }
  },
  computed: {
    token() {
      return localStorage.getItem('token')
    }
  },
  methods: {
    logout() {
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      this.$router.push('/login')
    }
  },
  watch: {
    $route(to) {
      this.activeMenu = to.path
    }
  }
}
</script>

<style scoped>
.user-center {
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

.center-container {
  flex: 1;
  margin: 20px;
}

.sidebar {
  height: calc(100vh - 100px);
  border-radius: 4px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.content-card {
  height: calc(100vh - 100px);
  overflow-y: auto;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.content-card h2 {
  margin-bottom: 20px;
  font-size: 18px;
  color: #333;
  border-bottom: 1px solid #eaeaea;
  padding-bottom: 10px;
}

.footer {
  background-color: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: 40px;
}
</style>