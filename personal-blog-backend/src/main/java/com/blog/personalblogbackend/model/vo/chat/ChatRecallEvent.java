package com.blog.personalblogbackend.model.vo.chat;

import lombok.Data;

@Data
public class ChatRecallEvent {
    private Long messageId;
    private Long recalledBy;
    private Boolean admin;
}
