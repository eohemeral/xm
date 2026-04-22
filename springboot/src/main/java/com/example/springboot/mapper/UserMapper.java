package com.example.springboot.mapper;

import com.example.springboot.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {

    User findByUsername(String username);

    User findById(Integer id);

    void save(User user);

    void update(User user);

    void updatePassword(User user);

    void delete(Integer id);

    java.util.List<User> list();

    java.util.List<java.util.Map<String, Object>> listUsersForExport();
}