package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.entity.Course;
import com.example.springboot.service.CourseService;
import com.example.springboot.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/video")
public class VideoController {

    @Autowired
    private CourseService courseService;

    // 文件上传接口（支持视频和图片）
    @PostMapping("/upload")
    public Result uploadFile(@RequestParam("file") MultipartFile file, @RequestHeader(value = "token", required = false) String token) {
        // 允许未登录用户上传头像
        Integer userId = null;
        if (token != null) {
            userId = TokenUtils.getUserId(token);
        }

        // 检查文件类型
        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
        
        String uploadPath;
        String urlPrefix;
        
        // 视频文件
        if (".mp4".equals(fileExtension) || ".avi".equals(fileExtension) || ".mov".equals(fileExtension)) {
            // 检查文件大小（限制500MB）
            if (file.getSize() > 500 * 1024 * 1024) {
                return Result.error("视频文件大小不能超过500MB");
            }
            uploadPath = "e:\\xm\\library-management-main\\office-learning-platform\\springboot\\src\\main\\resources\\static\\videos\\";
            urlPrefix = "/videos/";
        }
        // 图片文件
        else if (".jpg".equals(fileExtension) || ".jpeg".equals(fileExtension) || ".png".equals(fileExtension) || ".gif".equals(fileExtension)) {
            // 检查文件大小（限制2MB）
            if (file.getSize() > 2 * 1024 * 1024) {
                return Result.error("图片文件大小不能超过2MB");
            }
            uploadPath = "e:\\xm\\library-management-main\\office-learning-platform\\springboot\\src\\main\\resources\\static\\images\\";
            urlPrefix = "/images/";
        }
        else {
            return Result.error("只支持MP4、AVI、MOV格式的视频和JPG、JPEG、PNG、GIF格式的图片");
        }

        // 生成唯一文件名
        String fileName = UUID.randomUUID().toString() + "_" + originalFilename;

        // 创建上传目录
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        try {
            // 保存文件
            file.transferTo(new File(uploadPath + fileName));
            String fileUrl = urlPrefix + fileName;
            return Result.success(fileUrl);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error("文件上传失败");
        }
    }

    // 更新课程视频
    @PutMapping("/update/{courseId}")
    public Result updateCourseVideo(@PathVariable Integer courseId, @RequestParam("file") MultipartFile file, @RequestHeader("token") String token) {
        Integer userId = TokenUtils.getUserId(token);
        if (userId == null) {
            return Result.error("请先登录");
        }

        // 检查文件类型
        String originalFilename = file.getOriginalFilename();
        if (!originalFilename.endsWith(".mp4") && !originalFilename.endsWith(".avi") && !originalFilename.endsWith(".mov")) {
            return Result.error("只支持MP4、AVI、MOV格式的视频");
        }

        // 检查文件大小（限制500MB）
        if (file.getSize() > 500 * 1024 * 1024) {
            return Result.error("视频文件大小不能超过500MB");
        }

        // 生成唯一文件名
        String fileName = UUID.randomUUID().toString() + "_" + originalFilename;
        String uploadPath = "e:\\xm\\library-management-main\\office-learning-platform\\springboot\\src\\main\\resources\\static\\videos\\";

        // 创建上传目录
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        try {
            // 保存文件
            file.transferTo(new File(uploadPath + fileName));
            String videoUrl = "/videos/" + fileName;

            // 更新课程视频URL
            Course course = courseService.findById(courseId);
            if (course == null) {
                return Result.error("课程不存在");
            }
            course.setVideoUrl(videoUrl);
            courseService.update(course);

            return Result.success("视频更新成功");
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error("视频上传失败");
        }
    }

    // 删除视频
    @DeleteMapping("/delete/{courseId}")
    public Result deleteVideo(@PathVariable Integer courseId, @RequestHeader("token") String token) {
        Integer userId = TokenUtils.getUserId(token);
        if (userId == null) {
            return Result.error("请先登录");
        }

        Course course = courseService.findById(courseId);
        if (course == null) {
            return Result.error("课程不存在");
        }

        if (course.getVideoUrl() != null) {
            // 删除视频文件
            String videoPath = "e:\\xm\\library-management-main\\office-learning-platform\\springboot\\src\\main\\resources\\static" + course.getVideoUrl();
            File videoFile = new File(videoPath);
            if (videoFile.exists()) {
                videoFile.delete();
            }

            // 清空课程视频URL
            course.setVideoUrl(null);
            courseService.update(course);
        }

        return Result.success("视频删除成功");
    }
}
