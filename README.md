# 办公学习平台 - 项目总体介绍

## 项目简介
办公学习平台是一款基于前后端分离架构开发的综合性管理系统，主要面向企业、单位或校园场景，提供日常办公、学习管理、课程管理、资料管理、用户管理、公告发布等一体化功能。

系统采用现代化技术栈开发，界面简洁易用，功能完整规范，是一套可直接用于毕业设计、学习实践与二次开发的完整项目。

## 系统架构
本项目采用前后端分离架构：
- 后端：SpringBoot 框架
- 前端：Vue 框架
- 数据库：MySQL
- 部署方式：本地运行 / 服务器部署

整体结构清晰、代码规范、模块划分合理，具备良好的可维护性与扩展性。

## 项目结构
项目包含完整的前后端代码、数据库脚本、开发文档、系统设计说明，结构如下：
- springboot/：后端服务核心代码
- vue/：前端页面与交互逻辑
- sql/：数据库初始化脚本
- docs/：系统设计文档、ER图、功能说明、架构图
- 启动脚本：支持一键启动项目

## 主要功能
### 用户功能
- 用户登录、注册、个人信息管理
- 查看公告、学习资料、课程信息
- 学习记录查看与管理

### 管理功能
- 用户管理
- 课程管理
- 学习资料管理
- 公告发布与管理
- 数据统计与查看

## 技术栈
### 后端技术
- SpringBoot
- MyBatis / MyBatis-Plus
- MySQL
- Maven

### 前端技术
- Vue
- Axios
- Element UI
- Node.js

## 项目特点
1. 完整可运行：项目包含前后端+数据库，可直接部署启动
2. 结构规范：采用标准分层架构，代码整洁易读
3. 适合毕业设计：功能齐全、文档完整、界面美观
4. 扩展性强：可轻松新增模块、修改页面、优化逻辑

## 运行环境要求
- JDK 1.8+
- MySQL 5.7+
- Node.js 14+
- Maven 3.6+

## 项目用途
- 毕业设计 / 毕业论文项目
- Java 学习项目
- Vue 前端学习项目
- 前后端分离实战项目
- 办公学习类系统二次开发基础

## 总结
办公学习平台是一套功能完整、结构清晰、技术主流的综合管理系统。
从开发到设计、从数据库到前端页面全部配套完善，可作为本人毕业设计项目记录，也适合学习与技术提升。
# 办公软件学习平台 - 核心功能模块代码

> 本系统核心功能 = 真正让系统"跑起来、实现业务功能"的关键逻辑代码  
> 每个模块只包含两类核心代码：Controller接口 + Service业务逻辑  
> 每段代码标注：「这段代码实现XX功能，核心逻辑是XX」

---

## 一、用户认证模块

> 实现用户注册、登录、JWT身份验证、个人信息修改

### 1. Controller层 - UserController.java

#### 用户登录接口

```java
/** 这段代码实现用户登录功能，核心逻辑是：
  * 1. 验证用户名密码不能为空
  * 2. 查询用户是否存在
  * 3. 验证密码（MD5加密后比对）
  * 4. 检查账号状态（是否被禁用）
  * 5. 生成JWT Token返回给前端 */
@PostMapping("/login")
public Result login(@RequestBody Map<String, String> params) {
    String username = params.get("username");
    String password = params.get("password");

    if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
        return Result.error("用户名或密码不能为空");
    }
    User user = userService.findByUsername(username);
    if (user == null) {
        return Result.error("用户不存在");
    }
    if (!user.getPassword().equals(md5(password))) {
        return Result.error("密码错误");
    }
    if (user.getStatus() == 0) {
        return Result.error("账号已禁用");
    }
    String token = TokenUtils.genToken(user.getId(), user.getPassword());
    return Result.success(Map.of("token", token, "user", user));
}
```

#### 用户注册接口

```java
/** 这段代码实现用户注册功能，核心逻辑是：
  * 1. 验证用户名密码不能为空
  * 2. 检查用户名是否已存在
  * 3. MD5加密密码
  * 4. 设置账号状态为启用（status=1）
  * 5. 保存用户到数据库 */
@PostMapping("/register")
public Result register(@RequestBody User user) {
    if (user.getUsername() == null || user.getUsername().trim().isEmpty() || 
        user.getPassword() == null || user.getPassword().trim().isEmpty()) {
        return Result.error("用户名或密码不能为空");
    }
    User existingUser = userService.findByUsername(user.getUsername());
    if (existingUser != null) {
        return Result.error("用户名已存在");
    }
    user.setPassword(md5(user.getPassword()));
    user.setStatus(1);
    userService.save(user);
    return Result.success();
}
```

