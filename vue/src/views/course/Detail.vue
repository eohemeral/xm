<template>
  <div class="course-detail">
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

    <!-- 课程详情 -->
    <div class="detail-container">
      <el-card class="course-card">
        <div class="course-header">
          <div v-if="course.cover" class="course-cover-container">
            <img :src="course.cover" class="course-cover">
          </div>
          <div v-else class="course-cover-placeholder">
            <i class="el-icon-picture-outline"></i>
            <span>暂无封面</span>
          </div>
          <div class="course-info">
            <h1>{{ course.title }}</h1>
            <div class="course-meta">
              <span class="course-type">{{ course.type }}</span>
              <span class="course-level">{{ course.level }}</span>
              <span class="course-scenario">{{ course.scenario }}</span>
            </div>
            <div class="course-stats">
              <span><i class="el-icon-view">{{ course.viewCount }}</i> 浏览</span>
              <span><i class="el-icon-star-on">{{ course.collectCount }}</i> 收藏</span>
              <span v-if="progress > 0"><i class="el-icon-video-camera">{{ progress }}%</i> 学习进度</span>
            </div>
            <div class="course-actions">
              <el-button type="primary" @click="handleStudy">开始学习</el-button>
              <el-button @click="collect" v-if="token && !isCollected">
                <i class="el-icon-star-off"></i> 收藏
              </el-button>
              <el-button type="warning" @click="uncollect" v-if="token && isCollected">
                <i class="el-icon-star-on"></i> 已收藏
              </el-button>
            </div>
          </div>
        </div>
        
        <!-- 视频播放区域 -->
        <div v-if="course.videoUrl" class="video-container">
          <h2>课程视频</h2>
          <div class="video-player">
            <div class="video-wrapper" ref="videoWrapper">
              <!-- 弹幕显示区域 -->
              <div class="danmaku-container" ref="danmakuContainer">
                <div 
                  v-for="(danmaku, index) in danmakus" 
                  :key="index" 
                  class="danmaku-item"
                  :style="{
                    left: danmaku.left + 'px',
                    top: danmaku.top + 'px',
                    animationDuration: danmaku.duration + 's',
                    fontSize: danmaku.fontSize + 'px',
                    color: danmaku.color
                  }"
                >
                  {{ danmaku.content }}
                </div>
              </div>
              <video 
                ref="videoPlayer" 
                :src="currentVideoUrl" 
                controls 
                @timeupdate="onVideoTimeUpdate"
                @ended="onVideoEnded"
                @loadedmetadata="onVideoLoaded"
                @waiting="onVideoWaiting"
                @playing="onVideoPlaying"
                @error="onVideoError"
                style="width: 100%; height: 100%;"
              >
                <track v-for="subtitle in subtitles" :key="subtitle.label" :src="subtitle.src" :kind="subtitle.kind" :srclang="subtitle.srclang" :label="subtitle.label">
                您的浏览器不支持视频播放
              </video>
              <div class="video-loading" v-if="isLoading">
                <i class="el-icon-loading"></i>
                <span>加载中...</span>
              </div>
              <div class="video-error" v-if="hasError">
                <i class="el-icon-warning"></i>
                <span>{{ errorMessage }}</span>
                <el-button type="primary" size="small" @click="reloadVideo">重新加载</el-button>
              </div>
            </div>
            <!-- 弹幕发送区域 -->
            <div class="danmaku-input-area" v-if="token">
              <el-input 
                v-model="danmakuContent" 
                placeholder="发送弹幕..." 
                @keyup.enter="sendDanmaku"
                style="width: 80%;"
              ></el-input>
              <el-color-picker v-model="danmakuColor" size="small" style="margin: 0 10px;"></el-color-picker>
              <el-button type="primary" size="small" @click="sendDanmaku">发送</el-button>
            </div>
            <div class="video-controls">
              <div class="control-group">
                <span class="control-label">倍速：</span>
                <el-select v-model="playbackRate" size="small" @change="setPlaybackRate">
                  <el-option label="0.5x" :value="0.5" />
                  <el-option label="0.75x" :value="0.75" />
                  <el-option label="1.0x" :value="1" />
                  <el-option label="1.25x" :value="1.25" />
                  <el-option label="1.5x" :value="1.5" />
                  <el-option label="2.0x" :value="2" />
                </el-select>
              </div>
              <div class="control-group" v-if="videoQualities.length > 1">
                <span class="control-label">清晰度：</span>
                <el-select v-model="currentQuality" size="small" @change="changeVideoQuality">
                  <el-option v-for="quality in videoQualities" :key="quality.value" :label="quality.label" :value="quality.value" />
                </el-select>
              </div>
              <div class="control-group" v-if="subtitles.length > 0">
                <span class="control-label">字幕：</span>
                <el-select v-model="currentSubtitle" size="small" @change="setSubtitle">
                  <el-option label="关闭" :value="''" />
                  <el-option v-for="subtitle in subtitles" :key="subtitle.label" :label="subtitle.label" :value="subtitle.label" />
                </el-select>
              </div>
              <div class="control-group">
                <el-button type="text" size="small" @click="toggleFullscreen">
                  <i class="el-icon-full-screen"></i> 全屏
                </el-button>
              </div>
            </div>
            <div class="video-progress" v-if="token">
              <el-progress :percentage="progress" :format="formatProgress" />
            </div>
          </div>
        </div>
        <div class="course-content">
          <h2>课程简介</h2>
          <p>{{ course.description }}</p>
          <h2>课程内容</h2>
          <div v-html="course.content"></div>
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
  name: 'CourseDetail',
  components: {
  },
  data() {
    return {
      course: {},
      isCollected: false,
      progress: 0,
      currentVideoUrl: '',
      playbackRate: 1,
      currentQuality: 'hd',
      videoQualities: [
        { label: '高清', value: 'hd' },
        { label: '标清', value: 'sd' }
      ],
      subtitles: [
        // 示例字幕，实际项目中从后端获取
        // { label: '中文', kind: 'subtitles', srclang: 'zh', src: 'subtitle_zh.vtt' },
        // { label: 'English', kind: 'subtitles', srclang: 'en', src: 'subtitle_en.vtt' }
      ],
      currentSubtitle: '',
      isLoading: false,
      hasError: false,
      errorMessage: '',
      networkStatus: 'good', // good, slow, poor
      bufferTime: 0,
      autoQuality: true,
      // 弹幕相关
      danmakus: [],
      danmakuContent: '',
      danmakuColor: '#ffffff',
      danmakuFontSize: 20
    }
  },
  computed: {
    token() {
      return localStorage.getItem('token')
    }
  },
  mounted() {
    this.loadCourse()
    if (this.token) {
      this.checkCollection()
      this.loadProgress()
    }
    // 初始化弹幕
    this.initDanmaku()
  },
  methods: {
    logout() {
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      this.$router.push('/login')
    },
    loadCourse() {
      const id = this.$route.params.id
      request.get(`/course/detail/${id}`).then(res => {
        this.course = res
        this.currentVideoUrl = res.videoUrl
      }).catch(error => {
        this.$message.error('获取课程详情失败')
        console.error(error)
      })
    },
    onVideoLoaded() {
      this.isLoading = false
      this.setVideoProgress()
    },
    onVideoWaiting() {
      this.isLoading = true
    },
    onVideoPlaying() {
      this.isLoading = false
    },
    onVideoError() {
      this.isLoading = false
      this.hasError = true
      this.errorMessage = '视频加载失败'
    },
    setPlaybackRate() {
      const video = this.$refs.videoPlayer
      if (video) {
        video.playbackRate = this.playbackRate
      }
    },
    changeVideoQuality() {
      // 实际项目中根据清晰度切换不同的视频URL
      // 这里简单示例，实际需要后端提供不同清晰度的视频地址
      this.isLoading = true
      setTimeout(() => {
        this.isLoading = false
        this.$message.success('清晰度已切换')
      }, 500)
    },
    setSubtitle() {
      const video = this.$refs.videoPlayer
      if (video) {
        const tracks = video.textTracks
        for (let i = 0; i < tracks.length; i++) {
          tracks[i].mode = tracks[i].label === this.currentSubtitle ? 'showing' : 'disabled'
        }
      }
    },
    toggleFullscreen() {
      const videoWrapper = this.$refs.videoWrapper
      if (!document.fullscreenElement) {
        if (videoWrapper.requestFullscreen) {
          videoWrapper.requestFullscreen()
        } else if (videoWrapper.mozRequestFullScreen) {
          videoWrapper.mozRequestFullScreen()
        } else if (videoWrapper.webkitRequestFullscreen) {
          videoWrapper.webkitRequestFullscreen()
        } else if (videoWrapper.msRequestFullscreen) {
          videoWrapper.msRequestFullscreen()
        }
      } else {
        if (document.exitFullscreen) {
          document.exitFullscreen()
        } else if (document.mozCancelFullScreen) {
          document.mozCancelFullScreen()
        } else if (document.webkitExitFullscreen) {
          document.webkitExitFullscreen()
        } else if (document.msExitFullscreen) {
          document.msExitFullscreen()
        }
      }
    },
    reloadVideo() {
      this.hasError = false
      this.isLoading = true
      const video = this.$refs.videoPlayer
      if (video) {
        video.load()
      }
    },
    checkCollection() {
      const id = this.$route.params.id
      // 这里可以通过获取用户的收藏列表来检查
      request.get('/course/myCollections').then(res => {
        const collections = res
        this.isCollected = collections.some(course => course.id === parseInt(id))
      }).catch(error => {
        console.error('检查收藏状态失败', error)
      })
    },
    loadProgress() {
      const id = this.$route.params.id
      // 这里可以通过获取用户的学习记录来获取进度
      request.get('/course/myCourses').then(res => {
        const myCourses = res.data || res
        const courseRecord = myCourses.find(course => course.courseId === parseInt(id))
        if (courseRecord) {
          this.progress = courseRecord.progress
          // 视频播放时设置进度
          this.setVideoProgress()
        }
      }).catch(error => {
        console.error('获取学习进度失败', error)
      })
    },
    handleStudy() {
      if (!this.token) {
        this.$confirm('请先登录后再学习，是否现在去登录？', '提示', {
          confirmButtonText: '去登录',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.$router.push('/login')
        }).catch(() => {})
      } else {
        this.startStudy()
      }
    },
    startStudy() {
      const id = this.$route.params.id
      this.$message.success('开始学习')
      // 记录学习进度为10%
      this.updateProgress(10)
      // 跳转到视频播放区域
      this.scrollToVideo()
    },
    scrollToVideo() {
      // 滚动到视频播放区域
      setTimeout(() => {
        const videoContainer = document.querySelector('.video-container')
        if (videoContainer) {
          videoContainer.scrollIntoView({ behavior: 'smooth' })
        }
      }, 100)
    },
    onVideoTimeUpdate() {
      if (!this.token) return
      
      const video = this.$refs.videoPlayer
      if (video) {
        const currentTime = video.currentTime
        const duration = video.duration
        if (duration > 0) {
          const progress = Math.round((currentTime / duration) * 100)
          // 每5%更新一次进度，提高精度
          if (progress % 5 === 0 && progress > this.progress) {
            this.updateProgress(progress)
          }
        }
      }
    },
    onVideoEnded() {
      if (!this.token) return
      // 视频结束时，更新进度为100%
      this.updateProgress(100)
      this.$message.success('恭喜您完成了本课程的学习！')
    },
    setVideoProgress() {
      // 根据学习进度设置视频播放位置
      const video = this.$refs.videoPlayer
      if (video && this.progress > 0) {
        setTimeout(() => {
          const duration = video.duration
          if (duration > 0) {
            const targetTime = (duration * this.progress) / 100
            video.currentTime = targetTime
            this.$message.success(`已从 ${Math.round(targetTime / 60)}:${Math.round(targetTime % 60)} 处继续播放`)
          }
        }, 500)
      }
    },
    formatProgress(percentage) {
      return `${percentage}%`
    },
    updateProgress(progress) {
      const id = this.$route.params.id
      request.post('/course/study', {
        courseId: id,
        progress: progress
      }).then(res => {
        this.progress = progress
        this.$message.success('学习进度已更新')
      }).catch(error => {
        console.error('更新学习进度失败', error)
        if (error.response && error.response.status === 403) {
          this.$message.warning('请先登录后再学习')
        } else {
          this.$message.error('更新学习进度失败')
        }
      })
    },
    collect() {
      const id = this.$route.params.id
      request.post(`/course/collect/${id}`).then(res => {
        this.isCollected = true
        this.course.collectCount++
        this.$message.success('收藏成功')
      })
    },
    uncollect() {
      const id = this.$route.params.id
      request.post(`/course/uncollect/${id}`).then(res => {
        this.isCollected = false
        this.course.collectCount--
        this.$message.success('取消收藏成功')
      })
    },
    // 发送弹幕
    sendDanmaku() {
      if (!this.danmakuContent.trim()) {
        this.$message.warning('请输入弹幕内容')
        return
      }
      
      const video = this.$refs.videoPlayer
      if (!video) return
      
      const currentTime = video.currentTime
      const danmaku = {
        content: this.danmakuContent,
        color: this.danmakuColor,
        fontSize: this.danmakuFontSize,
        time: currentTime,
        left: 800, // 初始位置，从右侧进入
        top: Math.random() * 300, // 随机垂直位置
        duration: 8 + Math.random() * 4 // 随机持续时间
      }
      
      this.danmakus.push(danmaku)
      this.danmakuContent = ''
      
      // 3秒后移除弹幕
      setTimeout(() => {
        this.danmakus.shift()
      }, danmaku.duration * 1000)
    },
    // 初始化弹幕
    initDanmaku() {
      // 这里可以从后端获取历史弹幕
      // 现在添加一些示例弹幕
      const sampleDanmakus = [
        { content: '课程内容很精彩！', color: '#ffffff', fontSize: 20, time: 10, left: 800, top: 50, duration: 10 },
        { content: '老师讲得很清楚', color: '#ff0000', fontSize: 18, time: 20, left: 800, top: 100, duration: 8 },
        { content: '这个知识点很重要', color: '#00ff00', fontSize: 22, time: 30, left: 800, top: 150, duration: 12 }
      ]
      this.danmakus = sampleDanmakus
    }
  }
}
</script>

