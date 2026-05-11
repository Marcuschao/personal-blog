package com.blog.personalblogbackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.blog.personalblogbackend.mapper")
public class PersonalBlogBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(PersonalBlogBackendApplication.class, args);
    }

}