#### 获取用户信息接口

```java
/** 这段代码实现获取当前登录用户信息功能，核心逻辑是：
  * 1. 从请求头中获取token
  * 2. 解析token获取userId
  * 3. 查询用户信息返回 */
@GetMapping("/info")
public Result getInfo(@RequestHeader("token") String token) {
    Integer userId = TokenUtils.getUserId(token);
    User user = userService.findById(userId);
    return Result.success(user);
}
```

#### 修改个人信息接口

```java
/** 这段代码实现修改个人信息功能，核心逻辑是：
  * 1. 从token中解析userId
  * 2. 设置user的id为当前登录用户
  * 3. 更新用户信息 */
@PutMapping("/update")
public Result update(@RequestBody User user, @RequestHeader("token") String token) {
    Integer userId = TokenUtils.getUserId(token);
    user.setId(userId);
    userService.update(user);
    return Result.success();
}
```

#### 修改密码接口

```java
/** 这段代码实现修改密码功能，核心逻辑是：
  * 1. 从token获取userId
  * 2. 验证原密码是否正确
  * 3. MD5加密新密码
  * 4. 更新密码到数据库 */
@PutMapping("/updatePassword")
public Result updatePassword(@RequestBody Map<String, String> params, @RequestHeader("token") String token) {
    Integer userId = TokenUtils.getUserId(token);
    if (userId == null) {
        return Result.error("请先登录");
    }
    String oldPassword = params.get("oldPassword");
    String newPassword = params.get("newPassword");
    if (oldPassword == null || oldPassword.trim().isEmpty() || newPassword == null || newPassword.trim().isEmpty()) {
        return Result.error("密码不能为空");
    }
    User user = userService.findById(userId);
    if (user == null) {
        return Result.error("用户不存在");
    }
    if (!user.getPassword().equals(md5(oldPassword))) {
        return Result.error("原密码错误");
    }
    user.setPassword(md5(newPassword));
    userService.update(user);
    return Result.success();
}
```

### 2. JWT工具类 - TokenUtils.java

#### 生成Token

```java
/** 这段代码实现JWT Token生成功能，核心逻辑是：
  * 1. 设置过期时间为24小时
  * 2. 使用HMAC256算法和密钥签名
  * 3. payload中存入userId和password
  * 4. 返回签名后的token字符串 */
private static final long EXPIRE_TIME = 24 * 60 * 60 * 1000; // 24小时
private static final String SECRET = "office-learning-platform";

public static String genToken(Integer userId, String password) {
    Date date = new Date(System.currentTimeMillis() + EXPIRE_TIME);
    Algorithm algorithm = Algorithm.HMAC256(SECRET);
    return JWT.create()
            .withClaim("userId", userId)
            .withClaim("password", password)
            .withExpiresAt(date)
            .sign(algorithm);
}
```

#### 解析Token获取用户ID

```java
/** 这段代码实现从Token中解析用户ID功能，核心逻辑是：
  * 1. 检查token是否为空
  * 2. 解码JWT token
  * 3. 提取userId claim并转为Integer
  * 4. 异常时返回null */
public static Integer getUserId(String token) {
    if (token == null || token.trim().isEmpty()) {
        return null;
    }
    try {
        return JWT.decode(token).getClaim("userId").asInt();
    } catch (Exception e) {
        return null;
    }
}
```

#### 验证Token

```java
/** 这段代码实现Token验证功能，核心逻辑是：
  * 1. 检查token是否为空
  * 2. 使用相同密钥重新验证签名
  * 3. 验证是否过期
  * 4. 返回验证结果 */
public static boolean verifyToken(String token) {
    if (token == null || token.trim().isEmpty()) {
        return false;
    }
    try {
        Algorithm algorithm = Algorithm.HMAC256(SECRET);
        JWT.require(algorithm).build().verify(token);
        return true;
    } catch (Exception e) {
        return false;
    }
}
```

### 3. Service层 - UserService.java

```java
/** 这段代码实现用户查询功能，核心逻辑是：
  * 调用Mapper层根据用户名查询用户（用于登录验证和注册查重） */
public User findByUsername(String username) {
    return userMapper.findByUsername(username);
}

/** 这段代码实现根据ID查询用户功能，核心逻辑是：
  * 调用Mapper层查询用户信息（用于获取个人信息和修改密码验证） */
public User findById(Integer id) {
    return userMapper.findById(id);
}

/** 这段代码实现保存用户功能，核心逻辑是：
  * 调用Mapper层保存用户（用于注册） */
public void save(User user) {
    userMapper.save(user);
}

/** 这段代码实现更新用户功能，核心逻辑是：
  * 调用Mapper层更新用户信息（用于修改个人信息和密码） */
public void update(User user) {
    userMapper.update(user);
}
```

