package com.blog.personalblogbackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List; // 用于存储文章关联的标签列表

@Data
@TableName("article")
public class Article {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String summary;
    private String content; // Markdown内容
    private String cover;
    private Long categoryId;
    private Integer status; // 0-草稿 1-发布
    private Integer viewCount;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // 非数据库字段，用于关联查询
    private String categoryName;
    private List<Tag> tags; // 文章关联的标签列表
}
