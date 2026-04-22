
package com.example.springboot.mapper;

import com.example.springboot.entity.Like;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface LikeMapper {

    void save(Like like);

    void delete(@Param("userId") Integer userId, @Param("targetId") Integer targetId, @Param("targetType") String targetType);

    Like findByUserIdAndTargetIdAndTargetType(@Param("userId") Integer userId, @Param("targetId") Integer targetId, @Param("targetType") String targetType);
}