---

## 二、课程学习模块

> 提供课程列表、详情查看、视频学习、收藏功能

### 1. Controller层 - CourseController.java

#### 获取课程列表

```java
/** 这段代码实现获取课程列表功能，核心逻辑是：
  * 1. 接收type、level、scenario三个筛选参数
  * 2. 调用Service层查询符合条件的课程
  * 3. 返回课程列表 */
@GetMapping("/list")
public Result list(@RequestParam(required = false) String type, 
                   @RequestParam(required = false) String level, 
                   @RequestParam(required = false) String scenario) {
    List<Course> courses = courseService.list(type, level, scenario);
    return Result.success(courses);
}
```

#### 获取课程详情

```java
/** 这段代码实现获取课程详情功能，核心逻辑是：
  * 1. 验证课程ID不能为空
  * 2. 查询课程信息
  * 3. 验证课程是否存在
  * 4. 课程浏览量+1
  * 5. 返回课程详情 */
@GetMapping("/detail/{id}")
public Result detail(@PathVariable Integer id) {
    if (id == null) {
        return Result.error("课程ID不能为空");
    }
    Course course = courseService.findById(id);
    if (course == null) {
        return Result.error("课程不存在");
    }
    courseService.incrementViewCount(id);
    return Result.success(course);
}
```

#### 收藏课程

```java
/** 这段代码实现收藏课程功能，核心逻辑是：
  * 1. 从token获取userId
  * 2. 调用Service层创建收藏记录
  * 3. 课程收藏数+1 */
@PostMapping("/collect/{id}")
public Result collect(@PathVariable Integer id, @RequestHeader("token") String token) {
    Integer userId = TokenUtils.getUserId(token);
    courseService.collect(userId, id);
    return Result.success();
}
```

#### 取消收藏课程

```java
/** 这段代码实现取消收藏课程功能，核心逻辑是：
  * 1. 从token获取userId
  * 2. 调用Service层删除收藏记录
  * 3. 课程收藏数-1 */
@PostMapping("/uncollect/{id}")
public Result uncollect(@PathVariable Integer id, @RequestHeader("token") String token) {
    Integer userId = TokenUtils.getUserId(token);
    courseService.uncollect(userId, id);
    return Result.success();
}
```

#### 获取我的收藏

```java
/** 这段代码实现获取用户收藏的课程列表功能，核心逻辑是：
  * 1. 从token获取userId
  * 2. 查询该用户收藏的所有课程 */
@GetMapping("/myCollections")
public Result myCollections(@RequestHeader("token") String token) {
    Integer userId = TokenUtils.getUserId(token);
    List<Course> courses = courseService.findMyCollections(userId);
    return Result.success(courses);
}
```

### 2. Service层 - CourseService.java

#### 收藏课程核心逻辑

```java
/** 这段代码实现收藏课程的核心业务逻辑，核心逻辑是：
  * 1. 创建CollectionItem对象
  * 2. 设置userId、targetId(课程ID)、targetType("course")
  * 3. 保存到collection表
  * 4. 课程收藏数+1 */
public void collect(Integer userId, Integer courseId) {
    CollectionItem collection = new CollectionItem();
    collection.setUserId(userId);
    collection.setTargetId(courseId);
    collection.setTargetType("course");
    collectionMapper.save(collection);
    courseMapper.incrementCollectCount(courseId);
}
```

#### 取消收藏核心逻辑

```java
/** 这段代码实现取消收藏的核心业务逻辑，核心逻辑是：
  * 1. 从collection表删除收藏记录
  * 2. 课程收藏数-1 */
public void uncollect(Integer userId, Integer courseId) {
    collectionMapper.delete(userId, courseId, "course");
    courseMapper.decrementCollectCount(courseId);
}
```

#### 增加浏览量

```java
/** 这段代码实现增加课程浏览量功能，核心逻辑是：
  * 调用Mapper层更新course表的view_count字段+1 */
public void incrementViewCount(Integer id) {
    courseMapper.incrementViewCount(id);
}
```

---

## 三、学习进度模块

> 自动保存学习进度，支持断点续学

### 1. Controller层 - CourseController.java

#### 保存学习进度

