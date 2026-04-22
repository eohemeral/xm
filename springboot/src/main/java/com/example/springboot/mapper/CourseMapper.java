package com.example.springboot.mapper;

import com.example.springboot.entity.Course;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CourseMapper {

    List<Course> list(String type, String level, String scenario);

    Course findById(Integer id);

    void incrementViewCount(Integer id);

    void incrementCollectCount(Integer id);

    void decrementCollectCount(Integer id);

    List<Course> findMyCollections(Integer userId);

    List<Course> recommend(Integer userId);

    void save(Course course);

    void update(Course course);

    void delete(Integer id);

    java.util.List<java.util.Map<String, Object>> statsByType();

    java.util.List<java.util.Map<String, Object>> listCoursesForExport();
}