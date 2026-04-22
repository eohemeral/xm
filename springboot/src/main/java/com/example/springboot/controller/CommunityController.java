package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.entity.Post;
import com.example.springboot.entity.Comment;
import com.example.springboot.service.CommunityService;
import com.example.springboot.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/community")
public class CommunityController {

    @Autowired
    private CommunityService communityService;

    @GetMapping("/list")
    public Result list(@RequestParam(required = false) String type) {
        List<Post> posts = communityService.list(type);
        return Result.success(posts);
    }

    @GetMapping("/detail/{id}")
    public Result detail(@PathVariable Integer id) {
        Map<String, Object> post = communityService.detail(id);
        communityService.incrementViewCount(id);
        return Result.success(post);
    }

    @PostMapping("/post")
    public Result post(@RequestBody Post post, @RequestHeader("token") String token) {
        Integer userId = TokenUtils.getUserId(token);
        post.setUserId(userId);
        communityService.savePost(post);
        return Result.success();
    }

    @PostMapping("/comment")
    public Result comment(@RequestBody Comment comment, @RequestHeader("token") String token) {
        Integer userId = TokenUtils.getUserId(token);
        comment.setUserId(userId);
        communityService.saveComment(comment);
        return Result.success();
    }

    @PostMapping("/like/{id}")
    public Result like(@PathVariable Integer id, @RequestHeader("token") String token) {
        Integer userId = TokenUtils.getUserId(token);
        communityService.like(userId, id, "post");
        return Result.success();
    }

    @PostMapping("/unlike/{id}")
    public Result unlike(@PathVariable Integer id, @RequestHeader("token") String token) {
        Integer userId = TokenUtils.getUserId(token);
        communityService.unlike(userId, id, "post");
        return Result.success();
    }

    @PostMapping("/collect/{id}")
    public Result collect(@PathVariable Integer id, @RequestHeader("token") String token) {
        Integer userId = TokenUtils.getUserId(token);
        communityService.collect(userId, id);
        return Result.success();
    }

    @PostMapping("/uncollect/{id}")
    public Result uncollect(@PathVariable Integer id, @RequestHeader("token") String token) {
        Integer userId = TokenUtils.getUserId(token);
        communityService.uncollect(userId, id);
        return Result.success();
    }

    @PostMapping("/comment/like/{id}")
    public Result likeComment(@PathVariable Integer id, @RequestHeader("token") String token) {
        Integer userId = TokenUtils.getUserId(token);
        communityService.like(userId, id, "comment");
        return Result.success();
    }

    @PostMapping("/comment/unlike/{id}")
    public Result unlikeComment(@PathVariable Integer id, @RequestHeader("token") String token) {
        Integer userId = TokenUtils.getUserId(token);
        communityService.unlike(userId, id, "comment");
        return Result.success();
    }

    @GetMapping("/myPosts")
    public Result myPosts(@RequestHeader("token") String token) {
        Integer userId = TokenUtils.getUserId(token);
        List<Post> posts = communityService.findMyPosts(userId);
        return Result.success(posts);
    }

    @GetMapping("/myComments")
    public Result myComments(@RequestHeader("token") String token) {
        Integer userId = TokenUtils.getUserId(token);
        List<Map<String, Object>> comments = communityService.findMyComments(userId);
        return Result.success(comments);
    }

    @GetMapping("/myCollections")
    public Result myCollections(@RequestHeader("token") String token) {
        Integer userId = TokenUtils.getUserId(token);
        List<Post> posts = communityService.findMyCollections(userId);
        return Result.success(posts);
    }

    // 管理员接口：获取所有帖子列表
    @GetMapping("/admin/list")
    public Result adminList() {
        List<Post> posts = communityService.adminList();
        return Result.success(posts);
    }

    // 管理员接口：修改帖子
    @PutMapping("/admin/update")
    public Result adminUpdate(@RequestBody Post post) {
        communityService.updatePost(post);
        return Result.success();
    }

    // 管理员接口：删除帖子
    @DeleteMapping("/admin/delete/{id}")
    public Result adminDelete(@PathVariable Integer id) {
        communityService.deletePost(id);
        return Result.success();
    }

    // 统计接口：帖子类型分布
    @GetMapping("/stats/type")
    public Result statsByType() {
        return Result.success(communityService.statsByType());
    }

    // 统计接口：帖子数量趋势
    @GetMapping("/stats/trend")
    public Result statsByTrend() {
        return Result.success(communityService.statsByTrend());
    }
}