```java
/** 这段代码实现保存/更新学习进度功能，核心逻辑是：
  * 1. 从token获取userId
  * 2. 从请求体解析courseId和progress
  * 3. 参数验证（userId、courseId不能为空）
  * 4. 调用Service层保存或更新学习记录
  * 5. 这是实现断点续学的核心接口 */
@PostMapping("/study")
public Result study(@RequestBody Map<String, Object> params, 
                    @RequestHeader(value = "token", required = false) String token) {
    try {
        Integer userId = TokenUtils.getUserId(token);
        if (userId == null) {
            return Result.error("请先登录");
        }
        Integer courseId = params.get("courseId") != null ? 
            Integer.valueOf(params.get("courseId").toString()) : null;
        Integer progress = params.get("progress") != null ? 
            Integer.valueOf(params.get("progress").toString()) : 0;
        
        if (courseId == null) {
            return Result.error("课程ID不能为空");
        }
        
        studyRecordService.saveOrUpdate(userId, courseId, progress);
        return Result.success();
    } catch (Exception e) {
        e.printStackTrace();
        return Result.error("保存学习记录失败：" + e.getMessage());
    }
}
```

#### 获取我的课程

```java
/** 这段代码实现获取用户学习过的课程列表功能，核心逻辑是：
  * 1. 从token获取userId
  * 2. 查询学习记录并关联课程信息（包含进度、最后学习时间） */
@GetMapping("/myCourses")
public Result myCourses(@RequestHeader("token") String token) {
    Integer userId = TokenUtils.getUserId(token);
    List<Map<String, Object>> courses = studyRecordService.findMyCourses(userId);
    return Result.success(courses);
}
```

### 2. Service层 - StudyRecordService.java

#### 保存或更新学习记录（断点续学核心）

```java
/** 这段代码是实现断点续学的最核心逻辑，核心逻辑是：
  * 1. 验证userId和courseId不能为空
  * 2. 查询是否已有学习记录
  * 3. 如果没有记录：创建新记录，保存初始进度和学习时间
  * 4. 如果有记录：更新进度为最新值，更新学习时间为当前时间
  * 5. 这样用户下次学习时可以从上次的进度继续 */
public void saveOrUpdate(Integer userId, Integer courseId, Integer progress) {
    if (userId == null || courseId == null) {
        throw new IllegalArgumentException("用户ID和课程ID不能为空");
    }
    try {
        StudyRecord record = studyRecordMapper.findByUserIdAndCourseId(userId, courseId);
        if (record == null) {
            // 首次学习：创建新记录
            record = new StudyRecord();
            record.setUserId(userId);
            record.setCourseId(courseId);
            record.setProgress(progress != null ? progress : 0);
            record.setLastStudyTime(new Date());
            studyRecordMapper.save(record);
        } else {
            // 继续学习：更新进度和时间
            record.setProgress(progress != null ? progress : record.getProgress());
            record.setLastStudyTime(new Date());
            studyRecordMapper.update(record);
        }
    } catch (Exception e) {
        e.printStackTrace();
        throw new RuntimeException("保存学习记录失败", e);
    }
}
```

#### 获取我的课程

```java
/** 这段代码实现获取用户学习课程列表功能，核心逻辑是：
  * 调用Mapper层查询学习记录，联表查询课程信息，包含学习进度 */
public List<Map<String, Object>> findMyCourses(Integer userId) {
    return studyRecordMapper.findMyCourses(userId);
}
```

---

## 四、个性化推荐模块

> 根据用户学习和收藏记录，智能推荐感兴趣的课程

### 1. Controller层 - CourseController.java

#### 推荐课程接口

```java
/** 这段代码实现个性化推荐课程功能，核心逻辑是：
  * 1. 尝试从token获取userId（允许未登录访问）
  * 2. 如果未登录：返回热门课程（按浏览量排序）
  * 3. 如果已登录：根据用户学习历史推荐相同类型课程 */
@GetMapping("/recommend")
public Result recommend(@RequestHeader(value = "token", required = false) String token) {
    Integer userId = null;
    if (token != null) {
        userId = TokenUtils.getUserId(token);
    }
    List<Course> courses = courseService.recommend(userId);
    return Result.success(courses);
}
```

### 2. Service层 - CourseService.java

#### 推荐算法核心逻辑

