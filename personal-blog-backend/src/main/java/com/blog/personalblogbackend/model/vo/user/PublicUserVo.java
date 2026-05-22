package com.blog.personalblogbackend.model.vo.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PublicUserVo {

    private Long id;
    private String nickname;
    private String avatar;
    private Integer gender;
    private String region;
    private String bio;
}
