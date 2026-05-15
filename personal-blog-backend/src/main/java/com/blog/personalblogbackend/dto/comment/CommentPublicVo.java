package com.blog.personalblogbackend.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentPublicVo {
    private Long id;
    private Long parentId;
    private String author;
    private String content;
    private LocalDateTime createTime;
}
