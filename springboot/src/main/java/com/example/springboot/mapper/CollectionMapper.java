package com.example.springboot.mapper;

import com.example.springboot.entity.CollectionItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CollectionMapper {

    void save(CollectionItem collection);

    void delete(@Param("userId") Integer userId, @Param("targetId") Integer targetId, @Param("targetType") String targetType);

    CollectionItem findByUserIdAndTargetIdAndTargetType(@Param("userId") Integer userId, @Param("targetId") Integer targetId, @Param("targetType") String targetType);
}