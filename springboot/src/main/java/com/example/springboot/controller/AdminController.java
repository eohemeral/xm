package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.service.UserService;
import com.example.springboot.service.CourseService;
import com.example.springboot.utils.ExportUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private CourseService courseService;

    // 导出用户信息为Word文档
    @GetMapping("/export/users")
    public void exportUsers(HttpServletResponse response) {
        try {
            // 获取用户列表
            List<Map<String, Object>> users = userService.listUsersForExport();
            
            // 设置响应头
            response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
            response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode("用户信息表.docx", "UTF-8"));
            
            // 导出为Word
            OutputStream outputStream = response.getOutputStream();
            ExportUtils.exportUsersToWord(users, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 导出课程信息为Word文档
    @GetMapping("/export/courses")
    public void exportCourses(HttpServletResponse response) {
        try {
            // 获取课程列表
            List<Map<String, Object>> courses = courseService.listCoursesForExport();
            
            // 设置响应头
            response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
            response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode("课程信息表.docx", "UTF-8"));
            
            // 导出为Word
            OutputStream outputStream = response.getOutputStream();
            ExportUtils.exportCoursesToWord(courses, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
