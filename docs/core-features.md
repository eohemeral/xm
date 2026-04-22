# 核心功能代码示例

## 5.4.1 前端请求示例

### 获取课程详情

```javascript
const loadCourse = () => {
  const id = this.$route.params.id
  request.get(`/course/detail/${id}`).then(res => {
    this.course = res.data
  })
}
```

### 更新学习进度

```javascript
const updateProgress = (progress) => {
  request.post('/course/study', {
    courseId: id,
    progress: progress
  }).then(res => {
    this.progress = progress
    this.$message.success('学习进度已更新')
  })
}
```

### 收藏课程

```javascript
const collect = () => {
  request.post(`/course/collect/${id}`).then(res => {
    this.isCollected = true
    this.course.collectCount++
    this.$message.success('收藏成功')
  })
}
```

---

## 5.4.2 后端推荐逻辑

### CourseService.java

```java
@Service
public class CourseService {
    
    public List<Course> recommend(Integer userId) {
        // 根据用户的学习记录和收藏记录，推荐相同类型的课程
        return courseMapper.recommend(userId);
    }
}
```

### CourseMapper.xml

```xml
<!-- 个性化推荐课程 -->
<select id="recommend" resultType="com.example.springboot.entity.Course">
    SELECT DISTINCT c.* 
    FROM course c
    WHERE c.id NOT IN (
        SELECT course_id FROM study_record WHERE user_id = #{userId}
    )
    AND (
        c.type IN (
            SELECT type FROM course WHERE id IN (
                SELECT course_id FROM study_record WHERE user_id = #{userId}
                UNION
                SELECT target_id FROM collection WHERE user_id = #{userId} AND target_type = 'course'
            )
        )
    )
    ORDER BY c.view_count DESC
    LIMIT 10
</select>
```

---

## 5.5 个性化推荐实现

**推荐策略**：基于物品的简单协同过滤

**实现逻辑**：
1. **记录行为**：用户浏览课程时 `view_count + 1`，学习时记录到 `study_record` 表，收藏时记录到 `collection` 表
2. **推荐算法**：获取用户学过或收藏的课程类型，从同类课程中选择浏览量最高的 Top 10
3. **冷启动**：若用户无历史记录，返回全局热门课程
4. **实时更新**：不需要离线计算，推荐结果实时生成

**技术特点**：
- 实时性好：推荐结果实时更新
- 实现简单：基于 SQL 查询，无需复杂算法
- 可解释性强：用户可理解推荐原因
- 适合初期平台：用户量不大时效果较好

---

## 5.6 学习进度追踪

### StudyRecordService.java

```java
@Service
public class StudyRecordService {
    
    public void saveOrUpdate(Integer userId, Integer courseId, Integer progress) {
        StudyRecord record = studyRecordMapper.findByUserIdAndCourseId(userId, courseId);
        if (record == null) {
            // 首次学习，创建新记录
            record = new StudyRecord();
            record.setUserId(userId);
            record.setCourseId(courseId);
            record.setProgress(progress);
            record.setLastStudyTime(new Date());
            studyRecordMapper.save(record);
        } else {
            // 继续学习，更新进度
            record.setProgress(progress);
            record.setLastStudyTime(new Date());
            studyRecordMapper.update(record);
        }
    }
}
```

**进度更新流程**：
```
用户点击"开始学习" → 前端调用/course/study → 后端检查 study_record 表 
→ 不存在则 INSERT，存在则 UPDATE → 返回成功 → 前端提示更新成功
```

---

## 5.7 热门内容统计

**热门课程查询**：
```sql
SELECT * FROM course 
WHERE createtime >= DATE_SUB(NOW(), INTERVAL 7 DAY)
ORDER BY view_count DESC 
LIMIT 10
```

**统计逻辑**：
- 每次访问详情页，`view_count + 1`
- 默认统计近 7 天数据
- 按浏览量降序排序

---

## 5.8 总结

**核心功能**：
1. 课程学习：在线学习、进度追踪、收藏管理
2. 个性化推荐：基于用户行为的实时推荐
3. 热门统计：按时间范围统计热门内容
4. 社区互动：发帖、评论、收藏、点赞

**技术特点**：
- 前后端分离：Vue + Spring Boot
- RESTful API：标准化接口
- JWT 认证：安全认证
- 实时性：推荐和统计实时更新
- 可扩展：预留算法优化空间
