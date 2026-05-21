package com.blog.personalblogbackend.model.vo.stream;

import lombok.Data;

@Data
public class DeadLetterVo {
    private String recordId;
    private String eventJson;
    private String error;
    private String originalId;
    private int retryCount;
}
