import Vue from 'vue'
import VueRouter from 'vue-router'
import Home from '../views/home/Home.vue'
import Login from '../views/user/Login.vue'
import Register from '../views/user/Register.vue'
import CourseList from '../views/course/List.vue'
import CourseDetail from '../views/course/Detail.vue'
import CommunityList from '../views/community/List.vue'
import CommunityDetail from '../views/community/Detail.vue'
import UserCenter from '../views/user/Center.vue'
import UserInfo from '../views/user/UserInfo.vue'
import MyCourses from '../views/user/MyCourses.vue'
import MyPosts from '../views/user/MyPosts.vue'
import MyComments from '../views/user/MyComments.vue'
import MyCollections from '../views/user/MyCollections.vue'
import AdminDashboard from '../views/admin/Dashboard.vue'
import AdminUserList from '../views/admin/UserList.vue'
import AdminCourseList from '../views/admin/CourseList.vue'
import AdminPostList from '../views/admin/PostList.vue'

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home
  },
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/register',
    name: 'Register',
    component: Register
  },
  {
    path: '/courses',
    name: 'CourseList',
    component: CourseList
  },
  {
    path: '/course/:id',
    name: 'CourseDetail',
    component: CourseDetail
  },
  {
    path: '/community',
    name: 'CommunityList',
    component: CommunityList
  },
  {
    path: '/community/:id',
    name: 'CommunityDetail',
    component: CommunityDetail
  },
  {
    path: '/user/center',
    name: 'UserCenter',
    component: UserCenter,
    redirect: '/user/center/info',
    children: [
      {
        path: 'info',
        name: 'UserInfo',
        component: UserInfo
      },
      {
        path: 'courses',
        name: 'MyCourses',
        component: MyCourses
      },
      {
        path: 'posts',
        name: 'MyPosts',
        component: MyPosts
      },
      {
        path: 'comments',
        name: 'MyComments',
        component: MyComments
      },
      {
        path: 'collections',
        name: 'MyCollections',
        component: MyCollections
      }
    ]
  },
  // 管理员路由
  {
    path: '/admin/dashboard',
    name: 'AdminDashboard',
    component: AdminDashboard
  },
  {
    path: '/admin/users',
    name: 'AdminUserList',
    component: AdminUserList
  },
  {
    path: '/admin/courses',
    name: 'AdminCourseList',
    component: AdminCourseList
  },
  {
    path: '/admin/posts',
    name: 'AdminPostList',
    component: AdminPostList
  }
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const requiresAuth = ['UserCenter', 'MyCourses', 'MyPosts', 'MyComments', 'MyCollections', 'AdminDashboard', 'AdminUserList', 'AdminCourseList', 'AdminPostList']
  if (requiresAuth.includes(to.name) && !token) {
    next({ name: 'Login' })
  } else {
    next()
  }
})

export default router