```java
/** 这段代码实现个性化推荐算法，核心逻辑是：
  * 1. 如果userId为null（未登录）：
  *    - 查询所有课程
  *    - 返回前5个热门课程（按浏览量）
  * 2. 如果userId不为null（已登录）：
  *    - 调用Mapper层执行推荐SQL
  *    - SQL逻辑：查询用户学过的课程类型 -> 推荐相同类型的其他课程 -> 按浏览量排序 */
public List<Course> recommend(Integer userId) {
    if (userId == null) {
        // 未登录用户：返回热门课程
        List<Course> allCourses = courseMapper.list(null, null, null);
        if (allCourses == null || allCourses.isEmpty()) {
            return List.of();
        }
        int limit = Math.min(5, allCourses.size());
        return allCourses.subList(0, limit);
    }
    // 已登录用户：根据学习历史推荐
    return courseMapper.recommend(userId);
}
```

---

## 五、社区互动模块

> 用户可以发布帖子、评论、点赞、收藏

### 1. Controller层 - CommunityController.java

#### 获取帖子列表

```java
/** 这段代码实现获取帖子列表功能，核心逻辑是：
  * 1. 接收type参数（可选，按类型筛选）
  * 2. 调用Service层查询帖子列表 */
@GetMapping("/list")
public Result list(@RequestParam(required = false) String type) {
    List<Post> posts = communityService.list(type);
    return Result.success(posts);
}
```

#### 获取帖子详情

```java
/** 这段代码实现获取帖子详情功能，核心逻辑是：
  * 1. 调用Service层查询帖子信息和评论列表
  * 2. 帖子浏览量+1
  * 3. 返回包含评论的完整帖子数据 */
@GetMapping("/detail/{id}")
public Result detail(@PathVariable Integer id) {
    Map<String, Object> post = communityService.detail(id);
    communityService.incrementViewCount(id);
    return Result.success(post);
}
```

#### 发布帖子

```java
/** 这段代码实现发布帖子功能，核心逻辑是：
  * 1. 从token获取userId
  * 2. 设置帖子的userId为当前登录用户
  * 3. 保存帖子到数据库 */
@PostMapping("/post")
public Result post(@RequestBody Post post, @RequestHeader("token") String token) {
    Integer userId = TokenUtils.getUserId(token);
    post.setUserId(userId);
    communityService.savePost(post);
    return Result.success();
}
```

#### 发表评论

```java
/** 这段代码实现发表评论功能，核心逻辑是：
  * 1. 从token获取userId
  * 2. 设置评论的userId
  * 3. 保存评论
  * 4. 帖子评论数+1 */
@PostMapping("/comment")
public Result comment(@RequestBody Comment comment, @RequestHeader("token") String token) {
    Integer userId = TokenUtils.getUserId(token);
    comment.setUserId(userId);
    communityService.saveComment(comment);
    return Result.success();
}
```

#### 点赞帖子/评论

```java
/** 这段代码实现点赞功能，核心逻辑是：
  * 1. 从token获取userId
  * 2. 调用Service层创建点赞记录
  * 3. 指定点赞目标类型（post或comment）
  * 4. 目标点赞数+1 */
@PostMapping("/like/{id}")
public Result like(@PathVariable Integer id, @RequestHeader("token") String token) {
    Integer userId = TokenUtils.getUserId(token);
    communityService.like(userId, id, "post");
    return Result.success();
}

/** 这段代码实现点赞评论功能，核心逻辑是：
  * 1. 从token获取userId
  * 2. 调用Service层创建点赞记录
  * 3. 指定目标类型为comment */
@PostMapping("/comment/like/{id}")
public Result likeComment(@PathVariable Integer id, @RequestHeader("token") String token) {
    Integer userId = TokenUtils.getUserId(token);
    communityService.like(userId, id, "comment");
    return Result.success();
}
```

#### 收藏/取消收藏帖子

```java
/** 这段代码实现收藏帖子功能，核心逻辑是：
  * 1. 从token获取userId
  * 2. 调用Service层创建收藏记录 */
@PostMapping("/collect/{id}")
public Result collect(@PathVariable Integer id, @RequestHeader("token") String token) {
    Integer userId = TokenUtils.getUserId(token);
    communityService.collect(userId, id);
    return Result.success();
}

/** 这段代码实现取消收藏帖子功能，核心逻辑是：
  * 1. 从token获取userId
  * 2. 调用Service层删除收藏记录 */
@PostMapping("/uncollect/{id}")
public Result uncollect(@PathVariable Integer id, @RequestHeader("token") String token) {
    Integer userId = TokenUtils.getUserId(token);
    communityService.uncollect(userId, id);
    return Result.success();
}
```

#### 获取我的帖子/评论/收藏

