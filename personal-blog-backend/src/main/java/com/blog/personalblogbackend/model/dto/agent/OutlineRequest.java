package com.blog.personalblogbackend.model.dto.agent;

import lombok.Data;

@Data
public class OutlineRequest {
    private String title;
    private String summary;
    private String topic;
}
