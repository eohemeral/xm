package com.example.springboot.mapper;

import com.example.springboot.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface CommentMapper {

    List<Comment> findByPostId(Integer postId);

    Comment findById(Integer id);

    void save(Comment comment);

    void update(Comment comment);

    void delete(Integer id);

    List<Map<String, Object>> findMyComments(Integer userId);
}