```java
/** 这段代码实现获取用户发布的帖子列表功能 */
@GetMapping("/myPosts")
public Result myPosts(@RequestHeader("token") String token) {
    Integer userId = TokenUtils.getUserId(token);
    List<Post> posts = communityService.findMyPosts(userId);
    return Result.success(posts);
}

/** 这段代码实现获取用户发表的评论列表功能 */
@GetMapping("/myComments")
public Result myComments(@RequestHeader("token") String token) {
    Integer userId = TokenUtils.getUserId(token);
    List<Map<String, Object>> comments = communityService.findMyComments(userId);
    return Result.success(comments);
}

/** 这段代码实现获取用户收藏的帖子列表功能 */
@GetMapping("/myCollections")
public Result myCollections(@RequestHeader("token") String token) {
    Integer userId = TokenUtils.getUserId(token);
    List<Post> posts = communityService.findMyCollections(userId);
    return Result.success(posts);
}
```

### 2. Service层 - CommunityService.java

#### 获取帖子详情

```java
/** 这段代码实现获取帖子详情的核心业务逻辑，核心逻辑是：
  * 1. 查询帖子详细信息
  * 2. 根据postId查询该帖子的所有评论
  * 3. 将评论列表添加到帖子数据中
  * 4. 返回完整的帖子数据（包含评论） */
public Map<String, Object> detail(Integer id) {
    Map<String, Object> post = postMapper.detail(id);
    List<Comment> comments = commentMapper.findByPostId(id);
    post.put("comments", comments);
    return post;
}
```

#### 发表评论

```java
/** 这段代码实现发表评论的核心业务逻辑，核心逻辑是：
  * 1. 保存评论到comment表
  * 2. 帖子的评论数+1 */
public void saveComment(Comment comment) {
    commentMapper.save(comment);
    postMapper.incrementCommentCount(comment.getPostId());
}
```

#### 点赞核心逻辑

```java
/** 这段代码实现点赞的核心业务逻辑，核心逻辑是：
  * 1. 创建Like对象
  * 2. 设置userId、targetId、targetType
  * 3. 保存点赞记录到like表
  * 4. 如果目标是帖子，则帖子点赞数+1 */
public void like(Integer userId, Integer targetId, String targetType) {
    Like like = new Like();
    like.setUserId(userId);
    like.setTargetId(targetId);
    like.setTargetType(targetType);
    likeMapper.save(like);
    if ("post".equals(targetType)) {
        postMapper.incrementLikeCount(targetId);
    }
}
```

#### 取消点赞核心逻辑

```java
/** 这段代码实现取消点赞的核心业务逻辑，核心逻辑是：
  * 1. 从like表删除点赞记录
  * 2. 如果目标是帖子，则帖子点赞数-1 */
public void unlike(Integer userId, Integer targetId, String targetType) {
    likeMapper.delete(userId, targetId, targetType);
    if ("post".equals(targetType)) {
        postMapper.decrementLikeCount(targetId);
    }
}
```

#### 收藏帖子

```java
/** 这段代码实现收藏帖子的核心业务逻辑，核心逻辑是：
  * 1. 创建CollectionItem对象
  * 2. 设置userId、targetId(帖子ID)、targetType("post")
  * 3. 保存到collection表 */
public void collect(Integer userId, Integer postId) {
    CollectionItem collection = new CollectionItem();
    collection.setUserId(userId);
    collection.setTargetId(postId);
    collection.setTargetType("post");
    collectionMapper.save(collection);
}
```

---

## 六、文件上传模块

> 支持图片、视频上传，配置静态资源映射

### Controller层 - VideoController.java

#### 上传图片

```java
/** 这段代码实现图片上传功能，核心逻辑是：
  * 1. 验证文件不能为空
  * 2. 验证文件类型必须是image/*
  * 3. 验证文件大小不超过10MB
  * 4. 生成UUID文件名防止重名
  * 5. 保存到服务器images目录
  * 6. 返回带/api前缀的访问URL */
@PostMapping("/upload/image")
public Result uploadImage(@RequestParam("file") MultipartFile file) {
    if (file.isEmpty()) {
        return Result.error("文件不能为空");
    }
    // 验证文件类型
    String contentType = file.getContentType();
    if (contentType == null || !contentType.startsWith("image/")) {
        return Result.error("只支持图片文件");
    }
    // 验证文件大小（10MB）
    if (file.getSize() > 10 * 1024 * 1024) {
        return Result.error("图片大小不能超过10MB");
    }
    try {
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileName = UUID.randomUUID().toString() + extension;
        String filePath = basePath + "/images/" + fileName;
        File dest = new File(filePath);
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }
        file.transferTo(dest);
        return Result.success("/api/images/" + fileName);
    } catch (Exception e) {
        e.printStackTrace();
        return Result.error("上传失败：" + e.getMessage());
    }
}
```

