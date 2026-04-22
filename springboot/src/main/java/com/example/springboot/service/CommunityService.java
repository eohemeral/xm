package com.example.springboot.service;

import com.example.springboot.entity.Post;
import com.example.springboot.entity.Comment;
import com.example.springboot.entity.Like;
import com.example.springboot.entity.CollectionItem;
import com.example.springboot.mapper.PostMapper;
import com.example.springboot.mapper.CommentMapper;
import com.example.springboot.mapper.LikeMapper;
import com.example.springboot.mapper.CollectionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class CommunityService {

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private LikeMapper likeMapper;

    @Autowired
    private CollectionMapper collectionMapper;

    public List<Post> list(String type) {
        return postMapper.list(type);
    }

    public List<Post> adminList() {
        return postMapper.adminList();
    }

    public Map<String, Object> detail(Integer id) {
        Map<String, Object> post = postMapper.detail(id);
        List<Comment> comments = commentMapper.findByPostId(id);
        post.put("comments", comments);
        return post;
    }

    public void incrementViewCount(Integer id) {
        postMapper.incrementViewCount(id);
    }

    public void savePost(Post post) {
        postMapper.save(post);
    }

    public void saveComment(Comment comment) {
        commentMapper.save(comment);
        postMapper.incrementCommentCount(comment.getPostId());
    }

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

    public void unlike(Integer userId, Integer targetId, String targetType) {
        likeMapper.delete(userId, targetId, targetType);
        if ("post".equals(targetType)) {
            postMapper.decrementLikeCount(targetId);
        }
    }

    public void collect(Integer userId, Integer postId) {
        CollectionItem collection = new CollectionItem();
        collection.setUserId(userId);
        collection.setTargetId(postId);
        collection.setTargetType("post");
        collectionMapper.save(collection);
    }

    public void uncollect(Integer userId, Integer postId) {
        collectionMapper.delete(userId, postId, "post");
    }

    public List<Post> findMyPosts(Integer userId) {
        return postMapper.findMyPosts(userId);
    }

    public List<Map<String, Object>> findMyComments(Integer userId) {
        return commentMapper.findMyComments(userId);
    }

    public List<Post> findMyCollections(Integer userId) {
        return postMapper.findMyCollections(userId);
    }

    public void updatePost(Post post) {
        postMapper.update(post);
    }

    public void deletePost(Integer id) {
        postMapper.delete(id);
    }

    public void deleteComment(Integer id) {
        Comment comment = commentMapper.findById(id);
        if (comment != null) {
            postMapper.decrementCommentCount(comment.getPostId());
            commentMapper.delete(id);
        }
    }

    public java.util.List<java.util.Map<String, Object>> statsByType() {
        return postMapper.statsByType();
    }

    public java.util.List<java.util.Map<String, Object>> statsByTrend() {
        return postMapper.statsByTrend();
    }
}