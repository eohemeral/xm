package com.example.springboot.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseInitializer implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        // 创建用户表
        String createUserTable = "CREATE TABLE IF NOT EXISTS `user` (`id` int(11) NOT NULL AUTO_INCREMENT, `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名', `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码', `nickname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '昵称', `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '邮箱', `phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '手机号', `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '头像', `status` tinyint(1) NULL DEFAULT 1 COMMENT '状态 1启用 0禁用', `createtime` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0), `updatetime` datetime(0) NULL DEFAULT NULL, PRIMARY KEY (`id`) USING BTREE, UNIQUE INDEX `username_key`(`username`) USING BTREE) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;";
        jdbcTemplate.execute(createUserTable);

        // 创建课程表
        String createCourseTable = "CREATE TABLE IF NOT EXISTS `course` (`id` int(11) NOT NULL AUTO_INCREMENT, `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '课程标题', `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '课程描述', `type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '课程类型 Word/Excel/PPT', `level` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '难度等级 入门/进阶', `scenario` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '应用场景 财务/行政/人力', `cover` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '课程封面', `video_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '视频地址', `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '课程内容', `view_count` int(11) NULL DEFAULT 0 COMMENT '浏览量', `collect_count` int(11) NULL DEFAULT 0 COMMENT '收藏量', `createtime` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0), `updatetime` datetime(0) NULL DEFAULT NULL, PRIMARY KEY (`id`) USING BTREE) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;";
        jdbcTemplate.execute(createCourseTable);

        // 创建帖子表
        String createPostTable = "CREATE TABLE IF NOT EXISTS `post` (`id` int(11) NOT NULL AUTO_INCREMENT, `user_id` int(11) NOT NULL COMMENT '用户ID', `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '帖子标题', `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '帖子内容', `type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '帖子类型 Word/Excel/PPT', `cover` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '封面图片', `video_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '视频地址', `view_count` int(11) NULL DEFAULT 0 COMMENT '浏览量', `like_count` int(11) NULL DEFAULT 0 COMMENT '点赞量', `comment_count` int(11) NULL DEFAULT 0 COMMENT '评论量', `createtime` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0), `updatetime` datetime(0) NULL DEFAULT NULL, PRIMARY KEY (`id`) USING BTREE, INDEX `user_id_index`(`user_id`) USING BTREE) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;";
        jdbcTemplate.execute(createPostTable);

        // 创建评论表
        String createCommentTable = "CREATE TABLE IF NOT EXISTS `comment` (`id` int(11) NOT NULL AUTO_INCREMENT, `post_id` int(11) NOT NULL COMMENT '帖子ID', `user_id` int(11) NOT NULL COMMENT '用户ID', `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '评论内容', `createtime` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0), `updatetime` datetime(0) NULL DEFAULT NULL, PRIMARY KEY (`id`) USING BTREE, INDEX `post_id_index`(`post_id`) USING BTREE, INDEX `user_id_index`(`user_id`) USING BTREE) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;";
        jdbcTemplate.execute(createCommentTable);

        // 创建学习记录表
        String createStudyRecordTable = "CREATE TABLE IF NOT EXISTS `study_record` (`id` int(11) NOT NULL AUTO_INCREMENT, `user_id` int(11) NOT NULL COMMENT '用户ID', `course_id` int(11) NOT NULL COMMENT '课程ID', `progress` int(11) NULL DEFAULT 0 COMMENT '学习进度', `last_study_time` datetime(0) NULL DEFAULT NULL COMMENT '最后学习时间', `createtime` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0), `updatetime` datetime(0) NULL DEFAULT NULL, PRIMARY KEY (`id`) USING BTREE, UNIQUE INDEX `user_course_index`(`user_id`, `course_id`) USING BTREE) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;";
        jdbcTemplate.execute(createStudyRecordTable);

        // 创建收藏表
        String createCollectionTable = "CREATE TABLE IF NOT EXISTS `collection` (`id` int(11) NOT NULL AUTO_INCREMENT, `user_id` int(11) NOT NULL COMMENT '用户ID', `target_id` int(11) NOT NULL COMMENT '目标ID 课程ID或帖子ID', `target_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '目标类型 course/post', `createtime` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0), PRIMARY KEY (`id`) USING BTREE, UNIQUE INDEX `user_target_index`(`user_id`, `target_id`, `target_type`) USING BTREE) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;";
        jdbcTemplate.execute(createCollectionTable);

        // 创建点赞表
        String createLikeTable = "CREATE TABLE IF NOT EXISTS `like` (`id` int(11) NOT NULL AUTO_INCREMENT, `user_id` int(11) NOT NULL COMMENT '用户ID', `target_id` int(11) NOT NULL COMMENT '目标ID 帖子ID或评论ID', `target_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '目标类型 post/comment', `createtime` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0), PRIMARY KEY (`id`) USING BTREE, UNIQUE INDEX `user_target_index`(`user_id`, `target_id`, `target_type`) USING BTREE) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;";
        jdbcTemplate.execute(createLikeTable);

        // 插入默认管理员用户（使用REPLACE INTO确保密码被更新）
        String insertAdminUser = "REPLACE INTO `user` VALUES (1, 'admin', 'e10adc3949ba59abbe56e057f20f883e', '管理员', 'admin@example.com', '13800138000', NULL, 1, CURRENT_TIMESTAMP, NULL);";
        jdbcTemplate.execute(insertAdminUser);

        // 插入默认课程数据
        String insertCourse1 = "INSERT IGNORE INTO `course` VALUES (1, 'Word基础教程', 'Word入门级教程，适合初学者', 'Word', '入门', '办公通用', NULL, NULL, '<h1>Word基础教程</h1><p>本教程将介绍Word的基本操作，包括文档创建、编辑、格式化等内容。</p>', 0, 0, CURRENT_TIMESTAMP, NULL);";
        jdbcTemplate.execute(insertCourse1);

        String insertCourse2 = "INSERT IGNORE INTO `course` VALUES (2, 'Excel函数进阶', 'Excel函数高级应用，适合有一定基础的用户', 'Excel', '进阶', '财务', NULL, NULL, '<h1>Excel函数进阶</h1><p>本教程将介绍Excel常用函数的高级应用，包括VLOOKUP、SUMIF、COUNTIF等函数。</p>', 0, 0, CURRENT_TIMESTAMP, NULL);";
        jdbcTemplate.execute(insertCourse2);

        String insertCourse3 = "INSERT IGNORE INTO `course` VALUES (3, 'PPT制作技巧', 'PPT制作高级技巧，提升演示效果', 'PPT', '进阶', '商务', NULL, NULL, '<h1>PPT制作技巧</h1><p>本教程将介绍PPT制作的高级技巧，包括动画效果、配色方案、版式设计等内容。</p>', 0, 0, CURRENT_TIMESTAMP, NULL);";
        jdbcTemplate.execute(insertCourse3);

        // 插入默认帖子数据
        String insertPost1 = "INSERT IGNORE INTO `post` VALUES (1, 1, 'Word中如何快速插入表格？', '最近在使用Word时，需要插入大量表格，有没有快速的方法？', 'Word', NULL, NULL, 0, 0, 0, CURRENT_TIMESTAMP, NULL);";
        jdbcTemplate.execute(insertPost1);

        String insertPost2 = "INSERT IGNORE INTO `post` VALUES (2, 1, 'Excel中如何使用VLOOKUP函数？', 'VLOOKUP函数的使用方法不太明白，有谁能详细解释一下吗？', 'Excel', NULL, NULL, 0, 0, 0, CURRENT_TIMESTAMP, NULL);";
        jdbcTemplate.execute(insertPost2);

        // 插入默认评论数据
        String insertComment1 = "INSERT IGNORE INTO `comment` VALUES (1, 1, 1, '可以使用快捷键Ctrl+T快速插入表格。', CURRENT_TIMESTAMP, NULL);";
        jdbcTemplate.execute(insertComment1);

        System.out.println("数据库初始化完成！");
    }
}