#### 上传视频

```java
/** 这段代码实现视频上传功能，核心逻辑是：
  * 1. 验证文件不能为空
  * 2. 验证文件类型必须是video/*
  * 3. 验证文件大小不超过1GB
  * 4. 生成UUID文件名
  * 5. 保存到服务器videos目录
  * 6. 返回带/api前缀的访问URL */
@PostMapping("/upload/video")
public Result uploadVideo(@RequestParam("file") MultipartFile file) {
    if (file.isEmpty()) {
        return Result.error("文件不能为空");
    }
    String contentType = file.getContentType();
    if (contentType == null || !contentType.startsWith("video/")) {
        return Result.error("只支持视频文件");
    }
    if (file.getSize() > 1024 * 1024 * 1024) {
        return Result.error("视频大小不能超过1GB");
    }
    try {
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileName = UUID.randomUUID().toString() + extension;
        String filePath = basePath + "/videos/" + fileName;
        File dest = new File(filePath);
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }
        file.transferTo(dest);
        return Result.success("/api/videos/" + fileName);
    } catch (Exception e) {
        e.printStackTrace();
        return Result.error("上传失败：" + e.getMessage());
    }
}
```

### 配置层 - WebConfig.java

```java
/** 这段代码实现静态资源映射功能，核心逻辑是：
  * 1. 将/api/images/**映射到服务器images目录
  * 2. 将/api/videos/**映射到服务器videos目录
  * 3. 这样前端可以通过URL直接访问上传的文件 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Value("${file.upload-path}")
    private String uploadPath;
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 图片资源映射
        registry.addResourceHandler("/api/images/**")
                .addResourceLocations("file:" + uploadPath + "/images/");
        // 视频资源映射
        registry.addResourceHandler("/api/videos/**")
                .addResourceLocations("file:" + uploadPath + "/videos/");
    }
}
```

---

## 七、管理员模块

> 管理员可以管理用户、课程、帖子、评论

### 1. 用户管理 - UserController.java

```java
/** 这段代码实现获取所有用户列表功能（管理员使用） */
@GetMapping("/list")
public Result list() {
    return Result.success(userService.list());
}

/** 这段代码实现修改用户状态功能，核心逻辑是：
  * 1. 接收id和status参数
  * 2. 查询用户是否存在
  * 3. 更新用户状态（0=禁用，1=启用） */
@PutMapping("/status")
public Result updateStatus(@RequestBody Map<String, Object> params) {
    Integer id = (Integer) params.get("id");
    Integer status = (Integer) params.get("status");
    User user = userService.findById(id);
    if (user == null) {
        return Result.error("用户不存在");
    }
    user.setStatus(status);
    userService.update(user);
    return Result.success();
}

/** 这段代码实现删除用户功能 */
@DeleteMapping("/delete/{id}")
public Result delete(@PathVariable Integer id) {
    User user = userService.findById(id);
    if (user == null) {
        return Result.error("用户不存在");
    }
    userService.delete(id);
    return Result.success();
}
```

### 2. 课程管理 - CourseController.java

```java
/** 这段代码实现管理员添加课程功能 */
@PostMapping("/admin/add")
public Result addCourse(@RequestBody Course course) {
    courseService.save(course);
    return Result.success();
}

/** 这段代码实现管理员修改课程功能 */
@PutMapping("/admin/update")
public Result updateCourse(@RequestBody Course course) {
    courseService.update(course);
    return Result.success();
}

/** 这段代码实现管理员删除课程功能 */
@DeleteMapping("/admin/delete/{id}")
public Result deleteCourse(@PathVariable Integer id) {
    courseService.delete(id);
    return Result.success();
}
```

### 3. 帖子管理 - CommunityController.java

```java
/** 这段代码实现管理员获取所有帖子列表功能 */
@GetMapping("/admin/list")
public Result adminList() {
    List<Post> posts = communityService.adminList();
    return Result.success(posts);
}

/** 这段代码实现管理员修改帖子功能 */
@PutMapping("/admin/update")
public Result adminUpdate(@RequestBody Post post) {
    communityService.updatePost(post);
    return Result.success();
}

/** 这段代码实现管理员删除帖子功能 */
@DeleteMapping("/admin/delete/{id}")
public Result adminDelete(@PathVariable Integer id) {
    communityService.deletePost(id);
    return Result.success();
}
```

---

## 八、数据统计模块

> 使用饼图、柱状图、折线图展示数据

### Controller层

#### 课程类型统计 - CourseController.java

