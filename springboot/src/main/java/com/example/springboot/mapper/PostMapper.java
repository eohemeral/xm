package com.example.springboot.mapper;

import com.example.springboot.entity.Post;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface PostMapper {

    List<Post> list(String type);

    List<Post> adminList();

    Map<String, Object> detail(Integer id);

    void incrementViewCount(Integer id);

    void incrementLikeCount(Integer id);

    void decrementLikeCount(Integer id);

    void incrementCommentCount(Integer postId);

    void decrementCommentCount(Integer postId);

    void save(Post post);

    void update(Post post);

    void delete(Integer id);

    java.util.List<java.util.Map<String, Object>> statsByType();

    java.util.List<java.util.Map<String, Object>> statsByTrend();

    List<Post> findMyPosts(Integer userId);

    List<Post> findMyCollections(Integer userId);
}