package com.blog.personalblogbackend.dto.agent;

import lombok.Data;

@Data
public class EditorContinueRequest {

    private String title;
    private String keywords;
    private String content;
    private Integer selectionStart;
    private Integer selectionEnd;
}
