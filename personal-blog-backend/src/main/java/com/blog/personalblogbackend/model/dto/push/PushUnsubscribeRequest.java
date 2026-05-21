package com.blog.personalblogbackend.model.dto.push;

import lombok.Data;

@Data
public class PushUnsubscribeRequest {
    private String endpoint;
}
