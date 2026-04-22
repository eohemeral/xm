<template>
  <div class="admin-dashboard">
    <!-- 顶部导航栏 -->
    <el-header height="60px" class="header">
      <div class="logo">办公软件学习平台 - 管理员后台</div>
      <div class="nav">
        <router-link to="/admin/dashboard" class="nav-item">控制台</router-link>
        <router-link to="/admin/users" class="nav-item">用户管理</router-link>
        <router-link to="/admin/courses" class="nav-item">课程管理</router-link>
        <router-link to="/admin/posts" class="nav-item">帖子管理</router-link>
        <el-button type="text" @click="logout" class="nav-item">退出登录</el-button>
      </div>
    </el-header>

    <!-- 主体内容 -->
    <div class="main">
      <h1>管理员控制台</h1>
      
      <!-- 统计卡片 -->
      <el-row :gutter="20" class="stats-row">
        <el-col :span="8">
          <el-card shadow="hover">
            <div class="stats-item">
              <h3>用户总数</h3>
              <p class="stats-value">{{ userCount }}</p>
            </div>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card shadow="hover">
            <div class="stats-item">
              <h3>课程总数</h3>
              <p class="stats-value">{{ courseCount }}</p>
            </div>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card shadow="hover">
            <div class="stats-item">
              <h3>帖子总数</h3>
              <p class="stats-value">{{ postCount }}</p>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 图表区域 -->
      <el-row :gutter="20" class="chart-row">
        <el-col :span="12">
          <el-card shadow="hover">
            <h3 class="chart-title">课程类型分布</h3>
            <div id="courseTypeChart" class="chart"></div>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card shadow="hover">
            <h3 class="chart-title">帖子类型分布</h3>
            <div id="postTypeChart" class="chart"></div>
          </el-card>
        </el-col>
      </el-row>

      <el-row :gutter="20" class="chart-row">
        <el-col :span="24">
          <el-card shadow="hover">
            <h3 class="chart-title">帖子数量趋势</h3>
            <div id="postTrendChart" class="chart chart-large"></div>
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
import * as echarts from 'echarts'

export default {
  name: 'AdminDashboard',
  data() {
    return {
      userCount: 0,
      courseCount: 0,
      postCount: 0
    }
  },
  mounted() {
    this.loadStats()
    this.$nextTick(() => {
      this.initCharts()
    })
  },
  methods: {
    logout() {
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      this.$router.push('/login')
    },
    loadStats() {
      // 获取用户总数
      request.get('/user/list').then(res => {
        this.userCount = res.length
      }).catch(error => {
        console.error('获取用户总数失败', error)
      })

      // 获取课程总数
      request.get('/course/list').then(res => {
        this.courseCount = res.length
      }).catch(error => {
        console.error('获取课程总数失败', error)
      })

      // 获取帖子总数
      request.get('/community/list').then(res => {
        this.postCount = res.length
      }).catch(error => {
        console.error('获取帖子总数失败', error)
      })
    },
    initCharts() {
      this.initCourseTypeChart()
      this.initPostTypeChart()
      this.initPostTrendChart()
    },
    initCourseTypeChart() {
      const chart = echarts.init(document.getElementById('courseTypeChart'))
      request.get('/course/stats/type').then(res => {
        const option = {
          tooltip: {
            trigger: 'item'
          },
          legend: {
            top: '5%',
            left: 'center'
          },
          series: [
            {
              name: '课程类型',
              type: 'pie',
              radius: ['40%', '70%'],
              avoidLabelOverlap: false,
              itemStyle: {
                borderRadius: 10,
                borderColor: '#fff',
                borderWidth: 2
              },
              label: {
                show: false,
                position: 'center'
              },
              emphasis: {
                label: {
                  show: true,
                  fontSize: '18',
                  fontWeight: 'bold'
                }
              },
              labelLine: {
                show: false
              },
              data: res
            }
          ]
        }
        chart.setOption(option)
      }).catch(error => {
        console.error('获取课程类型数据失败', error)
      })
    },
    initPostTypeChart() {
      const chart = echarts.init(document.getElementById('postTypeChart'))
      request.get('/community/stats/type').then(res => {
        const option = {
          tooltip: {
            trigger: 'axis',
            axisPointer: {
              type: 'shadow'
            }
          },
          grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
          },
          xAxis: {
            type: 'category',
            data: res.map(item => item.name)
          },
          yAxis: {
            type: 'value'
          },
          series: [
            {
              name: '帖子数量',
              type: 'bar',
              barWidth: '60%',
              data: res.map(item => item.value)
            }
          ]
        }
        chart.setOption(option)
      }).catch(error => {
        console.error('获取帖子类型数据失败', error)
      })
    },
    initPostTrendChart() {
      const chart = echarts.init(document.getElementById('postTrendChart'))
      request.get('/community/stats/trend').then(res => {
        const option = {
          tooltip: {
            trigger: 'axis'
          },
          grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
          },
          xAxis: {
            type: 'category',
            boundaryGap: false,
            data: res.map(item => item.date)
          },
          yAxis: {
            type: 'value'
          },
          series: [
            {
              name: '帖子数量',
              type: 'line',
              stack: 'Total',
              data: res.map(item => item.value)
            }
          ]
        }
        chart.setOption(option)
      }).catch(error => {
        console.error('获取帖子趋势数据失败', error)
      })
    }
  }
}
</script>

<style scoped>
.admin-dashboard {
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

.main {
  flex: 1;
  margin: 20px;
}

.main h1 {
  margin-bottom: 30px;
  color: #333;
}

.stats-item {
  text-align: center;
  padding: 20px 0;
}

.stats-item h3 {
  font-size: 16px;
  color: #666;
  margin-bottom: 10px;
}

.stats-value {
  font-size: 32px;
  font-weight: bold;
  color: #409EFF;
  margin: 0;
}

.stats-row {
  margin-bottom: 20px;
}

.chart-row {
  margin-bottom: 20px;
}

.chart-title {
  font-size: 16px;
  font-weight: 500;
  color: #333;
  margin-bottom: 15px;
}

.chart {
  width: 100%;
  height: 300px;
}

.chart-large {
  height: 400px;
}

.footer {
  background-color: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: 40px;
}
</style>