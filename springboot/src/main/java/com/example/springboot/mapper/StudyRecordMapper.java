package com.example.springboot.mapper;

import com.example.springboot.entity.StudyRecord;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface StudyRecordMapper {

    StudyRecord findByUserIdAndCourseId(Integer userId, Integer courseId);

    void save(StudyRecord studyRecord);

    void update(StudyRecord studyRecord);

    List<Map<String, Object>> findMyCourses(Integer userId);
}