<template>
  <div class="admin-courses">
    <!-- 顶部导航栏 -->
    <el-header height="60px" class="header">
      <div class="logo">办公软件学习平台 - 管理员后台</div>
      <div class="nav">
        <router-link to="/admin/dashboard" class="nav-item">控制台</router-link>
        <router-link to="/admin/users" class="nav-item">用户管理</router-link>
        <router-link to="/admin/courses" class="nav-item active">课程管理</router-link>
        <router-link to="/admin/posts" class="nav-item">帖子管理</router-link>
        <el-button type="text" @click="logout" class="nav-item">退出登录</el-button>
      </div>
    </el-header>

    <!-- 主体内容 -->
    <div class="main">
      <div class="header-actions">
        <h1>课程管理</h1>
        <el-button type="primary" @click="showAddDialog">
          <i class="el-icon-plus"></i> 添加课程
        </el-button>
      </div>
      <el-card shadow="hover">
        <el-table :data="courses" style="width: 100%">
          <el-table-column prop="id" label="ID" width="80"></el-table-column>
          <el-table-column prop="title" label="课程标题"></el-table-column>
          <el-table-column prop="type" label="课程类型"></el-table-column>
          <el-table-column prop="level" label="难度等级"></el-table-column>
          <el-table-column prop="scenario" label="应用场景"></el-table-column>
          <el-table-column prop="view_count" label="浏览量"></el-table-column>
          <el-table-column prop="collect_count" label="收藏量"></el-table-column>
          <el-table-column prop="createtime" label="创建时间">
            <template slot-scope="scope">
              {{ formatDate(scope.row.createtime) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200">
            <template slot-scope="scope">
              <el-button size="small" @click="showEditDialog(scope.row)">编辑</el-button>
              <el-button size="small" type="danger" @click="deleteCourse(scope.row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <div class="pagination-container">
          <el-pagination
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
            :current-page="currentPage"
            :page-sizes="[10, 20, 50, 100]"
            :page-size="pageSize"
            layout="total, sizes, prev, pager, next, jumper"
            :total="total">
          </el-pagination>
        </div>
      </el-card>
    </div>

    <!-- 添加课程对话框 -->
    <el-dialog title="添加课程" :visible.sync="addDialogVisible" width="800px">
      <el-form :model="courseForm" :rules="courseRules" ref="courseForm">
        <el-form-item label="课程标题" prop="title">
          <el-input v-model="courseForm.title" placeholder="请输入课程标题"></el-input>
        </el-form-item>
        <el-form-item label="课程类型" prop="type">
          <el-select v-model="courseForm.type" placeholder="请选择课程类型">
            <el-option label="Word" value="Word"></el-option>
            <el-option label="Excel" value="Excel"></el-option>
            <el-option label="PPT" value="PPT"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="难度等级" prop="level">
          <el-select v-model="courseForm.level" placeholder="请选择难度等级">
            <el-option label="入门" value="入门"></el-option>
            <el-option label="进阶" value="进阶"></el-option>
            <el-option label="高级" value="高级"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="应用场景" prop="scenario">
          <el-select v-model="courseForm.scenario" placeholder="请选择应用场景">
            <el-option label="办公通用" value="办公通用"></el-option>
            <el-option label="财务" value="财务"></el-option>
            <el-option label="行政" value="行政"></el-option>
            <el-option label="人力" value="人力"></el-option>
            <el-option label="商务" value="商务"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="课程描述" prop="description">
          <el-input type="textarea" v-model="courseForm.description" placeholder="请输入课程描述" rows="3"></el-input>
        </el-form-item>
        <el-form-item label="课程内容" prop="content">
          <el-input type="textarea" v-model="courseForm.content" placeholder="请输入课程内容" rows="5"></el-input>
        </el-form-item>
        <el-form-item label="课程封面">
          <el-upload
            class="upload-demo"
            action="/api/video/upload"
            :headers="{ token: token }"
            :on-success="handleCoverUploadSuccess"
            :on-error="handleUploadError"
            :file-list="coverFileList"
            :auto-upload="true"
          >
            <el-button size="small" type="primary">点击上传封面</el-button>
            <div slot="tip" class="el-upload__tip">只能上传jpg/png文件，且不超过2MB</div>
          </el-upload>
        </el-form-item>
        <el-form-item label="课程视频">
          <el-upload
            class="upload-demo"
            action="/api/video/upload"
            :headers="{ token: token }"
            :on-success="handleVideoUploadSuccess"
            :on-error="handleUploadError"
            :file-list="videoFileList"
            :auto-upload="true"
          >
            <el-button size="small" type="primary">点击上传视频</el-button>
            <div slot="tip" class="el-upload__tip">只能上传mp4/avi/mov文件，且不超过500MB</div>
          </el-upload>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="addDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="addCourse">添加</el-button>
      </span>
    </el-dialog>

    <!-- 编辑课程对话框 -->
    <el-dialog title="编辑课程" :visible.sync="editDialogVisible" width="800px">
      <el-form :model="courseForm" :rules="courseRules" ref="courseForm">
        <el-form-item label="课程标题" prop="title">
          <el-input v-model="courseForm.title" placeholder="请输入课程标题"></el-input>
        </el-form-item>
        <el-form-item label="课程类型" prop="type">
          <el-select v-model="courseForm.type" placeholder="请选择课程类型">
            <el-option label="Word" value="Word"></el-option>
            <el-option label="Excel" value="Excel"></el-option>
            <el-option label="PPT" value="PPT"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="难度等级" prop="level">
          <el-select v-model="courseForm.level" placeholder="请选择难度等级">
            <el-option label="入门" value="入门"></el-option>
            <el-option label="进阶" value="进阶"></el-option>
            <el-option label="高级" value="高级"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="应用场景" prop="scenario">
          <el-select v-model="courseForm.scenario" placeholder="请选择应用场景">
            <el-option label="办公通用" value="办公通用"></el-option>
            <el-option label="财务" value="财务"></el-option>
            <el-option label="行政" value="行政"></el-option>
            <el-option label="人力" value="人力"></el-option>
            <el-option label="商务" value="商务"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="课程描述" prop="description">
          <el-input type="textarea" v-model="courseForm.description" placeholder="请输入课程描述" rows="3"></el-input>
        </el-form-item>
        <el-form-item label="课程内容" prop="content">
          <el-input type="textarea" v-model="courseForm.content" placeholder="请输入课程内容" rows="5"></el-input>
        </el-form-item>
        <el-form-item label="课程封面">
          <el-upload
            class="upload-demo"
            action="/api/video/upload"
            :headers="{ token: token }"
            :on-success="handleCoverUploadSuccess"
            :on-error="handleUploadError"
            :file-list="coverFileList"
            :auto-upload="true"
          >
            <el-button size="small" type="primary">点击上传封面</el-button>
            <div slot="tip" class="el-upload__tip">只能上传jpg/png文件，且不超过2MB</div>
          </el-upload>
        </el-form-item>
        <el-form-item label="课程视频">
          <el-upload
            class="upload-demo"
            action="/api/video/upload"
            :headers="{ token: token }"
            :on-success="handleVideoUploadSuccess"
            :on-error="handleUploadError"
            :file-list="videoFileList"
            :auto-upload="true"
          >
            <el-button size="small" type="primary">点击上传视频</el-button>
            <div slot="tip" class="el-upload__tip">只能上传mp4/avi/mov文件，且不超过500MB</div>
          </el-upload>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="updateCourse">保存</el-button>
      </span>
    </el-dialog>

    <!-- 底部 -->
    <el-footer height="100px" class="footer">
      <p>© 2026 办公软件学习平台 版权所有</p>
    </el-footer>
  </div>
</template>

<script>
import request, { getToken } from '@/utils/request'

export default {
  name: 'AdminCourseList',
  data() {
    return {
      courses: [],
      currentPage: 1,
      pageSize: 10,
      total: 0,
      addDialogVisible: false,
      editDialogVisible: false,
      courseForm: {
        title: '',
        description: '',
        type: '',
        level: '',
        scenario: '',
        content: '',
        cover: '',
        videoUrl: ''
      },
      coverFileList: [],
      videoFileList: [],
      courseRules: {
        title: [
          { required: true, message: '请输入课程标题', trigger: 'blur' }
        ],
        type: [
          { required: true, message: '请选择课程类型', trigger: 'blur' }
        ],
        level: [
          { required: true, message: '请选择难度等级', trigger: 'blur' }
        ]
      }
    }
  },
  computed: {
    token() {
      return getToken()
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
    formatDate(date) {
      return new Date(date).toLocaleString()
    },
    handleSizeChange(size) {
      this.pageSize = size
      this.loadCourses()
    },
    handleCurrentChange(current) {
      this.currentPage = current
      this.loadCourses()
    },
    loadCourses() {
      request.get('/course/list').then(res => {
        this.courses = res
        this.total = res.length
      }).catch(error => {
        this.$message.error('获取课程列表失败')
        console.error(error)
      })
    },
    showAddDialog() {
      this.courseForm = {
        title: '',
        description: '',
        type: '',
        level: '',
        scenario: '',
        content: ''
      }
      this.addDialogVisible = true
    },
    showEditDialog(course) {
      this.courseForm = { ...course }
      // 重置文件列表
      this.coverFileList = []
      this.videoFileList = []
      // 如果有封面，添加到文件列表
      if (course.cover) {
        this.coverFileList.push({
          name: 'cover.jpg',
          url: course.cover
        })
      }
      // 如果有视频，添加到文件列表
      if (course.videoUrl) {
        this.videoFileList.push({
          name: 'video.mp4',
          url: course.videoUrl
        })
      }
      this.editDialogVisible = true
    },
    handleCoverUploadSuccess(response) {
      this.courseForm.cover = response
      this.coverFileList = [{
        name: 'cover.jpg',
        url: response
      }]
      this.$message.success('封面上传成功')
    },
    handleVideoUploadSuccess(response) {
      this.courseForm.videoUrl = response
      this.videoFileList = [{
        name: 'video.mp4',
        url: response
      }]
      this.$message.success('视频上传成功')
    },
    handleUploadError(error) {
      this.$message.error('上传失败')
      console.error(error)
    },
    addCourse() {
      this.$refs.courseForm.validate((valid) => {
        if (valid) {
          request.post('/course/admin/add', this.courseForm).then(res => {
            this.addDialogVisible = false
            this.$message.success('课程添加成功')
            this.loadCourses()
          }).catch(error => {
            this.$message.error('课程添加失败')
            console.error(error)
          })
        }
      })
    },
    updateCourse() {
      this.$refs.courseForm.validate((valid) => {
        if (valid) {
          request.put('/course/admin/update', this.courseForm).then(res => {
            this.editDialogVisible = false
            this.$message.success('课程更新成功')
            this.loadCourses()
          }).catch(error => {
            this.$message.error('课程更新失败')
            console.error(error)
          })
        }
      })
    },
    deleteCourse(id) {
      this.$confirm('确定要删除这门课程吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        request.delete(`/course/admin/delete/${id}`).then(res => {
          this.$message.success('课程删除成功')
          this.loadCourses()
        }).catch(error => {
          this.$message.error('课程删除失败')
          console.error(error)
        })
      }).catch(() => {
        // 取消删除
      })
    }
  }
}
</script>

<style scoped>
.admin-courses {
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

.main {
  flex: 1;
  margin: 20px;
}

.header-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.header-actions h1 {
  margin: 0;
  color: #333;
}

.pagination-container {
  margin-top: 20px;
  text-align: right;
}

.footer {
  background-color: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: 40px;
}
</style>