```java
/** 这段代码实现课程类型分布统计功能，核心逻辑是：
  * 调用Service层按type分组统计课程数量
  * 返回数据用于前端饼图展示 */
@GetMapping("/stats/type")
public Result statsByType() {
    return Result.success(courseService.statsByType());
}
```

#### 帖子类型统计 - CommunityController.java

```java
/** 这段代码实现帖子类型分布统计功能，核心逻辑是：
  * 调用Service层按type分组统计帖子数量
  * 返回数据用于前端饼图展示 */
@GetMapping("/stats/type")
public Result statsByType() {
    return Result.success(communityService.statsByType());
}
```

#### 帖子趋势统计 - CommunityController.java

```java
/** 这段代码实现帖子数量趋势统计功能，核心逻辑是：
  * 调用Service层按日期分组统计帖子数量
  * 返回数据用于前端折线图展示 */
@GetMapping("/stats/trend")
public Result statsByTrend() {
    return Result.success(communityService.statsByTrend());
}
```

### Service层

```java
// CourseService.java
/** 这段代码实现课程类型分布统计 */
public List<Map<String, Object>> statsByType() {
    return courseMapper.statsByType();
}

// CommunityService.java
/** 这段代码实现帖子类型分布统计 */
public List<Map<String, Object>> statsByType() {
    return postMapper.statsByType();
}

/** 这段代码实现帖子数量趋势统计（按日期） */
public List<Map<String, Object>> statsByTrend() {
    return postMapper.statsByTrend();
}
```

---

## 九、核心SQL语句

### 1. 推荐算法SQL - CourseMapper.xml

```xml
<!-- 这段SQL实现个性化推荐算法，核心逻辑是：
  * 1. 查询用户学过的所有课程类型
  * 2. 推荐相同类型的其他课程（排除已学过的）
  * 3. 按浏览量降序排序
  * 4. 返回前5个推荐课程 -->
<select id="recommend" resultType="com.example.springboot.entity.Course">
    SELECT c.* FROM course c
    WHERE c.type IN (
        SELECT DISTINCT c2.type FROM study_record sr
        LEFT JOIN course c2 ON sr.course_id = c2.id
        WHERE sr.user_id = #{userId}
    )
    AND c.id NOT IN (
        SELECT course_id FROM study_record WHERE user_id = #{userId}
    )
    ORDER BY c.view_count DESC
    LIMIT 5
</select>
```

### 2. 我的课程SQL - StudyRecordMapper.xml

```xml
<!-- 这段SQL实现获取用户学习课程列表，核心逻辑是：
  * 1. 联表查询study_record和course表
  * 2. 返回学习进度、最后学习时间、课程信息 -->
<select id="findMyCourses" resultType="map">
    SELECT sr.*, c.title, c.cover, c.type, c.level
    FROM study_record sr
    LEFT JOIN course c ON sr.course_id = c.id
    WHERE sr.user_id = #{userId}
    ORDER BY sr.last_study_time DESC
</select>
```

---

## 十、安全配置

```java
// SecurityConfig.java

/** 这段代码实现Spring Security配置，核心逻辑是：
  * 1. 关闭CSRF防护（前后端分离架构不需要）
  * 2. 将所有接口权限设置为permitAll()
  * 3. 实际权限控制由业务代码中的Token验证实现 */
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> auth
            .anyRequest().permitAll()
        );
    return http.build();
}
```

---

## 总结表格

| 模块 | 核心Controller接口 | 核心Service逻辑 | 关键功能 |
|------|-------------------|----------------|---------|
| **用户认证** | login, register, getInfo, update, updatePassword | findByUsername, save, update, MD5加密 | 登录注册、JWT Token、个人信息管理 |
| **课程学习** | list, detail, collect, uncollect, myCollections | collect, uncollect, incrementViewCount | 课程浏览、收藏管理、浏览量统计 |
| **学习进度** | study, myCourses | saveOrUpdate（断点续学核心） | 自动保存进度、断点续学 |
| **个性化推荐** | recommend | recommend（推荐算法） | 根据学习历史智能推荐 |
| **社区互动** | post, comment, like, collect, myPosts, myComments | saveComment, like, unlike, collect | 发帖、评论、点赞、收藏 |
| **文件上传** | uploadImage, uploadVideo | 文件验证、保存、返回URL | 图片/视频上传、静态资源映射 |
| **管理员** | admin/* 接口 | CRUD操作 | 用户/课程/帖子管理 |
| **数据统计** | stats/* 接口 | statsByType, statsByTrend | 饼图/折线图数据 |

