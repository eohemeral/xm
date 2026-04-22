package com.example.springboot;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 启动类核心修复：添加@MapperScan扫描Mapper接口
 */
@SpringBootApplication
@MapperScan("com.example.springboot.mapper") // 扫描mapper接口所在包（必须和实际包名一致）
public class SpringbootApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootApplication.class, args);
    }

}