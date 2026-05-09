package com.blog.personalblogbackend.dto;

import com.blog.personalblogbackend.entity.Article;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ArticleVO extends Article {
    // 如果 Article 实体中已经包含了 categoryName 和 tags，这里可以不需要额外字段
    // 如果有其他需要额外展示给前端的字段，可以在这里添加
}
