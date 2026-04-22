<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script>
export default {
  name: 'App',
  created() {
    // 只在首次加载时清理完全无效的登录状态
    const token = localStorage.getItem('token')
    const user = localStorage.getItem('user')

    // 检查token是否存在且有效（非空字符串）
    if (token === null || token === undefined || token.trim() === '') {
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      return
    }

    // 检查user信息是否存在且有效
    if (user) {
      try {
        const parsedUser = JSON.parse(user)
        // 只有在user信息完全无效时才清理
        if (parsedUser === null || parsedUser === undefined) {
          localStorage.removeItem('token')
          localStorage.removeItem('user')
        }
      } catch (e) {
        // 如果user信息解析失败，清理所有登录状态
        localStorage.removeItem('token')
        localStorage.removeItem('user')
      }
    }
  }
}
</script>

<style>
/* 全局样式重置 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

/* 全局样式变量 */
:root {
  --primary-color: #409EFF;
  --success-color: #67C23A;
  --warning-color: #E6A23C;
  --danger-color: #F56C6C;
  --info-color: #909399;
  --border-color: #DCDFE6;
  --background-color: #f5f7fa;
  --text-color: #303133;
  --text-color-secondary: #606266;
  --text-color-light: #909399;
  --white: #FFFFFF;
  --card-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  --card-hover-shadow: 0 4px 16px 0 rgba(0, 0, 0, 0.15);
  --border-radius: 4px;
  --transition: all 0.3s ease;
}

body {
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', Arial, sans-serif;
  font-size: 14px;
  color: var(--text-color);
  background-color: var(--background-color);
  line-height: 1.5;
}

#app {
  min-height: 100vh;
}

/* 通用样式 */
a {
  color: var(--primary-color);
  text-decoration: none;
  transition: var(--transition);
}

a:hover {
  color: #66b1ff;
}

/* 卡片样式 */
.el-card {
  border-radius: var(--border-radius);
  transition: var(--transition);
}

.el-card:hover {
  box-shadow: var(--card-hover-shadow);
  transform: translateY(-2px);
}

/* 按钮样式 */
.el-button {
  border-radius: var(--border-radius);
  transition: var(--transition);
}

/* 表单样式 */
.el-form-item {
  margin-bottom: 20px;
}

/* 导航栏样式 */
.header {
  background-color: var(--primary-color);
  color: var(--white);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.nav-item {
  position: relative;
}

.nav-item.active::after {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 0;
  width: 100%;
  height: 2px;
  background-color: var(--white);
  border-radius: 2px;
}

/* 响应式设计 */
@media screen and (max-width: 768px) {
  .header {
    flex-direction: column;
    height: auto;
    padding: 10px 0;
  }

  .nav {
    margin-top: 10px;
  }

  .nav-item {
    margin: 0 10px;
  }

  .filter {
    flex-wrap: wrap;
  }

  .filter-item {
    margin-bottom: 10px;
  }

  .course-container .el-col,
  .section .el-col {
    width: 100% !important;
  }
}
</style>
