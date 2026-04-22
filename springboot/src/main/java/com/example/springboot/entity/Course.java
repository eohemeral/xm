package com.example.springboot.entity;

import lombok.Data;

import java.util.Date;

@Data
public class Course {
    private Integer id;
    private String title;
    private String description;
    private String type;
    private String level;
    private String scenario;
    private String cover;
    private String videoUrl;
    private String content;
    private Integer viewCount;
    private Integer collectCount;
    private Date createtime;
    private Date updatetime;
}