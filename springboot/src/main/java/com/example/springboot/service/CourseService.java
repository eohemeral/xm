package com.example.springboot.service;

import com.example.springboot.entity.Course;
import com.example.springboot.entity.CollectionItem;
import com.example.springboot.mapper.CourseMapper;
import com.example.springboot.mapper.CollectionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseService {

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private CollectionMapper collectionMapper;

    public List<Course> list(String type, String level, String scenario) {
        return courseMapper.list(type, level, scenario);
    }

    public Course findById(Integer id) {
        return courseMapper.findById(id);
    }

    public void incrementViewCount(Integer id) {
        courseMapper.incrementViewCount(id);
    }

    public void collect(Integer userId, Integer courseId) {
        CollectionItem collection = new CollectionItem();
        collection.setUserId(userId);
        collection.setTargetId(courseId);
        collection.setTargetType("course");
        collectionMapper.save(collection);
        courseMapper.incrementCollectCount(courseId);
    }

    public void uncollect(Integer userId, Integer courseId) {
        collectionMapper.delete(userId, courseId, "course");
        courseMapper.decrementCollectCount(courseId);
    }

    public List<Course> findMyCollections(Integer userId) {
        return courseMapper.findMyCollections(userId);
    }

    public List<Course> recommend(Integer userId) {
        // 简单的推荐算法：根据用户的学习记录和收藏记录，推荐相同类型的课程
        if (userId == null) {
            // 如果没有用户ID，返回热门课程
            return courseMapper.list(null, null, null).subList(0, Math.min(5, courseMapper.list(null, null, null).size()));
        }
        return courseMapper.recommend(userId);
    }

    public void save(Course course) {
        courseMapper.save(course);
    }

    public void update(Course course) {
        courseMapper.update(course);
    }

    public void delete(Integer id) {
        courseMapper.delete(id);
    }

    public java.util.List<java.util.Map<String, Object>> statsByType() {
        return courseMapper.statsByType();
    }

    public java.util.List<java.util.Map<String, Object>> listCoursesForExport() {
        return courseMapper.listCoursesForExport();
    }
}