<style scoped>
.course-detail {
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

.course-card {
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.course-header {
  display: flex;
  margin-bottom: 30px;
}

.course-cover-container {
  width: 400px;
  height: 250px;
  overflow: hidden;
  margin-right: 30px;
}

.course-cover {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.course-cover-placeholder {
  width: 400px;
  height: 250px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background-color: #f0f0f0;
  border: 1px dashed #d9d9d9;
  margin-right: 30px;
  transition: all 0.3s ease;
}

.course-cover-placeholder i {
  font-size: 48px;
  color: #999;
  margin-bottom: 12px;
}

.course-cover-placeholder span {
  font-size: 16px;
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

.course-info h1 {
  margin-bottom: 20px;
  font-size: 24px;
  color: #333;
}

.course-meta {
  margin-bottom: 20px;
}

.course-type,
.course-level,
.course-scenario {
  display: inline-block;
  padding: 5px 15px;
  background-color: #f0f9ff;
  color: #409EFF;
  font-size: 14px;
  border-radius: 4px;
  margin-right: 15px;
  margin-bottom: 10px;
}

.course-stats {
  margin-bottom: 20px;
  font-size: 14px;
  color: #666;
}

.course-stats span {
  margin-right: 30px;
}

.course-actions {
  margin-bottom: 20px;
}

.course-actions button {
  margin-right: 15px;
}

.course-content {
  margin-top: 30px;
}

.course-content h2 {
  margin-bottom: 15px;
  font-size: 18px;
  color: #333;
  border-bottom: 1px solid #eaeaea;
  padding-bottom: 10px;
}

.course-content p {
  margin-bottom: 20px;
  font-size: 14px;
  color: #666;
  line-height: 1.6;
}

.footer {
  background-color: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: 40px;
}

.video-container {
  margin-top: 30px;
}

.video-player {
  margin-bottom: 20px;
}

.video-wrapper {
  position: relative;
  width: 100%;
  height: 400px;
  background-color: #000;
  border-radius: 4px;
  overflow: hidden;
}

.video-loading {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  color: white;
  display: flex;
  flex-direction: column;
  align-items: center;
  background-color: rgba(0, 0, 0, 0.7);
  padding: 20px;
  border-radius: 4px;
}

.video-loading i {
  font-size: 32px;
  margin-bottom: 10px;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.video-error {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  color: white;
  display: flex;
  flex-direction: column;
  align-items: center;
  background-color: rgba(0, 0, 0, 0.7);
  padding: 20px;
  border-radius: 4px;
}

.video-error i {
  font-size: 32px;
  margin-bottom: 10px;
  color: #F56C6C;
}

.video-error span {
  margin-bottom: 15px;
}

.video-controls {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 15px;
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.control-group {
  display: flex;
  align-items: center;
  margin-right: 20px;
}

.control-label {
  margin-right: 8px;
  font-size: 14px;
  color: #606266;
}

.video-progress {
  margin-top: 10px;
}

/* 弹幕相关样式 */
.danmaku-container {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  overflow: hidden;
  z-index: 10;
}

.danmaku-item {
  position: absolute;
  white-space: nowrap;
  color: white;
  font-size: 20px;
  font-weight: bold;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.8);
  animation: danmakuMove linear forwards;
  z-index: 10;
}

@keyframes danmakuMove {
  from { transform: translateX(100%); }
  to { transform: translateX(-100%); }
}

.danmaku-input-area {
  display: flex;
  align-items: center;
  margin-top: 15px;
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.danmaku-input-area .el-input {
  flex: 1;
  margin-right: 10px;
}
</style>