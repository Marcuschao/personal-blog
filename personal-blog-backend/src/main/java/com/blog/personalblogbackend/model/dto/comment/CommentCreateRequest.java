package com.blog.personalblogbackend.model.dto.comment;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CommentCreateRequest {
    @NotNull
    private Long articleId;
    private Long parentId;
    @NotBlank
    @Size(max = 64)
    private String author;
    @NotBlank
    @Email
    @Size(max = 128)
    private String email;
    @NotBlank
    @Size(max = 4000)
    private String content;
    @NotBlank
    private String captchaId;
    @NotNull
    private Integer captchaAnswer;
}
