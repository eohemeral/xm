package com.example.springboot.service;

import com.example.springboot.entity.StudyRecord;
import com.example.springboot.mapper.StudyRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class StudyRecordService {

    @Autowired
    private StudyRecordMapper studyRecordMapper;

    public void saveOrUpdate(Integer userId, Integer courseId, Integer progress) {
        StudyRecord record = studyRecordMapper.findByUserIdAndCourseId(userId, courseId);
        if (record == null) {
            record = new StudyRecord();
            record.setUserId(userId);
            record.setCourseId(courseId);
            record.setProgress(progress);
            record.setLastStudyTime(new Date());
            studyRecordMapper.save(record);
        } else {
            record.setProgress(progress);
            record.setLastStudyTime(new Date());
            studyRecordMapper.update(record);
        }
    }

    public List<Map<String, Object>> findMyCourses(Integer userId) {
        return studyRecordMapper.findMyCourses(userId);
    }

    public StudyRecord findByUserIdAndCourseId(Integer userId, Integer courseId) {
        return studyRecordMapper.findByUserIdAndCourseId(userId, courseId);
    }
}