package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.entity.Course;
import com.example.springboot.entity.StudyRecord;
import com.example.springboot.service.CourseService;
import com.example.springboot.service.StudyRecordService;
import com.example.springboot.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/course")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private StudyRecordService studyRecordService;

    @GetMapping("/list")
    public Result list(@RequestParam(required = false) String type, @RequestParam(required = false) String level, @RequestParam(required = false) String scenario) {
        List<Course> courses = courseService.list(type, level, scenario);
        return Result.success(courses);
    }

    @GetMapping("/detail/{id}")
    public Result detail(@PathVariable Integer id) {
        Course course = courseService.findById(id);
        courseService.incrementViewCount(id);
        return Result.success(course);
    }

    @PostMapping("/study")
    public Result study(@RequestBody Map<String, Object> params, @RequestHeader(value = "token", required = false) String token) {
        Integer userId = TokenUtils.getUserId(token);
        if (userId == null) {
            return Result.error("请先登录");
        }
        Integer courseId = (Integer) params.get("courseId");
        Integer progress = (Integer) params.get("progress");
        studyRecordService.saveOrUpdate(userId, courseId, progress);
        return Result.success();
    }

    @GetMapping("/myCourses")
    public Result myCourses(@RequestHeader("token") String token) {
        Integer userId = TokenUtils.getUserId(token);
        List<Map<String, Object>> courses = studyRecordService.findMyCourses(userId);
        return Result.success(courses);
    }

    @PostMapping("/collect/{id}")
    public Result collect(@PathVariable Integer id, @RequestHeader("token") String token) {
        Integer userId = TokenUtils.getUserId(token);
        courseService.collect(userId, id);
        return Result.success();
    }

    @PostMapping("/uncollect/{id}")
    public Result uncollect(@PathVariable Integer id, @RequestHeader("token") String token) {
        Integer userId = TokenUtils.getUserId(token);
        courseService.uncollect(userId, id);
        return Result.success();
    }

    @GetMapping("/myCollections")
    public Result myCollections(@RequestHeader("token") String token) {
        Integer userId = TokenUtils.getUserId(token);
        List<Course> courses = courseService.findMyCollections(userId);
        return Result.success(courses);
    }

    @GetMapping("/recommend")
    public Result recommend(@RequestHeader(value = "token", required = false) String token) {
        Integer userId = null;
        if (token != null) {
            userId = TokenUtils.getUserId(token);
        }
        List<Course> courses = courseService.recommend(userId);
        return Result.success(courses);
    }

    // 统计接口：课程类型分布
    @GetMapping("/stats/type")
    public Result statsByType() {
        return Result.success(courseService.statsByType());
    }

    // 管理员接口：添加课程
    @PostMapping("/admin/add")
    public Result addCourse(@RequestBody Course course) {
        courseService.save(course);
        return Result.success();
    }

    // 管理员接口：修改课程
    @PutMapping("/admin/update")
    public Result updateCourse(@RequestBody Course course) {
        courseService.update(course);
        return Result.success();
    }

    // 管理员接口：删除课程
    @DeleteMapping("/admin/delete/{id}")
    public Result deleteCourse(@PathVariable Integer id) {
        courseService.delete(id);
        return Result.success();
    }
}